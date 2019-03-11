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
import java.lang.ref.WeakReference;
import org.ehcache.spi.serialization.StatefulSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cdennis
 */
/* This code here for ease of debugging in the event the test fails. */
// private static void dumpHeap(String fileName, boolean live) {
// try {
// new File(fileName).delete();
// ManagementFactory.getPlatformMBeanServer().invoke(ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic"), "dumpHeap", new Object[]{fileName, Boolean.valueOf(live)}, new String[]{"java.lang.String", "boolean"});
// } catch (RuntimeException re) {
// throw re;
// } catch (Exception exp) {
// throw new RuntimeException(exp);
// }
// }
public class CompactJavaSerializerClassUnloadingTest {
    private static final int PACKING_UNIT = 512 * 1024;

    public volatile WeakReference<Class<?>> classRef;

    public volatile Serializable specialObject;

    @Test
    public void testClassUnloadingAfterSerialization() throws Exception {
        StatefulSerializer<Serializable> serializer = new CompactJavaSerializer<>(null);
        serializer.init(new TransientStateRepository());
        serializer.serialize(specialObject);
        specialObject = null;
        for (int i = 0; i < 10; i++) {
            if ((classRef.get()) == null) {
                return;
            } else {
                CompactJavaSerializerClassUnloadingTest.packHeap();
            }
        }
        throw new AssertionError();
    }

    @Test
    public void testClassUnloadingAfterSerializationAndDeserialization() throws Exception {
        Thread.currentThread().setContextClassLoader(specialObject.getClass().getClassLoader());
        try {
            StatefulSerializer<Serializable> serializer = new CompactJavaSerializer<>(null);
            serializer.init(new TransientStateRepository());
            specialObject = serializer.read(serializer.serialize(specialObject));
            Assert.assertEquals(CompactJavaSerializerClassUnloadingTest.SpecialClass.class.getName(), specialObject.getClass().getName());
            Assert.assertNotSame(CompactJavaSerializerClassUnloadingTest.SpecialClass.class, specialObject.getClass());
        } finally {
            specialObject = null;
            Thread.currentThread().setContextClassLoader(null);
        }
        for (int i = 0; i < 10; i++) {
            if ((classRef.get()) == null) {
                return;
            } else {
                CompactJavaSerializerClassUnloadingTest.packHeap();
            }
        }
        throw new AssertionError();
    }

    // empty impl
    public static class SpecialClass implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}

