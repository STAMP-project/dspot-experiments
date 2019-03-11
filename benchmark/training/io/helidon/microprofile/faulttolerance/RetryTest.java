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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Class RetryTest.
 */
public class RetryTest extends FaultToleranceTest {
    @Test
    public void testRetryBean() {
        RetryBean bean = newBean(RetryBean.class);
        MatcherAssert.assertThat(bean.getInvocations(), Matchers.is(0));
        bean.retry();
        MatcherAssert.assertThat(bean.getInvocations(), Matchers.is(3));
    }

    @Test
    public void testRetryBeanFallback() {
        RetryBean bean = newBean(RetryBean.class);
        MatcherAssert.assertThat(bean.getInvocations(), Matchers.is(0));
        String value = bean.retryWithFallback();
        MatcherAssert.assertThat(bean.getInvocations(), Matchers.is(2));
        MatcherAssert.assertThat(value, Matchers.is("fallback"));
    }

    @Test
    public void testRetryAsync() throws Exception {
        RetryBean bean = newBean(RetryBean.class);
        Future<String> future = bean.retryAsync();
        future.get();
        MatcherAssert.assertThat(bean.getInvocations(), Matchers.is(3));
    }

    @Test
    public void testRetryWithDelayAndJitter() throws Exception {
        RetryBean bean = newBean(RetryBean.class);
        long millis = System.currentTimeMillis();
        String value = bean.retryWithDelayAndJitter();
        MatcherAssert.assertThat(((System.currentTimeMillis()) - millis), Matchers.greaterThan(200L));
    }
}

