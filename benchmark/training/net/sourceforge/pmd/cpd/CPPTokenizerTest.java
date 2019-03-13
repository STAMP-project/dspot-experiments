/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import Tokenizer.OPTION_SKIP_BLOCKS;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CPPTokenizerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testUTFwithBOM() {
        Tokens tokens = parse("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n");
        Assert.assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        Assert.assertEquals(15, tokens.size());
    }

    @Test
    public void testUnicodeSupport() {
        String code = "\ufeff" + (((((((((("#include <iostream>\n" + "#include <string>\n") + "\n") + "// example\n") + "\n") + "int main()\n") + "{\n") + "    std::string text(\"\u0105\u0119\u0107\u015b\u017a\u0144\u00f3\");\n") + "    std::cout << text;\n") + "    return 0;\n") + "}\n");
        Tokens tokens = parse(code);
        Assert.assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        Assert.assertEquals(24, tokens.size());
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        String code = "#include <iostream>\n" + ((((((((("#include <string>\n" + "\n") + "// CPD-OFF\n") + "int main()\n") + "{\n") + "    std::string text(\"\u0105\u0119\u0107\u015b\u017a\u0144\u00f3\");\n") + "    std::cout << text;\n") + "    return 0;\n") + "// CPD-ON\n") + "}\n");
        Tokens tokens = parse(code);
        Assert.assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        Assert.assertEquals(2, tokens.size());// "}" + EOF

    }

    @Test
    public void testMultiLineMacros() {
        Tokens tokens = parse(CPPTokenizerTest.TEST1);
        Assert.assertEquals(7, tokens.size());
    }

    @Test
    public void testDollarSignInIdentifier() {
        parse(CPPTokenizerTest.TEST2);
    }

    @Test
    public void testDollarSignStartingIdentifier() {
        parse(CPPTokenizerTest.TEST3);
    }

    @Test
    public void testWideCharacters() {
        parse(CPPTokenizerTest.TEST4);
    }

    @Test
    public void testTokenizerWithSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = parse(test, true, new Tokens());
        Assert.assertEquals(19, tokens.size());
    }

    @Test
    public void testTokenizerWithSkipBlocksPattern() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = new Tokens();
        try {
            parse(test, true, "#if debug|#endif", tokens);
        } catch (TokenMgrError ignored) {
            // ignored
        }
        Assert.assertEquals(31, tokens.size());
    }

    @Test
    public void testTokenizerWithoutSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = new Tokens();
        try {
            parse(test, false, tokens);
        } catch (TokenMgrError ignored) {
            // ignored
        }
        Assert.assertEquals(37, tokens.size());
    }

    // ASM code containing the '@' character
    @Test
    public void testAsmWithAtSign() {
        Tokens tokens = parse(CPPTokenizerTest.TEST7);
        Assert.assertEquals(22, tokens.size());
    }

    @Test
    public void testEOLCommentInPreprocessingDirective() {
        parse(("#define LSTFVLES_CPP  //*" + (PMD.EOL)));
    }

    @Test
    public void testEmptyCharacter() {
        Tokens tokens = parse(("std::wstring wsMessage( sMessage.length(), L'');" + (PMD.EOL)));
        Assert.assertEquals(15, tokens.size());
    }

    @Test
    public void testHexCharacter() {
        Tokens tokens = parse(("if (*pbuf == \'\\0x05\')" + (PMD.EOL)));
        Assert.assertEquals(8, tokens.size());
    }

    @Test
    public void testWhiteSpaceEscape() {
        Tokens tokens = parse(("szPath = m_sdcacheDir + _T(\"\\    oMedia\");" + (PMD.EOL)));
        Assert.assertEquals(10, tokens.size());
    }

    @Test
    public void testRawStringLiteral() {
        String code = "const char* const KDefaultConfig = R\"(\n" + ((((("    [Sinks.1]\n" + "    Destination=Console\n") + "    AutoFlush=true\n") + "    Format=\"[%TimeStamp%] %ThreadId% %QueryIdHigh% %QueryIdLow% %LoggerFile%:%Line% (%Severity%) - %Message%\"\n") + "    Filter=\"%Severity% >= WRN\"\n") + ")\";\n");
        Tokens tokens = parse(code);
        Assert.assertTrue(((TokenEntry.getEOF()) != (tokens.getTokens().get(0))));
        Assert.assertEquals(9, tokens.size());
    }

    @Test
    public void testLexicalErrorFilename() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(OPTION_SKIP_BLOCKS, Boolean.toString(false));
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/issue-1559.cpp"), StandardCharsets.UTF_8);
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(test, "issue-1559.cpp"));
        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(properties);
        expectedException.expect(TokenMgrError.class);
        expectedException.expectMessage("Lexical error in file issue-1559.cpp at");
        tokenizer.tokenize(code, new Tokens());
    }

    private static final String TEST1 = ((((((((((((("#define FOO a +\\" + (PMD.EOL)) + "            b +\\") + (PMD.EOL)) + "            c +\\") + (PMD.EOL)) + "            d +\\") + (PMD.EOL)) + "            e +\\") + (PMD.EOL)) + "            f +\\") + (PMD.EOL)) + "            g") + (PMD.EOL)) + " void main() {}";

    private static final String TEST2 = " void main() { int x$y = 42; }";

    private static final String TEST3 = " void main() { int $x = 42; }";

    private static final String TEST4 = " void main() { char x = L'a'; }";

    private static final String TEST7 = ((((((((("asm void eSPI_boot()" + (PMD.EOL)) + "{") + (PMD.EOL)) + "  // setup stack pointer") + (PMD.EOL)) + "  lis r1, _stack_addr@h") + (PMD.EOL)) + "  ori r1, r1, _stack_addr@l") + (PMD.EOL)) + "}";
}

