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


import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.FieldMember;
import org.springsource.loaded.MethodMember;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.TypeDescriptor;
import org.springsource.loaded.TypeRegistry;


/**
 * Tests for TypeDescriptor usage.
 *
 * @author Andy Clement
 * @since 1.0
 */
public class TypeDescriptorTests extends SpringLoadedTests {
    @Test
    public void simpleMethodDescriptors() {
        TypeRegistry registry = getTypeRegistry("data.SimpleClass");
        byte[] bytes = loadBytesForClass("data.SimpleClass");
        TypeDescriptor typeDescriptor = extract(bytes, true);
        Assert.assertEquals("data/SimpleClass", typeDescriptor.getName());
        Assert.assertEquals("java/lang/Object", typeDescriptor.getSupertypeName());
        Assert.assertEquals(0, typeDescriptor.getSuperinterfacesName().length);
        Assert.assertEquals(32, typeDescriptor.getModifiers());
        Assert.assertEquals(0, typeDescriptor.getFields().length);
        Assert.assertEquals(5, typeDescriptor.getMethods().length);// will include catchers

        Assert.assertEquals("0x1 foo()V", typeDescriptor.getMethods()[0].toString());
    }

    @Test
    public void complexMethodDescriptors() {
        TypeRegistry registry = getTypeRegistry("data.ComplexClass");
        byte[] bytes = loadBytesForClass("data.ComplexClass");
        TypeDescriptor typeDescriptor = extract(bytes, true);
        Assert.assertEquals("data/ComplexClass", typeDescriptor.getName());
        Assert.assertEquals("data/SimpleClass", typeDescriptor.getSupertypeName());
        Assert.assertEquals(1, typeDescriptor.getSuperinterfacesName().length);
        Assert.assertEquals("java/io/Serializable", typeDescriptor.getSuperinterfacesName()[0]);
        Assert.assertEquals(32, typeDescriptor.getModifiers());
        Assert.assertEquals(3, typeDescriptor.getFields().length);
        Assert.assertEquals(9, typeDescriptor.getMethods().length);
        Assert.assertEquals("0x2 privateMethod()I", typeDescriptor.getMethods()[0].toString());
        Assert.assertEquals("0x1 publicMethod()Ljava/lang/String;", typeDescriptor.getMethods()[1].toString());
        Assert.assertEquals("0x0 defaultMethod()Ljava/util/List;", typeDescriptor.getMethods()[2].toString());
        Assert.assertEquals("0x0 thrower()V throws java/lang/Exception java/lang/IllegalStateException", typeDescriptor.getMethods()[3].toString());
    }

    @Test
    public void fieldDescriptors() {
        TypeRegistry registry = getTypeRegistry("");
        byte[] bytes = loadBytesForClass("data.SomeFields");
        TypeDescriptor typeDescriptor = extract(bytes, false);
        FieldMember[] fields = typeDescriptor.getFields();
        Assert.assertEquals(4, fields.length);
        FieldMember privateField = fields[0];
        Assert.assertEquals(Modifier.PRIVATE, privateField.getModifiers());
        Assert.assertEquals("privateField", privateField.getName());
        Assert.assertEquals("I", privateField.getDescriptor());
        Assert.assertNull(privateField.getGenericSignature());
        Assert.assertEquals("0x2 I privateField", privateField.toString());
        FieldMember publicField = fields[1];
        Assert.assertEquals(Modifier.PUBLIC, publicField.getModifiers());
        Assert.assertEquals("publicField", publicField.getName());
        Assert.assertEquals("Ljava/lang/String;", publicField.getDescriptor());
        Assert.assertNull(publicField.getGenericSignature());
        Assert.assertEquals("0x1 Ljava/lang/String; publicField", publicField.toString());
        FieldMember defaultField = fields[2];
        Assert.assertEquals(0, defaultField.getModifiers());
        Assert.assertEquals("defaultField", defaultField.getName());
        Assert.assertEquals("Ljava/util/List;", defaultField.getDescriptor());
        Assert.assertEquals("Ljava/util/List<Ljava/lang/String;>;", defaultField.getGenericSignature());
        Assert.assertEquals("0x0 Ljava/util/List; defaultField [Ljava/util/List<Ljava/lang/String;>;]", defaultField.toString());
        FieldMember protectedField = fields[3];
        Assert.assertEquals(Modifier.PROTECTED, protectedField.getModifiers());
        Assert.assertEquals("protectedField", protectedField.getName());
        Assert.assertEquals("Ljava/util/Map;", protectedField.getDescriptor());
        Assert.assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;", protectedField.getGenericSignature());
        Assert.assertEquals("0x4 Ljava/util/Map; protectedField [Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/Integer;>;>;]", protectedField.toString());
    }

    @Test
    public void constructorDescriptors() {
        TypeRegistry registry = getTypeRegistry("");
        byte[] bytes = loadBytesForClass("data.SomeConstructors");
        TypeDescriptor typeDescriptor = extract(bytes, false);
        MethodMember[] ctors = typeDescriptor.getConstructors();
        Assert.assertEquals(3, ctors.length);
        MethodMember publicCtor = ctors[0];
        Assert.assertEquals(Modifier.PUBLIC, publicCtor.getModifiers());
        Assert.assertEquals("<init>", publicCtor.getName());
        Assert.assertEquals("()V", publicCtor.getDescriptor());
        Assert.assertNull(publicCtor.getGenericSignature());
        Assert.assertEquals("0x1 <init>()V", publicCtor.toString());
        MethodMember privateCtor = ctors[1];
        Assert.assertEquals(Modifier.PRIVATE, privateCtor.getModifiers());
        Assert.assertEquals("<init>", privateCtor.getName());
        Assert.assertEquals("(Ljava/lang/String;I)V", privateCtor.getDescriptor());
        Assert.assertNull(privateCtor.getGenericSignature());
        Assert.assertEquals("0x2 <init>(Ljava/lang/String;I)V", privateCtor.toString());
        MethodMember protCtor = ctors[2];
        Assert.assertEquals(Modifier.PROTECTED, protCtor.getModifiers());
        Assert.assertEquals("<init>", protCtor.getName());
        Assert.assertEquals("(J)V", protCtor.getDescriptor());
        Assert.assertNull(protCtor.getGenericSignature());
        Assert.assertEquals("0x4 <init>(J)V", protCtor.toString());
    }

    @Test
    public void constructorDescriptorsAfterReloading() {
        TypeRegistry registry = getTypeRegistry("");
        String d = "data.SomeConstructors";
        ReloadableType rtype = registry.addType(d, loadBytesForClass(d));
        MethodMember[] latestConstructors = rtype.getLatestTypeDescriptor().getConstructors();
        Assert.assertEquals(3, latestConstructors.length);
        rtype.loadNewVersion("2", retrieveRename(d, (d + "002")));
        latestConstructors = rtype.getLatestTypeDescriptor().getConstructors();
        Assert.assertEquals(1, latestConstructors.length);
    }

    @Test
    public void defaultConstructorDescriptor() {
        TypeRegistry registry = getTypeRegistry("");
        byte[] bytes = loadBytesForClass("data.SomeConstructors2");
        TypeDescriptor typeDescriptor = extract(bytes, false);
        MethodMember[] ctors = typeDescriptor.getConstructors();
        Assert.assertEquals(1, ctors.length);
        MethodMember publicCtor = ctors[0];
        // visibility matches type vis (for public/default)
        Assert.assertEquals(Modifier.PUBLIC, publicCtor.getModifiers());
        Assert.assertEquals("<init>", publicCtor.getName());
        Assert.assertEquals("()V", publicCtor.getDescriptor());
        Assert.assertNull(publicCtor.getGenericSignature());
        Assert.assertEquals("0x1 <init>()V", publicCtor.toString());
    }
}

