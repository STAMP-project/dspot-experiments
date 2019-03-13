/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class PromiseAggregatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNullAggregatePromise() {
        expectedException.expect(NullPointerException.class);
        new PromiseAggregator<Void, Future<Void>>(null);
    }

    @Test
    public void testAddNullFuture() {
        @SuppressWarnings("unchecked")
        Promise<Void> p = Mockito.mock(Promise.class);
        PromiseAggregator<Void, Future<Void>> a = new PromiseAggregator<Void, Future<Void>>(p);
        expectedException.expect(NullPointerException.class);
        a.add(((Promise<Void>[]) (null)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessfulNoPending() throws Exception {
        Promise<Void> p = Mockito.mock(Promise.class);
        PromiseAggregator<Void, Future<Void>> a = new PromiseAggregator<Void, Future<Void>>(p);
        Future<Void> future = Mockito.mock(Future.class);
        Mockito.when(p.setSuccess(null)).thenReturn(p);
        a.add();
        a.operationComplete(future);
        Mockito.verifyNoMoreInteractions(future);
        Mockito.verify(p).setSuccess(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessfulPending() throws Exception {
        Promise<Void> p = Mockito.mock(Promise.class);
        PromiseAggregator<Void, Future<Void>> a = new PromiseAggregator<Void, Future<Void>>(p);
        Promise<Void> p1 = Mockito.mock(Promise.class);
        Promise<Void> p2 = Mockito.mock(Promise.class);
        Mockito.when(p1.addListener(a)).thenReturn(p1);
        Mockito.when(p2.addListener(a)).thenReturn(p2);
        Mockito.when(p1.isSuccess()).thenReturn(true);
        Mockito.when(p2.isSuccess()).thenReturn(true);
        Mockito.when(p.setSuccess(null)).thenReturn(p);
        Assert.assertThat(a.add(p1, null, p2), CoreMatchers.is(a));
        a.operationComplete(p1);
        a.operationComplete(p2);
        Mockito.verify(p1).addListener(a);
        Mockito.verify(p2).addListener(a);
        Mockito.verify(p1).isSuccess();
        Mockito.verify(p2).isSuccess();
        Mockito.verify(p).setSuccess(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailedFutureFailPending() throws Exception {
        Promise<Void> p = Mockito.mock(Promise.class);
        PromiseAggregator<Void, Future<Void>> a = new PromiseAggregator<Void, Future<Void>>(p);
        Promise<Void> p1 = Mockito.mock(Promise.class);
        Promise<Void> p2 = Mockito.mock(Promise.class);
        Throwable t = Mockito.mock(Throwable.class);
        Mockito.when(p1.addListener(a)).thenReturn(p1);
        Mockito.when(p2.addListener(a)).thenReturn(p2);
        Mockito.when(p1.isSuccess()).thenReturn(false);
        Mockito.when(p1.cause()).thenReturn(t);
        Mockito.when(p.setFailure(t)).thenReturn(p);
        Mockito.when(p2.setFailure(t)).thenReturn(p2);
        a.add(p1, p2);
        a.operationComplete(p1);
        Mockito.verify(p1).addListener(a);
        Mockito.verify(p2).addListener(a);
        Mockito.verify(p1).cause();
        Mockito.verify(p).setFailure(t);
        Mockito.verify(p2).setFailure(t);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailedFutureNoFailPending() throws Exception {
        Promise<Void> p = Mockito.mock(Promise.class);
        PromiseAggregator<Void, Future<Void>> a = new PromiseAggregator<Void, Future<Void>>(p, false);
        Promise<Void> p1 = Mockito.mock(Promise.class);
        Promise<Void> p2 = Mockito.mock(Promise.class);
        Throwable t = Mockito.mock(Throwable.class);
        Mockito.when(p1.addListener(a)).thenReturn(p1);
        Mockito.when(p2.addListener(a)).thenReturn(p2);
        Mockito.when(p1.isSuccess()).thenReturn(false);
        Mockito.when(p1.cause()).thenReturn(t);
        Mockito.when(p.setFailure(t)).thenReturn(p);
        a.add(p1, p2);
        a.operationComplete(p1);
        Mockito.verify(p1).addListener(a);
        Mockito.verify(p2).addListener(a);
        Mockito.verify(p1).isSuccess();
        Mockito.verify(p1).cause();
        Mockito.verify(p).setFailure(t);
    }
}

