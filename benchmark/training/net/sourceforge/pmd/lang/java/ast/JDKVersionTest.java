/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;


import java.util.List;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import org.junit.Assert;
import org.junit.Test;


public class JDKVersionTest {
    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk14_enum.java"));
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("jdk14_enum.java"));
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk15_enum.java"));
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk14_enum.java"));
    }

    // enum keyword/identifier
    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test1.java"));
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test2.java"));
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test3.java"));
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test4.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test5.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("assert_test7.java"));
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith13() {
        ParserTstUtil.parseJava13(JDKVersionTest.loadSource("assert_test5.java"));
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith13() {
        ParserTstUtil.parseJava13(JDKVersionTest.loadSource("assert_test6.java"));
    }

    // assert keyword/identifier
    @Test
    public void testVarargsShouldPassWith15() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk15_varargs.java"));
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("jdk15_varargs.java"));
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk15_forloop.java"));
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk15_forloop_with_modifier.java"));
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() {
        ParserTstUtil.parseJava14(JDKVersionTest.loadSource("jdk15_forloop.java"));
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("jdk15_generics.java"));
    }

    @Test
    public void testVariousParserBugs() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("fields_bug.java"));
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("gt_bug.java"));
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("annotations_bug.java"));
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("constant_field_in_annotation_bug.java"));
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("generic_in_field.java"));
    }

    @Test
    public void testNestedClassInMethodBug() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("inner_bug.java"));
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("inner_bug2.java"));
    }

    @Test
    public void testGenericsInMethodCall() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("generic_in_method_call.java"));
    }

    @Test
    public void testGenericINAnnotation() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("generic_in_annotation.java"));
    }

    @Test
    public void testGenericReturnType() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("generic_return_type.java"));
    }

    @Test
    public void testMultipleGenerics() {
        // See java/lang/concurrent/CopyOnWriteArraySet
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("funky_generics.java"));
        // See java/lang/concurrent/ConcurrentHashMap
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("multiple_generics.java"));
    }

    @Test
    public void testAnnotatedParams() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("annotated_params.java"));
    }

    @Test
    public void testAnnotatedLocals() {
        ParserTstUtil.parseJava15(JDKVersionTest.loadSource("annotated_locals.java"));
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith13Test2() {
        ParserTstUtil.parseJava13(JDKVersionTest.loadSource("assert_test5_a.java"));
    }

    @Test
    public final void testBinaryAndUnderscoresInNumericalLiterals() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_numerical_literals.java"));
    }

    @Test
    public final void testStringInSwitch() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_string_in_switch.java"));
    }

    @Test
    public final void testGenericDiamond() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_generic_diamond.java"));
    }

    @Test
    public final void testTryWithResources() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_try_with_resources.java"));
    }

    @Test
    public final void testTryWithResourcesSemi() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_try_with_resources_semi.java"));
    }

    @Test
    public final void testTryWithResourcesMulti() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_try_with_resources_multi.java"));
    }

    @Test
    public final void testTryWithResourcesWithAnnotations() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_try_with_resources_with_annotations.java"));
    }

    @Test
    public final void testMulticatch() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_multicatch.java"));
    }

    @Test
    public final void testMulticatchWithAnnotations() {
        ParserTstUtil.parseJava17(JDKVersionTest.loadSource("jdk17_multicatch_with_annotations.java"));
    }

    @Test(expected = ParseException.class)
    public final void jdk9PrivateInterfaceMethodsInJava18() {
        ParserTstUtil.parseJava18(JDKVersionTest.loadSource("jdk9_private_interface_methods.java"));
    }

    @Test
    public final void testPrivateMethods() {
        ParserTstUtil.parseJava18("public class Foo { private void bar() { } }");
    }

    @Test
    public final void testNestedPrivateMethods() {
        ParserTstUtil.parseJava18("public interface Baz { public static class Foo { private void bar() { } } }");
    }

    @Test
    public final void jdk9PrivateInterfaceMethods() {
        ParserTstUtil.parseJava9(JDKVersionTest.loadSource("jdk9_private_interface_methods.java"));
    }

    @Test
    public final void jdk9InvalidIdentifierInJava18() {
        ParserTstUtil.parseJava18(JDKVersionTest.loadSource("jdk9_invalid_identifier.java"));
    }

    @Test(expected = ParseException.class)
    public final void jdk9InvalidIdentifier() {
        ParserTstUtil.parseJava9(JDKVersionTest.loadSource("jdk9_invalid_identifier.java"));
    }

    @Test(expected = ParseException.class)
    public final void jdk9AnonymousDiamondInJava8() {
        ParserTstUtil.parseJava18(JDKVersionTest.loadSource("jdk9_anonymous_diamond.java"));
    }

    @Test
    public final void jdk9AnonymousDiamond() {
        ParserTstUtil.parseJava9(JDKVersionTest.loadSource("jdk9_anonymous_diamond.java"));
    }

    @Test(expected = ParseException.class)
    public final void jdk9ModuleInfoInJava8() {
        ParserTstUtil.parseJava18(JDKVersionTest.loadSource("jdk9_module_info.java"));
    }

    @Test
    public final void jdk9ModuleInfo() {
        ParserTstUtil.parseJava9(JDKVersionTest.loadSource("jdk9_module_info.java"));
    }

    @Test(expected = ParseException.class)
    public final void jdk9TryWithResourcesInJava8() {
        ParserTstUtil.parseJava18(JDKVersionTest.loadSource("jdk9_try_with_resources.java"));
    }

    @Test
    public final void jdk9TryWithResources() {
        ParserTstUtil.parseJava9(JDKVersionTest.loadSource("jdk9_try_with_resources.java"));
    }

    @Test
    public final void jdk7PrivateMethodInnerClassInterface1() {
        ASTCompilationUnit acu = ParserTstUtil.parseJava17(JDKVersionTest.loadSource("private_method_in_inner_class_interface1.java"));
        List<ASTMethodDeclaration> methods = acu.findDescendantsOfType(ASTMethodDeclaration.class, true);
        Assert.assertEquals(3, methods.size());
        for (ASTMethodDeclaration method : methods) {
            Assert.assertFalse(method.isInterfaceMember());
        }
    }

    @Test
    public final void jdk7PrivateMethodInnerClassInterface2() {
        try {
            ASTCompilationUnit acu = ParserTstUtil.parseJava17(JDKVersionTest.loadSource("private_method_in_inner_class_interface2.java"));
            Assert.fail("Expected exception");
        } catch (ParseException e) {
            Assert.assertTrue(e.getMessage().startsWith("Line 19"));
        }
    }
}

