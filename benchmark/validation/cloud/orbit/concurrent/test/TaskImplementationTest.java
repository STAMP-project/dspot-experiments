/**
 * Copyright (C) 2016 Electronic Arts Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
 * its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cloud.orbit.concurrent.test;


import cloud.orbit.concurrent.Task;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class TaskImplementationTest {
    @SuppressWarnings("deprecation")
    private static class CTask<T> extends Task<T> {
        @Override
        public boolean complete(T value) {
            return super.internalComplete(value);
        }

        @Override
        public boolean completeExceptionally(Throwable ex) {
            return super.internalCompleteExceptionally(ex);
        }
    }

    @Test
    public void testAllOf() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask<Integer> t2 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask<Integer> t3 = new TaskImplementationTest.CTask<>();
        Task<?> all = Task.allOf(t1, t2, t3);
        Assert.assertFalse(all.isDone());
        t1.complete(1);
        Assert.assertFalse(all.isDone());
        t2.complete(2);
        t3.complete(3);
        Assert.assertTrue(all.isDone());
    }

    @Test
    public void testAllOfWithError() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask<Integer> t2 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask<Integer> t3 = new TaskImplementationTest.CTask<>();
        Task<?> all = Task.allOf(t1, t2, t3);
        Assert.assertFalse(all.isDone());
        t1.complete(1);
        Assert.assertFalse(all.isDone());
        // simulating an error
        t2.completeExceptionally(new RuntimeException());
        Assert.assertFalse(all.isDone());
        t3.complete(3);
        // ensuring that allOf only completes after all subs are completed, even if there were errors.
        Assert.assertTrue(all.isDone());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testAllOfVariations() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask();
        TaskImplementationTest.CTask t2 = new TaskImplementationTest.CTask();
        TaskImplementationTest.CTask t3 = new TaskImplementationTest.CTask();
        CompletableFuture c4 = new CompletableFuture();
        Task all_regular = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, t1, t2, t3);
        Task all_array = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, new CompletableFuture[]{ t1, t2, t3 });
        Task all_array2 = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, new TaskImplementationTest.CTask[]{ t1, t2, t3 });
        Task all_collection = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, Arrays.asList(t1, t2, t3));
        Task all_stream = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, Arrays.asList(t1, t2, t3).stream());
        Stream<TaskImplementationTest.CTask> stream = Arrays.asList(t1, t2, t3).stream();
        Task all_stream2 = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, stream);
        Task all_stream3 = TaskImplementationTest.CTask.allOf(TaskImplementationTest.CTask, Arrays.asList(c4).stream());
        Stream<CompletableFuture> stream4 = Arrays.asList(t1, t2, t3, c4).stream();
        Task all_stream4 = Task.allOf(stream4);
        Task all_stream5 = Task.allOf(Arrays.asList(t1, t2, t3, c4).stream());
        t1.complete(1);
        t2.completeExceptionally(new RuntimeException());
        t3.complete(3);
        c4.complete(4);
        Assert.assertTrue(all_regular.isDone());
        Assert.assertTrue(all_array.isDone());
        Assert.assertTrue(all_array2.isDone());
        Assert.assertTrue(all_stream.isDone());
        Assert.assertTrue(all_stream2.isDone());
        Assert.assertTrue(all_stream3.isDone());
        Assert.assertTrue(all_stream4.isDone());
        Assert.assertTrue(all_stream5.isDone());
        Assert.assertTrue(all_collection.isDone());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testAnyOfVariations() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask t2 = new TaskImplementationTest.CTask<>();
        TaskImplementationTest.CTask<?> t3 = new TaskImplementationTest.CTask<>();
        CompletableFuture c4 = new CompletableFuture();
        Task group_regular = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, t1, t2, t3);
        Task group_array = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, new CompletableFuture[]{ t1, t2, t3 });
        Task group_array2 = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, new TaskImplementationTest.CTask[]{ t1, t2, t3 });
        Task group_collection = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, Arrays.asList(t1, t2, t3));
        Task group_stream = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, Arrays.asList(t1, t2, t3).stream());
        Stream<TaskImplementationTest.CTask> stream = Arrays.asList(t1, t2, t3).stream();
        Task group_stream2 = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, stream);
        Task group_stream3 = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, Arrays.asList(c4).stream());
        Stream<CompletableFuture> stream4 = Arrays.asList(t1, t2, t3, c4).stream();
        Task group_stream4 = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, stream4);
        Task group_stream5 = TaskImplementationTest.CTask.anyOf(TaskImplementationTest.CTask, Arrays.asList(t1, t2, t3, c4).stream());
        t1.complete(1);
        c4.complete(4);
        Assert.assertTrue(group_regular.isDone());
        Assert.assertTrue(group_array.isDone());
        Assert.assertTrue(group_array2.isDone());
        Assert.assertTrue(group_stream.isDone());
        Assert.assertTrue(group_stream2.isDone());
        Assert.assertTrue(group_stream3.isDone());
        Assert.assertTrue(group_stream4.isDone());
        Assert.assertTrue(group_stream5.isDone());
        Assert.assertTrue(group_collection.isDone());
    }

    @Test
    public void testThenApply() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenApply(( x) -> "a");
        Assert.assertFalse(t1.isDone());
        t1.complete(1);
        Assert.assertTrue(t2.isDone());
        Assert.assertEquals("a", t2.join());
    }

    @Test
    public void testThenApplyWithVoid() {
        TaskImplementationTest.CTask<Void> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenApply(( x) -> "a");
        Assert.assertFalse(t1.isDone());
        t1.complete(null);
        Assert.assertTrue(t2.isDone());
        Assert.assertEquals("a", t2.join());
    }

    @Test
    public void testThenReturn() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenReturn(() -> "a");
        Assert.assertFalse(t1.isDone());
        t1.complete(1);
        Assert.assertTrue(t2.isDone());
        Assert.assertEquals("a", t2.join());
    }

    @Test
    public void testThenCompose() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenCompose(( x) -> cloud.orbit.concurrent.test.CTask.fromValue((x + "a")));
        Task<String> t3 = t1.thenCompose(( x) -> CompletableFuture.completedFuture((x + "a")));
        Assert.assertFalse(t1.isDone());
        t1.complete(1);
        Assert.assertTrue(t2.isDone());
        Assert.assertEquals("1a", t2.join());
        Assert.assertEquals("1a", t3.join());
    }

    @Test
    public void testThenComposeNoParams() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenCompose(() -> Task.fromValue("b"));
        Task<String> t3 = t1.thenCompose(() -> CompletableFuture.completedFuture("c"));
        Assert.assertFalse(t1.isDone());
        t1.complete(1);
        Assert.assertTrue(t2.isDone());
        Assert.assertEquals("b", t2.join());
        Assert.assertEquals("c", t3.join());
    }

    @Test
    public void testThenReturnWithException() {
        TaskImplementationTest.CTask<Integer> t1 = new TaskImplementationTest.CTask<>();
        Task<String> t2 = t1.thenReturn(() -> "a");
        Assert.assertFalse(t1.isDone());
        t1.completeExceptionally(new RuntimeException());
        Assert.assertTrue(t2.isDone());
        Assert.assertTrue(t2.isCompletedExceptionally());
    }

    @Test
    public void testGetAndJoinWithWrappedForkJoinTask() throws InterruptedException, ExecutionException {
        final ForkJoinTask<String> task = ForkJoinTask.adapt(() -> "bla");
        final Task<String> t1 = Task.fromFuture(task);
        Assert.assertFalse(t1.isDone());
        task.invoke();
        Assert.assertEquals("bla", t1.join());
        Assert.assertTrue(t1.isDone());
        Assert.assertEquals("bla", t1.join());
        Assert.assertEquals("bla", t1.get());
    }

    @Test
    public void testGetAndJoinWithWrappedForkJoinTaskAndTimeout() throws InterruptedException, ExecutionException {
        final ForkJoinTask<String> task = ForkJoinTask.adapt(() -> "bla");
        final Task<String> t1 = Task.fromFuture(task);
        Assert.assertFalse(t1.isDone());
        Thread.sleep(10);
        task.fork();
        Assert.assertEquals("bla", t1.join());
        Assert.assertTrue(t1.isDone());
        Assert.assertEquals("bla", t1.join());
        Assert.assertEquals("bla", t1.get());
    }

    @Test
    public void testSleep() {
        long start = System.currentTimeMillis();
        final Task<Void> sleep = Task.sleep(20, TimeUnit.MILLISECONDS);
        Assert.assertFalse(sleep.isDone());
        final Task<Long> completionTime = sleep.thenApply(( x) -> System.currentTimeMillis());
        Assert.assertTrue((((completionTime.join()) - start) >= 20));
        Assert.assertTrue(sleep.isDone());
    }
}

