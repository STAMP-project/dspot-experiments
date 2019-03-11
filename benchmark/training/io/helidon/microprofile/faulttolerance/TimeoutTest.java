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


import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Class TimeoutTest.
 */
public class TimeoutTest extends FaultToleranceTest {
    @Test
    public void testForceTimeout() throws Exception {
        TimeoutBean bean = newBean(TimeoutBean.class);
        Assertions.assertThrows(TimeoutException.class, () -> {
            bean.forceTimeout();
        });
    }

    @Test
    public void testNoTimeout() throws Exception {
        TimeoutBean bean = newBean(TimeoutBean.class);
        MatcherAssert.assertThat(bean.noTimeout(), Matchers.is("success"));
    }

    @Test
    public void testTimeoutWithRetries() throws Exception {
        TimeoutBean bean = newBean(TimeoutBean.class);
        MatcherAssert.assertThat(bean.timeoutWithRetries(), Matchers.is("success"));
    }

    @Test
    public void testTimeoutWithFallback() throws Exception {
        TimeoutBean bean = newBean(TimeoutBean.class);
        MatcherAssert.assertThat(bean.timeoutWithFallback(), Matchers.is("fallback"));
    }

    @Test
    public void testTimeoutWithRetriesAndFallback() throws Exception {
        TimeoutBean bean = newBean(TimeoutBean.class);
        MatcherAssert.assertThat(bean.timeoutWithRetriesAndFallback(), Matchers.is("fallback"));
    }
}

