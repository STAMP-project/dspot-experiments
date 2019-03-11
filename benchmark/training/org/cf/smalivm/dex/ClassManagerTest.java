package org.cf.smalivm.dex;


import CommonTypes.INTEGER;
import CommonTypes.STRING;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualArray;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualPrimitive;
import org.cf.smalivm.type.VirtualType;
import org.junit.Assert;
import org.junit.Test;

import static CommonTypes.STRING;


public class ClassManagerTest {
    private static final String TEST_DIRECTORY = "resources/test/smali";

    private static ClassManager manager;

    @Test
    public void canGetVirtualClassFromString() {
        VirtualClass virtualClass = ClassManagerTest.manager.getVirtualClass(STRING);
        Assert.assertEquals(STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualClassFromClass() {
        VirtualClass virtualClass = ClassManagerTest.manager.getVirtualClass(String.class);
        Assert.assertEquals(STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualPrimitiveFromString() {
        VirtualType virtualType = ClassManagerTest.manager.getVirtualType(INTEGER);
        Assert.assertEquals(INTEGER, virtualType.getName());
        Assert.assertTrue((virtualType instanceof VirtualPrimitive));
    }

    @Test
    public void canGetVirtualPrimitiveFromClass() {
        VirtualType virtualType = ClassManagerTest.manager.getVirtualType(INTEGER);
        Assert.assertEquals(INTEGER, virtualType.getName());
        Assert.assertTrue((virtualType instanceof VirtualPrimitive));
    }

    @Test
    public void canGetVirtualArrayFromString() {
        String typeSignature = "[" + (STRING);
        VirtualType virtualType = ClassManagerTest.manager.getVirtualType(typeSignature);
        Assert.assertEquals(typeSignature, virtualType.getName());
        Assert.assertTrue((virtualType instanceof VirtualArray));
    }

    @Test
    public void canGetVirtualArrayFromClass() {
        VirtualType virtualType = ClassManagerTest.manager.getVirtualType(String[].class);
        Assert.assertEquals(("[" + (STRING)), virtualType.getName());
        Assert.assertTrue((virtualType instanceof VirtualArray));
    }
}

