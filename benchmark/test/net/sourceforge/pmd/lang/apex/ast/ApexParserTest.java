/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;


import apex.jorje.semantic.ast.compilation.Compilation;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.sourceforge.pmd.lang.ast.Node;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class ApexParserTest {
    @Test
    public void understandsSimpleFile() {
        // Setup
        String code = "@isTest\n public class SimpleClass {\n" + ((("    @isTest\n public static void testAnything() {\n" + "        \n") + "    }\n") + "}");
        // Exercise
        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(code);
        // Verify
        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        Assert.assertEquals(4, methods.size());
    }

    private String testCodeForLineNumbers = "public class SimpleClass {\n"// line 1
     + (((("    public void method1() {\n"// line 2
     + "        System.out.println(\"abc\");\n")// line 3
     + "        // this is a comment\n")// line 4
     + "    }\n")// line 5
     + "}\n");// line 6


    @Test
    public void verifyLineColumNumbers() {
        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(testCodeForLineNumbers);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    public void verifyLineColumNumbersWithWindowsLineEndings() {
        String windowsLineEndings = testCodeForLineNumbers.replaceAll(" \n", "\r\n");
        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(windowsLineEndings);
        assertLineNumbersForTestCode(rootNode);
    }

    @Test
    public void verifyEndLine() {
        String code = "public class SimpleClass {\n"// line 1
         + (((("    public void method1() {\n"// line 2
         + "    }\n")// line 3
         + "    public void method2() {\n")// line 4
         + "    }\n")// line 5
         + "}\n");// line 6

        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(code);
        Node method1 = rootNode.jjtGetChild(1);
        Assert.assertEquals("Wrong begin line", 2, method1.getBeginLine());
        Assert.assertEquals("Wrong end line", 3, method1.getEndLine());
        Node method2 = rootNode.jjtGetChild(2);
        Assert.assertEquals("Wrong begin line", 4, method2.getBeginLine());
        Assert.assertEquals("Wrong end line", 5, method2.getEndLine());
    }

    @Test
    public void parsesRealWorldClasses() throws Exception {
        File directory = new File("src/test/resources");
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if ((file.isFile()) && (file.getName().endsWith(".cls"))) {
                String sourceCode = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(sourceCode);
                Assert.assertNotNull(rootNode);
            }
        }
    }

    /**
     * See github issue #1546
     *
     * @see <a href="https://github.com/pmd/pmd/issues/1546">[apex] PMD parsing exception for Apex classes using 'inherited sharing' keyword</a>
     */
    @Test
    public void parseInheritedSharingClass() throws IOException {
        String source = IOUtils.toString(ApexParserTest.class.getResourceAsStream("InheritedSharing.cls"), StandardCharsets.UTF_8);
        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(source);
        Assert.assertNotNull(rootNode);
    }

    /**
     * See bug #1485
     *
     * @see <a href="https://sourceforge.net/p/pmd/bugs/1485/">#1485 [apex] Analysis of some apex classes cause a stackoverflow error</a>
     */
    @Test
    public void stackOverflowDuringClassParsing() throws Exception {
        String source = IOUtils.toString(ApexParserTest.class.getResourceAsStream("StackOverflowClass.cls"), StandardCharsets.UTF_8);
        ApexNode<Compilation> rootNode = ApexParserTestHelpers.parse(source);
        Assert.assertNotNull(rootNode);
        int count = visitPosition(rootNode, 0);
        Assert.assertEquals(427, count);
    }
}

