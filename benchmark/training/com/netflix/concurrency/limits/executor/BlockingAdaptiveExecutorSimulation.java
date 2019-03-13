package com.netflix.concurrency.limits.executor;


import com.netflix.concurrency.limits.Limiter;
import com.netflix.concurrency.limits.limit.AIMDLimit;
import com.netflix.concurrency.limits.limit.GradientLimit;
import com.netflix.concurrency.limits.limit.TracingLimitDecorator;
import com.netflix.concurrency.limits.limit.VegasLimit;
import com.netflix.concurrency.limits.limiter.SimpleLimiter;
import java.util.concurrent.Executor;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("These are simulations and not tests")
public class BlockingAdaptiveExecutorSimulation {
    @Test
    public void test() {
        Limiter<Void> limiter = SimpleLimiter.newBuilder().limit(AIMDLimit.newBuilder().initialLimit(10).build()).build();
        Executor executor = new com.netflix.concurrency.limits.executors.BlockingAdaptiveExecutor(limiter);
        run(10000, 20, executor, randomLatency(50, 150));
    }

    @Test
    public void testVegas() {
        Limiter<Void> limiter = SimpleLimiter.newBuilder().limit(TracingLimitDecorator.wrap(VegasLimit.newBuilder().initialLimit(100).build())).build();
        Executor executor = new com.netflix.concurrency.limits.executors.BlockingAdaptiveExecutor(limiter);
        run(10000, 50, executor, randomLatency(50, 150));
    }

    @Test
    public void testGradient() {
        Limiter<Void> limiter = SimpleLimiter.newBuilder().limit(TracingLimitDecorator.wrap(GradientLimit.newBuilder().initialLimit(100).build())).build();
        Executor executor = new com.netflix.concurrency.limits.executors.BlockingAdaptiveExecutor(limiter);
        run(100000, 50, executor, randomLatency(50, 150));
    }
}

