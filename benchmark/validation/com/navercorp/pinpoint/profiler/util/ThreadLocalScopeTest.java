/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.util;


import ExecutionPolicy.BOUNDARY;
import com.navercorp.pinpoint.bootstrap.instrument.DefaultInterceptorScopeDefinition;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author emeroad
 */
public class ThreadLocalScopeTest {
    @Test
    public void pushPop() {
        InterceptorScopeInvocation scope = new ThreadLocalScope(new DefaultInterceptorScopeDefinition("test"));
        Assert.assertTrue(scope.tryEnter(BOUNDARY));
        Assert.assertFalse(scope.tryEnter(BOUNDARY));
        Assert.assertFalse(scope.tryEnter(BOUNDARY));
        Assert.assertTrue(scope.isActive());
        Assert.assertFalse(scope.canLeave(BOUNDARY));
        Assert.assertFalse(scope.canLeave(BOUNDARY));
        Assert.assertTrue(scope.canLeave(BOUNDARY));
        scope.leave(BOUNDARY);
    }

    @Test(expected = IllegalStateException.class)
    public void pushPopError() {
        InterceptorScopeInvocation scope = new ThreadLocalScope(new DefaultInterceptorScopeDefinition("test"));
        scope.leave(BOUNDARY);
    }

    @Test
    public void getName() {
        InterceptorScopeInvocation scope = new ThreadLocalScope(new DefaultInterceptorScopeDefinition("test"));
        Assert.assertEquals(scope.getName(), "test");
    }
}

