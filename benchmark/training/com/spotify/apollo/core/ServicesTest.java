/**
 * -\-\-
 * Spotify Apollo Service Core (aka Leto)
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.core;


import Service.Instance;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ServicesTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCtor() throws Exception {
        expectedException.expect(InvocationTargetException.class);
        expectedException.expectCause(Matchers.is(Matchers.<Throwable>instanceOf(IllegalAccessError.class)));
        Constructor<Services> constructor = Services.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testUsingName() throws Exception {
        Service service = Services.usingName("test").build();
        Assert.assertThat(service.getServiceName(), Matchers.is("test"));
    }

    @Test
    public void testRun() throws Exception {
        String[] args = new String[]{ "a", "b", "c" };
        Service service = Mockito.mock(Service.class);
        Service.Instance instance = Mockito.mock(Instance.class);
        Mockito.when(service.start(args)).thenReturn(instance);
        Services.run(service, args);
        InOrder inOrder = Mockito.inOrder(service, instance);
        inOrder.verify(service, Mockito.times(1)).start(args);
        inOrder.verify(instance, Mockito.times(1)).waitForShutdown();
        inOrder.verify(instance, Mockito.atLeastOnce()).close();
    }
}

