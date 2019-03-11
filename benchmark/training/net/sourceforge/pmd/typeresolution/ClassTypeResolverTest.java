/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import junit.framework.TestCase;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.java.typeresolution.MethodType;
import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Bound;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Constraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Variable;
import net.sourceforge.pmd.typeresolution.testdata.AbstractReturnTypeUseCase;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousClassFromInterface;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.AnoymousExtendingObject;
import net.sourceforge.pmd.typeresolution.testdata.ArrayListFound;
import net.sourceforge.pmd.typeresolution.testdata.ArrayTypes;
import net.sourceforge.pmd.typeresolution.testdata.ArrayVariableDeclaration;
import net.sourceforge.pmd.typeresolution.testdata.DefaultJavaLangImport;
import net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.ExtraTopLevelClass;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccess;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericBounds;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericNested;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericParameter;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericRaw;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessPrimaryGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessShadow;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessStatic;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper;
import net.sourceforge.pmd.typeresolution.testdata.GenericMethodsImplicit;
import net.sourceforge.pmd.typeresolution.testdata.GenericsArrays;
import net.sourceforge.pmd.typeresolution.testdata.InnerClass;
import net.sourceforge.pmd.typeresolution.testdata.JavaTypeDefinitionToStringNPE;
import net.sourceforge.pmd.typeresolution.testdata.Literals;
import net.sourceforge.pmd.typeresolution.testdata.LocalGenericClass;
import net.sourceforge.pmd.typeresolution.testdata.MethodAccessibility;
import net.sourceforge.pmd.typeresolution.testdata.MethodFirstPhase;
import net.sourceforge.pmd.typeresolution.testdata.MethodGenericExplicit;
import net.sourceforge.pmd.typeresolution.testdata.MethodGenericParam;
import net.sourceforge.pmd.typeresolution.testdata.MethodMostSpecific;
import net.sourceforge.pmd.typeresolution.testdata.MethodPotentialApplicability;
import net.sourceforge.pmd.typeresolution.testdata.MethodSecondPhase;
import net.sourceforge.pmd.typeresolution.testdata.MethodStaticAccess;
import net.sourceforge.pmd.typeresolution.testdata.MethodThirdPhase;
import net.sourceforge.pmd.typeresolution.testdata.NestedAllocationExpressions;
import net.sourceforge.pmd.typeresolution.testdata.NestedAnonymousClass;
import net.sourceforge.pmd.typeresolution.testdata.Operators;
import net.sourceforge.pmd.typeresolution.testdata.OverloadedMethodsUsage;
import net.sourceforge.pmd.typeresolution.testdata.PmdStackOverflow;
import net.sourceforge.pmd.typeresolution.testdata.Promotion;
import net.sourceforge.pmd.typeresolution.testdata.SubTypeUsage;
import net.sourceforge.pmd.typeresolution.testdata.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.ThisExpression;
import net.sourceforge.pmd.typeresolution.testdata.VarArgsMethodUseCase;
import net.sourceforge.pmd.typeresolution.testdata.VarargsAsFixedArity;
import net.sourceforge.pmd.typeresolution.testdata.VarargsZeroArity;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.Converter;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.JavaTypeDefinitionEquals;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB2;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;


// TODO split that class
public class ClassTypeResolverTest {
    @Test
    public void stackOverflowTest() {
        // See #831 https://github.com/pmd/pmd/issues/831 - [java] StackOverflow in JavaTypeDefinitionSimple.toString
        parseAndTypeResolveForClass15(PmdStackOverflow.class);
    }

    @Test
    public void testClassNameExists() {
        ClassTypeResolver classTypeResolver = new ClassTypeResolver();
        Assert.assertEquals(true, classTypeResolver.classNameExists("java.lang.System"));
        Assert.assertEquals(false, classTypeResolver.classNameExists("im.sure.that.this.does.not.Exist"));
        Assert.assertEquals(true, classTypeResolver.classNameExists("java.awt.List"));
    }

    @Test
    public void acceptanceTest() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ArrayListFound.class);
        Assert.assertEquals(ArrayListFound.class, getType());
        Assert.assertEquals(ArrayListFound.class, getType());
        ASTImportDeclaration id = acu.getFirstDescendantOfType(ASTImportDeclaration.class);
        Assert.assertEquals("java.util", id.getPackage().getName());
        Assert.assertEquals(ArrayList.class, id.getType());
        Assert.assertEquals(ArrayList.class, getType());
        Assert.assertEquals(ArrayList.class, getType());
        Assert.assertEquals(ArrayList.class, getType());
        Assert.assertEquals(ArrayList.class, getType());
        Assert.assertEquals(ArrayList.class, getType());
        Assert.assertEquals(ArrayList.class, getType());
        acu = parseAndTypeResolveForClass15(DefaultJavaLangImport.class);
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(Override.class, getType());
    }

    /**
     * See bug #1138 Anonymous inner class in enum causes NPE
     */
    @Test
    public void testEnumAnonymousInnerClass() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(EnumWithAnonymousInnerClass.class);
        // try it in jshell, an enum constant with a body is compiled to an anonymous class,
        // the counter is shared with other anonymous classes of the enum
        Class<?> enumAnon = acu.getFirstDescendantOfType(ASTEnumConstant.class).getQualifiedName().getType();
        Assert.assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$1", enumAnon.getName());
        Class<?> inner = acu.getFirstDescendantOfType(ASTAllocationExpression.class).getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType();
        Assert.assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$2", inner.getName());
    }

    /**
     * See bug #899 toString causes NPE
     */
    @Test
    public void testNPEInJavaTypeDefinitionToString() {
        // Just parsing this file throws a NPE
        parseAndTypeResolveForClass(JavaTypeDefinitionToStringNPE.class, "1.8");
    }

    @Test
    public void testExtraTopLevelClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ExtraTopLevelClass.class);
        Class<?> theExtraTopLevelClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.TheExtraTopLevelClass");
        // First class
        ASTTypeDeclaration typeDeclaration = ((ASTTypeDeclaration) (acu.jjtGetChild(1)));
        Assert.assertEquals(ExtraTopLevelClass.class, typeDeclaration.getType());
        Assert.assertEquals(ExtraTopLevelClass.class, getType());
        // Second class
        typeDeclaration = ((ASTTypeDeclaration) (acu.jjtGetChild(2)));
        Assert.assertEquals(theExtraTopLevelClass, typeDeclaration.getType());
        Assert.assertEquals(theExtraTopLevelClass, getType());
    }

    @Test
    public void testInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(InnerClass.class);
        Class<?> theInnerClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.InnerClass$TheInnerClass");
        // Outer class
        ASTTypeDeclaration typeDeclaration = acu.getFirstDescendantOfType(ASTTypeDeclaration.class);
        Assert.assertEquals(InnerClass.class, typeDeclaration.getType());
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        Assert.assertEquals(InnerClass.class, outerClassDeclaration.getType());
        // Inner class
        Assert.assertEquals(theInnerClass, getType());
        // Method parameter as inner class
        ASTFormalParameter formalParameter = typeDeclaration.getFirstDescendantOfType(ASTFormalParameter.class);
        Assert.assertEquals(theInnerClass, formalParameter.getType());
    }

    /**
     * If we don't have the auxclasspath, we might not find the inner class. In
     * that case, we'll need to search by name for a match.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testInnerClassNotCompiled() throws Exception {
        Node acu = parseAndTypeResolveForString(("public class TestInnerClass {\n" + (((((("    public void foo() {\n" + "        Statement statement = new Statement();\n") + "    ") + "}\n") + "    static class Statement {\n") + "    }\n") + "}")), "1.8");
        ASTClassOrInterfaceType statement = acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        Assert.assertTrue(statement.isReferenceToClassSameCompilationUnit());
    }

    @Test
    public void testAnonymousClassFromInterface() throws Exception {
        Node acu = parseAndTypeResolveForClass(AnonymousClassFromInterface.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = ((TypeNode) (allocationExpression.jjtGetChild(0)));
        Assert.assertTrue(Comparator.class.isAssignableFrom(child.getType()));
        Assert.assertSame(Integer.class, getType());
    }

    @Test
    public void testNestedAnonymousClass() throws Exception {
        Node acu = parseAndTypeResolveForClass(NestedAnonymousClass.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        ASTAllocationExpression nestedAllocation = // get the declaration (boundary)
        allocationExpression.getFirstDescendantOfType(ASTClassOrInterfaceBodyDeclaration.class).getFirstDescendantOfType(ASTAllocationExpression.class);// and dive for the nested allocation

        TypeNode child = ((TypeNode) (nestedAllocation.jjtGetChild(0)));
        Assert.assertTrue(Converter.class.isAssignableFrom(child.getType()));
        Assert.assertSame(String.class, getType());
    }

    @Test
    public void testAnonymousExtendingObject() throws Exception {
        Node acu = parseAndTypeResolveForClass(AnoymousExtendingObject.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = ((TypeNode) (allocationExpression.jjtGetChild(0)));
        Assert.assertTrue(Object.class.isAssignableFrom(child.getType()));
    }

    @Test
    public void testAnonymousInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(AnonymousInnerClass.class);
        Class<?> theAnonymousInnerClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass$1");
        // Outer class
        ASTTypeDeclaration typeDeclaration = acu.getFirstDescendantOfType(ASTTypeDeclaration.class);
        Assert.assertEquals(AnonymousInnerClass.class, typeDeclaration.getType());
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        Assert.assertEquals(AnonymousInnerClass.class, outerClassDeclaration.getType());
        // Anonymous Inner class
        Assert.assertEquals(theAnonymousInnerClass, getType());
    }

    @Test
    public void testLiterals() throws JaxenException {
        List<ASTLiteral> literals = selectNodes(Literals.class, ASTLiteral.class);
        int index = 0;
        // String s = "s";
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(String.class, getType());
        // boolean boolean1 = false;
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        // boolean boolean2 = true;
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        // Object obj = null;
        Assert.assertNull(getType());
        Assert.assertNull(getType());
        // byte byte1 = 0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // byte byte2 = 0x0F;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // byte byte3 = -007;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // short short1 = 0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // short short2 = 0x0F;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // short short3 = -007;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // char char1 = 0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // char char2 = 0x0F;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // char char3 = 007;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // char char4 = 'a';
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Character.TYPE, getType());
        // int int1 = 0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // int int2 = 0x0F;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // int int3 = -007;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // int int4 = 'a';
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Character.TYPE, getType());
        // long long1 = 0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // long long2 = 0x0F;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // long long3 = -007;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // long long4 = 0L;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Long.TYPE, getType());
        // long long5 = 0x0Fl;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Long.TYPE, getType());
        // long long6 = -007L;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Long.TYPE, getType());
        // long long7 = 'a';
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Character.TYPE, getType());
        // float float1 = 0.0f;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Float.TYPE, getType());
        // float float2 = -10e+01f;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Float.TYPE, getType());
        // float float3 = 0x08.08p3f;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Float.TYPE, getType());
        // float float4 = 0xFF;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // float float5 = 'a';
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Character.TYPE, getType());
        // double double1 = 0.0;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Double.TYPE, getType());
        // double double2 = -10e+01;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Double.TYPE, getType());
        // double double3 = 0x08.08p3;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Double.TYPE, getType());
        // double double4 = 0xFF;
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Integer.TYPE, getType());
        // double double5 = 'a';
        Assert.assertEquals(0, literals.get(index).jjtGetNumChildren());
        Assert.assertEquals(Character.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All literals not tested", index, literals.size());
    }

    @Test
    public void testUnaryNumericPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'unaryNumericPromotion']]//Expression[UnaryExpression]")), ASTExpression.class);
        int index = 0;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryNumericPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'binaryNumericPromotion']]//Expression[AdditiveExpression]")), ASTExpression.class);
        int index = 0;
        // LHS = byte
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = short
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = char
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = int
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = long
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = float
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Float.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // LHS = double
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryStringPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryStringPromotion']]//Expression"), ASTExpression.class);
        int index = 0;
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testUnaryLogicalOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryLogicalOperators']]//Expression"), ASTExpression.class);
        int index = 0;
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryLogicalOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryLogicalOperators']]//Expression"), ASTExpression.class);
        int index = 0;
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        Assert.assertEquals(Boolean.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testUnaryNumericOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<TypeNode> expressions = new ArrayList<>();
        expressions.addAll(ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//Expression"), TypeNode.class));
        expressions.addAll(ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'unaryNumericOperators']]//PostfixExpression")), TypeNode.class));
        expressions.addAll(ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'unaryNumericOperators']]//PreIncrementExpression")), TypeNode.class));
        expressions.addAll(ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'unaryNumericOperators']]//PreDecrementExpression")), TypeNode.class));
        int index = 0;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryNumericOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryNumericOperators']]//Expression"), ASTExpression.class);
        int index = 0;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testAssignmentOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTStatementExpression> expressions = ClassTypeResolverTest.convertList(acu.findChildNodesWithXPath(("//Block[preceding-sibling::MethodDeclarator[@Image = " + "'assignmentOperators']]//StatementExpression")), ASTStatementExpression.class);
        int index = 0;
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        Assert.assertEquals(Long.TYPE, getType());
        // Make sure we got them all.
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    /**
     * The type should be filled also on the ASTVariableDeclaratorId node, not
     * only on the variable name declaration.
     */
    @Test
    public void testFullyQualifiedType() {
        String source = "public class Foo {\n" + (((((("    public void bar() {\n" + "        java.util.StringTokenizer st = new StringTokenizer(\"a.b.c.d\", \".\");\n") + "        while (st.hasMoreTokens()) {\n") + "            System.out.println(st.nextToken());\n") + "        }\n") + "    }\n") + "}");
        ASTCompilationUnit acu = parseAndTypeResolveForString(source, "1.5");
        List<ASTName> names = acu.findDescendantsOfType(ASTName.class);
        ASTName theStringTokenizer = null;
        for (ASTName name : names) {
            if (name.hasImageEqualTo("st.hasMoreTokens")) {
                theStringTokenizer = name;
                break;
            }
        }
        Assert.assertNotNull(theStringTokenizer);
        VariableNameDeclaration declaration = ((VariableNameDeclaration) (theStringTokenizer.getNameDeclaration()));
        Assert.assertNotNull(declaration);
        Assert.assertEquals("java.util.StringTokenizer", declaration.getTypeImage());
        Assert.assertNotNull(declaration.getType());
        Assert.assertSame(StringTokenizer.class, declaration.getType());
        ASTVariableDeclaratorId id = ((ASTVariableDeclaratorId) (declaration.getNode()));
        Assert.assertNotNull(id.getType());
        Assert.assertSame(StringTokenizer.class, id.getType());
    }

    @Test
    public void testThisExpression() {
        ASTCompilationUnit compilationUnit = parseAndTypeResolveForClass15(ThisExpression.class);
        // need to cross borders, to find expressions of the nested classes
        List<ASTPrimaryExpression> expressions = compilationUnit.findDescendantsOfType(ASTPrimaryExpression.class, true);
        List<ASTPrimaryPrefix> prefixes = compilationUnit.findDescendantsOfType(ASTPrimaryPrefix.class, true);
        int index = 0;
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.ThisExprNested.class, getType());
        Assert.assertEquals(ThisExpression.ThisExprNested.class, getType());
        // Qualified this
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.class, getType());
        Assert.assertEquals(ThisExpression.ThisExprStaticNested.class, getType());
        Assert.assertEquals(ThisExpression.ThisExprStaticNested.class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
        Assert.assertEquals("All expressions not tested", index, prefixes.size());
    }

    @Test
    public void testSuperExpression() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(SuperExpression.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix");
        int index = 0;
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperExpression.class, getType());
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperExpression.class, getType());
        Assert.assertEquals(SuperExpression.class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testArrayTypes() throws JaxenException {
        // We must not select the expression in the dimensions
        List<ASTExpression> expressions = selectNodes(ArrayTypes.class, ASTExpression.class, "//VariableInitializer/Expression");
        int index = 0;
        // int[] a = new int[1];
        // ----------
        Assert.assertEquals(int[].class, getType());
        // Object[][] b = new Object[1][0];
        // ----------------
        Assert.assertEquals(Object[][].class, getType());
        // ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };
        // ---------------------------------------------
        Assert.assertEquals(ArrayTypes[][][].class, getType());
        // ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };
        // --------------------
        Assert.assertEquals(ArrayTypes[][].class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testReferenceType() {
        List<ASTReferenceType> referenceTypes = selectNodes(ArrayTypes.class, ASTReferenceType.class);
        int index = 0;
        // int[] a = new int[1];
        // -----
        Assert.assertEquals(int[].class, getType());
        // Object[][] b = new Object[1][0];
        // ----------
        Assert.assertEquals(Object[][].class, getType());
        // ArrayTypes[][][] c = new ArrayTypes[][][] { ... };
        // ----------------
        Assert.assertEquals(ArrayTypes[][][].class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, referenceTypes.size());
    }

    @Test
    public void testHeterogeneousArrayFieldDeclaration() throws JaxenException {
        List<ASTFieldDeclaration> fields = selectNodes(ArrayVariableDeclaration.class, ASTFieldDeclaration.class);
        List<ASTLocalVariableDeclaration> locals = selectNodes(ArrayVariableDeclaration.class, ASTLocalVariableDeclaration.class);
        // public int[] a, b[];
        testPrimitiveTypeFieldDecl(fields.get(0));
        testPrimitiveTypeFieldDecl(locals.get(0));
        // public String[] c, d[];
        testRefTypeFieldDecl(fields.get(1));
        testRefTypeFieldDecl(locals.get(1));
    }

    @Test
    public void testFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccess.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // param.field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // local.field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // f.f.f.field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // (this).f.f.field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(FieldAccess.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(FieldAccess.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(FieldAccess.class, getChildType(expressions.get(index), 2));
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 3));
        // field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessNested() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessNested.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // field = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // a = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 0));
        // net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested.Nested.this.a = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 2));
        // FieldAccessNested.Nested.this.a = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 2));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessShadow() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessShadow.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // field = "shadow";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // this.field = new Integer(10);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(FieldAccessShadow.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 1));
        // (this).field = new Integer(10);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(FieldAccessShadow.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 1));
        // s2 = new SuperClassB2();
        Assert.assertEquals(SuperClassB2.class, getType());
        Assert.assertEquals(SuperClassB2.class, getChildType(expressions.get((index++)), 0));
        // privateShadow = 10;
        Assert.assertEquals(Number.class, getType());
        Assert.assertEquals(Number.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessSuper() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessSuper.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // s = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 0));
        // (this).s.s2 = new SuperClassA2();
        Assert.assertEquals(SuperClassA2.class, getType());
        Assert.assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassA2.class, getChildType(expressions.get((index++)), 2));
        // s.s.s2 = new SuperClassA2();
        Assert.assertEquals(SuperClassA2.class, getType());
        Assert.assertEquals(SuperClassA2.class, getChildType(expressions.get((index++)), 0));
        // super.s = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 1));
        // net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper.this.s = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 2));
        // s = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 0));
        // bs = new SuperClassB();
        Assert.assertEquals(SuperClassB.class, getType());
        Assert.assertEquals(SuperClassB.class, getChildType(expressions.get((index++)), 0));
        // FieldAccessSuper.Nested.super.bs = new SuperClassB();
        Assert.assertEquals(SuperClassB.class, getType());
        Assert.assertEquals(FieldAccessSuper.Nested.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassB.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassB.class, getChildType(expressions.get((index++)), 2));
        // FieldAccessSuper.super.s = new SuperClassA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 2));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBoundsGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericBounds.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // superGeneric.first = ""; // ? super String
        Assert.assertEquals(forClass(LOWER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(LOWER_WILDCARD, String.class), getChildTypeDef(expressions.get((index++)), 0));
        // superGeneric.second = null; // ?
        Assert.assertEquals(forClass(UPPER_WILDCARD, Object.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(UPPER_WILDCARD, Object.class), getChildTypeDef(expressions.get((index++)), 0));
        // inheritedSuperGeneric.first = ""; // ? super String
        Assert.assertEquals(forClass(LOWER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(LOWER_WILDCARD, String.class), getChildTypeDef(expressions.get((index++)), 0));
        // inheritedSuperGeneric.second = null; // ?
        Assert.assertEquals(forClass(UPPER_WILDCARD, Object.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(UPPER_WILDCARD, Object.class), getChildTypeDef(expressions.get((index++)), 0));
        // upperBound.first = null; // ? extends Number
        Assert.assertEquals(forClass(UPPER_WILDCARD, Number.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(UPPER_WILDCARD, Number.class), getChildTypeDef(expressions.get((index++)), 0));
        // inheritedUpperBound.first = null; // ? extends String
        Assert.assertEquals(forClass(UPPER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        Assert.assertEquals(forClass(UPPER_WILDCARD, String.class), getChildTypeDef(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testParameterGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericParameter.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // classGeneric = null; // Double
        Assert.assertEquals(Double.class, getType());
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 0));
        // localGeneric = null; // Character
        Assert.assertEquals(Character.class, getType());
        Assert.assertEquals(Character.class, getChildType(expressions.get((index++)), 0));
        // parameterGeneric.second.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // localGeneric = null; // Number
        Assert.assertEquals(Number.class, getType());
        Assert.assertEquals(Number.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testSimpleGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericSimple.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // genericField.first = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // genericField.second = new Double(0);
        Assert.assertEquals(Double.class, getType());
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 0));
        // genericTypeArg.second.second = new Double(0);
        Assert.assertEquals(Double.class, getType());
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 0));
        // param.first = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // local.second = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // param.generic.first = new Character('c');
        Assert.assertEquals(Character.class, getType());
        Assert.assertEquals(Character.class, getChildType(expressions.get((index++)), 0));
        // local.generic.second = new Float(0);
        Assert.assertEquals(Float.class, getType());
        Assert.assertEquals(Float.class, getChildType(expressions.get((index++)), 0));
        // genericField.generic.generic.generic.first = new Double(0);
        Assert.assertEquals(Double.class, getType());
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 0));
        // fieldA = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // fieldB.generic.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // fieldAcc.fieldA = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // fieldA = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testRawGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericRaw.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // rawGeneric.first = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // rawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // rawGeneric.third = new Object();
        Assert.assertEquals(Object.class, getType());
        Assert.assertEquals(Object.class, getChildType(expressions.get((index++)), 0));
        // rawGeneric.fourth.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // rawGeneric.rawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.first = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.third = new Object();
        Assert.assertEquals(Object.class, getType());
        Assert.assertEquals(Object.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.fourth.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.rawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.first = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.third = new Object();
        Assert.assertEquals(Object.class, getType());
        Assert.assertEquals(Object.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.fourth.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.rawGeneric.second = new Integer(0);
        Assert.assertEquals(Integer.class, getType());
        Assert.assertEquals(Integer.class, getChildType(expressions.get((index++)), 0));
        // bug #471
        // rawGeneric.fifth = new GenericClass();
        Assert.assertEquals(GenericClass.class, getType());
        Assert.assertEquals(GenericClass.class, getChildType(expressions.get((index++)), 0));
        // inheritedRawGeneric.fifth = new GenericClass();
        Assert.assertEquals(GenericClass.class, getType());
        Assert.assertEquals(GenericClass.class, getChildType(expressions.get((index++)), 0));
        // parameterRawGeneric.fifth = new GenericClass();
        Assert.assertEquals(GenericClass.class, getType());
        Assert.assertEquals(GenericClass.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testPrimarySimpleGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessPrimaryGenericSimple.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // this.genericField.first = "";
        Assert.assertEquals(String.class, getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 1, String.class, Double.class);
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 2));
        // (this).genericField.second = new Double(0);
        Assert.assertEquals(Double.class, getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 1, String.class, Double.class);
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 2));
        // this.genericTypeArg.second.second = new Double(0);
        Assert.assertEquals(Double.class, getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 2, Number.class, Double.class);
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 3));
        // (this).genericField.generic.generic.generic.first = new Double(0);
        Assert.assertEquals(Double.class, getType());
        Assert.assertEquals(Double.class, getChildType(expressions.get((index++)), 5));
        // (this).fieldA = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 1));
        // this.fieldB.generic.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 3));
        // super.fieldA = new Long(0);
        Assert.assertEquals(Long.class, getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 0, Long.class);
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 1));
        // super.fieldB.generic.second = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 3));
        // this.field.first = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 2));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessGenericNested() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericNested.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // n.field = null;
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // n.generic.first = null;
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessStatic() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessStatic.class, AbstractJavaTypeNode.class, "//StatementExpression/PrimaryExpression");
        int index = 0;
        // staticPrimitive = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // staticGeneric.first = new Long(0);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // StaticMembers.staticPrimitive = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.staticPrimitive = 10;
        Assert.assertEquals(Integer.TYPE, getType());
        Assert.assertEquals(Integer.TYPE, getChildType(expressions.get((index++)), 0));
        // net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers
        // .staticGeneric.generic.second = new Long(10);
        Assert.assertEquals(Long.class, getType());
        Assert.assertEquals(Long.class, getChildType(expressions.get((index++)), 0));
        // staticPrimitive = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // staticChar = 3.1; // it's a double
        Assert.assertEquals(Double.TYPE, getType());
        Assert.assertEquals(Double.TYPE, getChildType(expressions.get((index++)), 0));
        // FieldAccessStatic.Nested.staticPrimitive = "";
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 0));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodPotentialApplicability() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodPotentialApplicability.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // int a = vararg("");
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // int b = vararg("", 10);
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // String c = notVararg(0, 0);
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Number d = noArguments();
        Assert.assertEquals(Number.class, getType());
        Assert.assertEquals(Number.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Number.class, getChildType(expressions.get((index++)), 1));
        // Number e = field.noArguments();
        Assert.assertEquals(Number.class, getType());
        Assert.assertEquals(Number.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Number.class, getChildType(expressions.get((index++)), 1));
        // int f = this.vararg("");
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 2));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodAccessibility() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodAccessibility.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // SuperClassA a = inheritedA();
        Assert.assertEquals(SuperClassA.class, getType());
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassA.class, getChildType(expressions.get((index++)), 1));
        // SuperClassB b = inheritedB();
        Assert.assertEquals(SuperClassB.class, getType());
        Assert.assertEquals(SuperClassB.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassB.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodFirstPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodFirstPhase.class, "1.8", AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // int a = subtype(10, 'a', "");
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // Exception b = vararg((Object) null);
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // Set<String> set = new HashSet<>();
        Assert.assertEquals(HashSet.class, getType());
        // List<String> myList = new ArrayList<>();
        Assert.assertEquals(ArrayList.class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodMostSpecific() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodMostSpecific.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // String a = moreSpecific((Number) null, (AbstractCollection) null);
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Exception b = moreSpecific((Integer) null, (AbstractList) null);
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // int c = moreSpecific((Double) null, (RoleList) null);
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodSecondPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodSecondPhase.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // String a = boxing(10, "");
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Exception b = boxing('a', "");
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // int c = boxing(10L, "");
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // String d = unboxing("", (Integer) null);
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Exception e = unboxing("", (Character) null);
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // int f = unboxing("", (Byte) null);
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodThirdPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodThirdPhase.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // Exception a = vararg(10, (Number) null, (Number) null);
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // Exception b = vararg(10);
        Assert.assertEquals(Exception.class, getType());
        Assert.assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(Exception.class, getChildType(expressions.get((index++)), 1));
        // int c = vararg(10, "", "", "");
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // String d = mostSpecific(10, 10, 10);
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodStaticAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodStaticAccess.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // int a = primitiveStaticMethod();
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // StaticMembers b = staticInstanceMethod();
        Assert.assertEquals(StaticMembers.class, getType());
        Assert.assertEquals(StaticMembers.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(StaticMembers.class, getChildType(expressions.get((index++)), 1));
        // int c = StaticMembers.primitiveStaticMethod();
        Assert.assertEquals(int.class, getType());
        Assert.assertEquals(int.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(int.class, getChildType(expressions.get((index++)), 1));
        // String c = MethodStaticAccess.Nested.primitiveStaticMethod();
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodGenericExplicit() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodGenericExplicit.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // String s = this.<String>foo();
        Assert.assertEquals(String.class, getType());
        Assert.assertEquals(String.class, getChildType(expressions.get(index), 1));
        Assert.assertEquals(String.class, getChildType(expressions.get((index++)), 2));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testGenericArrays() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(GenericsArrays.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // List<String> var = Arrays.asList(params);
        AbstractJavaTypeNode expression = expressions.get((index++));
        // TODO : Type inference is still incomplete, we fail to detect the return type of the method
        // assertEquals(List.class, expression.getTypeDefinition().getType());
        // assertEquals(String.class, expression.getTypeDefinition().getGenericType(0).getType());
        // List<String> var2 = Arrays.<String>asList(params);
        AbstractJavaTypeNode expression2 = expressions.get((index++));
        Assert.assertEquals(List.class, getType());
        Assert.assertEquals(String.class, getType());
        // List<String[]> var3 = Arrays.<String[]>asList(params);
        AbstractJavaTypeNode expression3 = expressions.get((index++));
        Assert.assertEquals(List.class, getType());
        Assert.assertEquals(String[].class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInference() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(GenericMethodsImplicit.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // SuperClassA2 a = bar((SuperClassA) null, (SuperClassAOther) null, null, (SuperClassAOther2) null);
        Assert.assertEquals(SuperClassA2.class, getType());
        Assert.assertEquals(SuperClassA2.class, getChildType(expressions.get(index), 0));
        Assert.assertEquals(SuperClassA2.class, getChildType(expressions.get((index++)), 1));
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInferenceVarargsZeroArity() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(VarargsZeroArity.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // int var = aMethod();
        Assert.assertEquals(int.class, getType());
        // String var2 = aMethod("");
        Assert.assertEquals(String.class, getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInferenceVarargsAsFixedArity() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(VarargsAsFixedArity.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");
        int index = 0;
        // int var = aMethod("");
        Assert.assertEquals(int.class, getType());
        // String var2 = aMethod();
        Assert.assertEquals(String.class, getType());
        // String var3 = aMethod("", "");
        Assert.assertEquals(String.class, getType());
        // String var4 = aMethod(new Object[] { null });
        Assert.assertEquals(String.class, getType());
        // null literal has null type
        Assert.assertNull(getType());
        // Make sure we got them all
        Assert.assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testJavaTypeDefinitionEquals() {
        JavaTypeDefinition a = forClass(Integer.class);
        JavaTypeDefinition b = forClass(Integer.class);
        // test non-generic types
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(a, null);
        // test generic arg equality
        b = forClass(List.class, a);
        a = forClass(List.class, a);
        Assert.assertEquals(a, b);
        a = forClass(List.class, forClass(String.class));
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);
        // test raw vs proper, proper vs raw
        a = forClass(JavaTypeDefinitionEquals.class);
        b = forClass(JavaTypeDefinitionEquals.class, forClass(List.class, a));
        Assert.assertEquals(a, b);
        Assert.assertEquals(b, a);
    }

    @Test
    public void testJavaTypeDefinitionGetSuperTypeSet() {
        JavaTypeDefinition originalTypeDef = forClass(List.class, forClass(Integer.class));
        Set<JavaTypeDefinition> set = originalTypeDef.getSuperTypeSet();
        Assert.assertEquals(set.size(), 4);
        TestCase.assertTrue(set.contains(forClass(Object.class)));
        TestCase.assertTrue(set.contains(originalTypeDef));
        TestCase.assertTrue(set.contains(forClass(Collection.class, forClass(Integer.class))));
        TestCase.assertTrue(set.contains(forClass(Iterable.class, forClass(Integer.class))));
    }

    @Test
    public void testJavaTypeDefinitionGetErasedSuperTypeSet() {
        JavaTypeDefinition originalTypeDef = forClass(List.class, forClass(Integer.class));
        Set<Class<?>> set = originalTypeDef.getErasedSuperTypeSet();
        Assert.assertEquals(set.size(), 4);
        TestCase.assertTrue(set.contains(Object.class));
        TestCase.assertTrue(set.contains(Collection.class));
        TestCase.assertTrue(set.contains(Iterable.class));
        TestCase.assertTrue(set.contains(List.class));
    }

    @Test
    public void testMethodInitialBounds() throws NoSuchMethodException {
        JavaTypeDefinition context = forClass(GenericMethodsImplicit.class, forClass(Thread.class));
        List<Variable> variables = new ArrayList<>();
        List<Bound> initialBounds = new ArrayList<>();
        Method method = GenericMethodsImplicit.class.getMethod("foo");
        MethodTypeResolution.produceInitialBounds(method, context, variables, initialBounds);
        Assert.assertEquals(initialBounds.size(), 6);
        // A
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(0), forClass(Object.class), SUBTYPE)));
        // B
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(1), forClass(Number.class), SUBTYPE)));
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(1), forClass(Runnable.class), SUBTYPE)));
        // C
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(2), variables.get(3), SUBTYPE)));
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(2), forClass(Object.class), SUBTYPE)));
        // D
        TestCase.assertTrue(initialBounds.contains(new Bound(variables.get(3), forClass(Thread.class), SUBTYPE)));
    }

    @Test
    public void testMethodInitialConstraints() throws NoSuchMethodException, JaxenException {
        List<AbstractJavaNode> expressions = selectNodes(GenericMethodsImplicit.class, AbstractJavaNode.class, "//ArgumentList");
        List<Variable> variables = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            variables.add(new Variable());
        }
        Method method = GenericMethodsImplicit.class.getMethod("bar", Object.class, Object.class, Integer.class, Object.class);
        ASTArgumentList argList = ((ASTArgumentList) (expressions.get(0)));
        List<Constraint> constraints = MethodTypeResolution.produceInitialConstraints(method, argList, variables);
        Assert.assertEquals(constraints.size(), 3);
        // A
        TestCase.assertTrue(constraints.contains(new Constraint(forClass(SuperClassA.class), variables.get(0), LOOSE_INVOCATION)));
        TestCase.assertTrue(constraints.contains(new Constraint(forClass(SuperClassAOther.class), variables.get(0), LOOSE_INVOCATION)));
        // B
        TestCase.assertTrue(constraints.contains(new Constraint(forClass(SuperClassAOther2.class), variables.get(1), LOOSE_INVOCATION)));
    }

    @Test
    public void testMethodParameterization() throws NoSuchMethodException {
        ASTArgumentList argList = selectNodes(GenericMethodsImplicit.class, ASTArgumentList.class).get(0);
        JavaTypeDefinition context = forClass(GenericMethodsImplicit.class, forClass(Thread.class));
        Method method = GenericMethodsImplicit.class.getMethod("bar", Object.class, Object.class, Integer.class, Object.class);
        MethodType inferedMethod = MethodTypeResolution.parameterizeInvocation(context, method, argList);
        Assert.assertEquals(inferedMethod.getParameterTypes().get(0), forClass(SuperClassA2.class));
        Assert.assertEquals(inferedMethod.getParameterTypes().get(1), forClass(SuperClassA2.class));
        Assert.assertEquals(inferedMethod.getParameterTypes().get(2), forClass(Integer.class));
        Assert.assertEquals(inferedMethod.getParameterTypes().get(3), forClass(SuperClassAOther2.class));
    }

    @Test
    public void testNestedAllocationExpressions() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(NestedAllocationExpressions.class);
        List<ASTAllocationExpression> allocs = acu.findDescendantsOfType(ASTAllocationExpression.class);
        Assert.assertFalse(allocs.get(0).isAnonymousClass());
        Assert.assertEquals(Thread.class, getType());
        TestCase.assertTrue(allocs.get(1).isAnonymousClass());
        // FUTURE 1.8 use Class.getTypeName() instead of toString
        TestCase.assertTrue(getType().toString().endsWith("NestedAllocationExpressions$1"));
    }

    @Test
    public void testAnnotatedTypeParams() {
        parseAndTypeResolveForString("public class Foo { public static <T extends @NonNull Enum<?>> T getEnum() { return null; } }", "1.8");
    }

    @Test
    public void testMethodOverrides() throws Exception {
        parseAndTypeResolveForClass(SubTypeUsage.class, "1.8");
    }

    @Test
    public void testMethodWildcardParam() throws Exception {
        parseAndTypeResolveForClass(MethodGenericParam.class, "1.8");
    }

    @Test
    public void testAbstractMethodReturnType() throws Exception {
        parseAndTypeResolveForClass(AbstractReturnTypeUseCase.class, "1.8");
    }

    @Test
    public void testMethodOverloaded() throws Exception {
        parseAndTypeResolveForClass(OverloadedMethodsUsage.class, "1.8");
    }

    @Test
    public void testVarArgsMethodUseCase() throws Exception {
        parseAndTypeResolveForClass(VarArgsMethodUseCase.class, "1.8");
    }

    @Test
    public void testLocalGenericClass() throws Exception {
        parseAndTypeResolveForClass(LocalGenericClass.class, "9");
    }
}

