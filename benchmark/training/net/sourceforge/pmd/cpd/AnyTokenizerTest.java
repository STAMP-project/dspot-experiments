/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import net.sourceforge.pmd.PMD;
import org.junit.Assert;
import org.junit.Test;


public class AnyTokenizerTest {
    @Test
    public void testMultiLineMacros() {
        AnyTokenizer tokenizer = new AnyTokenizer();
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(AnyTokenizerTest.TEST1));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        Assert.assertEquals(30, tokens.size());
    }

    private static final String TEST1 = (((((((((((((((("using System;" + (PMD.EOL)) + "namespace HelloNameSpace {") + (PMD.EOL)) + "") + (PMD.EOL)) + "    public class HelloWorld {") + (PMD.EOL)) + "        static void Main(string[] args) {") + (PMD.EOL)) + "            Console.WriteLine(\"Hello World!\");") + (PMD.EOL)) + "        }") + (PMD.EOL)) + "    }") + (PMD.EOL)) + "}") + (PMD.EOL);
}

