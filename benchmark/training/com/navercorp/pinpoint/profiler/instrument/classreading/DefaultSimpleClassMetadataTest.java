/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.instrument.classreading;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class DefaultSimpleClassMetadataTest {
    @Test
    public void base() throws Exception {
        final byte[] classBinary = new byte[1];
        DefaultSimpleClassMetadata classMetadata = new DefaultSimpleClassMetadata(1, 1, "java/lang/String", "java/lang/Object", Arrays.asList("java/lang/Comparable", "java/lang/Serializable"), classBinary);
        Assert.assertEquals("java.lang.String", classMetadata.getClassName());
        Assert.assertEquals("java.lang.Object", classMetadata.getSuperClassName());
        Assert.assertTrue(classMetadata.getInterfaceNames().contains("java.lang.Comparable"));
        Assert.assertTrue(classMetadata.getInterfaceNames().contains("java.lang.Serializable"));
        Assert.assertEquals(1, classMetadata.getAccessFlag());
        Assert.assertEquals(1, classMetadata.getVersion());
        Assert.assertEquals(classBinary, classMetadata.getClassBinary());
    }

    @Test
    public void interfaceNameNull() throws Exception {
        final byte[] classBinary = new byte[1];
        DefaultSimpleClassMetadata classMetadata = new DefaultSimpleClassMetadata(1, 1, "java/lang/String", "java/lang/Object", null, classBinary);
        Assert.assertEquals(0, classMetadata.getInterfaceNames().size());
    }
}

