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
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
public class ArrayPackageScopeTest {
    @Test
    public void testArrayPackageScope() throws Exception {
        StatefulSerializer<Serializable> serializer = new CompactJavaSerializer<>(null);
        serializer.init(new TransientStateRepository());
        ClassLoader loaderA = SerializerTestUtilities.createClassNameRewritingLoader(ArrayPackageScopeTest.Foo_A.class);
        Serializable a = ((Serializable) (Array.newInstance(loaderA.loadClass(SerializerTestUtilities.newClassName(ArrayPackageScopeTest.Foo_A.class)), 0)));
        ByteBuffer encodedA = serializer.serialize(a);
        SerializerTestUtilities.pushTccl(SerializerTestUtilities.createClassNameRewritingLoader(ArrayPackageScopeTest.Foo_B.class));
        try {
            Serializable b = serializer.read(encodedA);
            Assert.assertTrue(b.getClass().isArray());
        } finally {
            SerializerTestUtilities.popTccl();
        }
    }

    public static class Foo_A implements Serializable {
        private static final long serialVersionUID = 0L;
    }

    static class Foo_B {
        private static final long serialVersionUID = 0L;
    }
}

