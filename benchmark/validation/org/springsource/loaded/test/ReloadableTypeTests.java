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


import ReturnType.Kind.ARRAY;
import ReturnType.Kind.PRIMITIVE;
import ReturnType.Kind.REFERENCE;
import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.GlobalConfiguration;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.SpringLoaded;
import org.springsource.loaded.TypeRegistry;
import org.springsource.loaded.Utils.ReturnType;
import org.springsource.loaded.test.infra.Result;


/**
 * Tests for the TypeRegistry that exercise it in the same way it will actively be used when managing ReloadableType
 * instances.
 *
 * @author Andy Clement
 * @since 1.0
 */
public class ReloadableTypeTests extends SpringLoadedTests {
    /**
     * Check the basics.
     */
    @Test
    public void loadType() {
        TypeRegistry typeRegistry = getTypeRegistry("data.SimpleClass");
        byte[] sc = loadBytesForClass("data.SimpleClass");
        ReloadableType rtype = new ReloadableType("data.SimpleClass", sc, 1, typeRegistry, null);
        Assert.assertEquals(1, rtype.getId());
        Assert.assertEquals("data.SimpleClass", rtype.getName());
        Assert.assertEquals("data/SimpleClass", rtype.getSlashedName());
        Assert.assertNotNull(rtype.getTypeDescriptor());
        Assert.assertEquals(typeRegistry, rtype.getTypeRegistry());
    }

    /**
     * Check calling it, reloading it and calling the new version.
     */
    @Test
    public void callIt() throws Exception {
        TypeRegistry typeRegistry = getTypeRegistry("basic.Basic");
        byte[] sc = loadBytesForClass("basic.Basic");
        ReloadableType rtype = typeRegistry.addType("basic.Basic", sc);
        Class<?> simpleClass = rtype.getClazz();
        Result r = runUnguarded(simpleClass, "foo");
        r = runUnguarded(simpleClass, "getValue");
        Assert.assertEquals(5, r.returnValue);
        rtype.loadNewVersion("002", retrieveRename("basic.Basic", "basic.Basic002"));
        r = runUnguarded(simpleClass, "getValue");
        Assert.assertEquals(7, r.returnValue);
    }

    @Test
    public void removingStaticMethod() throws Exception {
        String t = "remote.Perf1";
        TypeRegistry typeRegistry = getTypeRegistry(t);
        byte[] sc = loadBytesForClass(t);
        ReloadableType rtype = typeRegistry.addType(t, sc);
        Class<?> clazz = rtype.getClazz();
        runUnguarded(clazz, "time");
        rtype.loadNewVersion("002", retrieveRename(t, "remote.Perf2"));
        runUnguarded(clazz, "time");
    }

    @Test
    public void protectedFieldAccessors() throws Exception {
        TypeRegistry tr = getTypeRegistry("prot.SubOne");
        ReloadableType rtype = tr.addType("prot.SubOne", loadBytesForClass("prot.SubOne"));
        Object instance = rtype.getClazz().newInstance();
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setPublicField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getPublicField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedShortField", ((short) (33)));
        Assert.assertEquals(((short) (33)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedShortField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedByteField", ((byte) (133)));
        Assert.assertEquals(((byte) (133)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedByteField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedCharField", ((char) (12)));
        Assert.assertEquals(((char) (12)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedCharField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedBooleanField", true);
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "isProtectedBooleanField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedDoubleField", 3.1);
        Assert.assertEquals(3.1, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedDoubleField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedFloatField", 8.0F);
        Assert.assertEquals(8.0F, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedFloatField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedLongField", 888L);
        Assert.assertEquals(888L, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedLongField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfInts", new int[]{ 1, 2, 3 });
        Assert.assertEquals("[1,2,3]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfInts").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfStrings", ((Object) (new String[]{ "a", "b", "c" })));
        Assert.assertEquals("[a,b,c]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfStrings").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfArrayOfLongs", ((Object) (new long[][]{ new long[]{ 3L }, new long[]{ 4L, 45L }, new long[]{ 7L } })));
        Assert.assertEquals("[[3],[4,45],[7]]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfArrayOfLongs").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedStaticField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedStaticField").returnValue);
        rtype.loadNewVersion(rtype.bytesInitial);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setPublicField", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getPublicField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedShortField", ((short) (33)));
        Assert.assertEquals(((short) (33)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedShortField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedByteField", ((byte) (133)));
        Assert.assertEquals(((byte) (133)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedByteField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedCharField", ((char) (12)));
        Assert.assertEquals(((char) (12)), SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedCharField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedBooleanField", true);
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "isProtectedBooleanField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedDoubleField", 3.1);
        Assert.assertEquals(3.1, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedDoubleField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedFloatField", 8.0F);
        Assert.assertEquals(8.0F, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedFloatField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedLongField", 888L);
        Assert.assertEquals(888L, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedLongField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfInts", new int[]{ 1, 2, 3 });
        Assert.assertEquals("[1,2,3]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfInts").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfStrings", ((Object) (new String[]{ "a", "b", "c" })));
        Assert.assertEquals("[a,b,c]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfStrings").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedArrayOfArrayOfLongs", ((Object) (new long[][]{ new long[]{ 3L }, new long[]{ 4L, 45L }, new long[]{ 7L } })));
        Assert.assertEquals("[[3],[4,45],[7]]", toString(SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedArrayOfArrayOfLongs").returnValue));
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setProtectedStaticField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getProtectedStaticField").returnValue);
    }

    // github issue 4
    @Test
    public void invokeStaticReloading_gh4_1() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic..*");
        tr.addType("invokestatic.issue4.A", loadBytesForClass("invokestatic.issue4.A"));
        ReloadableType B = tr.addType("invokestatic.issue4.B", loadBytesForClass("invokestatic.issue4.B"));
        Result r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
        B.loadNewVersion(B.bytesInitial);
        r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
    }

    @Test
    public void invokeStaticReloading_gh4_2() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic..*");
        tr.addType("invokestatic.issue4.AA", loadBytesForClass("invokestatic.issue4.AA"));
        ReloadableType BB = tr.addType("invokestatic.issue4.BB", loadBytesForClass("invokestatic.issue4.BB"));
        Result r = runUnguarded(BB.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
        BB.loadNewVersion(BB.bytesInitial);
        r = runUnguarded(BB.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
    }

    @Test
    public void invokeStaticReloading_gh4_3() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic..*");
        ReloadableType AAA = tr.addType("invokestatic.issue4.AAA", loadBytesForClass("invokestatic.issue4.AAA"));
        ReloadableType BBB = tr.addType("invokestatic.issue4.BBB", loadBytesForClass("invokestatic.issue4.BBB"));
        Result r = runUnguarded(BBB.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
        AAA.loadNewVersion(AAA.bytesInitial);
        r = runUnguarded(BBB.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
    }

    @Test
    public void invokeStaticReloading_gh4_4() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic..*");
        ReloadableType A = tr.addType("invokestatic.issue4.A", loadBytesForClass("invokestatic.issue4.A"));
        ReloadableType B = tr.addType("invokestatic.issue4.B", loadBytesForClass("invokestatic.issue4.B"));
        Result r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
        A.loadNewVersion(A.bytesInitial);
        B.loadNewVersion(B.bytesInitial);
        r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("String1", r.returnValue);
    }

    // The supertype is not reloadable,it is in a jar
    @Test
    public void invokeStaticReloading_gh4_5() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic.issue4..*");
        ReloadableType B = tr.addType("invokestatic.issue4.BBBB", loadBytesForClass("invokestatic.issue4.BBBB"));
        Result r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
        ReloadableType thesuper = B.getSuperRtype();
        Assert.assertNull(thesuper);
        thesuper = tr.getReloadableType("invokestatic/issue4/subpkg/AAAA");
        Assert.assertNull(thesuper);
        B.loadNewVersion(B.bytesInitial);
        r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
    }

    // Basic write/read then reload then write/read again
    @Test
    public void serialization1() throws Exception {
        TypeRegistry tr = getTypeRegistry("remote..*");
        ReloadableType person = tr.addType("remote.Person", loadBytesForClass("remote.Person"));
        // When the Serialize class is run directly, we see: byteinfo:len=98:crc=c1047cf6
        // When run via this test, we see: byteinfo:len=98:crc=7e07276a
        // Tried running the Serialize code directly but with a clinit in the Person class: 2b4c0df4
        ReloadableType runner = tr.addType("remote.Serialize", loadBytesForClass("remote.Serialize"));
        Result r = null;
        r = runUnguarded(runner.getClazz(), "run");
        assertContains("check ok", r.stdout);
        person.loadNewVersion("2", retrieveRename("remote.Person", "remote.Person2"));
        r = runUnguarded(runner.getClazz(), "run");
        assertContains("check ok", r.stdout);
    }

    // Unlike the first test, this one will reload the class in between serialize and deserialize
    @Test
    public void serialization2() throws Exception {
        TypeRegistry tr = getTypeRegistry("remote..*");
        ReloadableType person = tr.addType("remote.Person", loadBytesForClass("remote.Person"));
        // byteinfo:len=98:crc=7e07276a
        ReloadableType runner = tr.addType("remote.Serialize", loadBytesForClass("remote.Serialize"));
        Class<?> clazz = runner.getClazz();
        Object instance = clazz.newInstance();
        Result r = null;
        // Basic: write and read the same Person
        r = SpringLoadedTests.runOnInstance(clazz, instance, "writePerson");
        assertStdoutContains("Person stored ok", r);
        r = SpringLoadedTests.runOnInstance(clazz, instance, "readPerson");
        assertContains("Person read ok", r.stdout);
        // Advanced: write it, reload, then read back from the written form
        r = SpringLoadedTests.runOnInstance(clazz, instance, "writePerson");
        assertStdoutContains("Person stored ok", r);
        person.loadNewVersion("2", retrieveRename("remote.Person", "remote.Person2"));
        r = SpringLoadedTests.runOnInstance(clazz, instance, "readPerson");
        assertContains("Person read ok", r.stdout);
    }

    // Variant of the second test but using serialVersionUID and adding methods to the class on reload
    @Test
    public void serialization3() throws Exception {
        TypeRegistry tr = getTypeRegistry("remote..*");
        ReloadableType person = tr.addType("remote.PersonB", loadBytesForClass("remote.PersonB"));
        ReloadableType runner = tr.addType("remote.SerializeB", loadBytesForClass("remote.SerializeB"));
        Class<?> clazz = runner.getClazz();
        Object instance = clazz.newInstance();
        Result r = null;
        // Basic: write and read the same Person
        r = SpringLoadedTests.runOnInstance(runner.getClazz(), instance, "writePerson");
        assertStdoutContains("Person stored ok", r);
        r = SpringLoadedTests.runOnInstance(runner.getClazz(), instance, "readPerson");
        assertContains("Person read ok", r.stdout);
        // Advanced: write it, reload, then read back from the written form
        r = SpringLoadedTests.runOnInstance(runner.getClazz(), instance, "writePerson");
        assertStdoutContains("Person stored ok", r);
        person.loadNewVersion("2", retrieveRename("remote.PersonB", "remote.PersonB2"));
        r = SpringLoadedTests.runOnInstance(runner.getClazz(), instance, "readPerson");
        assertContains("Person read ok", r.stdout);
        r = SpringLoadedTests.runOnInstance(clazz, instance, "printInitials");
        assertContains("Person read ok\nWS", r.stdout);
    }

    // extra class in the middle: A in jar, subtype AB reloadable, subtype BBBBB reloadable
    @Test
    public void invokeStaticReloading_gh4_6() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic.issue4..*");
        tr.addType("invokestatic.issue4.AB", loadBytesForClass("invokestatic.issue4.AB"));
        ReloadableType B = tr.addType("invokestatic.issue4.BBBBB", loadBytesForClass("invokestatic.issue4.BBBBB"));
        Result r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
        ReloadableType thesuper = B.getSuperRtype();
        thesuper = tr.getReloadableType("invokestatic/issue4/subpkg/AAAA");
        Assert.assertNull(thesuper);
        B.loadNewVersion(B.bytesInitial);
        r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
    }

    @Test
    public void verifyingAssociatedTypesInfo() throws Exception {
        TypeRegistry tr = getTypeRegistry("invokestatic..*");
        ReloadableType mid = tr.addType("invokestatic.issue4.AB", loadBytesForClass("invokestatic.issue4.AB"));
        ReloadableType B = tr.addType("invokestatic.issue4.BBBBB", loadBytesForClass("invokestatic.issue4.BBBBB"));
        Result r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
        ReloadableType thesuper = B.getSuperRtype();
        thesuper = tr.getReloadableType("invokestatic/issue4/subpkg/AAAA");
        Assert.assertNull(thesuper);
        B.loadNewVersion(B.bytesInitial);
        r = runUnguarded(B.getClazz(), "getMessage");
        Assert.assertEquals("Hello", r.returnValue);
        assertAssociateSubtypes(mid, "invokestatic.issue4.BBBBB");
        Assert.assertTrue(B.isAffectedByReload());
        Assert.assertTrue(mid.isAffectedByReload());// supertype should also be marked as affected

        // The super-super type is not reloadable so no need to check it is tagged
    }

    @Test
    public void protectedFieldAccessors2() throws Exception {
        TypeRegistry tr = getTypeRegistry("prot.SubTwo");
        ReloadableType rtype = tr.addType("prot.SubTwo", loadBytesForClass("prot.SubTwo"));
        Object instance = rtype.getClazz().newInstance();
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setSomeField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getSomeField").returnValue);
        rtype.loadNewVersion(rtype.bytesInitial);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setSomeField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getSomeField").returnValue);
    }

    /**
     * In this test a protected field has the same name as another field being referenced from the reloadable type.
     * Check only the right one is redirect to the accessor.
     */
    @Test
    public void protectedFieldAccessors3() throws Exception {
        TypeRegistry tr = getTypeRegistry("prot.SubThree,prot.PeerThree");
        // ReloadableType rtypePeer =
        tr.addType("prot.PeerThree", loadBytesForClass("prot.PeerThree"));
        ReloadableType rtype = tr.addType("prot.SubThree", loadBytesForClass("prot.SubThree"));
        Object instance = rtype.getClazz().newInstance();
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setPeerField", 5);
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getPeerField").returnValue);
        // if this returns 5, the wrong field got set in setPeerField!
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getField").returnValue);
        rtype.loadNewVersion(rtype.bytesInitial);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setField", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getField").returnValue);
        SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "setPeerField", 5);
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getPeerField").returnValue);
        // if this returns 5, the wrong field got set in setPeerField!
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "getField").returnValue);
    }

    @Test
    public void testReturnTypeFactoryMethod() throws Exception {
        ReturnType rt = ReturnType.getReturnType("I");
        Assert.assertEquals(PRIMITIVE, rt.kind);
        Assert.assertEquals("I", rt.descriptor);
        Assert.assertTrue(rt.isPrimitive());
        Assert.assertFalse(rt.isDoubleSlot());
        Assert.assertFalse(rt.isVoid());
        rt = ReturnType.getReturnType("[Ljava/lang/String;");
        Assert.assertEquals(ARRAY, rt.kind);
        Assert.assertEquals("[Ljava/lang/String;", rt.descriptor);
        Assert.assertFalse(rt.isPrimitive());
        Assert.assertFalse(rt.isDoubleSlot());
        Assert.assertFalse(rt.isVoid());
        rt = ReturnType.getReturnType("Ljava/lang/String;");
        Assert.assertEquals(REFERENCE, rt.kind);
        Assert.assertEquals("java/lang/String", rt.descriptor);
        Assert.assertFalse(rt.isPrimitive());
        Assert.assertFalse(rt.isDoubleSlot());
        Assert.assertFalse(rt.isVoid());
        rt = ReturnType.getReturnType("[I");
        Assert.assertEquals(ARRAY, rt.kind);
        Assert.assertEquals("[I", rt.descriptor);
        Assert.assertFalse(rt.isPrimitive());
        Assert.assertFalse(rt.isDoubleSlot());
        Assert.assertFalse(rt.isVoid());
    }

    @Test
    public void preventingBadReloadsInterfaceChange() {
        boolean original = GlobalConfiguration.verifyReloads;
        try {
            GlobalConfiguration.verifyReloads = true;
            TypeRegistry tr = getTypeRegistry("baddata.One");
            ReloadableType rt = loadType(tr, "baddata.One");
            Assert.assertFalse(rt.loadNewVersion("002", retrieveRename("baddata.One", "baddata.OneA")));
        } finally {
            GlobalConfiguration.verifyReloads = original;
        }
    }

    @Test
    public void useReloadingAPI() throws Exception {
        TypeRegistry typeRegistry = getTypeRegistry("basic.Basic");
        byte[] sc = loadBytesForClass("basic.Basic");
        ReloadableType rtype = typeRegistry.addType("basic.Basic", sc);
        Class<?> simpleClass = rtype.getClazz();
        Result r = runUnguarded(simpleClass, "foo");
        r = runUnguarded(simpleClass, "getValue");
        Assert.assertEquals(5, r.returnValue);
        int rc = SpringLoaded.loadNewVersionOfType(rtype.getClazz(), retrieveRename("basic.Basic", "basic.Basic002"));
        Assert.assertEquals(0, rc);
        Assert.assertEquals(7, runUnguarded(simpleClass, "getValue").returnValue);
        rc = SpringLoaded.loadNewVersionOfType(rtype.getClazz().getClassLoader(), rtype.dottedtypename, retrieveRename("basic.Basic", "basic.Basic003"));
        Assert.assertEquals(0, rc);
        Assert.assertEquals(3, runUnguarded(simpleClass, "getValue").returnValue);
        // null classloader
        rc = SpringLoaded.loadNewVersionOfType(null, rtype.dottedtypename, retrieveRename("basic.Basic", "basic.Basic003"));
        Assert.assertEquals(1, rc);
        // fake typename
        rc = SpringLoaded.loadNewVersionOfType(rtype.getClazz().getClassLoader(), "a.b.C", retrieveRename("basic.Basic", "basic.Basic003"));
        Assert.assertEquals(2, rc);
    }

    @Test
    public void innerTypesLosingStaticModifier() throws Exception {
        TypeRegistry typeRegistry = getTypeRegistry("inners.Outer$Inner");
        byte[] sc = loadBytesForClass("inners.Outer$Inner");
        ReloadableType rtype = typeRegistry.addType("inners.Outer$Inner", sc);
        Class<?> simpleClass = rtype.getClazz();
        Result r = null;
        r = runUnguarded(simpleClass, "foo");
        Assert.assertEquals("foo!", r.returnValue);
        Assert.assertTrue(Modifier.isPublic(((Integer) (runUnguarded(simpleClass, "getModifiers").returnValue))));
        Assert.assertTrue(Modifier.isStatic(((Integer) (runUnguarded(simpleClass, "getModifiers").returnValue))));
        rtype.loadNewVersion("002", retrieveRename("inners.Outer$Inner", "inners.Outer2$Inner2"));
        r = runUnguarded(simpleClass, "foo");
        Assert.assertEquals("bar!", r.returnValue);
        Assert.assertTrue(Modifier.isPublic(((Integer) (runUnguarded(simpleClass, "getModifiers").returnValue))));
        Assert.assertTrue(Modifier.isStatic(((Integer) (runUnguarded(simpleClass, "getModifiers").returnValue))));
    }
}

