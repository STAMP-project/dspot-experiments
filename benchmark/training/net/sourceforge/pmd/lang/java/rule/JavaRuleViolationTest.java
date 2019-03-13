/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;


import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Philip Graf
 */
public class JavaRuleViolationTest {
    /**
     * Verifies that {@link JavaRuleViolation} sets the variable name for an
     * {@link ASTFormalParameter} node.
     */
    @Test
    public void testASTFormalParameterVariableName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        final ASTFormalParameter node = ast.getFirstDescendantOfType(ASTFormalParameter.class);
        final RuleContext context = new RuleContext();
        final JavaRuleViolation violation = new JavaRuleViolation(null, context, node, null);
        Assert.assertEquals("x", violation.getVariableName());
    }

    /**
     * Tests that the method name is taken correctly from the given node.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1250/">#1250</a>
     */
    @Test
    public void testMethodName() {
        ASTCompilationUnit ast = parse("class Foo { void bar(int x) {} }");
        ASTMethodDeclaration md = ast.getFirstDescendantOfType(ASTMethodDeclaration.class);
        final RuleContext context = new RuleContext();
        final JavaRuleViolation violation = new JavaRuleViolation(null, context, md, null);
        Assert.assertEquals("bar", violation.getMethodName());
    }

    /**
     * Tests that the enum name is taken correctly from the given node.
     */
    @Test
    public void testEnumName() {
        ASTCompilationUnit ast = parse("enum Foo {FOO; void bar(int x) {} }");
        ASTMethodDeclaration md = ast.getFirstDescendantOfType(ASTMethodDeclaration.class);
        final RuleContext context = new RuleContext();
        final JavaRuleViolation violation = new JavaRuleViolation(null, context, md, null);
        Assert.assertEquals("Foo", violation.getClassName());
    }

    /**
     * Tests that the class name is taken correctly, even if the node is outside
     * of a class scope, e.g. a import declaration.
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1529/">#1529</a>
     */
    @Test
    public void testPackageAndClassName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);
        JavaRuleViolation violation = new JavaRuleViolation(null, new RuleContext(), importNode, null);
        Assert.assertEquals("pkg", violation.getPackageName());
        Assert.assertEquals("Foo", violation.getClassName());
    }

    @Test
    public void testPackageAndEnumName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; public enum FooE { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);
        JavaRuleViolation violation = new JavaRuleViolation(null, new RuleContext(), importNode, null);
        Assert.assertEquals("pkg", violation.getPackageName());
        Assert.assertEquals("FooE", violation.getClassName());
    }

    @Test
    public void testDefaultPackageAndClassName() {
        ASTCompilationUnit ast = parse("import java.util.List; public class Foo { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);
        JavaRuleViolation violation = new JavaRuleViolation(null, new RuleContext(), importNode, null);
        Assert.assertEquals("", violation.getPackageName());
        Assert.assertEquals("Foo", violation.getClassName());
    }

    @Test
    public void testPackageAndMultipleClassesName() {
        ASTCompilationUnit ast = parse("package pkg; import java.util.List; class Foo { } public class Bar { }");
        ASTImportDeclaration importNode = ast.getFirstDescendantOfType(ASTImportDeclaration.class);
        JavaRuleViolation violation = new JavaRuleViolation(null, new RuleContext(), importNode, null);
        Assert.assertEquals("pkg", violation.getPackageName());
        Assert.assertEquals("Bar", violation.getClassName());
    }
}

