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


import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.ISMgr;
import org.springsource.loaded.MethodInvokerRewriter;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.TypeRegistry;
import org.springsource.loaded.test.infra.ClassPrinter;
import org.springsource.loaded.test.infra.Result;
import org.springsource.loaded.test.infra.ResultException;


/**
 * Tests that change a type by modifying/adding/removing fields.
 *
 * @author Andy Clement
 * @since 1.0
 */
public class FieldReloadingTests extends SpringLoadedTests {
    // Field 'i' is added to Add on a reload and interacted with
    @Test
    public void newFieldAdded() throws Exception {
        TypeRegistry r = getTypeRegistry("fields.Add");
        ReloadableType add = loadType(r, "fields.Add");
        Class<?> addClazz = add.getClazz();
        Object addInstance = addClazz.newInstance();
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        // ClassPrinter.print(add.bytesLoaded, true);
        add.loadNewVersion("2", retrieveRename("fields.Add", "fields.Add002"));
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setValue", 45);
        Assert.assertEquals(45, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        Assert.assertEquals(45, add.getField(addInstance, "i", false));
    }

    // Variant of the first test but uses a new instance after reloading
    @Test
    public void newFieldAddedInstance() throws Exception {
        TypeRegistry r = getTypeRegistry("fields.Add");
        ReloadableType add = loadType(r, "fields.Add");
        Class<?> addClazz = add.getClazz();
        Object addInstance = addClazz.newInstance();
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        ClassPrinter.print(add.bytesLoaded, true);
        add.loadNewVersion("2", retrieveRename("fields.Add", "fields.Add002"));
        addInstance = addClazz.newInstance();
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setValue", 45);
        Assert.assertEquals(45, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        Assert.assertEquals(45, add.getField(addInstance, "i", false));
    }

    /**
     * Do an early set (via FieldReaderWriter) after a reload, so that no prior setup will have been done on the values
     * map for that type. This checks the logic in FieldReaderWriter.setValue()
     */
    @Test
    public void earlySet() throws Exception {
        String s = "fields.S";
        TypeRegistry r = getTypeRegistry(s);
        ReloadableType add = loadType(r, s);
        Class<?> addClazz = add.getClazz();
        Object addInstance = addClazz.newInstance();
        // assertEquals(0, runOnInstance(addClazz, addInstance, "getValue").returnValue);
        add.loadNewVersion("2", retrieveRename(s, (s + "2")));
        add.setField(addInstance, "i", false, 1234);
        Assert.assertEquals(1234, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getValue").returnValue);
        // runOnInstance(addClazz, addInstance, "setValue", 45);
        // 
        // assertEquals(45, runOnInstance(addClazz, addInstance, "getValue").returnValue);
        // assertEquals(45, add.getField(addInstance, "i", false));
    }

    /**
     * Simple test working with double slotters - double and long
     */
    @Test
    public void doubleSlotFields() throws Exception {
        String d = "fields.TwoSlot";
        TypeRegistry r = getTypeRegistry(d);
        ReloadableType add = loadType(r, d);
        Class<?> clazz = add.getClazz();
        Object instance = clazz.newInstance();
        Assert.assertEquals(34.5, SpringLoadedTests.runOnInstance(clazz, instance, "getDouble").returnValue);
        Assert.assertEquals(123L, SpringLoadedTests.runOnInstance(clazz, instance, "getLong").returnValue);
        SpringLoadedTests.runOnInstance(clazz, instance, "setDouble", 1323.0);
        SpringLoadedTests.runOnInstance(clazz, instance, "setLong", 23L);
        add.loadNewVersion("2", retrieveRename(d, (d + "2")));
        Assert.assertEquals(0.0, SpringLoadedTests.runOnInstance(clazz, instance, "getDouble").returnValue);
        Assert.assertEquals(0L, SpringLoadedTests.runOnInstance(clazz, instance, "getLong").returnValue);
        SpringLoadedTests.runOnInstance(clazz, instance, "setDouble", 1323.0);
        SpringLoadedTests.runOnInstance(clazz, instance, "setLong", 23L);
        Assert.assertEquals(1323.0, SpringLoadedTests.runOnInstance(clazz, instance, "getDouble").returnValue);
        Assert.assertEquals(23L, SpringLoadedTests.runOnInstance(clazz, instance, "getLong").returnValue);
    }

    /**
     * In this test there are some fields in a reloadable type shadowing some in a non-reloadable supertype. When the
     * reloadable ones are removed the supertype ones become visible, check they are correctly accessible.
     */
    @Test
    public void setWithFieldsRevealedOnReloadHierarchyNonStatic() throws Exception {
        // String a = "fields.A"; // not reloadable!
        String b = "fields.B";
        String c = "fields.C";
        TypeRegistry r = getTypeRegistry(((b + ",") + c));
        ReloadableType btype = loadType(r, b);
        ReloadableType ctype = loadType(r, c);
        Class<?> cclazz = ctype.getClazz();
        Object cinstance = cclazz.newInstance();
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setInt", 22);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setString", "fromBfromB");
        Assert.assertEquals(22, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromBfromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        ctype.setField(cinstance, "i", false, 345);
        Assert.assertEquals(345, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals(345, ctype.getField(cinstance, "i", false));
        btype.loadNewVersion("2", retrieveRename(b, (b + "2")));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromA", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setInt", 22);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setString", "fromBfromB");
        Assert.assertEquals(22, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromBfromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
    }

    /**
     * In this test there are some fields in a reloadable type shadowing some in a non-reloadable supertype. When the
     * reloadable ones are removed the supertype ones become visible, check they are correctly accessible.
     */
    @Test
    public void setWithFieldsRevealedOnReloadHierarchyStatic() throws Exception {
        // String a = "fields.A"; // not reloadable!
        String b = "fields.Ba";
        String c = "fields.Ca";
        TypeRegistry r = getTypeRegistry(((b + ",") + c));
        ReloadableType btype = loadType(r, b);
        ReloadableType ctype = loadType(r, c);
        Class<?> cclazz = ctype.getClazz();
        Object cinstance = cclazz.newInstance();
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setInt", 22);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setString", "fromBfromB");
        Assert.assertEquals(22, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromBfromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        ctype.setField(null, "i", true, 11111);
        Assert.assertEquals(11111, ctype.getField(null, "i", true));
        Assert.assertEquals(11111, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        btype.loadNewVersion("2", retrieveRename(b, (b + "2")));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromA", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setInt", 22);
        SpringLoadedTests.runOnInstance(cclazz, cinstance, "setString", "fromBfromB");
        Assert.assertEquals(22, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
        Assert.assertEquals("fromBfromB", SpringLoadedTests.runOnInstance(cclazz, cinstance, "getString").returnValue);
        ctype.setField(null, "i", true, 12221);
        Assert.assertEquals(12221, ctype.getField(null, "i", true));
        Assert.assertEquals(12221, SpringLoadedTests.runOnInstance(cclazz, cinstance, "getInt").returnValue);
    }

    // All the other primitive field types
    @Test
    public void newFieldAdded2() throws Exception {
        TypeRegistry r = getTypeRegistry("fields.AddB");
        ReloadableType add = loadType(r, "fields.AddB");
        Class<?> addClazz = add.getClazz();
        Object addInstance = addClazz.newInstance();
        Assert.assertEquals(((short) (0)), SpringLoadedTests.runOnInstance(addClazz, addInstance, "getShort").returnValue);
        Assert.assertEquals(0L, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getLong").returnValue);
        Assert.assertEquals(false, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getBoolean").returnValue);
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(addClazz, addInstance, "getChar").returnValue);
        Assert.assertEquals(0.0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getDouble").returnValue);
        Assert.assertEquals(0.0F, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getFloat").returnValue);
        add.loadNewVersion("2", retrieveRename("fields.AddB", "fields.AddB002"));
        Assert.assertEquals(((short) (0)), SpringLoadedTests.runOnInstance(addClazz, addInstance, "getShort").returnValue);
        Assert.assertEquals(0L, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getLong").returnValue);
        Assert.assertEquals(false, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getBoolean").returnValue);
        Assert.assertEquals(((char) (0)), SpringLoadedTests.runOnInstance(addClazz, addInstance, "getChar").returnValue);
        Assert.assertEquals(0.0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getDouble").returnValue);
        Assert.assertEquals(0.0F, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getFloat").returnValue);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setShort", ((short) (451)));
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setLong", 328L);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setBoolean", true);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setChar", 'z');
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setDouble", 2222.0);
        SpringLoadedTests.runOnInstance(addClazz, addInstance, "setFloat", 12.0F);
        Assert.assertEquals(((short) (451)), SpringLoadedTests.runOnInstance(addClazz, addInstance, "getShort").returnValue);
        Assert.assertEquals(328L, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getLong").returnValue);
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getBoolean").returnValue);
        Assert.assertEquals('z', SpringLoadedTests.runOnInstance(addClazz, addInstance, "getChar").returnValue);
        Assert.assertEquals(2222.0, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getDouble").returnValue);
        Assert.assertEquals(12.0F, SpringLoadedTests.runOnInstance(addClazz, addInstance, "getFloat").returnValue);
    }

    // public void fieldRemoved() throws Exception {
    // TypeRegistry r = getTypeRegistry("fields.Removed");
    // ReloadableType add = loadType(r, "fields.Removed");
    // Class<?> addClazz = add.getClazz();
    // Object addInstance = addClazz.newInstance();
    // assertEquals(0, runOnInstance(addClazz, addInstance, "getValue").returnValue);
    // runOnInstance(addClazz, addInstance, "setValue", 45);
    // assertEquals(45, runOnInstance(addClazz, addInstance, "getValue").returnValue);
    // 
    // add.loadNewVersion("2", retrieveRename("fields.Removed", "fields.Removed002"));
    // 
    // assertEquals(0, runOnInstance(addClazz, addInstance, "getValue").returnValue);
    // 
    // runOnInstance(addClazz, addInstance, "setValue", 45);
    // 
    // assertEquals(45, runOnInstance(addClazz, addInstance, "getValue").returnValue);
    // }
    // @Test
    // public void fieldOverloading() throws Exception {
    // TypeRegistry r = getTypeRegistry("fields..*");
    // 
    // ReloadableType one = loadType(r, "fields.One");
    // ReloadableType two = loadType(r, "fields.Two");
    // 
    // Class<?> oneClazz = one.getClazz();
    // Object oneInstance = oneClazz.newInstance();
    // Class<?> twoClazz = two.getClazz();
    // Object twoInstance = twoClazz.newInstance();
    // 
    // // Field 'a' is only defined in One and 'inherited' by Two
    // assertEquals("a from One", runOnInstance(oneClazz, oneInstance, "getOneA").returnValue);
    // assertEquals("a from One", runOnInstance(twoClazz, twoInstance, "getTwoA").returnValue);
    // 
    // runOnInstance(oneClazz, oneInstance, "setOneA", "abcde");
    // assertEquals("abcde", runOnInstance(oneClazz, oneInstance, "getOneA").returnValue);
    // 
    // runOnInstance(twoClazz, twoInstance, "setOneA", "abcde");
    // assertEquals("abcde", runOnInstance(twoClazz, twoInstance, "getTwoA").returnValue);
    // 
    // // Field 'b' is defined in One and Two
    // assertEquals("b from One", runOnInstance(oneClazz, oneInstance, "getOneB").returnValue);
    // assertEquals("b from Two", runOnInstance(twoClazz, twoInstance, "getTwoB").returnValue);
    // 
    // // Field 'c' is private in One and public in Two
    // assertEquals("c from One", runOnInstance(oneClazz, oneInstance, "getOneC").returnValue);
    // assertEquals("c from Two", runOnInstance(twoClazz, twoInstance, "getTwoC").returnValue);
    // 
    // // Now... set the private field 'c' in One then try to access the field c in both One and Two
    // // Should be different if the FieldAccessor is preserving things correctly
    // runOnInstance(twoClazz, twoInstance, "setOneC", "abcde");
    // assertEquals("abcde", runOnInstance(twoClazz, twoInstance, "getOneC").returnValue);
    // assertEquals("c from Two", runOnInstance(twoClazz, twoInstance, "getTwoC").returnValue);
    // }
    // 
    // @Test
    // public void invokevirtual() throws Exception {
    // TypeRegistry typeRegistry = getTypeRegistry("virtual.CalleeOne");
    // 
    // // The first target does not define toString()
    // ReloadableType target = typeRegistry.addType("virtual.CalleeOne", loadBytesForClass("virtual.CalleeOne"));
    // 
    // Class<?> callerClazz = loadit("virtual.CallerOne", loadBytesForClass("virtual.CallerOne"));
    // 
    // // Run the initial version which does not define toString()
    // Result result = runUnguarded(callerClazz, "run");
    // // something like virtual.CalleeOne@4cee32
    // assertTrue(((String) result.returnValue).startsWith("virtual.CalleeOne@"));
    // 
    // // Load a version that does define toString()
    // target.loadNewVersion("002", retrieveRename("virtual.CalleeOne", "virtual.CalleeOne002"));
    // result = runUnguarded(callerClazz, "run");
    // assertEquals("abcd", result.returnValue);
    // 
    // // Load a version that does not define toString()
    // target.loadNewVersion("003", retrieveRename("virtual.CalleeOne", "virtual.CalleeOne003"));
    // result = runUnguarded(callerClazz, "run");
    // // something like virtual.CalleeOne@4cee32
    // assertTrue(((String) result.returnValue).startsWith("virtual.CalleeOne@"));
    // }
    // TODO [perf][crit] reduce boxing by having variants of set/get for all primitives and calling them
    // instead of the generic Object one checking fields ok after rewriting
    @Test
    public void intArrayFieldAccessing() throws Exception {
        TypeRegistry typeRegistry = TypeRegistry.getTypeRegistryFor(binLoader);
        // Configure it directly such that data.Pear is considered reloadable
        configureForTesting(typeRegistry, "data.Pear");
        // ReloadableType pear =
        typeRegistry.addType("data.Pear", loadBytesForClass("data.Pear"));
        byte[] callerbytes = loadBytesForClass("data.Banana");
        byte[] rewrittenBytes = MethodInvokerRewriter.rewrite(typeRegistry, callerbytes);
        Class<?> callerClazz = loadit("data.Banana", rewrittenBytes);
        Object o = callerClazz.newInstance();
        Result result = SpringLoadedTests.runOnInstance(callerClazz, o, "getIntArrayField");
        System.out.println(result);
        Assert.assertTrue(((result.returnValue) instanceof int[]));
        Assert.assertEquals(3, ((int[]) (result.returnValue))[1]);
        result = SpringLoadedTests.runOnInstance(callerClazz, o, "setIntArrayField", new Object[]{ new int[]{ 44, 55, 66 } });
        result = SpringLoadedTests.runOnInstance(callerClazz, o, "getIntArrayField");
        Assert.assertTrue(((result.returnValue) instanceof int[]));
        Assert.assertEquals(55, ((int[]) (result.returnValue))[1]);
    }

    @Test
    public void staticIntArrayFieldAccessing() throws Exception {
        TypeRegistry typeRegistry = TypeRegistry.getTypeRegistryFor(binLoader);
        // Configure it directly such that data.Pear is considered reloadable
        configureForTesting(typeRegistry, "data.Pear");
        typeRegistry.addType("data.Pear", loadBytesForClass("data.Pear"));
        byte[] callerbytes = loadBytesForClass("data.Banana");
        byte[] rewrittenBytes = MethodInvokerRewriter.rewrite(typeRegistry, callerbytes);
        Class<?> callerClazz = loadit("data.Banana", rewrittenBytes);
        Object o = callerClazz.newInstance();
        Result result = SpringLoadedTests.runOnInstance(callerClazz, o, "getStaticIntArrayField");
        Assert.assertTrue(((result.returnValue) instanceof int[]));
        Assert.assertEquals(33, ((int[]) (result.returnValue))[1]);
        result = SpringLoadedTests.runOnInstance(callerClazz, o, "setStaticIntArrayField", new Object[]{ new int[]{ 44, 55, 66 } });
        result = SpringLoadedTests.runOnInstance(callerClazz, o, "getStaticIntArrayField");
        Assert.assertTrue(((result.returnValue) instanceof int[]));
        Assert.assertEquals(55, ((int[]) (result.returnValue))[1]);
    }

    // The problem with default visibility fields is that the executors cannot see them.  So we create accessors for
    // these fields in the target types.  If two types in a hierarchy declare the same field, then these accessor methods will be in an
    // override relationship.  The overriding accessors will be subject to virtual dispatch - but the runtime type should be
    // correct so that the right one is called.
    @Test
    public void defaultFields() throws Exception {
        TypeRegistry tr = getTypeRegistry("accessors..*");
        ReloadableType top = tr.addType("accessors.DefaultFields", loadBytesForClass("accessors.DefaultFields"));
        ReloadableType bottom = tr.addType("accessors.DefaultFieldsSub", loadBytesForClass("accessors.DefaultFieldsSub"));
        ClassPrinter.print(top.bytesLoaded);
        Object topInstance = top.getClazz().newInstance();
        Result result = SpringLoadedTests.runOnInstance(top.getClazz(), topInstance, "a");
        Assert.assertEquals(1, result.returnValue);
        Object botInstance = bottom.getClazz().newInstance();
        result = SpringLoadedTests.runOnInstance(bottom.getClazz(), botInstance, "a");
        Assert.assertEquals(1, result.returnValue);
        result = SpringLoadedTests.runOnInstance(bottom.getClazz(), botInstance, "b");
        Assert.assertEquals(2, result.returnValue);
    }

    // Tests allowing for inherited fields from a non-reloadable type.  Reload a new version of the class
    // that shadows the method in the non-reloadable type.
    @Test
    public void inheritedNonReloadable1() throws Exception {
        String top = "fields.ReloadableTop";
        TypeRegistry tr = getTypeRegistry(top);
        ReloadableType rTop = tr.addType(top, loadBytesForClass(top));
        Class<?> cTop = rTop.getClazz();
        Object iTop = cTop.newInstance();
        // The field 'I' is in code that is not reloadable.
        // First lets use the accessors that are also not in reloadable code
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_NotReloadable").returnValue);
        SpringLoadedTests.runOnInstance(cTop, iTop, "setI_ReloadableTop", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_NotReloadable").returnValue);
        // Now use accessors that are in reloadable code
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_ReloadableTop").returnValue);
        SpringLoadedTests.runOnInstance(cTop, iTop, "setI_ReloadableTop", 3);
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_ReloadableTop").returnValue);
        // Now reload the ReloadableTop class and introduce that field i which will shadow the one in the supertype
        rTop.loadNewVersion("2", retrieveRename(top, (top + "002")));
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_ReloadableTop").returnValue);
        SpringLoadedTests.runOnInstance(cTop, iTop, "setI_ReloadableTop", 44);
        Assert.assertEquals(44, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_ReloadableTop").returnValue);
        // Now access the older version of the field still there on the non-reloadable type
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(cTop, iTop, "getI_NotReloadable").returnValue);
    }

    // Now a pair of types with one field initially.  New field added - original should continue to be accessed
    // through the GETFIELD, the new one via the map
    @Test
    public void inheritedNonReloadable2() throws Exception {
        String top = "fields.X";
        TypeRegistry tr = getTypeRegistry(top);
        ReloadableType rTop = tr.addType(top, loadBytesForClass(top));
        Class<?> cTop = rTop.getClazz();
        Object iTop = cTop.newInstance();
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(cTop, iTop, "getI").returnValue);
        SpringLoadedTests.runOnInstance(cTop, iTop, "setI", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(cTop, iTop, "getI").returnValue);
        // // Now reload the ReloadableTop class and introduce that field i which will shadow the one in the supertype
        rTop.loadNewVersion("2", retrieveRename(top, (top + "002")));
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(cTop, iTop, "getJ").returnValue);
        SpringLoadedTests.runOnInstance(cTop, iTop, "setJ", 44);
        Assert.assertEquals(44, SpringLoadedTests.runOnInstance(cTop, iTop, "getJ").returnValue);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(cTop, iTop, "getI").returnValue);
        ISMgr fa = getFieldAccessor(iTop);
        assertContains("j=44", fa.toString());
        assertDoesNotContain("i=", fa.toString());
    }

    @Test
    public void changingFieldFromNonstaticToStatic() throws Exception {
        String y = "fields.Y";
        TypeRegistry tr = getTypeRegistry(y);
        ReloadableType rtype = tr.addType(y, loadBytesForClass(y));
        Class<?> clazz = rtype.getClazz();
        Object instance = clazz.newInstance();
        Object instance2 = clazz.newInstance();
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        rtype.loadNewVersion("2", retrieveRename(y, (y + "002")));
        SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        String staticFieldMapData = getStaticFieldsMap(clazz);
        assertContains("j=4", staticFieldMapData);
        SpringLoadedTests.runOnInstance(clazz, instance2, "setJ", 404);
        Assert.assertEquals(404, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        Assert.assertEquals(404, SpringLoadedTests.runOnInstance(clazz, instance2, "getJ").returnValue);
    }

    /**
     * An instance field is accessed from a subtype, then the supertype is reloaded where the field has been made
     * static. Should be an error when the subtype attempts to access it again.
     */
    @Test
    public void changingFieldFromNonstaticToStaticWithSubtypes() throws Exception {
        String y = "fields.Ya";
        String z = "fields.Za";
        TypeRegistry tr = getTypeRegistry(((y + ",") + z));
        ReloadableType rtype = tr.addType(y, loadBytesForClass(y));
        ReloadableType ztype = tr.addType(z, loadBytesForClass(z));
        Class<?> clazz = ztype.getClazz();
        Object instance = clazz.newInstance();
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        rtype.loadNewVersion("2", retrieveRename(y, (y + "002")));
        try {
            SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
            Assert.fail("should not have worked, field has changed from non-static to static");
        } catch (ResultException re) {
            Assert.assertTrue(((re.getCause()) instanceof InvocationTargetException));
            Assert.assertTrue(((re.getCause().getCause()) instanceof IncompatibleClassChangeError));
            Assert.assertEquals("Expected non-static field fields.Za.j", re.getCause().getCause().getMessage());
        }
        // Now should be an IncompatibleClassChangeError
        try {
            SpringLoadedTests.runOnInstance(clazz, instance, "getJ");
            Assert.fail("should not have worked, field has changed from non-static to static");
        } catch (ResultException re) {
            Assert.assertTrue(((re.getCause()) instanceof InvocationTargetException));
            Assert.assertTrue(((re.getCause().getCause()) instanceof IncompatibleClassChangeError));
            Assert.assertEquals("Expected non-static field fields.Za.j", re.getCause().getCause().getMessage());
        }
    }

    /**
     * A static field is accessed from a subtype, then the supertype is reloaded where the field has been made
     * non-static. Should be an error when the subtype attempts to access it again.
     */
    @Test
    public void changingFieldFromStaticToNonstaticWithSubtypes() throws Exception {
        String y = "fields.Yb";
        String z = "fields.Zb";
        TypeRegistry tr = getTypeRegistry(((y + ",") + z));
        ReloadableType rtype = tr.addType(y, loadBytesForClass(y));
        ReloadableType ztype = tr.addType(z, loadBytesForClass(z));
        Class<?> clazz = ztype.getClazz();
        Object instance = clazz.newInstance();
        Assert.assertEquals(5, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(clazz, instance, "getJ").returnValue);
        rtype.loadNewVersion("2", retrieveRename(y, (y + "002")));
        // Now should be an IncompatibleClassChangeError
        try {
            SpringLoadedTests.runOnInstance(clazz, instance, "setJ", 4);
            Assert.fail("Should not have worked, field has changed from static to non-static");
        } catch (ResultException re) {
            Assert.assertTrue(((re.getCause()) instanceof InvocationTargetException));
            Assert.assertTrue(((re.getCause().getCause()) instanceof IncompatibleClassChangeError));
            // When compiled with AspectJ vs Eclipse JDT the GETSTATIC actually varies.
            // With AspectJ it is: PUTSTATIC fields/Yb.j : I
            // With JDT (4.3) it is: PUTSTATIC fields/Zb.j : I
            // hence the error is different
            Assert.assertEquals("Expected static field fields.Zb.j", re.getCause().getCause().getMessage());
        }
        // Now should be an IncompatibleClassChangeError
        try {
            SpringLoadedTests.runOnInstance(clazz, instance, "getJ");
            Assert.fail("Should not have worked, field has changed from static to non-static");
        } catch (ResultException re) {
            Assert.assertTrue(((re.getCause()) instanceof InvocationTargetException));
            Assert.assertTrue(((re.getCause().getCause()) instanceof IncompatibleClassChangeError));
            // When compiled with AspectJ vs Eclipse JDT the GETSTATIC actually varies.
            // With AspectJ it is: GETSTATIC fields/Yb.j : I
            // With JDT (4.3) it is: GETSTATIC fields/Zb.j : I
            // hence the error is different
            Assert.assertEquals("Expected static field fields.Zb.j", re.getCause().getCause().getMessage());
        }
    }

    /**
     * Load a hierarchy of types. There is a field 'i' in both P and Q. R returns 'i'. Initially it returns Q.i but once
     * Q has been reloaded it should be returning P.i
     */
    @Test
    public void removingFieldsShadowingSuperFields() throws Exception {
        String p = "fields.P";
        String q = "fields.Q";
        String r = "fields.R";
        TypeRegistry tr = getTypeRegistry(((((p + ",") + q) + ",") + r));
        // ReloadableType ptype =
        tr.addType(p, loadBytesForClass(p));
        ReloadableType qtype = tr.addType(q, loadBytesForClass(q));
        ReloadableType rtype = tr.addType(r, loadBytesForClass(r));
        Class<?> rClazz = rtype.getClazz();
        Object rInstance = rClazz.newInstance();
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        qtype.loadNewVersion("2", retrieveRename(q, (q + "2")));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
    }

    /**
     * Similar to previous but now fields.Pa is not reloadable.
     */
    @Test
    public void removingFieldsShadowingSuperFields2() throws Exception {
        // String p = "fields.Pa";
        String q = "fields.Qa";
        String r = "fields.Ra";
        TypeRegistry tr = getTypeRegistry(((q + ",") + r));
        // ReloadableType ptype = tr.addType(p, loadBytesForClass(p));
        ReloadableType qtype = tr.addType(q, loadBytesForClass(q));
        ReloadableType rtype = tr.addType(r, loadBytesForClass(r));
        Class<?> rClazz = rtype.getClazz();
        Object rInstance = rClazz.newInstance();
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        qtype.loadNewVersion("2", retrieveRename(q, (q + "2")));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
    }

    /**
     * Here the field in Qc is shadowing (same name) a field in the interface. Once the new version of Qc is loaded that
     * doesn't define the field, this exposes the field in the interface but it is static and as we are running a
     * GETFIELD operation in Rc, it is illegal (IncompatibleClassChangeError)
     */
    @Test
    public void removingFieldsShadowingInterfaceFields1() throws Exception {
        // String i = "fields.Ic";
        String q = "fields.Qc";
        String r = "fields.Rc";
        TypeRegistry tr = getTypeRegistry(((q + ",") + r));
        ReloadableType qtype = tr.addType(q, loadBytesForClass(q));
        ReloadableType rtype = tr.addType(r, loadBytesForClass(r));
        Class<?> rClazz = rtype.getClazz();
        Object rInstance = rClazz.newInstance();
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getNumber").returnValue);
        qtype.loadNewVersion("2", retrieveRename(q, (q + "2")));
        try {
            SpringLoadedTests.runOnInstance(rClazz, rInstance, "getNumber");
            Assert.fail();
        } catch (ResultException re) {
            Throwable e = re.getCause();
            Assert.assertTrue(((e.getCause()) instanceof IncompatibleClassChangeError));
            Assert.assertEquals("Expected non-static field fields.Rc.number", e.getCause().getMessage());
        }
    }

    /**
     * Both 'normal' reloadable field access and reflective field access will use the same set methods on
     * ReloadableType. In the reflection case the right FieldAccessor must be discovered, in the normal case it is
     * passed in. This test checks that using either route behaves - exercising the method directly and not through
     * reflection.
     */
    @Test
    public void accessingFieldsThroughReloadableType() throws Exception {
        String p = "fields.P";
        String q = "fields.Q";
        String r = "fields.R";
        TypeRegistry tr = getTypeRegistry(((((p + ",") + q) + ",") + r));
        ReloadableType ptype = tr.addType(p, loadBytesForClass(p));
        ReloadableType qtype = tr.addType(q, loadBytesForClass(q));
        ReloadableType rtype = tr.addType(r, loadBytesForClass(r));
        Class<?> rClazz = rtype.getClazz();
        Object rInstance = rClazz.newInstance();
        // Before reloading, check both routes get to the same field:
        Assert.assertEquals(2, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        Assert.assertEquals(2, rtype.getField(rInstance, "i", false));
        // Now mess with it via one route and check result via the other:
        rtype.setField(rInstance, "i", false, 44);
        Assert.assertEquals(44, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        qtype.loadNewVersion("2", retrieveRename(q, (q + "2")));
        // After reloading, check both routes get to the same field:
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        Assert.assertEquals(1, ptype.getField(rInstance, "i", false));
        // Now mess with it via one route and check result via the other:
        rtype.setField(rInstance, "i", false, 357);
        Assert.assertEquals(357, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
    }

    /**
     * Fields are changed from int>String and String>int. When this happens we should remove the int value we have and
     * treat the field as unset.
     */
    @Test
    public void changingFieldType() throws Exception {
        String p = "fields.T";
        TypeRegistry tr = getTypeRegistry(p);
        ReloadableType type = tr.addType(p, loadBytesForClass(p));
        Class<?> rClazz = type.getClazz();
        Object rInstance = rClazz.newInstance();
        // Before reloading, check both routes get to the same field
        Assert.assertEquals(345, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        Assert.assertEquals(345, type.getField(rInstance, "i", false));
        Assert.assertEquals("stringValue", SpringLoadedTests.runOnInstance(rClazz, rInstance, "getJ").returnValue);
        Assert.assertEquals("stringValue", type.getField(rInstance, "j", false));
        type.loadNewVersion("2", retrieveRename(p, (p + "2")));
        // On the reload the field changed type, so we now check it was reset to default value
        Assert.assertEquals(null, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getI").returnValue);
        Assert.assertEquals(null, type.getField(rInstance, "i", false));
        Assert.assertEquals(0, SpringLoadedTests.runOnInstance(rClazz, rInstance, "getJ").returnValue);
        Assert.assertEquals(0, type.getField(rInstance, "j", false));
    }

    /**
     * Two types in a hierarchy, the field is initially accessed in the supertype but an interface that the subtype
     * implements then introduces that field on a reload. Although it is introduced in the interface, the bytecode
     * reference to the field is directly to that in the supertype - so the interface one will not be found (the subtype
     * would be pointing at the interface one if B was recompiled with this setup).
     */
    @Test
    public void introducingStaticFieldInInterface() throws Exception {
        String a = "sfields.A";
        String b = "sfields.B";
        String i = "sfields.I";
        TypeRegistry tr = getTypeRegistry(((((a + ",") + b) + ",") + i));
        ReloadableType itype = tr.addType(i, loadBytesForClass(i));
        ReloadableType atype = tr.addType(a, loadBytesForClass(a));
        ReloadableType btype = tr.addType(b, loadBytesForClass(b));
        Class<?> bClazz = btype.getClazz();
        Object bInstance = bClazz.newInstance();
        // No changes, should find i in the supertype A
        Assert.assertEquals(45, SpringLoadedTests.runOnInstance(bClazz, bInstance, "getI").returnValue);
        Assert.assertEquals(45, atype.getField(bInstance, "i", true));
        // Reload i which now has a definition of the field
        itype.loadNewVersion("2", retrieveRename(i, (i + "2")));
        // Introduced in the interface but the instruction was GETSTATIC A.i so we won't find it on the interface
        // Even if we did find it on the interface the value would be zero because the staticinitializer for I2
        // has not run
        Assert.assertEquals(45, SpringLoadedTests.runOnInstance(bClazz, bInstance, "getI").returnValue);
        Assert.assertEquals(45, atype.getField(bInstance, "i", true));
    }

    /**
     * In this test several fields are setup and queried, then on reload their type is changed - ensure the results are
     * as expected.
     */
    @Test
    public void testingCompatibilityOnFieldTypeChanges() throws Exception {
        String a = "fields.TypeChanging";
        String b = "fields.Middle";
        TypeRegistry tr = getTypeRegistry(((a + ",") + b));
        ReloadableType type = tr.addType(a, loadBytesForClass(a));
        tr.addType(b, loadBytesForClass(b));
        Class<?> clazz = type.getClazz();
        Object instance = clazz.newInstance();
        // Should be fine
        Assert.assertEquals("SubInstance", toString(SpringLoadedTests.runOnInstance(clazz, instance, "getSuper").returnValue));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(clazz, instance, "getI").returnValue);
        Assert.assertEquals(34L, SpringLoadedTests.runOnInstance(clazz, instance, "getLong").returnValue);
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(clazz, instance, "getBoolean").returnValue);
        Assert.assertEquals(((short) (32)), SpringLoadedTests.runOnInstance(clazz, instance, "getShort").returnValue);
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(clazz, instance, "getChar").returnValue);
        Assert.assertEquals(2.0F, SpringLoadedTests.runOnInstance(clazz, instance, "getFloat").returnValue);
        Assert.assertEquals(3.141, SpringLoadedTests.runOnInstance(clazz, instance, "getDouble").returnValue);
        Assert.assertEquals(((byte) (255)), SpringLoadedTests.runOnInstance(clazz, instance, "getByte").returnValue);
        Assert.assertEquals("{1,2,3}", toString(SpringLoadedTests.runOnInstance(clazz, instance, "getWasArray").returnValue));
        Assert.assertEquals("abc", toString(SpringLoadedTests.runOnInstance(clazz, instance, "getWasNotArray").returnValue));
        // This changes all the field types
        type.loadNewVersion("2", retrieveRename(a, (a + "2")));
        Assert.assertEquals("SubInstance", toString(SpringLoadedTests.runOnInstance(clazz, instance, "getSuper").returnValue));
        Assert.assertEquals(1, SpringLoadedTests.runOnInstance(clazz, instance, "getI").returnValue);
        Assert.assertEquals(34L, SpringLoadedTests.runOnInstance(clazz, instance, "getLong").returnValue);
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(clazz, instance, "getBoolean").returnValue);
        Assert.assertEquals(((short) (32)), SpringLoadedTests.runOnInstance(clazz, instance, "getShort").returnValue);
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(clazz, instance, "getChar").returnValue);
        Assert.assertEquals(2.0F, SpringLoadedTests.runOnInstance(clazz, instance, "getFloat").returnValue);
        Assert.assertEquals(3.141, SpringLoadedTests.runOnInstance(clazz, instance, "getDouble").returnValue);
        Assert.assertEquals(((byte) (255)), SpringLoadedTests.runOnInstance(clazz, instance, "getByte").returnValue);
        Assert.assertEquals("0", toString(SpringLoadedTests.runOnInstance(clazz, instance, "getWasArray").returnValue));
        Assert.assertEquals(null, toString(SpringLoadedTests.runOnInstance(clazz, instance, "getWasNotArray").returnValue));
    }

    // bogus test - the inlining of constants means that second version of the class already returns the value...
    // @Test
    // public void newFieldOnInterface() throws Exception {
    // String intface = "sfields.X";
    // String impl = "sfields.Y";
    // TypeRegistry tr = getTypeRegistry(intface + "," + impl);
    // ReloadableType intType = tr.addType(intface, loadBytesForClass(intface));
    // ReloadableType implType = tr.addType(impl, loadBytesForClass(impl));
    // 
    // Class<?> implClazz = implType.getClazz();
    // Object implInstance = implClazz.newInstance();
    // 
    // assertEquals(34, runOnInstance(implClazz, implInstance, "getnum").returnValue);
    // 
    // intType.loadNewVersion("2", retrieveRename(intface, intface + "2"));
    // implType.loadNewVersion("2", retrieveRename(impl, impl + "2", "sfields.X2:sfields.X"));
    // 
    // assertEquals(34, runOnInstance(implClazz, implInstance, "getnum").returnValue);
    // }
    @Test
    public void modifyingStaticFieldsInHierarchy() throws Exception {
        String c = "sfields.C";
        String d = "sfields.D";
        String e = "sfields.E";
        TypeRegistry tr = getTypeRegistry(((((c + ",") + d) + ",") + e));
        // ReloadableType ctype =
        tr.addType(c, loadBytesForClass(c));
        ReloadableType dtype = tr.addType(d, loadBytesForClass(d));
        ReloadableType etype = tr.addType(e, loadBytesForClass(e));
        Class<?> eClazz = etype.getClazz();
        Object eInstance = eClazz.newInstance();
        // No changes, the field in D should be used
        Assert.assertEquals(4, SpringLoadedTests.runOnInstance(eClazz, eInstance, "getI").returnValue);
        Assert.assertEquals(4, etype.getField(null, "i", true));
        etype.setField(null, "i", true, 123);
        Assert.assertEquals(123, SpringLoadedTests.runOnInstance(eClazz, eInstance, "getI").returnValue);
        // Reload d which removes the field and makes the variant in C visible
        dtype.loadNewVersion("2", retrieveRename(d, (d + "2")));
        Assert.assertEquals(3, SpringLoadedTests.runOnInstance(eClazz, eInstance, "getI").returnValue);
        Assert.assertEquals(3, etype.getField(null, "i", true));
        // Now interact with that field in C that has become visible
        etype.setField(null, "i", true, 12345);
        Assert.assertEquals(12345, SpringLoadedTests.runOnInstance(eClazz, eInstance, "getI").returnValue);
        Assert.assertEquals(12345, etype.getField(null, "i", true));
        // Use the setter
        SpringLoadedTests.runOnInstance(eClazz, eInstance, "setI", 6789);
        Assert.assertEquals(6789, SpringLoadedTests.runOnInstance(eClazz, eInstance, "getI").returnValue);
        Assert.assertEquals(6789, etype.getField(null, "i", true));
    }

    /**
     * Switch some fields from their primitive forms to their boxed forms, check data is preserved, then switch them
     * back and check data is preserved.
     */
    @Test
    public void switchToFromBoxing() throws Exception {
        String e = "fields.E";
        TypeRegistry tr = getTypeRegistry(e);
        ReloadableType type = tr.addType(e, loadBytesForClass(e));
        Class<?> clazz = type.getClazz();
        Object rInstance = clazz.newInstance();
        // Before reloading, check both routes get to the same field
        Assert.assertEquals(100, SpringLoadedTests.runOnInstance(clazz, rInstance, "getInt").returnValue);
        Assert.assertEquals(100, type.getField(rInstance, "i", false));
        Assert.assertEquals(((short) (200)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getShort").returnValue);
        Assert.assertEquals(((short) (200)), type.getField(rInstance, "s", false));
        Assert.assertEquals(324L, SpringLoadedTests.runOnInstance(clazz, rInstance, "getLong").returnValue);
        Assert.assertEquals(324L, type.getField(rInstance, "j", false));
        Assert.assertEquals(2.5, SpringLoadedTests.runOnInstance(clazz, rInstance, "getDouble").returnValue);
        Assert.assertEquals(2.5, type.getField(rInstance, "d", false));
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(clazz, rInstance, "getBoolean").returnValue);
        Assert.assertEquals(true, type.getField(rInstance, "z", false));
        Assert.assertEquals(32.0F, SpringLoadedTests.runOnInstance(clazz, rInstance, "getFloat").returnValue);
        Assert.assertEquals(32.0F, type.getField(rInstance, "f", false));
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(clazz, rInstance, "getChar").returnValue);
        Assert.assertEquals('a', type.getField(rInstance, "c", false));
        Assert.assertEquals(((byte) (255)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getByte").returnValue);
        Assert.assertEquals(((byte) (255)), type.getField(rInstance, "b", false));
        type.loadNewVersion("2", retrieveRename(e, (e + "2")));
        // Now all boxed versions
        Assert.assertEquals(100, SpringLoadedTests.runOnInstance(clazz, rInstance, "getInt").returnValue);
        Assert.assertEquals(100, type.getField(rInstance, "i", false));
        Assert.assertEquals(((short) (200)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getShort").returnValue);
        Assert.assertEquals(((short) (200)), type.getField(rInstance, "s", false));
        Assert.assertEquals(324L, SpringLoadedTests.runOnInstance(clazz, rInstance, "getLong").returnValue);
        Assert.assertEquals(324L, type.getField(rInstance, "j", false));
        Assert.assertEquals(2.5, SpringLoadedTests.runOnInstance(clazz, rInstance, "getDouble").returnValue);
        Assert.assertEquals(2.5, type.getField(rInstance, "d", false));
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(clazz, rInstance, "getBoolean").returnValue);
        Assert.assertEquals(true, type.getField(rInstance, "z", false));
        Assert.assertEquals(32.0F, SpringLoadedTests.runOnInstance(clazz, rInstance, "getFloat").returnValue);
        Assert.assertEquals(32.0F, type.getField(rInstance, "f", false));
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(clazz, rInstance, "getChar").returnValue);
        Assert.assertEquals('a', type.getField(rInstance, "c", false));
        Assert.assertEquals(((byte) (255)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getByte").returnValue);
        Assert.assertEquals(((byte) (255)), type.getField(rInstance, "b", false));
        // revert to unboxed
        type.loadNewVersion("3", loadBytesForClass(e));
        Assert.assertEquals(100, SpringLoadedTests.runOnInstance(clazz, rInstance, "getInt").returnValue);
        Assert.assertEquals(100, type.getField(rInstance, "i", false));
        Assert.assertEquals(((short) (200)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getShort").returnValue);
        Assert.assertEquals(((short) (200)), type.getField(rInstance, "s", false));
        Assert.assertEquals(324L, SpringLoadedTests.runOnInstance(clazz, rInstance, "getLong").returnValue);
        Assert.assertEquals(324L, type.getField(rInstance, "j", false));
        Assert.assertEquals(2.5, SpringLoadedTests.runOnInstance(clazz, rInstance, "getDouble").returnValue);
        Assert.assertEquals(2.5, type.getField(rInstance, "d", false));
        Assert.assertEquals(true, SpringLoadedTests.runOnInstance(clazz, rInstance, "getBoolean").returnValue);
        Assert.assertEquals(true, type.getField(rInstance, "z", false));
        Assert.assertEquals(32.0F, SpringLoadedTests.runOnInstance(clazz, rInstance, "getFloat").returnValue);
        Assert.assertEquals(32.0F, type.getField(rInstance, "f", false));
        Assert.assertEquals('a', SpringLoadedTests.runOnInstance(clazz, rInstance, "getChar").returnValue);
        Assert.assertEquals('a', type.getField(rInstance, "c", false));
        Assert.assertEquals(((byte) (255)), SpringLoadedTests.runOnInstance(clazz, rInstance, "getByte").returnValue);
        Assert.assertEquals(((byte) (255)), type.getField(rInstance, "b", false));
    }

    @Test
    public void ctorReloadWithNewField() throws Exception {
        String y = "ctors.JR";
        TypeRegistry tr = getTypeRegistry(y);
        ReloadableType rtype = tr.addType(y, loadBytesForClass(y));
        Class<?> clazz = rtype.getClazz();
        Object instance = runStaticUnguarded(clazz, "getInstance").returnValue;
        Assert.assertEquals("hello", SpringLoadedTests.runOnInstance(clazz, instance, "printMessage").returnValue);
        rtype.loadNewVersion("2", retrieveRename(y, (y + "2")));
        Assert.assertEquals("goodbye", SpringLoadedTests.runOnInstance(clazz, instance, "printMessage").returnValue);
        Object instance2 = runStaticUnguarded(clazz, "getInstance").returnValue;
        Object ret = SpringLoadedTests.runOnInstance(clazz, instance2, "getFieldReflectively").returnValue;
        Assert.assertEquals(34, ret);
        ret = SpringLoadedTests.runOnInstance(clazz, instance2, "setFieldReflectively", 99).returnValue;
        ret = SpringLoadedTests.runOnInstance(clazz, instance2, "getFieldReflectively").returnValue;
        Assert.assertEquals(99, ret);
    }
}

