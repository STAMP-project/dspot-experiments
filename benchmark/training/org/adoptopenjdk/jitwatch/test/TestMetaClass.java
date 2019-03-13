package org.adoptopenjdk.jitwatch.test;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.adoptopenjdk.jitwatch.model.IMetaMember;
import org.adoptopenjdk.jitwatch.model.MemberSignatureParts;
import org.adoptopenjdk.jitwatch.model.MetaClass;
import org.adoptopenjdk.jitwatch.model.MetaConstructor;
import org.adoptopenjdk.jitwatch.model.MetaMethod;
import org.adoptopenjdk.jitwatch.model.MetaPackage;
import org.adoptopenjdk.jitwatch.model.bytecode.ClassBC;
import org.adoptopenjdk.jitwatch.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestMetaClass {
    // test constructor
    public TestMetaClass() {
    }

    @Test
    public void testGetMemberFromSignature1() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "primitiveReturnPrimitiveParam";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ int.class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        String testRetType = "int";
        List<String> paramList = new ArrayList<>();
        paramList.add("int");
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testGetMemberFromSignature2() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "voidReturnPrimitiveParam";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ int.class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        String testRetType = S_TYPE_NAME_VOID;
        List<String> paramList = new ArrayList<>();
        paramList.add("int");
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testGetMemberFromSignature3() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "voidReturnNoParams";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[0]);
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        String testRetType = S_TYPE_NAME_VOID;
        List<String> paramList = new ArrayList<>();
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testGetMemberFromSignature4() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "objectReturnObjectParam";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ String.class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        String testRetType = "java.lang.String";
        List<String> paramList = new ArrayList<>();
        paramList.add("java.lang.String");
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testGetMemberFromSignature5() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "arrayReturnArrayParam";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ int[].class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        String testRetType = "[Ljava.lang.String;";
        List<String> paramList = new ArrayList<>();
        paramList.add("[I");
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testGetMemberFromSignature6() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "<init>";
        Constructor<?> constructor = TestClassWithGenerics.class.getDeclaredConstructor(new Class[0]);
        MetaConstructor testConstructor = new MetaConstructor(constructor, metaClass);
        metaClass.addMember(testConstructor);
        String testRetType = "TestClassWithGenerics";
        List<String> paramList = new ArrayList<>();
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromParts(metaClass.getFullyQualifiedName(), testMethodName, testRetType, paramList));
        Assert.assertNotNull(result);
        Assert.assertEquals(testConstructor.toString(), result.toString());
    }

    @Test
    public void testRegressionGenericSubstitution() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "genericReturnAndParams";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ Object.class, String.class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        IMetaMember result = metaClass.getMemberForSignature(MemberSignatureParts.fromBytecodeSignature(metaClass.getFullyQualifiedName(), "public static <T extends java.lang.Object> T genericReturnAndParams(T, java.lang.String);"));
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testRegressionGenericDeclaredAtClassLevel() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "genericReturnDeclaredOnClass";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[0]);
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(metaClass.getFullyQualifiedName(), "public K genericReturnDeclaredOnClass();");
        ClassBC classBytecode = new ClassBC(TestClassWithGenerics.class.getName());
        classBytecode.addGenericsMapping("K", "java.lang.Object");
        msp.setClassBC(classBytecode);
        IMetaMember result = metaClass.getMemberForSignature(msp);
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testRegressionGenericWildcardReturnType() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "genericReturnTypeWildcard";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[0]);
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(metaClass.getFullyQualifiedName(), "public java.lang.Class<?>[] genericReturnTypeWildcard();");
        IMetaMember result = metaClass.getMemberForSignature(msp);
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testRegressionGenericWildcardParamType() throws NoSuchMethodException, SecurityException {
        String thisClassName = "TestClassWithGenerics";
        MetaPackage metaPackage = new MetaPackage(StringUtil.getPackageName(thisClassName));
        MetaClass metaClass = new MetaClass(metaPackage, StringUtil.getUnqualifiedClassName(thisClassName));
        String testMethodName = "genericParamTypeWildcard";
        Method method = TestClassWithGenerics.class.getDeclaredMethod(testMethodName, new Class[]{ Class[].class });
        MetaMethod testMethod = new MetaMethod(method, metaClass);
        metaClass.addMember(testMethod);
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(metaClass.getFullyQualifiedName(), "public void genericParamTypeWildcard(java.lang.Class<?>[]);");
        IMetaMember result = metaClass.getMemberForSignature(msp);
        Assert.assertNotNull(result);
        Assert.assertEquals(testMethod.toString(), result.toString());
    }

    @Test
    public void testRegressionDefaultPackageParamMatchPrimitive() throws Throwable {
        String className = "FooClassInDefaultPackage";
        Class<?> defaultPackageTestClass = Class.forName(className);
        Class<?> defaultPackageTestClassParam = int.class;
        String expectedParamClassName = "int";
        MetaClass metaClass = new MetaClass(new MetaPackage(S_EMPTY), className);
        Method getterPrimitiveParam = defaultPackageTestClass.getDeclaredMethod("getPrimitiveParam", new Class[]{  });
        MetaMethod metaMethodGetPrimitiveParam = new MetaMethod(getterPrimitiveParam, metaClass);
        String[] paramTypeNamesGetPrimitiveParam = metaMethodGetPrimitiveParam.getParamTypeNames();
        Assert.assertEquals(0, paramTypeNamesGetPrimitiveParam.length);
        String returnTypeNameGetPrimitiveParam = metaMethodGetPrimitiveParam.getReturnTypeName();
        Assert.assertEquals(expectedParamClassName, returnTypeNameGetPrimitiveParam);
        Method setterPrimitiveParam = defaultPackageTestClass.getDeclaredMethod("setPrimitiveParam", new Class[]{ defaultPackageTestClassParam });
        MetaMethod metaMethodSetPrimitiveParam = new MetaMethod(setterPrimitiveParam, metaClass);
        String[] paramTypeNamesSetPrimitiveParam = metaMethodSetPrimitiveParam.getParamTypeNames();
        Assert.assertEquals(1, paramTypeNamesSetPrimitiveParam.length);
        Assert.assertEquals(expectedParamClassName, paramTypeNamesSetPrimitiveParam[0]);
        String returnTypeNameSetParam = metaMethodSetPrimitiveParam.getReturnTypeName();
        Assert.assertEquals(S_TYPE_NAME_VOID, returnTypeNameSetParam);
    }

    @Test
    public void testRegressionDefaultPackageParamMatchClass() throws Throwable {
        String className = "FooClassInDefaultPackage";
        String paramClassName = "IsUsedForTestingDefaultPackage";
        Class<?> defaultPackageTestClass = Class.forName(className);
        Class<?> defaultPackageTestClassParam = Class.forName(paramClassName);
        MetaClass metaClass = new MetaClass(new MetaPackage(S_EMPTY), className);
        Method getterPrimitiveParam = defaultPackageTestClass.getDeclaredMethod("getClassParam", new Class[]{  });
        MetaMethod metaMethodGetPrimitiveParam = new MetaMethod(getterPrimitiveParam, metaClass);
        String[] paramTypeNamesGetPrimitiveParam = metaMethodGetPrimitiveParam.getParamTypeNames();
        Assert.assertEquals(0, paramTypeNamesGetPrimitiveParam.length);
        String returnTypeNameGetPrimitiveParam = metaMethodGetPrimitiveParam.getReturnTypeName();
        Assert.assertEquals(paramClassName, returnTypeNameGetPrimitiveParam);
        Method setterPrimitiveParam = defaultPackageTestClass.getDeclaredMethod("setClassParam", new Class[]{ defaultPackageTestClassParam });
        MetaMethod metaMethodSetPrimitiveParam = new MetaMethod(setterPrimitiveParam, metaClass);
        String[] paramTypeNamesSetPrimitiveParam = metaMethodSetPrimitiveParam.getParamTypeNames();
        Assert.assertEquals(1, paramTypeNamesSetPrimitiveParam.length);
        Assert.assertEquals(paramClassName, paramTypeNamesSetPrimitiveParam[0]);
        String returnTypeNameSetPrimitive = metaMethodSetPrimitiveParam.getReturnTypeName();
        Assert.assertEquals(S_TYPE_NAME_VOID, returnTypeNameSetPrimitive);
    }

    @Test
    public void testRegressionDefaultPackageParamMatchPrimitiveArray() throws Throwable {
        String className = "FooClassInDefaultPackage";
        Class<?> defaultPackageTestClass = Class.forName(className);
        Class<?> defaultPackageTestClassParam = int[].class;
        String expectedParamClassName = "int[]";
        MetaClass metaClass = new MetaClass(new MetaPackage(S_EMPTY), className);
        Method getterPrimitiveArrayParam = defaultPackageTestClass.getDeclaredMethod("getPrimitiveArrayParam", new Class[]{  });
        MetaMethod metaMethodGetPrimitiveArrayParam = new MetaMethod(getterPrimitiveArrayParam, metaClass);
        String[] paramTypeNamesGetPrimitiveArrayParam = metaMethodGetPrimitiveArrayParam.getParamTypeNames();
        Assert.assertEquals(0, paramTypeNamesGetPrimitiveArrayParam.length);
        String returnTypeNameGetPrimitiveArrayParam = metaMethodGetPrimitiveArrayParam.getReturnTypeName();
        Assert.assertEquals(expectedParamClassName, returnTypeNameGetPrimitiveArrayParam);
        Method setterPrimitiveArrayParam = defaultPackageTestClass.getDeclaredMethod("setPrimitiveArrayParam", new Class[]{ defaultPackageTestClassParam });
        MetaMethod metaMethodSetPrimitiveArrayParam = new MetaMethod(setterPrimitiveArrayParam, metaClass);
        String[] paramTypeNamesSetPrimitiveArrayParam = metaMethodSetPrimitiveArrayParam.getParamTypeNames();
        Assert.assertEquals(1, paramTypeNamesSetPrimitiveArrayParam.length);
        Assert.assertEquals(expectedParamClassName, paramTypeNamesSetPrimitiveArrayParam[0]);
        String returnTypeNameSetParam = metaMethodSetPrimitiveArrayParam.getReturnTypeName();
        Assert.assertEquals(S_TYPE_NAME_VOID, returnTypeNameSetParam);
    }

    @Test
    public void testRegressionDefaultPackageParamMatchClassArray() throws Throwable {
        String className = "FooClassInDefaultPackage";
        String paramClassName = "IsUsedForTestingDefaultPackage";
        Class<?> defaultPackageTestClass = Class.forName(className);
        Class<?> defaultPackageTestClassParam = Class.forName((("[L" + paramClassName) + ";"));
        String expectedParamClassName = paramClassName + "[]";
        MetaClass metaClass = new MetaClass(new MetaPackage(S_EMPTY), className);
        Method getterClassArrayParam = defaultPackageTestClass.getDeclaredMethod("getClassArrayParam", new Class[]{  });
        MetaMethod metaMethodGetClassArrayParam = new MetaMethod(getterClassArrayParam, metaClass);
        String[] paramTypeNamesGetClassArrayParam = metaMethodGetClassArrayParam.getParamTypeNames();
        Assert.assertEquals(0, paramTypeNamesGetClassArrayParam.length);
        String returnTypeNameGetClassArrayParam = metaMethodGetClassArrayParam.getReturnTypeName();
        Assert.assertEquals(expectedParamClassName, returnTypeNameGetClassArrayParam);
        Method setterClassArrayParam = defaultPackageTestClass.getDeclaredMethod("setClassArrayParam", new Class[]{ defaultPackageTestClassParam });
        MetaMethod metaMethodSetClassArrayParam = new MetaMethod(setterClassArrayParam, metaClass);
        String[] paramTypeNamesSetClassArrayParam = metaMethodSetClassArrayParam.getParamTypeNames();
        Assert.assertEquals(1, paramTypeNamesSetClassArrayParam.length);
        Assert.assertEquals(expectedParamClassName, paramTypeNamesSetClassArrayParam[0]);
        String returnTypeNameSetClassArray = metaMethodSetClassArrayParam.getReturnTypeName();
        Assert.assertEquals(S_TYPE_NAME_VOID, returnTypeNameSetClassArray);
    }
}

