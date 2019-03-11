package org.jooq.lambda;


import java.util.concurrent.CompletionStage;
import org.junit.Assert;
import org.junit.Test;


public class AsyncTest {
    @Test
    public void testNoCustomExecutor() {
        CompletionStage<Void> completionStage = Async.runAsync(() -> {
        });
        Assert.assertNull(completionStage.toCompletableFuture().join());
        completionStage = Async.supplyAsync(() -> null);
        Assert.assertNull(completionStage.toCompletableFuture().join());
    }
}

