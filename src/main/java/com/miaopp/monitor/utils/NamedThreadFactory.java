package com.miaopp.monitor.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by miaoping on 16/12/5.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

    private final AtomicInteger mThreadNum;

    private final String mPrefix;

    private final boolean mDaemo;

    private final ThreadGroup mGroup;

    public NamedThreadFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemo) {
        this.mThreadNum = new AtomicInteger(1);
        this.mPrefix = prefix + "-thread-";
        this.mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        this.mGroup = s == null?Thread.currentThread().getThreadGroup():s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = this.mPrefix + this.mThreadNum.getAndIncrement();
        Thread ret = new Thread(this.mGroup, r, name, 0L);
        ret.setDaemon(mDaemo);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return mGroup;
    }
}
