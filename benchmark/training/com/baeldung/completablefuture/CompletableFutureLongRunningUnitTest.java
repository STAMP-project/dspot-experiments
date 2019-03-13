package com.baeldung.completablefuture;


import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompletableFutureLongRunningUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(CompletableFutureLongRunningUnitTest.class);

    @Test
    public void whenRunningCompletableFutureAsynchronously_thenGetMethodWaitsForResult() throws InterruptedException, ExecutionException {
        Future<String> completableFuture = calculateAsync();
        String result = completableFuture.get();
        Assert.assertEquals("Hello", result);
    }

    @Test
    public void whenRunningCompletableFutureWithResult_thenGetMethodReturnsImmediately() throws InterruptedException, ExecutionException {
        Future<String> completableFuture = CompletableFuture.completedFuture("Hello");
        String result = completableFuture.get();
        Assert.assertEquals("Hello", result);
    }

    @Test(expected = CancellationException.class)
    public void whenCancelingTheFuture_thenThrowsCancellationException() throws InterruptedException, ExecutionException {
        Future<String> future = calculateAsyncWithCancellation();
        future.get();
    }

    @Test
    public void whenCreatingCompletableFutureWithSupplyAsync_thenFutureReturnsValue() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
        Assert.assertEquals("Hello", future.get());
    }

    @Test
    public void whenAddingThenAcceptToFuture_thenFunctionExecutesAfterComputationIsFinished() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenAccept(( s) -> CompletableFutureLongRunningUnitTest.LOG.debug(("Computation returned: " + s)));
        future.get();
    }

    @Test
    public void whenAddingThenRunToFuture_thenFunctionExecutesAfterComputationIsFinished() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<Void> future = completableFuture.thenRun(() -> CompletableFutureLongRunningUnitTest.LOG.debug("Computation finished."));
        future.get();
    }

    @Test
    public void whenAddingThenApplyToFuture_thenFunctionExecutesAfterComputationIsFinished() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future = completableFuture.thenApply(( s) -> s + " World");
        Assert.assertEquals("Hello World", future.get());
    }

    @Test
    public void whenUsingThenCompose_thenFuturesExecuteSequentially() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello").thenCompose(( s) -> CompletableFuture.supplyAsync(() -> s + " World"));
        Assert.assertEquals("Hello World", completableFuture.get());
    }

    @Test
    public void whenUsingThenCombine_thenWaitForExecutionOfBothFutures() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello").thenCombine(CompletableFuture.supplyAsync(() -> " World"), ( s1, s2) -> s1 + s2);
        Assert.assertEquals("Hello World", completableFuture.get());
    }

    @Test
    public void whenUsingThenAcceptBoth_thenWaitForExecutionOfBothFutures() throws InterruptedException, ExecutionException {
        CompletableFuture.supplyAsync(() -> "Hello").thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"), ( s1, s2) -> CompletableFutureLongRunningUnitTest.LOG.debug((s1 + s2)));
    }

    @Test
    public void whenFutureCombinedWithAllOfCompletes_thenAllFuturesAreDone() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
        // ...
        combinedFuture.get();
        Assert.assertTrue(future1.isDone());
        Assert.assertTrue(future2.isDone());
        Assert.assertTrue(future3.isDone());
        String combined = Stream.of(future1, future2, future3).map(CompletableFuture::join).collect(Collectors.joining(" "));
        Assert.assertEquals("Hello Beautiful World", combined);
    }

    @Test
    public void whenFutureThrows_thenHandleMethodReceivesException() throws InterruptedException, ExecutionException {
        String name = null;
        // ...
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return "Hello, " + name;
        }).handle(( s, t) -> s != null ? s : "Hello, Stranger!");
        Assert.assertEquals("Hello, Stranger!", completableFuture.get());
    }

    @Test(expected = ExecutionException.class)
    public void whenCompletingFutureExceptionally_thenGetMethodThrows() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        // ...
        completableFuture.completeExceptionally(new RuntimeException("Calculation failed!"));
        // ...
        completableFuture.get();
    }

    @Test
    public void whenAddingThenApplyAsyncToFuture_thenFunctionExecutesAfterComputationIsFinished() throws InterruptedException, ExecutionException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future = completableFuture.thenApplyAsync(( s) -> s + " World");
        Assert.assertEquals("Hello World", future.get());
    }

    @Test
    public void whenPassingTransformation_thenFunctionExecutionWithThenApply() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> finalResult = compute().thenApply(( s) -> s + 1);
        Assert.assertTrue(((finalResult.get()) == 11));
    }

    @Test
    public void whenPassingPreviousStage_thenFunctionExecutionWithThenCompose() throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
        Assert.assertTrue(((finalResult.get()) == 20));
    }
}

