package org.jooq.lambda;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.Test;


public class SameExecutorCompletionStageTest {
    public SameExecutorCompletionStageTest() {
    }

    private static final CallCountingExecutor executorA = CallCountingExecutor.wrap(Executors.newFixedThreadPool(10, ThreadFactories.daemon()));

    private static final CallCountingExecutor executorB = CallCountingExecutor.wrap(Executors.newFixedThreadPool(10, ThreadFactories.daemon()));

    private CountDownLatch latch1;

    private CountDownLatch latch2;

    private CountDownLatch latch3;

    @Test
    public void testThenApply() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenApply(Function.identity());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenApplyAsync(latchIdentity(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenApplyAsync(latchIdentity(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testThenAccept() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenAccept(noopConsumer());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenAcceptAsync(latchConsumer(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenAcceptAsync(latchConsumer(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testThenRun() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenRun(noopRunnable());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenRunAsync(latchRunnable(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenRunAsync(latchRunnable(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testThenCombine() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiFunction<Void, Void, Void> bf = (Void x,Void y) -> null;
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenCombine(completedFuture(), bf);
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiFunction<Void, Void, Void> bf2 = Unchecked.biFunction((Void x,Void y) -> {
            latch2.await();
            return null;
        });
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenCombineAsync(completedFuture(), bf2);
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        BiFunction<Void, Void, Void> bf3 = Unchecked.biFunction((Void x,Void y) -> {
            latch3.await();
            return null;
        });
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenCombineAsync(completedFuture(), bf3, SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testThenAcceptBoth() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiConsumer<Void, Void> bc = (Void x,Void y) -> {
        };
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenAcceptBoth(completedFuture(), bc);
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiConsumer<Void, Void> bf2 = Unchecked.biConsumer((Void x,Void y) -> {
            latch2.await();
        });
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenAcceptBothAsync(completedFuture(), bf2);
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        BiConsumer<Void, Void> bf3 = Unchecked.biConsumer((Void x,Void y) -> {
            latch3.await();
        });
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenAcceptBothAsync(completedFuture(), bf3, SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testRunAfterBoth() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.runAfterBoth(completedFuture(), noopRunnable());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.runAfterBothAsync(completedFuture(), latchRunnable(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.runAfterBothAsync(completedFuture(), latchRunnable(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testApplyToEither() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.applyToEither(neverCompleteFuture(), Function.identity());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.applyToEitherAsync(neverCompleteFuture(), latchIdentity(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.applyToEitherAsync(neverCompleteFuture(), latchIdentity(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testAcceptEither() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.acceptEither(neverCompleteFuture(), noopConsumer());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.acceptEitherAsync(neverCompleteFuture(), latchConsumer(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.acceptEitherAsync(neverCompleteFuture(), latchConsumer(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testRunAfterEither() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.runAfterEither(neverCompleteFuture(), noopRunnable());
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.runAfterEitherAsync(neverCompleteFuture(), latchRunnable(latch2));
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.runAfterEitherAsync(neverCompleteFuture(), latchRunnable(latch3), SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testThenCompose() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        Function<Void, CompletableFuture<Void>> fn = ( x) -> CompletableFuture.completedFuture(x);
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.thenCompose(fn);
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        Function<Void, CompletableFuture<Void>> fn2 = Unchecked.function(( x) -> {
            latch2.await();
            return CompletableFuture.completedFuture(x);
        });
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.thenComposeAsync(fn2);
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        Function<Void, CompletableFuture<Void>> fn3 = Unchecked.function(( x) -> {
            latch3.await();
            return CompletableFuture.completedFuture(x);
        });
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.thenComposeAsync(fn3, SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testExceptionally() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        firstCompletionStage = firstCompletionStage.exceptionally(( ex) -> null);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
    }

    @Test
    public void testWhenComplete() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiConsumer<Void, Throwable> bc = (Void x,Throwable y) -> {
        };
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.whenComplete(bc);
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiConsumer<Void, Throwable> bc2 = Unchecked.biConsumer((Void x,Throwable y) -> {
            latch2.await();
        });
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.whenCompleteAsync(bc2);
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        BiConsumer<Void, Throwable> bc3 = Unchecked.biConsumer((Void x,Throwable y) -> {
            latch3.await();
        });
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.whenCompleteAsync(bc3, SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }

    @Test
    public void testHandle() throws InterruptedException {
        CompletionStage<Void> firstCompletionStage = Async.supplyAsync(latchSupplier(latch1), SameExecutorCompletionStageTest.executorA);
        latch1.countDown();
        firstCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiFunction<Void, Throwable, Void> bf = (Void x,Throwable y) -> null;
        CompletionStage<Void> secondCompletionStage = firstCompletionStage.handle(bf);
        secondCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 1);
        BiFunction<Void, Throwable, Void> bf2 = Unchecked.biFunction((Void x,Throwable y) -> {
            latch2.await();
            return null;
        });
        CompletionStage<Void> thirdCompletionStage = secondCompletionStage.handleAsync(bf2);
        latch2.countDown();
        thirdCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        BiFunction<Void, Throwable, Void> bf3 = Unchecked.biFunction((Void x,Throwable y) -> {
            latch3.await();
            return null;
        });
        CompletionStage<Void> fourthCompletionStage = thirdCompletionStage.handleAsync(bf3, SameExecutorCompletionStageTest.executorB);
        latch3.countDown();
        fourthCompletionStage.toCompletableFuture().join();
        assertHits(SameExecutorCompletionStageTest.executorA, 2);
        assertHits(SameExecutorCompletionStageTest.executorB, 1);
    }
}

