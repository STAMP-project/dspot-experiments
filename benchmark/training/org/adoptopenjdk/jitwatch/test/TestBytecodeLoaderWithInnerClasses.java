/**
 * Copyright (c) 2013-2017 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.adoptopenjdk.jitwatch.model.MemberSignatureParts;
import org.adoptopenjdk.jitwatch.model.bytecode.ClassBC;
import org.adoptopenjdk.jitwatch.model.bytecode.MemberBytecode;
import org.adoptopenjdk.jitwatch.model.bytecode.SourceMapper;
import org.junit.Assert;
import org.junit.Test;


/* javap -v * | egrep "Classfile|line|public"

Classfile /home/chris/jitwatch/sandbox/classes/TestInner.class
public TestInner();
line 4: 0
line 5: 4
line 7: 12
line 9: 16
line 10: 25
public void a();
line 14: 0
line 15: 8
public static void main(java.lang.String[]);
line 52: 0
line 53: 8

Classfile /home/chris/jitwatch/sandbox/classes/TestInner$Inner1.class
public TestInner$Inner1(TestInner);
line 20: 0
line 21: 9
line 23: 17
line 25: 21
line 26: 30
public void b();
line 30: 0
line 31: 8

Classfile /home/chris/jitwatch/sandbox/classes/TestInner$Inner1$Inner2.class
public TestInner$Inner1$Inner2(TestInner$Inner1);
line 36: 0
line 37: 9
line 39: 17
line 40: 21
public void c();
line 44: 0
line 45: 8
 */
public class TestBytecodeLoaderWithInnerClasses {
    private String classNameOuter = "TestInner";

    private String classNameInner1 = "TestInner$Inner1";

    private String classNameInner2 = "TestInner$Inner1$Inner2";

    private Path pathToSourceDir;

    private Path pathToTempClassDir;

    private List<String> classpathLocations;

    private ClassBC classBytecodeForOuter;

    private ClassBC classBytecodeForInner1;

    private ClassBC classBytecodeForInner2;

    @Test
    public void testCompilationCreatedCorrectOutputs() {
        Assert.assertTrue(Paths.get(pathToTempClassDir.toString(), ((classNameOuter) + ".class")).toFile().exists());
        Assert.assertTrue(Paths.get(pathToTempClassDir.toString(), ((classNameInner1) + ".class")).toFile().exists());
        Assert.assertTrue(Paths.get(pathToTempClassDir.toString(), ((classNameInner2) + ".class")).toFile().exists());
        Assert.assertEquals(((classNameOuter) + ".java"), classBytecodeForOuter.getSourceFile());
        checkMemberNames(classBytecodeForOuter, "main", "TestInner", "a");
        List<String> innerClasses = classBytecodeForOuter.getInnerClassNames();
        Assert.assertEquals(1, innerClasses.size());
        Assert.assertEquals(classNameInner1, innerClasses.get(0));
        Assert.assertEquals(((classNameOuter) + ".java"), classBytecodeForInner1.getSourceFile());
        checkMemberNames(classBytecodeForInner1, "TestInner$Inner1", "b");
        List<String> innerClassesOfInner1 = classBytecodeForInner1.getInnerClassNames();
        Assert.assertEquals(1, innerClassesOfInner1.size());
        Assert.assertEquals(classNameInner2, innerClassesOfInner1.get(0));
        Assert.assertEquals(((classNameOuter) + ".java"), classBytecodeForInner2.getSourceFile());
        checkMemberNames(classBytecodeForInner2, "TestInner$Inner1$Inner2", "c");
        List<ClassBC> classBytecodeListForOuter = SourceMapper.getClassBytecodeList(classBytecodeForOuter);
        Assert.assertNotNull(classBytecodeListForOuter);
        Assert.assertEquals(3, classBytecodeListForOuter.size());
    }

    @Test
    public void testSearchFromSourceOuterClassConstructor() {
        String fqClassNameOuter = classBytecodeForOuter.getFullyQualifiedClassName();
        Assert.assertEquals(classNameOuter, fqClassNameOuter);
        MemberBytecode memberBytecodeForConstructor = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForOuter, 4);
        Assert.assertNotNull(memberBytecodeForConstructor);
        MemberSignatureParts mspOuterConstructor = memberBytecodeForConstructor.getMemberSignatureParts();
        Assert.assertNotNull(mspOuterConstructor);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspOuterConstructor.getReturnType());
        Assert.assertEquals(0, mspOuterConstructor.getParamTypes().size());
        Assert.assertEquals(classNameOuter, mspOuterConstructor.getMemberName());
    }

    @Test
    public void testSearchFromSourceOuterClassMethod() {
        MemberBytecode memberBytecodeForMethod = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForOuter, 14);
        Assert.assertNotNull(memberBytecodeForMethod);
        MemberSignatureParts mspOuterMethod = memberBytecodeForMethod.getMemberSignatureParts();
        Assert.assertNotNull(mspOuterMethod);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspOuterMethod.getReturnType());
        Assert.assertEquals(0, mspOuterMethod.getParamTypes().size());
        Assert.assertEquals("a", mspOuterMethod.getMemberName());
    }

    @Test
    public void testSearchFromSourceInner1ClassConstructor() {
        String fqClassNameInner1 = classBytecodeForInner1.getFullyQualifiedClassName();
        Assert.assertEquals(classNameInner1, fqClassNameInner1);
        MemberBytecode memberBytecodeForInner1Constructor = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner1, 20);
        Assert.assertNotNull(memberBytecodeForInner1Constructor);
        MemberSignatureParts mspInner1Constructor = memberBytecodeForInner1Constructor.getMemberSignatureParts();
        Assert.assertNotNull(mspInner1Constructor);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspInner1Constructor.getReturnType());
        Assert.assertEquals(1, mspInner1Constructor.getParamTypes().size());
        Assert.assertEquals(classNameOuter, mspInner1Constructor.getParamTypes().get(0));
        Assert.assertEquals(classNameInner1, mspInner1Constructor.getMemberName());
    }

    @Test
    public void testSearchFromSourceInner1ClassMethod() {
        MemberBytecode memberBytecodeForInner1Method = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner1, 30);
        Assert.assertNotNull(memberBytecodeForInner1Method);
        MemberSignatureParts mspInner1Method = memberBytecodeForInner1Method.getMemberSignatureParts();
        Assert.assertNotNull(mspInner1Method);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspInner1Method.getReturnType());
        Assert.assertEquals(0, mspInner1Method.getParamTypes().size());
        Assert.assertEquals("b", mspInner1Method.getMemberName());
    }

    @Test
    public void testSearchFromSourceInner2ClassConstructor() {
        String fqClassNameInner2 = classBytecodeForInner2.getFullyQualifiedClassName();
        Assert.assertEquals(classNameInner2, fqClassNameInner2);
        MemberBytecode memberBytecodeForInner2Constructor = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner2, 36);
        Assert.assertNotNull(memberBytecodeForInner2Constructor);
        MemberSignatureParts mspInner2Constructor = memberBytecodeForInner2Constructor.getMemberSignatureParts();
        Assert.assertNotNull(mspInner2Constructor);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspInner2Constructor.getReturnType());
        Assert.assertEquals(1, mspInner2Constructor.getParamTypes().size());
        Assert.assertEquals(classNameInner1, mspInner2Constructor.getParamTypes().get(0));
        Assert.assertEquals(classNameInner2, mspInner2Constructor.getMemberName());
    }

    @Test
    public void testSearchFromSourceInner2ClassMethod() {
        MemberBytecode memberBytecodeForInner2Method = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner2, 44);
        Assert.assertNotNull(memberBytecodeForInner2Method);
        MemberSignatureParts mspInner2Method = memberBytecodeForInner2Method.getMemberSignatureParts();
        Assert.assertNotNull(mspInner2Method);
        Assert.assertEquals(S_TYPE_NAME_VOID, mspInner2Method.getReturnType());
        Assert.assertEquals(0, mspInner2Method.getParamTypes().size());
        Assert.assertEquals("c", mspInner2Method.getMemberName());
    }

    @Test
    public void testSearchFromBytecodeOuterClassConstructor() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForOuter, 4);
        // line 4: 0
        // line 5: 4
        // line 7: 12
        // line 9: 16
        // line 10: 25
        Assert.assertEquals(4, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(5, SourceMapper.getSourceLineFromBytecode(memberBytecode, 4));
        Assert.assertEquals(7, SourceMapper.getSourceLineFromBytecode(memberBytecode, 12));
        Assert.assertEquals(9, SourceMapper.getSourceLineFromBytecode(memberBytecode, 16));
        Assert.assertEquals(10, SourceMapper.getSourceLineFromBytecode(memberBytecode, 25));
    }

    @Test
    public void testSearchFromBytecodeOuterClassMethod() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForOuter, 14);
        // line 14: 0
        // line 15: 8
        Assert.assertEquals(14, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(15, SourceMapper.getSourceLineFromBytecode(memberBytecode, 8));
    }

    @Test
    public void testSearchFromBytecodeInner1ClassConstructor() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner1, 20);
        // line 20: 0
        // line 21: 9
        // line 23: 17
        // line 25: 21
        // line 26: 30
        Assert.assertEquals(20, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(21, SourceMapper.getSourceLineFromBytecode(memberBytecode, 9));
        Assert.assertEquals(23, SourceMapper.getSourceLineFromBytecode(memberBytecode, 17));
        Assert.assertEquals(25, SourceMapper.getSourceLineFromBytecode(memberBytecode, 21));
        Assert.assertEquals(26, SourceMapper.getSourceLineFromBytecode(memberBytecode, 30));
    }

    @Test
    public void testSearchFromBytecodeInner1ClassMethod() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner1, 30);
        // line 30: 0
        // line 31: 8
        Assert.assertEquals(30, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(31, SourceMapper.getSourceLineFromBytecode(memberBytecode, 8));
    }

    @Test
    public void testSearchFromBytecodeInner2ClassConstructor() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner2, 36);
        // line 36: 0
        // line 37: 9
        // line 39: 17
        // line 40: 21
        Assert.assertEquals(36, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(37, SourceMapper.getSourceLineFromBytecode(memberBytecode, 9));
        Assert.assertEquals(39, SourceMapper.getSourceLineFromBytecode(memberBytecode, 17));
        Assert.assertEquals(40, SourceMapper.getSourceLineFromBytecode(memberBytecode, 21));
    }

    @Test
    public void testSearchFromBytecodeInner2ClassMethod() {
        MemberBytecode memberBytecode = SourceMapper.getMemberBytecodeForSourceLine(classBytecodeForInner2, 44);
        // line 44: 0
        // line 45: 8
        Assert.assertEquals(44, SourceMapper.getSourceLineFromBytecode(memberBytecode, 0));
        Assert.assertEquals(45, SourceMapper.getSourceLineFromBytecode(memberBytecode, 8));
    }
}

