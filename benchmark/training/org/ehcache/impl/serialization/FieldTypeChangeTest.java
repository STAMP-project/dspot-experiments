/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.serialization;


import java.io.Serializable;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class FieldTypeChangeTest {
    @Test
    public void fieldTypeChangeWithOkayObject() throws Exception {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        ClassLoader loaderW = SerializerTestUtilities.createClassNameRewritingLoader(FieldTypeChangeTest.Foo_W.class);
        Serializable a = ((Serializable) (loaderW.loadClass(SerializerTestUtilities.newClassName(FieldTypeChangeTest.Foo_W.class)).getConstructor(Object.class).newInstance("foo")));
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(FieldTypeChangeTest.Foo_R.class));
        try {
            s.read(s.serialize(a));
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    @Test
    public void fieldTypeChangeWithIncompatibleObject() throws Exception {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        ClassLoader loaderW = SerializerTestUtilities.createClassNameRewritingLoader(FieldTypeChangeTest.Foo_W.class);
        Serializable a = ((Serializable) (loaderW.loadClass(SerializerTestUtilities.newClassName(FieldTypeChangeTest.Foo_W.class)).getConstructor(Object.class).newInstance(Integer.valueOf(42))));
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(FieldTypeChangeTest.Foo_R.class));
        try {
            s.read(s.serialize(a));
            Assert.fail("Expected ClassCastException");
        } catch (ClassCastException e) {
            // expected
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    public static class Foo_W implements Serializable {
        private static final long serialVersionUID = 0L;

        Object obj;

        public Foo_W(Object obj) {
            this.obj = obj;
        }
    }

    public static class Foo_R implements Serializable {
        private static final long serialVersionUID = 0L;

        String obj;
    }
}

