/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.filter;


import org.junit.Test;


public class RegexStringFilterTest {
    @Test
    public void testFilterAbsoluteWithExtension() {
        String pattern = "C:/workspace/project/X.java";
        RegexStringFilterTest.verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        RegexStringFilterTest.verifyFilterFalse("same relative path w/o extension", pattern, "X");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        RegexStringFilterTest.verifyFilterTrue("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        RegexStringFilterTest.verifyFilterFalse("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterAbsoluteWithoutExtension() {
        String pattern = "C:/workspace/project/X";
        RegexStringFilterTest.verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        RegexStringFilterTest.verifyFilterFalse("same relative path w/o extension", pattern, "X");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        RegexStringFilterTest.verifyFilterFalse("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        RegexStringFilterTest.verifyFilterTrue("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterRelativeWithExtension() {
        String pattern = ".*X.java";
        RegexStringFilterTest.verifyFilterTrue("same relative path w/ extension", pattern, "X.java");
        RegexStringFilterTest.verifyFilterFalse("same relative path w/o extension", pattern, "X");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        RegexStringFilterTest.verifyFilterTrue("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        RegexStringFilterTest.verifyFilterFalse("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterRelativeWithoutExtension() {
        String pattern = ".*X";
        RegexStringFilterTest.verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        RegexStringFilterTest.verifyFilterTrue("same relative path w/o extension", pattern, "X");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        RegexStringFilterTest.verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        RegexStringFilterTest.verifyFilterFalse("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        RegexStringFilterTest.verifyFilterTrue("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        RegexStringFilterTest.verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testEndsWith() {
        // These patterns cannot be optimized to use String.endsWith
        RegexStringFilterTest.verifyEndsWith("no literal path", ".*", null);
        RegexStringFilterTest.verifyEndsWith("not ends with", "x", null);
        RegexStringFilterTest.verifyEndsWith("glob on end", ".*XXX.*", null);
        RegexStringFilterTest.verifyEndsWith("special character \\", ".*X\\Y", null);
        RegexStringFilterTest.verifyEndsWith("special character [", ".*X[Y", null);
        RegexStringFilterTest.verifyEndsWith("special character (", ".*X(Y", null);
        RegexStringFilterTest.verifyEndsWith("special character *", ".*X*Y", null);
        RegexStringFilterTest.verifyEndsWith("special character ?", ".*X?Y", null);
        RegexStringFilterTest.verifyEndsWith("special character +", ".*X+Y", null);
        RegexStringFilterTest.verifyEndsWith("special character |", ".*X|Y", null);
        RegexStringFilterTest.verifyEndsWith("special character {", ".*X{Y", null);
        RegexStringFilterTest.verifyEndsWith("special character $", ".*X$Y", null);
        RegexStringFilterTest.verifyEndsWith("too many .", ".*X.Y.java", null);
        // These patterns can be optimized to use String.endsWith
        RegexStringFilterTest.verifyEndsWith("unescaped .", ".*X.java", "X.java");
        RegexStringFilterTest.verifyEndsWith("escaped .", ".*X\\.java", "X.java");
        RegexStringFilterTest.verifyEndsWith("no extension", ".*X", "X");
        RegexStringFilterTest.verifyEndsWith("begin anchor, unescaped .", "^.*X.java", "X.java");
        RegexStringFilterTest.verifyEndsWith("begin anchor, escaped .", "^.*X\\.java", "X.java");
        RegexStringFilterTest.verifyEndsWith("begin anchor, no extension", "^.*X", "X");
        RegexStringFilterTest.verifyEndsWith("end anchor, unescaped .", ".*X.java$", "X.java");
        RegexStringFilterTest.verifyEndsWith("end anchor, escaped .", ".*X\\.java$", "X.java");
        RegexStringFilterTest.verifyEndsWith("end anchor, no extension", ".*X$", "X");
        RegexStringFilterTest.verifyEndsWith("begin and end anchors, unescaped .", "^.*X.java$", "X.java");
        RegexStringFilterTest.verifyEndsWith("begin and end anchors, escaped .", "^.*X\\.java$", "X.java");
        RegexStringFilterTest.verifyEndsWith("begin and end anchors, no extension", "^.*X$", "X");
    }
}

