/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.common.internal.exceptions;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Foundation for tests on {@link ClusterException} subclasses.
 */
public abstract class BaseClusteredEhcacheExceptionTest<T extends ClusterException> {
    private final Class<T> testClass;

    protected BaseClusteredEhcacheExceptionTest(Class<T> testClass) {
        if (testClass == null) {
            throw new NullPointerException("testClass");
        }
        this.testClass = testClass;
    }

    @Test
    public void testHasServerVersionUID() throws Exception {
        testClass.getDeclaredField("serialVersionUID");
    }

    @Test
    public void testType() throws Exception {
        Assert.assertThat(testClass, Matchers.is(BaseClusteredEhcacheExceptionTest.typeCompatibleWith(ClusterException.class)));
    }

    @Test
    public final void ctorMessage() throws Exception {
        T baseException = this.create("message text");
        Assert.assertThat(getMessage(), BaseClusteredEhcacheExceptionTest.is("message text"));
        Assert.assertThat(getCause(), Matchers.is(BaseClusteredEhcacheExceptionTest.nullValue()));
        checkWithClientStack(baseException);
    }

    @Test
    public final void ctorMessageThrowable() throws Exception {
        Throwable baseCause = new Throwable("base cause");
        T baseException = this.create("message text", baseCause);
        Assert.assertThat(getMessage(), BaseClusteredEhcacheExceptionTest.is("message text"));
        Assert.assertThat(getCause(), BaseClusteredEhcacheExceptionTest.is(baseCause));
        checkWithClientStack(baseException);
    }

    @Test
    public final void ctorThrowable() throws Exception {
        Throwable baseCause = new Throwable("base cause");
        T baseException = this.create(baseCause);
        Assert.assertThat(getMessage(), BaseClusteredEhcacheExceptionTest.is(baseCause.toString()));
        Assert.assertThat(getCause(), BaseClusteredEhcacheExceptionTest.is(baseCause));
        checkWithClientStack(baseException);
    }
}

