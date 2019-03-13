/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.commandline;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the basic features of the command-line interface.
 *
 * @author Marcin Mi?kowski
 */
public class MainTest extends AbstractSecurityTestCase {
    private final File enTestFile;

    private final File xxRuleFile;

    private final File xxFalseFriendFile;

    private final File bitextFile;

    private ByteArrayOutputStream out;

    private ByteArrayOutputStream err;

    private PrintStream stdout;

    private PrintStream stderr;

    public MainTest() throws IOException {
        enTestFile = writeToTempFile(("This is an test.\n\n" + ("This is a test of of language tool.\n\n" + "This is is a test of language tool.")));
        xxRuleFile = writeToTempXMLFile(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((("<rules lang=\"en\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n") + "<category id=\"CAT1\" name=\"trivial category\">\n") + "<rule id=\"EXAMPLE_RULE\" name=\"External rule to test\">\n") + "<pattern><token>language</token><token>tool</token></pattern>\n") + "<message>This is wrong!</message>\n") + "<example correction=\"\">language tool</example>\n") + "</rule></category></rules>")));
        xxFalseFriendFile = writeToTempXMLFile(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((((((((((((("<!DOCTYPE rules SYSTEM \"false-friends.dtd\">\n" + "<rules>\n") + "<rulegroup id=\"LASKA_SK_PL\">\n") + "        <rule>\n") + "            <pattern lang=\"sk\">\n") + "                <token>l\u00e1ska</token>\n") + "            </pattern>\n") + "            <translation lang=\"pl\">mi\u0142o\u015b\u0107</translation>\n") + "        </rule>\n") + "        <rule>\n") + "            <pattern lang=\"pl\">\n") + "                <token inflected=\"yes\">mi\u0142o\u015b\u0107</token>\n") + "            </pattern>\n") + "            <translation lang=\"sk\">laska</translation>\n") + "        </rule>\n") + "    </rulegroup>\n</rules>\n") + "    ")));
        bitextFile = writeToTempXMLFile(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ((((((((((((((((((((("<rules targetLang=\"pl\" xsi:noNamespaceSchemaLocation=\"../bitext.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" + "<category id=\"CAT1\" name=\"idioms\">\n") + "<rule lang=\"pl\" id=\"red_herring\" name=\"Red herring -> odwraca uwag\u0119\">\n") + "<pattern>\n") + "\t<source lang=\"en\">\n") + "\t\t<token>is</token>\n") + "\t\t<token>a</token>\n") + "\t\t<token>red</token>\n") + "\t\t<token>herring</token>\n") + "\t</source>\n") + "\t<target>\n") + "\t\t<token>jest</token>\n") + "\t\t<token>czerwony</token>\n") + "\t\t<token>\u015bled\u017a</token>\n") + "\t</target>\n") + "</pattern>\n") + "<message>Czy chodzi\u0142o o <suggestion>odwraca uwag\u0119</suggestion>?</message>\n") + "<example type=\"correct\"><srcExample>This is a red herring.</srcExample>\n") + "\t\t\t\t\t<trgExample>To odwraca uwag\u0119.</trgExample></example>\n") + "<example type=\"incorrect\" correction=\"odwraca uwag\u0119\"><srcExample>This <marker>is a red herring</marker>.</srcExample>\n") + "<trgExample>To <marker>jest czerwony \u015bled\u017a</marker>.</trgExample></example>\n") + "</rule></category></rules>\n")));
    }

    @Test
    public void testUsageMessage() throws Exception {
        try {
            String[] args = new String[]{ "-h" };
            Main.main(args);
            Assert.fail("LT should have exited with status 0!");
        } catch (AbstractSecurityTestCase.ExitException e) {
            String output = new String(this.out.toByteArray());
            Assert.assertTrue(output.contains("Usage: java -jar languagetool-commandline.jar"));
            Assert.assertEquals("Exit status", 1, e.status);
        }
    }

    @Test
    public void testPrintLanguages() throws Exception {
        try {
            String[] args = new String[]{ "--list" };
            Main.main(args);
            Assert.fail("LT should have exited with status 0!");
        } catch (AbstractSecurityTestCase.ExitException e) {
            String output = new String(this.out.toByteArray());
            Assert.assertTrue(output.contains("German"));
            Assert.assertTrue(output.contains("de-DE"));
            Assert.assertTrue(output.contains("English"));
            Assert.assertEquals("Exit status", 0, e.status);
        }
    }

    @Test
    public void testFileWithExternalRule() throws Exception {
        // note: we pretend this is Breton because the English language tool is already initialized
        String[] args = new String[]{ "-l", "br", "--rulefile", getRuleFilePath(), getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Breton")) == 0));
        Assert.assertTrue(stdout.contains("Rule ID: EXAMPLE_RULE"));
    }

    @Test
    public void testEnglishFile() throws Exception {
        String[] args = new String[]{ "-l", "en", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testEnglishFileAutoDetect() throws Exception {
        String[] args = new String[]{ "-adl", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(stderr.contains("Using English for file"));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testEnglishStdInAutoDetect() throws Exception {
        String test = "This is an test.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-adl" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Working on STDIN...")) == 0));
        Assert.assertTrue(stderr.contains("Using English for"));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testStdInWithExternalFalseFriends() throws Exception {
        String test = "L\u00e1ska!\n";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "sk", "--falsefriends", getExternalFalseFriends(), "-m", "pl", "-" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(stderr.contains("Expected text language: Slovak"));
        Assert.assertTrue(stderr.contains("Working on STDIN..."));
        Assert.assertTrue(stdout.contains("Rule ID: LASKA"));
    }

    @Test
    public void testEnglishFileVerbose() throws Exception {
        String[] args = new String[]{ "-l", "en", "-v", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
        String tagText = new String(this.err.toByteArray());
        Assert.assertTrue(("Got: " + tagText), tagText.contains("<S> This[this/DT,B-NP-singular|E-NP-singular] is[be/VBZ,B-VP] an[a/DT,B-NP-singular] test[test/NN,E-NP-singular].[./.,</S>,O]"));
    }

    @Test
    public void testEnglishFileApplySuggestions() throws Exception {
        String[] args = new String[]{ "-l", "en", "--apply", getTestFilePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(output.contains(("This is a test.\n\n" + ("This is a test of language tool.\n\n" + "This is a test of language tool."))));// \r\n in Windows tests at the end...

    }

    @Test
    public void testEnglishStdIn1() throws Exception {
        String test = "This is an test.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testEnglishStdIn2() throws Exception {
        String test = "This is an test.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en", "-" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 9, Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testEnglishStdIn3() throws Exception {
        String test = "This is an test.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en", "-a", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertEquals("This is a test.", output);
    }

    @Test
    public void testEnglishStdIn4() throws Exception {
        System.setIn(new FileInputStream(enTestFile));
        String[] args = new String[]{ "-l", "en", "--api", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(("Got: " + output), output.contains(("<error fromy=\"4\" fromx=\"5\" toy=\"4\" " + (("tox=\"10\" ruleId=\"ENGLISH_WORD_REPEAT_RULE\" msg=\"Possible typo: you repeated a word\" shortmsg=\"Word repetition\" " + "replacements=\"is\" context=\"....  This is a test of of language tool.  This is is a test of language tool. \"") + " contextoffset=\"48\" offset=\"60\" errorlength=\"5\" category=\"Miscellaneous\" categoryid=\"MISC\" locqualityissuetype=\"duplication\"/>"))));
    }

    @Test
    public void testEnglishStdInJsonOutput() throws Exception {
        System.setIn(new FileInputStream(enTestFile));
        String[] args = new String[]{ "-l", "en", "--json", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(("Got: " + output), output.contains("\"matches\":[{\"message\":\"Use \\\"a\\\" instead of \'an\'"));
        Assert.assertTrue(("Got: " + output), output.contains("\"shortMessage\":\"Wrong article\""));
        Assert.assertTrue(("Got: " + output), output.contains("\"replacements\":[{\"value\":\"a\"}]"));
        Assert.assertTrue(("Got: " + output), output.contains("\"offset\":8"));
        Assert.assertTrue(("Got: " + output), output.contains("\"length\":2"));
        Assert.assertTrue(("Got: " + output), output.contains("\"context\":{\"text\":\"This is an test.  This is a test of of language tool.  ...\""));
        Assert.assertTrue(("Got: " + output), output.contains("\"id\":\"EN_A_VS_AN\""));
        Assert.assertTrue(("Got: " + output), output.contains("\"description\":\"Use of"));
        Assert.assertTrue(("Got: " + output), output.contains("\"issueType\":\"misspelling\""));
        Assert.assertTrue(("Got: " + output), output.contains("\"category\":{\"id\":\"MISC\",\"name\":\"Miscellaneous\"}"));
        Assert.assertTrue(("Got: " + output), output.contains("\"message\":\"Possible typo: you repeated a word\""));
        Assert.assertTrue(("Got: " + output), output.contains("\"sentence\":\"This is an test.\""));
        Assert.assertTrue("Doesn't display Time", (!(output.contains("Time: "))));
        Assert.assertTrue("Json start check", output.startsWith("{\"software\":{\"name\":\"LanguageTool\",\"version\":"));
        Assert.assertTrue("Json end check", output.endsWith("}]}"));
    }

    // test line mode vs. para mode
    // first line mode
    @Test
    public void testEnglishLineMode() throws Exception {
        String test = "This is what I mean\nand you know it.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en", "-a", "-b", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertEquals("This is what I mean\nAnd you know it.", output);
    }

    // first line mode
    @Test
    public void testEnglishParaMode() throws Exception {
        String test = "This is what I mean\nand you know it.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en", "-a", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertEquals("This is what I mean\nand you know it.", output);
    }

    @Test
    public void testPolishStdInDefaultOff() throws Exception {
        String test = "To jest test, kt?ry zrobi?em, kt?ry mi si? podoba.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "pl", "-e", "PL_WORD_REPEAT", "-" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stderr.contains("Working on STDIN..."));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 31, Rule ID: PL_WORD_REPEAT"));
    }

    @Test
    public void testPolishApiStdInDefaultOff() throws Exception {
        String test = "To jest test, kt?ry zrobi?em, kt?ry mi si? podoba.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "--api", "-l", "pl", "-eo", "-e", "PL_WORD_REPEAT", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertThat(StringUtils.countMatches(output, "<error "), CoreMatchers.is(1));
        Assert.assertThat(StringUtils.countMatches(output, "<matches "), CoreMatchers.is(1));
        Assert.assertThat(StringUtils.countMatches(output, "</matches>"), CoreMatchers.is(1));// https://github.com/languagetool-org/languagetool/issues/251

    }

    @Test
    public void testPolishApiStdInDefaultOffNoErrors() throws Exception {
        String test = "To jest test.";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "--api", "-l", "pl", "-e", "PL_WORD_REPEAT", "-" };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertThat(StringUtils.countMatches(output, "<error "), CoreMatchers.is(0));
        Assert.assertThat(StringUtils.countMatches(output, "<matches "), CoreMatchers.is(1));
        Assert.assertThat(StringUtils.countMatches(output, "</matches>"), CoreMatchers.is(1));
    }

    @Test
    public void testPolishSpelling() throws Exception {
        String test = "Zwuasdac?";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "pl", "-e", "MORFOLOGIK_RULE_PL_PL", "-" };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stderr.contains("Working on STDIN..."));
        Assert.assertTrue(stdout.contains("1.) Line 1, column 1, Rule ID: MORFOLOGIK_RULE_PL_PL"));
    }

    @Test
    public void testEnglishFileRuleDisabled() throws Exception {
        String[] args = new String[]{ "-l", "en", "-d", "EN_A_VS_AN", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue((!(stdout.contains("Rule ID: EN_A_VS_AN"))));
    }

    @Test
    public void testEnglishFileRuleEnabled() throws Exception {
        String[] args = new String[]{ "-l", "en", "-e", "EN_A_VS_AN", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(stdout.contains("Rule ID: EN_A_VS_AN"));
    }

    @Test
    public void testEnglishFileFakeRuleEnabled() throws Exception {
        String test = "Zwuasdac?";
        System.setIn(new ByteArrayInputStream(test.getBytes()));
        String[] args = new String[]{ "-l", "en", "-e", "FOO_BAR_BLABLA", "-" };
        Main.main(args);
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
    }

    @Test
    public void testEnglishFileAPI() throws Exception {
        String[] args = new String[]{ "-l", "en", "--api", getTestFilePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(((output.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) == 0));
        Assert.assertTrue(output.contains(("<error fromy=\"0\" fromx=\"8\" toy=\"0\" tox=\"10\" ruleId=\"EN_A_VS_AN\" " + (((("msg=\"Use &apos;a&apos; instead of &apos;an&apos; if the following word doesn&apos;t start with a vowel sound, e.g. &apos;a sentence&apos;, " + "&apos;a university&apos;\" ") + "shortmsg=\"Wrong article\" ") + "replacements=\"a\" context=\"This is an test.  This is a test of of language tool.  ...\" ") + "contextoffset=\"8\" offset=\"8\" errorlength=\"2\" category=\"Miscellaneous\" categoryid=\"MISC\" locqualityissuetype=\"misspelling\"/>"))));
    }

    @Test
    public void testGermanFileWithURL() throws Exception {
        File input = writeToTempFile("Ward ihr zufrieden damit?");
        String[] args = new String[]{ "-l", "de", "--api", input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(((output.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) == 0));
        Assert.assertTrue(output.contains("ruleId=\"WARD_VS_WART\" subId=\"1\""));
        // check URL part
        Assert.assertTrue(output.contains("url=\"https://www.korrekturen.de/beliebte_fehler/ward.shtml\""));
        // now check in normal mode and check for URL
        String[] args2 = new String[]{ "-l", "de", input.getAbsolutePath() };
        Main.main(args2);
        String output2 = new String(this.out.toByteArray());
        Assert.assertTrue(output2.contains("More info: https://www.korrekturen.de/beliebte_fehler/ward.shtml"));
    }

    @Test
    public void testPolishFileAPI() throws Exception {
        File input = writeToTempFile("To jest ?winia kt?ra si? ?lini.");
        String[] args = new String[]{ "-l", "pl", "--api", "-c", "utf-8", input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray(), "UTF-8");
        Assert.assertTrue(((output.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) == 0));
        Assert.assertTrue(output.contains("<error fromy=\"0\" fromx=\"8\" toy=\"0\" tox=\"20\" ruleId=\"BRAK_PRZECINKA_KTORY\""));
        // This tests whether XML encoding is actually UTF-8:
        Assert.assertTrue(output.contains("msg=\"Brak przecinka w tym fragmencie zdania. Przecinek prawdopodobnie nale\u017cy postawi\u0107 tak: &apos;\u015bwinia, kt\u00f3ra&apos;.\""));
        Assert.assertTrue(output.contains("replacements=\"\u015bwinia, kt\u00f3ra\" "));
        Assert.assertTrue(output.contains("context=\"To jest \u015bwinia kt\u00f3ra si\u0119 \u015blini."));
        Assert.assertTrue(output.contains("contextoffset=\"8\" offset=\"8\" errorlength=\"12\" category=\"B\u0142\u0119dy interpunkcyjne\""));
    }

    @Test
    public void testPolishLineNumbers() throws Exception {
        File input = writeToTempFile(("Test.\n" + (((((("Test.\n" + "Test.\n") + "Test.\n") + "Test.\n") + "Test.\n") + "\n") + "Test kt?ry wyka?e b??d.")));
        String[] args = new String[]{ "-l", "pl", "-c", "utf-8", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray(), "UTF-8");
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stdout.contains("Line 8, column 1, Rule ID: BRAK_PRZECINKA_KTORY"));
    }

    @Test
    public void testEnglishTagger() throws Exception {
        String[] args = new String[]{ "-l", "en", "--taggeronly", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English")) == 0));
        Assert.assertTrue(("Got: " + stdout), stdout.contains("<S> This[this/DT,B-NP-singular|E-NP-singular] is[be/VBZ,B-VP] an[a/DT,B-NP-singular] test[test/NN,E-NP-singular].[./.,</S>,O]"));
    }

    @Test
    public void testBitextMode() throws Exception {
        File input = writeToTempFile(("This is not actual.\tTo nie jest aktualne.\n" + ("Test\tTest\n" + "ab\tVery strange data indeed, much longer than input")));
        String[] args = new String[]{ "-l", "pl", "-c", "UTF-8", "--bitext", "-m", "en", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stdout.contains("Message: Hint: \"aktualny\" (Polish) means \"current\", \"(the) latest\", \"up-to-date\" (English). Did you mean \'rzeczywisty\'?"));
        Assert.assertTrue(stdout.contains("Line 1, column 32, Rule ID: ACTUAL"));
        Assert.assertTrue(stdout.contains("Line 3, column 3, Rule ID: TRANSLATION_LENGTH"));
    }

    @Test
    public void testBitextModeWithDisabledRule() throws Exception {
        File input = writeToTempFile(("this is not actual.\tTo nie jest aktualne.\n" + ("test\tTest\n" + "ab\tVery strange data indeed, much longer than input")));
        String[] args = new String[]{ "-l", "pl", "--bitext", "-m", "en", "-d", "UPPERCASE_SENTENCE_START,TRANSLATION_LENGTH", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stdout.contains("Message: Hint: \"aktualny\" (Polish) means \"current\", \"(the) latest\", \"up-to-date\" (English). Did you mean \'rzeczywisty\'?"));
        Assert.assertTrue(stdout.contains("Line 1, column 32, Rule ID: ACTUAL"));
        Assert.assertFalse(stdout.contains("Rule ID: TRANSLATION_LENGTH"));
    }

    @Test
    public void testBitextModeWithEnabledRule() throws Exception {
        File input = writeToTempFile(("this is not actual.\tTo nie jest aktualne.\n" + ("test\tTest\n" + "ab\tVery strange data indeed, much longer than input")));
        String[] args = new String[]{ "-l", "pl", "--bitext", "-m", "en", "-e", "TRANSLATION_LENGTH", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertFalse(stdout.contains("Message: Hint: \"aktualny\" (Polish) means \"current\", \"(the) latest\", \"up-to-date\" (English). Did you mean \'rzeczywisty\'?"));
        Assert.assertFalse(stdout.contains("Line 1, column 32, Rule ID: ACTUAL"));
        Assert.assertTrue(stdout.contains("Rule ID: TRANSLATION_LENGTH"));
    }

    @Test
    public void testBitextModeApply() throws Exception {
        File input = writeToTempFile("There is a dog.\tNie ma psa.");
        String[] args = new String[]{ "-l", "pl", "--bitext", "-m", "en", "--apply", input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(output.startsWith("Istnieje psa."));
    }

    @Test
    public void testBitextWithExternalRule() throws Exception {
        File input = writeToTempFile("This is a red herring.\tTo jest czerwony \u015bled\u017a.");
        String[] args = new String[]{ "-l", "pl", "-c", "UTF-8", "--bitext", "-m", "en", "--bitextrules", bitextFile.getAbsolutePath(), input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(("red_herring rule should be in the output" + output), output.contains("Rule ID: red_herring"));
    }

    @Test
    public void testListUnknown() throws Exception {
        String[] args = new String[]{ "-l", "pl", "-u", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue(stdout.contains("Unknown words: [., This, an, is, language, of, tool]"));
    }

    @Test
    public void testNoListUnknown() throws Exception {
        String[] args = new String[]{ "-l", "pl", getTestFilePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Polish")) == 0));
        Assert.assertTrue((!(stdout.contains("Unknown words: [This, an, is]"))));
    }

    @Test
    public void testLangWithCountryVariant() throws Exception {
        File input = writeToTempFile("This is modelling.");
        String[] args = new String[]{ "-l", "en-US", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: English (US)")) == 0));
        Assert.assertTrue(stdout.contains("MORFOLOGIK_RULE_EN_US"));
    }

    @Test
    public void testValencianCatalan() throws Exception {
        File input = writeToTempFile("Que sigui aix?.");
        String[] args = new String[]{ "-l", "ca-ES-valencia", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Catalan (Valencian)")) == 0));
        Assert.assertTrue(stdout.contains("EXIGEIX_VERBS_VALENCIANS"));
    }

    @Test
    public void testCatalan() throws Exception {
        File input = writeToTempFile("Que siga aix?.");
        String[] args = new String[]{ "-l", "ca-ES", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Catalan")) == 0));
        Assert.assertTrue(stdout.contains("EXIGEIX_VERBS_CENTRAL"));
    }

    @Test
    public void testCatalan2() throws Exception {
        File input = writeToTempFile("Que siga aix?.");
        String[] args = new String[]{ "-l", "ca", input.getAbsolutePath() };
        Main.main(args);
        String stdout = new String(this.out.toByteArray());
        String stderr = new String(this.err.toByteArray());
        Assert.assertTrue(((stderr.indexOf("Expected text language: Catalan")) == 0));
        Assert.assertTrue(stdout.contains("EXIGEIX_VERBS_CENTRAL"));
    }

    @Test
    public void testNoXmlFilteringByDefault() throws Exception {
        File input = writeToTempFile("This < is is > filtered.");
        String[] args = new String[]{ input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertTrue(output.contains("ENGLISH_WORD_REPEAT_RULE"));
    }

    @Test
    public void testXmlFiltering() throws Exception {
        File input = writeToTempFile("This < is is > filtered.");
        String[] args = new String[]{ "--xmlfilter", input.getAbsolutePath() };
        Main.main(args);
        String output = new String(this.out.toByteArray());
        Assert.assertFalse(output.contains("ENGLISH_WORD_REPEAT_RULE"));
    }
}

