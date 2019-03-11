/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.csp.sentinel.context;


import Constants.CONTEXT_DEFAULT_NAME;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link Context} and {@link ContextUtil}.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class ContextTest {
    @Test
    public void testEnterCustomContextWhenExceedsThreshold() {
        fillContext();
        try {
            String contextName = "abc";
            ContextUtil.enter(contextName, "bcd");
            Context curContext = ContextUtil.getContext();
            Assert.assertNotEquals(contextName, curContext.getName());
            Assert.assertTrue((curContext instanceof NullContext));
            Assert.assertEquals("", curContext.getOrigin());
        } finally {
            ContextUtil.exit();
            resetContextMap();
        }
    }

    @Test
    public void testDefaultContextWhenExceedsThreshold() {
        fillContext();
        try {
            ContextUtil.trueEnter(CONTEXT_DEFAULT_NAME, "");
            Context curContext = ContextUtil.getContext();
            Assert.assertEquals(CONTEXT_DEFAULT_NAME, curContext.getName());
            Assert.assertNotNull(curContext.getEntranceNode());
        } finally {
            ContextUtil.exit();
            resetContextMap();
        }
    }

    @Test
    public void testEnterContext() {
        final String contextName = "contextA";
        final String origin = "originA";
        ContextUtil.enter(contextName, origin);
        Context curContext = ContextUtil.getContext();
        Assert.assertEquals(contextName, curContext.getName());
        Assert.assertEquals(origin, curContext.getOrigin());
        Assert.assertFalse(curContext.isAsync());
        ContextUtil.exit();
        Assert.assertNull(ContextUtil.getContext());
    }

    @Test
    public void testReplaceContext() {
        final String contextName = "contextA";
        final String origin = "originA";
        ContextUtil.enter(contextName, origin);
        Context contextB = Context.newAsyncContext(null, "contextB").setOrigin("originA");
        Context contextA = ContextUtil.replaceContext(contextB);
        Assert.assertEquals(contextName, contextA.getName());
        Assert.assertEquals(origin, contextA.getOrigin());
        Assert.assertFalse(contextA.isAsync());
        Context curContextAfterReplace = ContextUtil.getContext();
        Assert.assertEquals(contextB.getName(), curContextAfterReplace.getName());
        Assert.assertEquals(contextB.getOrigin(), curContextAfterReplace.getOrigin());
        Assert.assertTrue(curContextAfterReplace.isAsync());
        ContextUtil.replaceContext(null);
        Assert.assertNull(ContextUtil.getContext());
    }

    @Test
    public void testRunOnContext() {
        final String contextName = "contextA";
        final String origin = "originA";
        ContextUtil.enter(contextName, origin);
        final Context contextB = Context.newAsyncContext(null, "contextB").setOrigin("originB");
        Assert.assertEquals(contextName, ContextUtil.getContext().getName());
        ContextUtil.runOnContext(contextB, new Runnable() {
            @Override
            public void run() {
                Context curContext = ContextUtil.getContext();
                Assert.assertEquals(contextB.getName(), curContext.getName());
                Assert.assertEquals(contextB.getOrigin(), curContext.getOrigin());
                Assert.assertTrue(curContext.isAsync());
            }
        });
        Assert.assertEquals(contextName, ContextUtil.getContext().getName());
    }
}

