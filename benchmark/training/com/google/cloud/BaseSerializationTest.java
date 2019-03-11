/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.common.base.MoreObjects;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base class for serialization tests. To use this class in your tests override the {@code serializableObjects()} method to return all objects that must be serializable. Also override
 * {@code restorableObjects()} method to return all restorable objects whose state must be tested
 * for proper serialization. Both methods can return {@code null} if no such object needs to be
 * tested.
 */
public abstract class BaseSerializationTest {
    @Test
    public void testSerializableObjects() throws Exception {
        for (Serializable obj : MoreObjects.firstNonNull(serializableObjects(), new Serializable[0])) {
            Object copy = serializeAndDeserialize(obj);
            Assert.assertEquals(obj, obj);
            Assert.assertEquals(obj, copy);
            Assert.assertEquals(obj.hashCode(), copy.hashCode());
            Assert.assertEquals(obj.toString(), copy.toString());
            Assert.assertNotSame(obj, copy);
            Assert.assertEquals(copy, copy);
        }
    }

    @Test
    public void testRestorableObjects() throws Exception {
        for (Restorable restorable : MoreObjects.firstNonNull(restorableObjects(), new Restorable[0])) {
            RestorableState<?> state = restorable.capture();
            RestorableState<?> deserializedState = serializeAndDeserialize(state);
            Assert.assertEquals(state, deserializedState);
            Assert.assertEquals(state.hashCode(), deserializedState.hashCode());
            Assert.assertEquals(state.toString(), deserializedState.toString());
        }
    }
}

