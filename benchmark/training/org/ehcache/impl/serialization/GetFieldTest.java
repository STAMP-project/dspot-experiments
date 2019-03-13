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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class GetFieldTest {
    @Test
    public void testGetField() throws Exception {
        @SuppressWarnings("unchecked")
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        ClassLoader loaderA = SerializerTestUtilities.createClassNameRewritingLoader(GetFieldTest.Foo_A.class);
        Serializable a = ((Serializable) (loaderA.loadClass(SerializerTestUtilities.newClassName(GetFieldTest.Foo_A.class)).newInstance()));
        ByteBuffer encodedA = s.serialize(a);
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(GetFieldTest.Foo_B.class));
        try {
            s.read(encodedA.duplicate());
        } finally {
            SerializerTestUtilities.popTccl();
        }
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(GetFieldTest.Foo_C.class));
        try {
            s.read(encodedA.duplicate());
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    public static class Foo_A implements Serializable {
        private static final long serialVersionUID = 0L;

        boolean z = true;

        byte b = 5;

        char c = '5';

        short s = 5;

        int i = 5;

        long j = 5;

        float f = 5.0F;

        double d = 5.0;

        String str = "5";
    }

    public static class Foo_B implements Serializable {
        private static final long serialVersionUID = 0L;

        int blargh;

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            ObjectInputStream.GetField fields = in.readFields();
            if (!(fields.defaulted("blargh"))) {
                throw new Error();
            }
            try {
                fields.defaulted("nonexistant");
                throw new Error();
            } catch (IllegalArgumentException ex) {
            }
            if ((((((((((fields.get("z", false)) != true) || ((fields.get("b", ((byte) (0)))) != 5)) || ((fields.get("c", '0')) != '5')) || ((fields.get("s", ((short) (0)))) != 5)) || ((fields.get("i", 0)) != 5)) || ((fields.get("j", 0L)) != 5)) || ((fields.get("f", 0.0F)) != 5.0F)) || ((fields.get("d", 0.0)) != 5.0)) || (!(fields.get("str", null).equals("5")))) {
                throw new Error();
            }
        }
    }

    public static class Foo_C implements Serializable {
        private static final long serialVersionUID = 0L;

        boolean z;

        byte b;

        char c;

        short s;

        int i;

        long j;

        float f;

        double d;

        String str;

        Object extra;

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            ObjectInputStream.GetField fields = in.readFields();
            if ((((((((((fields.get("z", false)) != true) || ((fields.get("b", ((byte) (0)))) != 5)) || ((fields.get("c", '0')) != '5')) || ((fields.get("s", ((short) (0)))) != 5)) || ((fields.get("i", 0)) != 5)) || ((fields.get("j", 0L)) != 5)) || ((fields.get("f", 0.0F)) != 5.0F)) || ((fields.get("d", 0.0)) != 5.0)) || (!(fields.get("str", null).equals("5")))) {
                throw new Error();
            }
        }
    }
}

