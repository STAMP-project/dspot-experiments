/**
 * Copyright (c) 2013-2016 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.adoptopenjdk.jitwatch.model.LogParseException;
import org.adoptopenjdk.jitwatch.model.MemberSignatureParts;
import org.junit.Assert;
import org.junit.Test;


public class TestMemberSignatureParts {
    // used in this class to test static initialiser bytecode matching
    public static long timestamp = 0;

    static {
        TestMemberSignatureParts.timestamp = System.currentTimeMillis();
    }

    @Test
    public void testPackageConstructorNoParams() throws Exception {
        String sigBC = "java.lang.String();";
        String sigLog = "java.lang.String <init> ()V";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(0, modListBC.size());
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, mspBC.getReturnType());
        Assert.assertEquals("String", mspBC.getMemberName());
        Assert.assertEquals(0, mspBC.getParamTypes().size());
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testPublicConstructorNoParams() throws Exception {
        String sigBC = "public java.lang.String()";
        String sigLog = "java.lang.String <init> ()V";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(1, modListBC.size());
        Assert.assertEquals("public", modListBC.get(0));
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, mspBC.getReturnType());
        Assert.assertEquals("String", mspBC.getMemberName());
        Assert.assertEquals(0, mspBC.getParamTypes().size());
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testConstructorWithParams() throws Exception {
        String sigBC = "public java.lang.String(java.lang.String, int)";
        String sigLog = "java.lang.String <init> (Ljava.lang.String;I)V";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(1, modListBC.size());
        Assert.assertEquals("public", modListBC.get(0));
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, mspBC.getReturnType());
        Assert.assertEquals("String", mspBC.getMemberName());
        Assert.assertEquals(2, mspBC.getParamTypes().size());
        List<String> paramTypeListBC = mspBC.getParamTypes();
        Assert.assertEquals("java.lang.String", paramTypeListBC.get(0));
        Assert.assertEquals("int", paramTypeListBC.get(1));
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testSimpleMethodNoParams() throws Exception {
        String sigBC = "public void gc()";
        String sigLog = "java.lang.System gc ()V";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(1, modListBC.size());
        Assert.assertEquals("public", modListBC.get(0));
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, mspBC.getReturnType());
        Assert.assertEquals("gc", mspBC.getMemberName());
        Assert.assertEquals(0, mspBC.getParamTypes().size());
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testSimpleMethodWithParams() throws Exception {
        String sigBC = "public boolean matches(java.lang.String)";
        String sigLog = "java.lang.String matches (Ljava.lang.String;)Z";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(1, modListBC.size());
        Assert.assertEquals("public", modListBC.get(0));
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals("boolean", mspBC.getReturnType());
        Assert.assertEquals("matches", mspBC.getMemberName());
        Assert.assertEquals(1, mspBC.getParamTypes().size());
        List<String> paramTypeList = mspBC.getParamTypes();
        Assert.assertEquals("java.lang.String", paramTypeList.get(0));
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testSimpleMethodWithParamsAndParamNames() throws Exception {
        String sigBC = "public boolean startsWith(java.lang.String foo, int bar)";
        String sigLog = "java.lang.String startsWith (Ljava.lang.String;I)Z";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        MemberSignatureParts mspLog = MemberSignatureParts.fromLogCompilationSignature(sigLog);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(1, modListBC.size());
        Assert.assertEquals("public", modListBC.get(0));
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals("boolean", mspBC.getReturnType());
        Assert.assertEquals("startsWith", mspBC.getMemberName());
        Assert.assertEquals(2, mspBC.getParamTypes().size());
        List<String> paramTypeListBC = mspBC.getParamTypes();
        Assert.assertEquals("java.lang.String", paramTypeListBC.get(0));
        Assert.assertEquals("int", paramTypeListBC.get(1));
        checkSame(mspBC, mspLog);
    }

    @Test
    public void testSimpleGenericMethod() {
        String sig = "public Map<String,String> copy(Map<String,String>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("com.chrisnewland.Test", sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(1, modList.size());
        Assert.assertEquals("public", modList.get(0));
        Assert.assertEquals(0, msp.getGenerics().size());
        Assert.assertEquals("Map<String,String>", msp.getReturnType());
        Assert.assertEquals("copy", msp.getMemberName());
        Assert.assertEquals(1, msp.getParamTypes().size());
        List<String> paramTypeList = msp.getParamTypes();
        Assert.assertEquals("Map<String,String>", paramTypeList.get(0));
    }

    @Test
    public void testSignatureWithGenericExtends() {
        String sig = "public static <T extends java.lang.Object, U extends java.lang.Object> T[] copyOf(U[], int, java.lang.Class<? extends T[]>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("java.util.Arrays", sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(2, modList.size());
        Assert.assertEquals("public", modList.get(0));
        Assert.assertEquals("static", modList.get(1));
        Map<String, String> genMap = msp.getGenerics();
        Assert.assertEquals(2, genMap.size());
        Assert.assertEquals(true, genMap.containsKey("T"));
        Assert.assertEquals(true, genMap.containsKey("U"));
        Assert.assertEquals("java.lang.Object", genMap.get("T"));
        Assert.assertEquals("java.lang.Object", genMap.get("U"));
        Assert.assertEquals("T[]", msp.getReturnType());
        Assert.assertEquals("copyOf", msp.getMemberName());
        List<String> paramTypes = msp.getParamTypes();
        Assert.assertEquals(3, paramTypes.size());
        Assert.assertEquals("U[]", paramTypes.get(0));
        Assert.assertEquals("int", paramTypes.get(1));
        Assert.assertEquals("java.lang.Class<? extends T[]>", paramTypes.get(2));
    }

    @Test
    public void testSignatureWithGenericRegressionReturnTypeHasGenerics() {
        String sig = "public static <T extends java.lang.Object> java.lang.Class<T> asWrapperType(java.lang.Class<T>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("sun.invoke.util.Wrapper", sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(2, modList.size());
        Assert.assertEquals("public", modList.get(0));
        Assert.assertEquals("static", modList.get(1));
        Map<String, String> genMap = msp.getGenerics();
        Assert.assertEquals(1, genMap.size());
        Assert.assertEquals(true, genMap.containsKey("T"));
        Assert.assertEquals("java.lang.Object", genMap.get("T"));
        Assert.assertEquals("java.lang.Class<T>", msp.getReturnType());
        Assert.assertEquals("asWrapperType", msp.getMemberName());
        List<String> paramTypes = msp.getParamTypes();
        Assert.assertEquals(1, paramTypes.size());
        Assert.assertEquals("java.lang.Class<T>", paramTypes.get(0));
    }

    @Test
    public void testSignatureWithGenericNoExtends() {
        String sig = "public static <T,U> T[] copyOf(U[], int, java.lang.Class<? extends T[]>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("java.util.Arrays", sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(2, modList.size());
        Assert.assertEquals("public", modList.get(0));
        Assert.assertEquals("static", modList.get(1));
        Map<String, String> genMap = msp.getGenerics();
        Assert.assertEquals(2, genMap.size());
        Assert.assertEquals(true, genMap.containsKey("T"));
        Assert.assertEquals(true, genMap.containsKey("U"));
        Assert.assertEquals(null, genMap.get("T"));
        Assert.assertEquals(null, genMap.get("U"));
        Assert.assertEquals("T[]", msp.getReturnType());
        Assert.assertEquals("copyOf", msp.getMemberName());
        List<String> paramTypes = msp.getParamTypes();
        Assert.assertEquals(3, paramTypes.size());
        Assert.assertEquals("U[]", paramTypes.get(0));
        Assert.assertEquals("int", paramTypes.get(1));
        Assert.assertEquals("java.lang.Class<? extends T[]>", paramTypes.get(2));
    }

    @Test
    public void testStaticInitialiserBytecode() throws Exception {
        String sigBC = "static {}";
        MemberSignatureParts mspBC = MemberSignatureParts.fromBytecodeSignature("java.lang.String", sigBC);
        List<String> modListBC = mspBC.getModifiers();
        Assert.assertEquals(0, modListBC.size());
        Assert.assertEquals(0, mspBC.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, mspBC.getReturnType());
        Assert.assertEquals(S_STATIC_INIT, mspBC.getMemberName());
        Assert.assertEquals(0, mspBC.getParamTypes().size());
    }

    @Test
    public void testJava7DisassemblySignature() throws LogParseException {
        String sig = "# {method} &apos;chainA2&apos; &apos;(J)J&apos; in &apos;org/adoptopenjdk/jitwatch/demo/MakeHotSpotLog&apos;";
        MemberSignatureParts msp = MemberSignatureParts.fromAssembly(sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(0, modList.size());
        Assert.assertEquals(0, msp.getGenerics().size());
        Assert.assertEquals("long", msp.getReturnType());
        Assert.assertEquals("org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", msp.getFullyQualifiedClassName());
        Assert.assertEquals("chainA2", msp.getMemberName());
        Assert.assertEquals(1, msp.getParamTypes().size());
        Assert.assertEquals("long", msp.getParamTypes().get(0));
    }

    @Test
    public void testJava8DisassemblySignature() throws LogParseException {
        String sig = "  # {method} {0x00007fb6a89c4f80} &apos;hashCode&apos; &apos;()I&apos; in &apos;java/lang/String&apos;";
        MemberSignatureParts msp = MemberSignatureParts.fromAssembly(sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(0, modList.size());
        Assert.assertEquals(0, msp.getGenerics().size());
        Assert.assertEquals("int", msp.getReturnType());
        Assert.assertEquals("java.lang.String", msp.getFullyQualifiedClassName());
        Assert.assertEquals("hashCode", msp.getMemberName());
        Assert.assertEquals(0, msp.getParamTypes().size());
    }

    @Test
    public void testFromAssemblyRegression() throws LogParseException {
        String toParse = "# {method} &apos;write&apos; &apos;(Ljava/lang/String;II)V&apos; in &apos;java/io/BufferedWriter&apos;";
        MemberSignatureParts msp = MemberSignatureParts.fromAssembly(toParse);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(0, modList.size());
        Assert.assertEquals(0, msp.getGenerics().size());
        Assert.assertEquals(S_TYPE_NAME_VOID, msp.getReturnType());
        Assert.assertEquals("java.io.BufferedWriter", msp.getFullyQualifiedClassName());
        Assert.assertEquals("write", msp.getMemberName());
        Assert.assertEquals(3, msp.getParamTypes().size());
        Assert.assertEquals("java.lang.String", msp.getParamTypes().get(0));
        Assert.assertEquals("int", msp.getParamTypes().get(1));
        Assert.assertEquals("int", msp.getParamTypes().get(2));
    }

    /* TODO: class initialiser <clinit> does not appear in
    Class.getDeclared{Methods|Constructors}() Need to detect access and
    return dummy member. Bytecode for <clinit> is available via ClassBC

    @Test public void testMatchStaticInitialiser() { JITDataModel model = new
    JITDataModel();

    MetaClass metaClassThis = model.buildAndGetMetaClass(getClass());

    MemberSignatureParts msp =
    MemberSignatureParts.fromBytecodeSignature(getClass().getName(),
    ParseUtil.STATIC_BYTECODE_SIGNATURE);

    IMetaMember memberFromSig = metaClassThis.getMemberFromSignature(msp);

    ClassBC classBC = metaClassThis.getClassBytecode(null);

    for (String sig : classBC.getBytecodeMethodSignatures()) {
    System.out.println("BC: " + sig); }

    assertNotNull(memberFromSig);

    }
     */
    @Test
    public void testNashornSignatureWithColon() {
        String bcSig = "public static jdk.nashorn.internal.runtime.ScriptFunction :createProgramFunction(jdk.nashorn.internal.runtime.ScriptObject);";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature(getClass().getName(), bcSig);
        Assert.assertNotNull(msp);
        List<String> paramTypes = new ArrayList<>();
        paramTypes.add("jdk.nashorn.internal.runtime.ScriptObject");
        Assert.assertEquals(paramTypes, msp.getParamTypes());
        Assert.assertEquals("jdk.nashorn.internal.runtime.ScriptFunction", msp.getReturnType());
        Assert.assertEquals(":createProgramFunction", msp.getMemberName());
        List<String> modifiers = new ArrayList<>();
        modifiers.add("public");
        modifiers.add("static");
        Assert.assertEquals(modifiers, msp.getModifiers());
    }

    @Test
    public void testRegressionSubstituteGenericsForClassloading() {
        String sig = "public static <T extends java.lang.Object, U extends java.lang.Object> T[] copyOf(U[], int, java.lang.Class<? extends T[]>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("java.util.Arrays", sig);
        Assert.assertEquals("T[]", msp.getReturnType());
        Assert.assertEquals("java.lang.Object[]", msp.applyGenericSubstitutionsForClassLoading(msp.getReturnType()));
    }

    @Test
    public void testRegressionGenericsWithSuper() {
        String sig = "public static <T extends java.lang.Comparable<? super T>> void sort(java.util.List<T>)";
        MemberSignatureParts msp = MemberSignatureParts.fromBytecodeSignature("java.util.Collections", sig);
        List<String> modList = msp.getModifiers();
        Assert.assertEquals(2, modList.size());
        Assert.assertEquals("public", modList.get(0));
        Assert.assertEquals("static", modList.get(1));
        Map<String, String> genMap = msp.getGenerics();
        Assert.assertEquals(1, genMap.size());
        Assert.assertEquals(true, genMap.containsKey("T"));
        Assert.assertEquals("java.lang.Comparable<? super T>", genMap.get("T"));
        Assert.assertEquals("void", msp.getReturnType());
        Assert.assertEquals("sort", msp.getMemberName());
        List<String> paramTypes = msp.getParamTypes();
        Assert.assertEquals(1, paramTypes.size());
        Assert.assertEquals("java.util.List<T>", paramTypes.get(0));
    }
}

