/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MatchAlgorithmTest {
    private static final String LINE_1 = "public class Foo { ";

    private static final String LINE_2 = " public void bar() {";

    private static final String LINE_3 = "  System.out.println(\"hello\");";

    private static final String LINE_4 = "  System.out.println(\"hello\");";

    private static final String LINE_5 = "  int i = 5";

    private static final String LINE_6 = "  System.out.print(\"hello\");";

    private static final String LINE_7 = " }";

    private static final String LINE_8 = "}";

    @Test
    public void testSimple() throws IOException {
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(MatchAlgorithmTest.getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        Assert.assertEquals(41, tokens.size());
        Map<String, SourceCode> codeMap = new HashMap<>();
        codeMap.put("Foo.java", sourceCode);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        Assert.assertFalse(matches.hasNext());
        Iterator<Mark> marks = match.iterator();
        Mark mark1 = marks.next();
        Mark mark2 = marks.next();
        Assert.assertFalse(marks.hasNext());
        Assert.assertEquals(3, mark1.getBeginLine());
        Assert.assertEquals("Foo.java", mark1.getFilename());
        Assert.assertEquals(MatchAlgorithmTest.LINE_3, mark1.getSourceCodeSlice());
        Assert.assertEquals(4, mark2.getBeginLine());
        Assert.assertEquals("Foo.java", mark2.getFilename());
        Assert.assertEquals(MatchAlgorithmTest.LINE_4, mark2.getSourceCodeSlice());
    }

    @Test
    public void testIgnore() throws IOException {
        JavaTokenizer tokenizer = new JavaTokenizer();
        tokenizer.setIgnoreLiterals(true);
        tokenizer.setIgnoreIdentifiers(true);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(MatchAlgorithmTest.getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        Map<String, SourceCode> codeMap = new HashMap<>();
        codeMap.put("Foo.java", sourceCode);
        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        Assert.assertFalse(matches.hasNext());
        Iterator<Mark> marks = match.iterator();
        marks.next();
        marks.next();
        marks.next();
        Assert.assertFalse(marks.hasNext());
    }
}

