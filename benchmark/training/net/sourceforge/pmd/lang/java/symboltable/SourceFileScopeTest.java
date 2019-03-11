/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import org.junit.Assert;
import org.junit.Test;


public class SourceFileScopeTest extends STBBaseTst {
    @Test
    public void testClassDeclAppears() {
        parseCode(SourceFileScopeTest.TEST1);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.getScope().getDeclarations();
        ClassNameDeclaration classNameDeclaration = ((ClassNameDeclaration) (m.keySet().iterator().next()));
        Assert.assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testPackageIsEmptyString() {
        parseCode(SourceFileScopeTest.TEST1);
        ASTCompilationUnit decl = acu;
        Assert.assertEquals(decl.getScope().getEnclosingScope(SourceFileScope.class).getPackageName(), "");
    }

    @Test
    public void testPackageNameFound() {
        parseCode(SourceFileScopeTest.TEST2);
        ASTCompilationUnit decl = acu;
        Assert.assertEquals(decl.getScope().getEnclosingScope(SourceFileScope.class).getPackageName(), "foo.bar");
    }

    @Test
    public void testNestedClasses() {
        parseCode(SourceFileScopeTest.TEST3);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.getScope().getDeclarations();
        Iterator<NameDeclaration> iterator = m.keySet().iterator();
        ClassNameDeclaration classNameDeclaration = ((ClassNameDeclaration) (iterator.next()));
        Assert.assertEquals(classNameDeclaration.getImage(), "Foo");
        Assert.assertFalse(iterator.hasNext());
    }

    private static final String TEST1 = "public class Foo {}" + (PMD.EOL);

    private static final String TEST2 = (((("package foo.bar;" + (PMD.EOL)) + "public class Foo {") + (PMD.EOL)) + "}") + (PMD.EOL);

    private static final String TEST3 = (((((("public class Foo {" + (PMD.EOL)) + " public class Bar {") + (PMD.EOL)) + " }") + (PMD.EOL)) + "}") + (PMD.EOL);
}

