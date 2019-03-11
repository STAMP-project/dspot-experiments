/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.auth;


import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.common.EventLoopRule;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import javax.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AuthorizerTest {
    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    @Nullable
    private static ServiceRequestContext serviceCtx;

    @Test
    public void orElseFirst() {
        final Authorizer<String> a = AuthorizerTest.newMock();
        Mockito.when(a.authorize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(true));
        final Authorizer<String> b = AuthorizerTest.newMock();
        final Boolean result = a.orElse(b).authorize(AuthorizerTest.serviceCtx, "data").toCompletableFuture().join();
        assertThat(result).isTrue();
        Mockito.verify(a, Mockito.times(1)).authorize(AuthorizerTest.serviceCtx, "data");
        Mockito.verify(b, Mockito.never()).authorize(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    /**
     * When the first {@link Authorizer} raises an exception, the second {@link Authorizer} shouldn't be
     * invoked.
     */
    @Test
    public void orElseFirstException() {
        final Exception expected = new Exception();
        final Authorizer<String> a = AuthorizerTest.newMock();
        Mockito.when(a.authorize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(exceptionallyCompletedFuture(expected));
        final Authorizer<String> b = AuthorizerTest.newMock();
        assertThatThrownBy(() -> a.orElse(b).authorize(AuthorizerTest.serviceCtx, "data").toCompletableFuture().join()).isInstanceOf(CompletionException.class).hasCause(expected);
        Mockito.verify(a, Mockito.times(1)).authorize(AuthorizerTest.serviceCtx, "data");
        Mockito.verifyZeroInteractions(b);
    }

    /**
     * When the first {@link Authorizer} returns a {@link CompletionStage} that fulfills with {@code null},
     * the second {@link Authorizer} shouldn't be invoked.
     */
    @Test
    public void orElseFirstNull() {
        final Authorizer<String> a = AuthorizerTest.newMock();
        Mockito.when(a.authorize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(null));
        final Authorizer<String> b = AuthorizerTest.newMock();
        assertThatThrownBy(() -> a.orElse(b).authorize(AuthorizerTest.serviceCtx, "data").toCompletableFuture().join()).isInstanceOf(CompletionException.class).hasCauseInstanceOf(NullPointerException.class);
        Mockito.verify(a, Mockito.times(1)).authorize(AuthorizerTest.serviceCtx, "data");
        Mockito.verifyZeroInteractions(b);
    }

    @Test
    public void orElseSecond() {
        final Authorizer<String> a = AuthorizerTest.newMock();
        Mockito.when(a.authorize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(false));
        final Authorizer<String> b = AuthorizerTest.newMock();
        Mockito.when(b.authorize(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(true));
        final Boolean result = a.orElse(b).authorize(AuthorizerTest.serviceCtx, "data").toCompletableFuture().join();
        assertThat(result).isTrue();
        Mockito.verify(a, Mockito.times(1)).authorize(AuthorizerTest.serviceCtx, "data");
        Mockito.verify(b, Mockito.times(1)).authorize(AuthorizerTest.serviceCtx, "data");
    }

    @Test
    public void orElseToString() {
        final Authorizer<Object> a = new AuthorizerTest.AuthorizerWithToString("A");
        final Authorizer<Object> b = new AuthorizerTest.AuthorizerWithToString("B");
        final Authorizer<Object> c = new AuthorizerTest.AuthorizerWithToString("C");
        final Authorizer<Object> d = new AuthorizerTest.AuthorizerWithToString("D");
        // A + B
        assertThat(a.orElse(b).toString()).isEqualTo("[A, B]");
        // A + B
        assertThat(a.orElse(b).orElse(c).toString()).isEqualTo("[A, B, C]");
        // (A + B) + (C + D)
        assertThat(a.orElse(b).orElse(c.orElse(d)).toString()).isEqualTo("[A, B, C, D]");
    }

    private static final class AuthorizerWithToString implements Authorizer<Object> {
        private final String strVal;

        AuthorizerWithToString(String strVal) {
            this.strVal = strVal;
        }

        @Override
        public CompletionStage<Boolean> authorize(ServiceRequestContext ctx, Object data) {
            return CompletableFuture.completedFuture(true);
        }

        @Override
        public String toString() {
            return strVal;
        }
    }
}

