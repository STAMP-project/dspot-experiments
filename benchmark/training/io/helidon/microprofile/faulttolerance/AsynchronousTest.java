/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.microprofile.faulttolerance;


import java.util.concurrent.Future;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Class AsynchronousTest.
 */
public class AsynchronousTest extends FaultToleranceTest {
    @Test
    public void testAsync() throws Exception {
        AsynchronousBean bean = newBean(AsynchronousBean.class);
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(false));
        Future<String> future = bean.async();
        future.get();
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(true));
    }

    @Test
    public void testAsyncWithFallback() throws Exception {
        AsynchronousBean bean = newBean(AsynchronousBean.class);
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(false));
        Future<String> future = bean.asyncWithFallback();
        String value = future.get();
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(true));
        MatcherAssert.assertThat(value, Matchers.is("fallback"));
    }

    @Test
    public void testAsyncNoGet() throws Exception {
        AsynchronousBean bean = newBean(AsynchronousBean.class);
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(false));
        Future<String> future = bean.async();
        while (!(future.isDone())) {
            Thread.sleep(100);
        } 
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(true));
    }

    @Test
    public void testNotAsync() throws Exception {
        AsynchronousBean bean = newBean(AsynchronousBean.class);
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(false));
        Future<String> future = bean.notAsync();
        MatcherAssert.assertThat(bean.getCalled(), Matchers.is(true));
        future.get();
    }

    @Test
    public void testAsyncError() throws Exception {
        Assertions.assertThrows(FaultToleranceDefinitionException.class, () -> {
            AsynchronousBean bean = newBean(AsynchronousBean.class);
            bean.asyncError();
        });
    }
}

