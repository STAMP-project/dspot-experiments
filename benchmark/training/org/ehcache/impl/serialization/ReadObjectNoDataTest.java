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


import java.io.ObjectStreamException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class ReadObjectNoDataTest {
    @Test
    public void test() throws Exception {
        StatefulSerializer<Serializable> s = new CompactJavaSerializer<>(null);
        s.init(new TransientStateRepository());
        ClassLoader loaderW = SerializerTestUtilities.createClassNameRewritingLoader(ReadObjectNoDataTest.C_W.class, ReadObjectNoDataTest.B_W.class);
        ByteBuffer b = s.serialize(((Serializable) (loaderW.loadClass(SerializerTestUtilities.newClassName(ReadObjectNoDataTest.C_W.class)).newInstance())));
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(ReadObjectNoDataTest.C_R.class, ReadObjectNoDataTest.B_R.class, ReadObjectNoDataTest.A_R.class));
        try {
            Object out = s.read(b);
            Assert.assertTrue(out.getClass().getField("called").getBoolean(out));
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    public static class B_W implements Serializable {
        private static final long serialVersionUID = 0L;
    }

    public static class C_W extends ReadObjectNoDataTest.B_W {
        private static final long serialVersionUID = 0L;
    }

    public static class A_R implements Serializable {
        private static final long serialVersionUID = 0L;

        public boolean called = false;

        private void readObjectNoData() throws ObjectStreamException {
            called = true;
        }
    }

    public static class B_R extends ReadObjectNoDataTest.A_R {
        private static final long serialVersionUID = 0L;
    }

    public static class C_R extends ReadObjectNoDataTest.B_R {
        private static final long serialVersionUID = 0L;
    }
}

