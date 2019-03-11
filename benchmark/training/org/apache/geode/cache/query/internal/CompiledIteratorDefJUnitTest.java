/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.query.internal;


import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.query.internal.types.TypeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CompiledIteratorDefJUnitTest {
    @Test
    public void cacheClosedWhileEvaluateCollectionShouldNotThrowTypeMismatchException() throws Exception {
        CompiledValue compiledValue = Mockito.mock(CompiledValue.class);
        CompiledIteratorDef compiledIteratorDef = new CompiledIteratorDef("TestIterator", TypeUtils.OBJECT_TYPE, compiledValue);
        ExecutionContext executionContext = Mockito.mock(ExecutionContext.class);
        RuntimeIterator runtimeIterator = Mockito.mock(RuntimeIterator.class);
        Mockito.when(runtimeIterator.evaluateCollection(executionContext)).thenThrow(CacheClosedException.class);
        try {
            compiledIteratorDef.evaluateCollectionForIndependentIterator(executionContext, runtimeIterator);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CacheClosedException));
        }
    }
}

