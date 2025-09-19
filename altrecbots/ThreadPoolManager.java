/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  l2.commons.threading.LoggingRejectedExecutionHandler
 *  l2.commons.threading.PriorityThreadFactory
 *  l2.commons.threading.RunnableImpl
 *  l2.commons.threading.RunnableStatsWrapper
 *  l2.gameserver.Config
 *  l2.gameserver.ThreadPoolManager
 */
package com.lucera2.scripts.altrecbots;

import com.lucera2.scripts.altrecbots.Config;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import l2.commons.threading.LoggingRejectedExecutionHandler;
import l2.commons.threading.PriorityThreadFactory;
import l2.commons.threading.RunnableImpl;
import l2.commons.threading.RunnableStatsWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThreadPoolManager {
    private static final Logger a = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static final long b = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2L;
    private static final ThreadPoolManager c = Config.DEDICATED_EXECUTOR ? com.lucera2.scripts.altrecbots.ThreadPoolManager$a.a(Config.DEDICATED_EXECUTOR_THREADS, Config.DEDICATED_SCHEDULED_THREADS) : new ThreadPoolManager(){};

    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        return c;
    }

    public ScheduledFuture<?> schedule(RunnableImpl runnableImpl, long l) {
        return l2.gameserver.ThreadPoolManager.getInstance().schedule((Runnable)runnableImpl, l);
    }

    public void execute(RunnableImpl runnableImpl) {
        l2.gameserver.ThreadPoolManager.getInstance().execute((Runnable)runnableImpl);
    }

    private static class a
    extends ThreadPoolManager {
        private final ScheduledThreadPoolExecutor a;
        private final ThreadPoolExecutor b;

        private a(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, ThreadPoolExecutor threadPoolExecutor) {
            this.a = scheduledThreadPoolExecutor;
            this.b = threadPoolExecutor;
        }

        private static Runnable a(Runnable runnable) {
            return l2.gameserver.Config.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap((Runnable)runnable) : runnable;
        }

        private static long a(long l) {
            return Math.max(0L, Math.min(b, l));
        }

        private static a a(int n, int n2) {
            final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(n, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), (ThreadFactory)new PriorityThreadFactory("AltRecBotsThreadPoolExecutor", 4), (RejectedExecutionHandler)new LoggingRejectedExecutionHandler());
            final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(n2, (ThreadFactory)new PriorityThreadFactory("AltRecBotsScheduledThreadPool", 4), (RejectedExecutionHandler)new LoggingRejectedExecutionHandler());
            scheduledThreadPoolExecutor.scheduleAtFixedRate((Runnable)new RunnableImpl(){

                public void runImpl() throws Exception {
                    scheduledThreadPoolExecutor.purge();
                    threadPoolExecutor.purge();
                }
            }, 15L, 15L, TimeUnit.MINUTES);
            return new a(scheduledThreadPoolExecutor, threadPoolExecutor);
        }

        @Override
        public ScheduledFuture<?> schedule(RunnableImpl runnableImpl, long l) {
            return this.a.schedule(com.lucera2.scripts.altrecbots.ThreadPoolManager$a.a((Runnable)runnableImpl), com.lucera2.scripts.altrecbots.ThreadPoolManager$a.a(l), TimeUnit.MILLISECONDS);
        }

        @Override
        public void execute(RunnableImpl runnableImpl) {
            this.b.execute(com.lucera2.scripts.altrecbots.ThreadPoolManager$a.a((Runnable)runnableImpl));
        }
    }
}

