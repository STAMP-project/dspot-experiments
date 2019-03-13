/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import SharedStateRegistry.Result;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.flink.core.fs.FSDataInputStream;
import org.junit.Assert;
import org.junit.Test;


public class SharedStateRegistryTest {
    /**
     * Validate that all states can be correctly registered at the registry.
     */
    @Test
    public void testRegistryNormal() {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        // register one state
        SharedStateRegistryTest.TestSharedState firstState = new SharedStateRegistryTest.TestSharedState("first");
        SharedStateRegistry.Result result = sharedStateRegistry.registerReference(firstState.getRegistrationKey(), firstState);
        Assert.assertEquals(1, result.getReferenceCount());
        Assert.assertTrue((firstState == (result.getReference())));
        TestCase.assertFalse(firstState.isDiscarded());
        // register another state
        SharedStateRegistryTest.TestSharedState secondState = new SharedStateRegistryTest.TestSharedState("second");
        result = sharedStateRegistry.registerReference(secondState.getRegistrationKey(), secondState);
        Assert.assertEquals(1, result.getReferenceCount());
        Assert.assertTrue((secondState == (result.getReference())));
        TestCase.assertFalse(firstState.isDiscarded());
        TestCase.assertFalse(secondState.isDiscarded());
        // attempt to register state under an existing key
        SharedStateRegistryTest.TestSharedState firstStatePrime = new SharedStateRegistryTest.TestSharedState(firstState.getRegistrationKey().getKeyString());
        result = sharedStateRegistry.registerReference(firstState.getRegistrationKey(), firstStatePrime);
        Assert.assertEquals(2, result.getReferenceCount());
        TestCase.assertFalse((firstStatePrime == (result.getReference())));
        Assert.assertTrue((firstState == (result.getReference())));
        Assert.assertTrue(firstStatePrime.isDiscarded());
        TestCase.assertFalse(firstState.isDiscarded());
        // reference the first state again
        result = sharedStateRegistry.registerReference(firstState.getRegistrationKey(), firstState);
        Assert.assertEquals(3, result.getReferenceCount());
        Assert.assertTrue((firstState == (result.getReference())));
        TestCase.assertFalse(firstState.isDiscarded());
        // unregister the second state
        result = sharedStateRegistry.unregisterReference(secondState.getRegistrationKey());
        Assert.assertEquals(0, result.getReferenceCount());
        Assert.assertTrue(((result.getReference()) == null));
        Assert.assertTrue(secondState.isDiscarded());
        // unregister the first state
        result = sharedStateRegistry.unregisterReference(firstState.getRegistrationKey());
        Assert.assertEquals(2, result.getReferenceCount());
        Assert.assertTrue((firstState == (result.getReference())));
        TestCase.assertFalse(firstState.isDiscarded());
    }

    /**
     * Validate that unregister a nonexistent key will throw exception
     */
    @Test(expected = IllegalStateException.class)
    public void testUnregisterWithUnexistedKey() {
        SharedStateRegistry sharedStateRegistry = new SharedStateRegistry();
        sharedStateRegistry.unregisterReference(new SharedStateRegistryKey("non-existent"));
    }

    private static class TestSharedState implements StreamStateHandle {
        private static final long serialVersionUID = 4468635881465159780L;

        private SharedStateRegistryKey key;

        private boolean discarded;

        TestSharedState(String key) {
            this.key = new SharedStateRegistryKey(key);
            this.discarded = false;
        }

        public SharedStateRegistryKey getRegistrationKey() {
            return key;
        }

        @Override
        public void discardState() throws Exception {
            this.discarded = true;
        }

        @Override
        public long getStateSize() {
            return key.toString().length();
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public FSDataInputStream openInputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        public boolean isDiscarded() {
            return discarded;
        }
    }
}

