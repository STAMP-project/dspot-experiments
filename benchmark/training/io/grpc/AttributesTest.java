/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc;


import Attributes.EMPTY;
import Attributes.Key;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Attributes}.
 */
@RunWith(JUnit4.class)
public class AttributesTest {
    private static final Attributes.Key<String> YOLO_KEY = Key.create("yolo");

    @Test
    public void buildAttributes() {
        Attributes attrs = Attributes.newBuilder().set(io.grpc.YOLO_KEY, "To be, or not to be?").build();
        Assert.assertSame("To be, or not to be?", attrs.get(io.grpc.YOLO_KEY));
        assertThat(attrs.keysForTest()).hasSize(1);
    }

    @Test
    public void duplicates() {
        Attributes attrs = Attributes.newBuilder().set(io.grpc.YOLO_KEY, "To be?").set(io.grpc.YOLO_KEY, "Or not to be?").set(Key.create("yolo"), "I'm not a duplicate").build();
        assertThat(attrs.get(io.grpc.YOLO_KEY)).isEqualTo("Or not to be?");
        assertThat(attrs.keysForTest()).hasSize(2);
    }

    @Test
    public void toBuilder() {
        Attributes attrs = Attributes.newBuilder().set(io.grpc.YOLO_KEY, "To be?").build().toBuilder().set(io.grpc.YOLO_KEY, "Or not to be?").set(Key.create("yolo"), "I'm not a duplicate").build();
        assertThat(attrs.get(io.grpc.YOLO_KEY)).isEqualTo("Or not to be?");
        assertThat(attrs.keysForTest()).hasSize(2);
    }

    @Test
    public void empty() {
        assertThat(EMPTY.keysForTest()).isEmpty();
    }

    @Test
    public void valueEquality() {
        class EqualObject {
            @Override
            public boolean equals(Object o) {
                return o instanceof EqualObject;
            }

            @Override
            public int hashCode() {
                return 42;
            }
        }
        Attributes.Key<EqualObject> key = Key.create("ints");
        EqualObject v1 = new EqualObject();
        EqualObject v2 = new EqualObject();
        Assert.assertNotSame(v1, v2);
        Assert.assertEquals(v1, v2);
        Attributes attr1 = Attributes.newBuilder().set(key, v1).build();
        Attributes attr2 = Attributes.newBuilder().set(key, v2).build();
        Assert.assertEquals(attr1, attr2);
        Assert.assertEquals(attr1.hashCode(), attr2.hashCode());
    }
}

