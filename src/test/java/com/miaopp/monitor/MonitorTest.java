package com.miaopp.monitor;

import com.miaopp.monitor.metrics.Monitor;
import org.junit.Test;

/**
 * Created by miaoping on 16/12/5.
 */
public class MonitorTest {

    @Test
    public void run() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 19990; i++) {
            Monitor.recordOne("dubbo#miaomiao", 100l);
        }

        System.out.println(System.currentTimeMillis() - start);
    }
}
