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
package org.assertj.core.api.future;


import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.BaseTest;
import org.assertj.core.error.future.ShouldHaveFailed;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class CompletableFutureAssert_hasFailedWithThrowableThat_Test extends BaseTest {
    @Test
    public void should_pass_if_completable_future_has_failed() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("some random error"));
        Assertions.assertThat(future).hasFailedWithThrowableThat().isInstanceOf(RuntimeException.class).hasMessage("some random error");
    }

    @Test
    public void should_fail_when_completable_future_is_null() {
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(((CompletableFuture<String>) (null))).hasFailedWithThrowableThat()).isInstanceOf(AssertionError.class).hasMessage(String.format(FailureMessages.actualIsNull()));
    }

    @Test
    public void should_fail_if_completable_future_has_failed_with_wrong_throwable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("some random error"));
        // @format:off
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(future).hasFailedWithThrowableThat().isInstanceOf(.class)).isInstanceOf(AssertionError.class).hasMessageContaining(String.format(("%nExpecting:%n" + (("  <java.lang.RuntimeException: some random error>%n" + "to be an instance of:%n") + "  <java.lang.IllegalArgumentException>%n"))));
        // @format:on
    }

    @Test
    public void should_fail_if_completable_future_is_incomplete() {
        CompletableFuture<String> future = new CompletableFuture<>();
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(future).hasFailedWithThrowableThat()).isInstanceOf(AssertionError.class).hasMessage(ShouldHaveFailed.shouldHaveFailed(future).create());
    }

    @Test
    public void should_fail_if_completable_future_is_completed() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("done");
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(future).hasFailedWithThrowableThat()).isInstanceOf(AssertionError.class).hasMessage(ShouldHaveFailed.shouldHaveFailed(future).create());
    }

    @Test
    public void should_fail_if_completable_future_was_cancelled() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.cancel(true);
        AssertionsForClassTypes.assertThatThrownBy(() -> assertThat(future).hasFailedWithThrowableThat()).isInstanceOf(AssertionError.class).hasMessage(ShouldHaveFailed.shouldHaveFailed(future).create());
    }
}

