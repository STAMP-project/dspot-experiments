/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.operation;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.MockingDetails;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapOperationProviderDelegatorTest extends HazelcastTestSupport {
    private MapOperationProvider operationProvider;

    private MapOperationProviderDelegatorTest.TestMapOperationProviderDelegator delegator;

    @Test
    public void testDelegator() throws Exception {
        Method[] methods = MapOperationProviderDelegator.class.getDeclaredMethods();
        // invoke all methods of MapOperationProviderDelegator
        for (Method method : methods) {
            if ((Modifier.isAbstract(method.getModifiers())) || (Modifier.isPrivate(method.getModifiers()))) {
                continue;
            }
            Object[] parameters = getParameters(method);
            try {
                method.invoke(delegator, parameters);
            } catch (Exception e) {
                System.err.println(String.format("Could not invoke method %s: %s", method.getName(), e.getMessage()));
            }
        }
        // get a list of all method invocations from Mockito
        List<String> methodsCalled = new ArrayList<String>();
        MockingDetails mockingDetails = Mockito.mockingDetails(operationProvider);
        Collection<Invocation> invocations = mockingDetails.getInvocations();
        for (Invocation invocation : invocations) {
            methodsCalled.add(invocation.getMethod().getName());
        }
        // verify that all methods have been called on the delegated MapOperationProvider
        for (Method method : methods) {
            if ((Modifier.isAbstract(method.getModifiers())) || (Modifier.isPrivate(method.getModifiers()))) {
                continue;
            }
            Assert.assertTrue(String.format("Method %s() should have been called", method.getName()), methodsCalled.contains(method.getName()));
        }
    }

    private class TestMapOperationProviderDelegator extends MapOperationProviderDelegator {
        @Override
        MapOperationProvider getDelegate() {
            return operationProvider;
        }
    }
}

