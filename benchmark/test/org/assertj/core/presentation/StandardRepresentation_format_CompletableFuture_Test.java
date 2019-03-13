/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.presentation;


import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class StandardRepresentation_format_CompletableFuture_Test {
    @Test
    public void should_format_incomplete_future() {
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(new CompletableFuture())).isEqualTo("CompletableFuture[Incomplete]");
    }

    @Test
    public void should_format_complete_future() {
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(CompletableFuture.completedFuture("done"))).isEqualTo("CompletableFuture[Completed: \"done\"]");
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(CompletableFuture.completedFuture(42))).isEqualTo("CompletableFuture[Completed: 42]");
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(CompletableFuture.completedFuture(null))).isEqualTo("CompletableFuture[Completed: null]");
    }

    @Test
    public void should_format_failed_future() {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("some random error"));
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(future)).startsWith(String.format("CompletableFuture[Failed: java.lang.RuntimeException: some random error]%n")).contains("Caused by: java.lang.RuntimeException: some random error");
    }

    @Test
    public void should_format_cancelled_future() {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.cancel(true);
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(future)).isEqualTo("CompletableFuture[Cancelled]");
    }

    @Test
    public void should_not_stack_overflow_when_formatting_future_completed_with_itself() {
        CompletableFuture<CompletableFuture<?>> future = new CompletableFuture<>();
        future.complete(future);
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(future)).isEqualTo((("CompletableFuture[Completed: " + future) + "]"));
    }

    @Test
    public void should_not_stack_overflow_when_formatting_future_with_reference_cycle() {
        CompletableFuture<CompletableFuture<?>> future1 = new CompletableFuture<>();
        CompletableFuture<CompletableFuture<?>> future2 = new CompletableFuture<>();
        future1.complete(future2);
        future2.complete(future1);
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(future1)).isEqualTo((("CompletableFuture[Completed: " + future2) + "]"));
        Assertions.assertThat(StandardRepresentation.STANDARD_REPRESENTATION.toStringOf(future2)).isEqualTo((("CompletableFuture[Completed: " + future1) + "]"));
    }
}

