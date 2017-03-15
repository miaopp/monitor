package com.miaopp.monitor.metrics;

import com.miaopp.monitor.constant.Measure;
import com.miaopp.monitor.constant.MonitorType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by miaoping on 16/12/2.
 */
@Aspect
public class MonitorAspectJ {

    private static final Logger logger = LoggerFactory.getLogger(MonitorAspectJ.class);

    //todo 量大之后可以考虑用缓存，异步写

    //todo 线上环境报警用，暂时还未做报警，不用
    private String monitorEnv;

    @Pointcut("@annotation(com.miaopp.monitor.metrics.Monitorable)")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before() {
        logger.info("collecting data start.");
    }

    @Around("pointCut()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = pjp.proceed();
        nativeMonitor(pjp, System.currentTimeMillis() - start);
        return obj;
    }

    @After("pointCut()")
    public void after() {
        logger.info("collecting data end.");
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "error")
    public void afterThrowing(JoinPoint jp, Throwable error) {
        logger.info("error: {}", error);
    }

    private void nativeMonitor(ProceedingJoinPoint pjp, long duration) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Monitorable monitorable = method.getAnnotation(Monitorable.class);
        if (null == monitorable) {
            return;
        }
        monitorDataCollect(monitorable, method, duration);
    }

    private void monitorDataCollect(Monitorable monitorable, Method method, long duration) {
        try {
            MonitorType type = monitorable.type();
            Measure measure = monitorable.measure();
            String monitorName = buildMonitorName(measure, method);
            switch (type) {
                case time:
                    Monitor.recordTime(monitorName, duration);
                    break;
                case time_count:
                    Monitor.recordOne(monitorName, duration);
                    break;
                case count:
                    Monitor.recordOne(monitorName);
                    break;
                default:
                    break;
            }
            //todo 异步报警
        } catch (Exception e) {
            logger.error("collecting data error", e);
        }
    }

    private String buildMonitorName(Measure measure, Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        return measure.name() + "#" + className + "_" + methodName;
    }


}
