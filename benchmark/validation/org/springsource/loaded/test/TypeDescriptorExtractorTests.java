/**
 * Copyright 2010-2012 VMware and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.loaded.test;


import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.TypeDescriptor;
import org.springsource.loaded.TypeRegistry;


/**
 *
 *
 * @author Andy Clement
 */
public class TypeDescriptorExtractorTests extends SpringLoadedTests {
    /**
     * Test extraction on a very simple class.
     */
    @Test
    public void simpleExtractor() {
        TypeRegistry tr = getTypeRegistry("");
        byte[] bytes = loadBytesForClass("data.SimpleClass");
        TypeDescriptor typeDescriptor = extract(bytes, true);
        Assert.assertEquals("data/SimpleClass", typeDescriptor.getName());
        Assert.assertEquals("java/lang/Object", typeDescriptor.getSupertypeName());
        Assert.assertEquals(0, typeDescriptor.getSuperinterfacesName().length);
        Assert.assertEquals(32, typeDescriptor.getModifiers());
        Assert.assertEquals(0, typeDescriptor.getFields().length);
        Assert.assertEquals(5, typeDescriptor.getMethods().length);
        Assert.assertEquals("0x1 foo()V", typeDescriptor.getMethods()[0].toString());
    }

    @Test
    public void nonReloadableExtract() {
        TypeRegistry tr = getTypeRegistry("");
        byte[] bytes = loadBytesForClass("java.lang.Object");
        TypeDescriptor typeDescriptor = extract(bytes, false);
        System.out.println(typeDescriptor.toString());
        Assert.assertEquals("java/lang/Object", typeDescriptor.getName());
        Assert.assertEquals(null, typeDescriptor.getSupertypeName());
        Assert.assertEquals(0, typeDescriptor.getSuperinterfacesName().length);
        Assert.assertEquals(33, typeDescriptor.getModifiers());
        Assert.assertEquals(0, typeDescriptor.getFields().length);
        Assert.assertEquals(12, typeDescriptor.getMethods().length);
        Assert.assertNotNull(findMethod("0x104 clone()Ljava/lang/Object; throws java/lang/CloneNotSupportedException", typeDescriptor));
    }
}

