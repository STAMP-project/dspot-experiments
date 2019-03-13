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


import ExecutionPolicy.ALWAYS;
import ExecutionPolicy.BOUNDARY;
import ExecutionPolicy.INTERNAL;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import com.navercorp.pinpoint.profiler.interceptor.scope.DefaultInterceptorScopeInvocation;
import org.junit.Assert;
import org.junit.Test;


public class DefaultScopeTest {
    @Test
    public void test0() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        Assert.assertFalse(transaction.isActive());
        Assert.assertFalse(transaction.tryEnter(INTERNAL));
        Assert.assertFalse(transaction.isActive());
        Assert.assertTrue(transaction.tryEnter(BOUNDARY));
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.tryEnter(INTERNAL));
        Assert.assertTrue(transaction.isActive());
        Assert.assertFalse(transaction.tryEnter(BOUNDARY));
        Assert.assertTrue(transaction.isActive());
        Assert.assertFalse(transaction.canLeave(BOUNDARY));
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.canLeave(INTERNAL));
        transaction.leave(INTERNAL);
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.canLeave(BOUNDARY));
        Assert.assertTrue(transaction.isActive());
        transaction.leave(BOUNDARY);
        Assert.assertFalse(transaction.isActive());
        Assert.assertFalse(transaction.canLeave(INTERNAL));
        Assert.assertFalse(transaction.isActive());
    }

    @Test
    public void test1() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        Assert.assertFalse(transaction.isActive());
        Assert.assertTrue(transaction.tryEnter(ALWAYS));
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.tryEnter(ALWAYS));
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.canLeave(ALWAYS));
        Assert.assertTrue(transaction.isActive());
        transaction.leave(ALWAYS);
        Assert.assertTrue(transaction.isActive());
        Assert.assertTrue(transaction.canLeave(ALWAYS));
        Assert.assertTrue(transaction.isActive());
        transaction.leave(ALWAYS);
        Assert.assertFalse(transaction.isActive());
    }

    @Test
    public void testAttachment() {
        String attachment = "context";
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        Assert.assertNull(transaction.getAttachment());
        transaction.setAttachment(attachment);
        Assert.assertSame(transaction.getAttachment(), attachment);
        transaction.tryEnter(ALWAYS);
        Assert.assertSame(transaction.getAttachment(), attachment);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
        Assert.assertSame(transaction.getAttachment(), attachment);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
    }

    @Test
    public void testAttachment2() {
        String attachment = "context";
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        Assert.assertNull(transaction.getAttachment());
        transaction.setAttachment(attachment);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
        transaction.tryEnter(ALWAYS);
        Assert.assertNull(transaction.getAttachment());
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
    }

    @Test
    public void testAttachment3() {
        String oldAttachment = "context";
        String newAttachment = "newnew";
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        transaction.setAttachment(oldAttachment);
        Assert.assertSame(oldAttachment, transaction.getAttachment());
        Assert.assertSame(oldAttachment, transaction.setAttachment(newAttachment));
        Assert.assertSame(newAttachment, transaction.getAttachment());
        Assert.assertSame(newAttachment, transaction.removeAttachment());
        Assert.assertNull(transaction.getAttachment());
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetAttachmentFail() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.setAttachment("attachment");
    }

    @Test(expected = IllegalStateException.class)
    public void testSetAttachmentFail2() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
        transaction.setAttachment("attachment");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAttachmentFail() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.getAttachment();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAttachmentFail2() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
        transaction.getAttachment();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAttachmentFail() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.removeAttachment();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAttachmentFail2() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.tryEnter(ALWAYS);
        transaction.canLeave(ALWAYS);
        transaction.leave(ALWAYS);
        transaction.removeAttachment();
    }

    @Test(expected = IllegalStateException.class)
    public void testAfterWithoutBefore() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.leave(ALWAYS);
    }

    @Test(expected = IllegalStateException.class)
    public void testAfterWithoutBefore2() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.leave(BOUNDARY);
    }

    @Test(expected = IllegalStateException.class)
    public void testAfterWithoutBefore3() {
        InterceptorScopeInvocation transaction = new DefaultInterceptorScopeInvocation("test");
        transaction.leave(INTERNAL);
    }
}

