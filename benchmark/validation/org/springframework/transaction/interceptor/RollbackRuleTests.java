/**
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.transaction.interceptor;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;


/**
 * Unit tests for the {@link RollbackRuleAttribute} class.
 *
 * @author Rod Johnson
 * @author Rick Evans
 * @author Chris Beams
 * @author Sam Brannen
 * @since 09.04.2003
 */
public class RollbackRuleTests {
    @Test
    public void foundImmediatelyWithString() {
        RollbackRuleAttribute rr = new RollbackRuleAttribute(Exception.class.getName());
        Assert.assertEquals(0, rr.getDepth(new Exception()));
    }

    @Test
    public void foundImmediatelyWithClass() {
        RollbackRuleAttribute rr = new RollbackRuleAttribute(Exception.class);
        Assert.assertEquals(0, rr.getDepth(new Exception()));
    }

    @Test
    public void notFound() {
        RollbackRuleAttribute rr = new RollbackRuleAttribute(IOException.class.getName());
        Assert.assertEquals((-1), rr.getDepth(new MyRuntimeException("")));
    }

    @Test
    public void ancestry() {
        RollbackRuleAttribute rr = new RollbackRuleAttribute(Exception.class.getName());
        // Exception -> Runtime -> NestedRuntime -> MyRuntimeException
        Assert.assertThat(rr.getDepth(new MyRuntimeException("")), CoreMatchers.equalTo(3));
    }

    @Test
    public void alwaysTrueForThrowable() {
        RollbackRuleAttribute rr = new RollbackRuleAttribute(Throwable.class.getName());
        Assert.assertTrue(((rr.getDepth(new MyRuntimeException(""))) > 0));
        Assert.assertTrue(((rr.getDepth(new IOException())) > 0));
        Assert.assertTrue(((rr.getDepth(new FatalBeanException(null, null))) > 0));
        Assert.assertTrue(((rr.getDepth(new RuntimeException())) > 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorArgMustBeAThrowableClassWithNonThrowableType() {
        new RollbackRuleAttribute(StringBuffer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorArgMustBeAThrowableClassWithNullThrowableType() {
        new RollbackRuleAttribute(((Class<?>) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorArgExceptionStringNameVersionWithNull() {
        new RollbackRuleAttribute(((String) (null)));
    }
}

