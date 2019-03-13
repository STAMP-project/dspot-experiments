package org.jabref.logic.bibtexkeypattern;


import FieldName.AUTHOR;
import FieldName.CROSSREF;
import java.util.Optional;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.importer.ParseException;
import org.jabref.logic.importer.fileformat.BibtexParser;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.model.util.FileUpdateMonitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BibtexKeyGeneratorTest {
    private static final String AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_1 = "Isaac Newton";

    private static final String AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2 = "Isaac Newton and James Maxwell";

    private static final String AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_3 = "Isaac Newton and James Maxwell and Albert Einstein";

    private static final String AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1 = "Wil van der Aalst";

    private static final String AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2 = "Wil van der Aalst and Tammo van Lessen";

    private static final String AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1 = "I. Newton";

    private static final String AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2 = "I. Newton and J. Maxwell";

    private static final String AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3 = "I. Newton and J. Maxwell and A. Einstein";

    private static final String AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4 = "I. Newton and J. Maxwell and A. Einstein and N. Bohr";

    private static final String AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5 = "I. Newton and J. Maxwell and A. Einstein and N. Bohr and Harry Unknown";

    private static final String TITLE_STRING_ALL_LOWER_FOUR_SMALL_WORDS_ONE_EN_DASH = "application migration effort in the cloud - the case of cloud platforms";

    private static final String TITLE_STRING_ALL_LOWER_FIRST_WORD_IN_BRACKETS_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON = "{BPEL} conformance in open source engines: the case of static analysis";

    private static final String TITLE_STRING_CASED = "Process Viewing Patterns";

    private static final String TITLE_STRING_CASED_ONE_UPPER_WORD_ONE_SMALL_WORD = "BPMN Conformance in Open Source Engines";

    private static final String TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AT_THE_BEGINNING = "The Difference Between Graph-Based and Block-Structured Business Process Modelling Languages";

    private static final String TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON = "Cloud Computing: The Next Revolution in IT";

    private static final String TITLE_STRING_CASED_TWO_SMALL_WORDS_ONE_CONNECTED_WORD = "Towards Choreography-based Process Distribution in the Cloud";

    private static final String TITLE_STRING_CASED_FOUR_SMALL_WORDS_TWO_CONNECTED_WORDS = "On the Measurement of Design-Time Adaptability for Process-Based Systems ";

    private static ImportFormatPreferences importFormatPreferences;

    private final FileUpdateMonitor fileMonitor = new DummyFileUpdateMonitor();

    @Test
    public void testAndInAuthorName() throws ParseException {
        Optional<BibEntry> entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Simon Holland}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Holland", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth", new BibDatabase()), true));
    }

    @Test
    public void testCrossrefAndInAuthorNames() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "Simon Holland");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("Holland", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "auth", database), true));
    }

    @Test
    public void testAndAuthorNames() throws ParseException {
        String bibtexString = "@ARTICLE{whatevery, author={Mari D. Herland and Mona-Iren Hauge and Ingeborg M. Helgeland}}";
        Optional<BibEntry> entry = BibtexParser.singleFromString(bibtexString, BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("HerlandHaugeHelgeland", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "authors3", new BibDatabase()), true));
    }

    @Test
    public void testCrossrefAndAuthorNames() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "Mari D. Herland and Mona-Iren Hauge and Ingeborg M. Helgeland");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("HerlandHaugeHelgeland", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "authors3", database), true));
    }

    @Test
    public void testSpecialLatexCharacterInAuthorName() throws ParseException {
        Optional<BibEntry> entry = BibtexParser.singleFromString("@ARTICLE{kohn, author={Simon Popovi\\v{c}ov\\\'{a}}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Popovicova", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "auth", new BibDatabase()), true));
    }

    /**
     * Test for https://sourceforge.net/forum/message.php?msg_id=4498555 Test the Labelmaker and all kind of accents ? ?
     * ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?
     */
    @Test
    public void testMakeLabelAndCheckLegalKeys() throws ParseException {
        Optional<BibEntry> entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas K?ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Koe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Aoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Eoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Ioe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Loe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Noe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Ooe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Roe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Soe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Uoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Yoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Zoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
    }

    /**
     * Test the Labelmaker and with accent grave Chars to test: "?????";
     */
    @Test
    public void testMakeLabelAndCheckLegalKeysAccentGrave() throws ParseException {
        Optional<BibEntry> entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Aoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Eoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Ioe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Ooe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andreas ??ning}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("Uoe", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Oraib Al-Ketan}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("AlK", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
        entry0 = BibtexParser.singleFromString("@ARTICLE{kohn, author={Andr?s D'Alessandro}, year={2000}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("DAl", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry0.get(), "auth3", new BibDatabase()), true));
    }

    /**
     * Tests if checkLegalKey replaces Non-ASCII chars. There are quite a few chars that should be replaced. Perhaps
     * there is a better method than the current.
     *
     * @see BibtexKeyGenerator#checkLegalKey(String)
     */
    @Test
    public void testCheckLegalKey() {
        // not tested/ not in hashmap UNICODE_CHARS:
        // ? ?   ? ? ? ?   ? ?   ? ?   ? ? ? ?   ? ?   ? ? ? ? ? ?   ? ? ? ?   ? ?	? ? ? ? ? ?
        // " ? ? ? ? ? ?   " +
        // "? ?   ? ?  " +
        // "? ?   ? ? ? ?   ? ?   ? ?   ? ? ? ?   ? ?   ? ? ? ? ? ?   ? ?
        String accents = "?????????? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?";
        String expectedResult = "AaEeIiOoUuAaCcEeGgHhIiJjOoSsUuWwYy";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "????????????";
        expectedResult = "AeaeEeIiOeoeUeueYy";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?";
        expectedResult = "CcGgKkLlNnRrSsTt";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ? ? ?";
        expectedResult = "AaEeGgIiOoUu";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ?";
        expectedResult = "CcEeGgIiZz";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ?";
        expectedResult = "AaEeIiOoUu";// O or Q? o or q?

        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ? ? ?";
        expectedResult = "AaEeIiOoUuYy";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?";
        expectedResult = "AaCcDdEeIiLlNnOoRrSsTtUuZz";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        expectedResult = "AaEeIiNnOoUuYy";
        accents = "??????????????";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        accents = "? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?";
        expectedResult = "DdHhLlLlMmNnRrRrSsTt";
        Assertions.assertEquals(expectedResult, BibtexKeyGenerator.cleanKey(accents, true));
        String totest = "? ? ? ? ? ? ? ? ? ?   ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?  ? ? ? ? ? ? ? ? ? ? ? ?    " + ((((("? ? ? ? ? ? ? ? ? ? ? ? ? ?   ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?" + " ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?   ") + "? ? ? ? ? ? ? ? ? ? ? ?") + "? ? ? ? ? ? ? ? ? ? ? ?   ") + "? ? ? ? ? ? ? ? ? ?   ? ? ? ? ? ? ? ? ? ?   ") + "? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ? ?   ");
        String expectedResults = "AaEeIiOoUuAaCcEeGgHhIiJjOoSsUuWwYyAeaeEeIiOeoeUeueYy" + ((((("AaEeIiNnOoUuYyCcGgKkLlNnRrSsTt" + "AaCcDdEeIiLlNnOoRrSsTtUuZz") + "AaEeIiOoUuYy") + "AaEeGgIiOoUu") + "CcEeGgIiZzAaEeIiOoUu") + "DdHhLlLlMmNnRrRrSsTt");
        Assertions.assertEquals(expectedResults, BibtexKeyGenerator.cleanKey(totest, true));
    }

    @Test
    public void testFirstAuthor() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.firstAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5));
        Assertions.assertEquals("Newton", BibtexKeyGenerator.firstAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        // https://sourceforge.net/forum/message.php?msg_id=4498555
        Assertions.assertEquals("K{\\\"o}ning", BibtexKeyGenerator.firstAuthor("K{\\\"o}ning"));
        Assertions.assertEquals("", BibtexKeyGenerator.firstAuthor(""));
    }

    @Test
    public void testFirstAuthorNull() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.firstAuthor(null));
    }

    @Test
    public void testUniversity() throws ParseException {
        Optional<BibEntry> entry = BibtexParser.singleFromString("@ARTICLE{kohn, author={{Link{\\\"{o}}ping University}}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("UniLinkoeping", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "auth", new BibDatabase()), true));
    }

    @Test
    public void testcrossrefUniversity() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "{Link{\\\"{o}}ping University}}");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("UniLinkoeping", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "auth", database), true));
    }

    @Test
    public void testDepartment() throws ParseException {
        Optional<BibEntry> entry = BibtexParser.singleFromString("@ARTICLE{kohn, author={{Link{\\\"{o}}ping University, Department of Electrical Engineering}}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("UniLinkoepingEE", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "auth", new BibDatabase()), true));
    }

    @Test
    public void testcrossrefDepartment() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "{Link{\\\"{o}}ping University, Department of Electrical Engineering}}");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("UniLinkoepingEE", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "auth", database), true));
    }

    @Test
    public void testSchool() throws ParseException {
        Optional<BibEntry> entry = BibtexParser.singleFromString("@ARTICLE{kohn, author={{Link{\\\"{o}}ping University, School of Computer Engineering}}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("UniLinkoepingCE", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "auth", new BibDatabase()), true));
    }

    @Test
    public void testcrossrefSchool() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "{Link{\\\"{o}}ping University, School of Computer Engineering}}");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("UniLinkoepingCE", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "auth", database), true));
    }

    @Test
    public void testInstituteOfTechnology() throws ParseException {
        Optional<BibEntry> entry = BibtexParser.singleFromString("@ARTICLE{kohn, author={{Massachusetts Institute of Technology}}}", BibtexKeyGeneratorTest.importFormatPreferences, fileMonitor);
        Assertions.assertEquals("MIT", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry.get(), "auth", new BibDatabase()), true));
    }

    @Test
    public void testcrossrefInstituteOfTechnology() throws Exception {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("entry2");
        entry2.setField(AUTHOR, "{Massachusetts Institute of Technology}");
        database.insertEntry(entry1);
        database.insertEntry(entry2);
        Assertions.assertEquals("MIT", BibtexKeyGenerator.cleanKey(BibtexKeyGenerator.generateKey(entry1, "auth", database), true));
    }

    @Test
    public void testAuthIniN() {
        Assertions.assertEquals("NMEB", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5, 4));
        Assertions.assertEquals("NMEB", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4, 4));
        Assertions.assertEquals("NeME", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3, 4));
        Assertions.assertEquals("NeMa", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2, 4));
        Assertions.assertEquals("Newt", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 4));
        Assertions.assertEquals("", "");
        Assertions.assertEquals("N", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 1));
        Assertions.assertEquals("", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 0));
        Assertions.assertEquals("", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, (-1)));
        Assertions.assertEquals("Newton", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 6));
        Assertions.assertEquals("Newton", BibtexKeyGenerator.authIniN(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 7));
    }

    @Test
    public void testAuthIniNNull() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.authIniN(null, 3));
    }

    @Test
    public void testAuthIniNEmptyReturnsEmpty() {
        Assertions.assertEquals("", BibtexKeyGenerator.authIniN("", 1));
    }

    /**
     * Tests  [auth.auth.ea]
     */
    @Test
    public void authAuthEa() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.authAuthEa(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("Newton.Maxwell", BibtexKeyGenerator.authAuthEa(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("Newton.Maxwell.ea", BibtexKeyGenerator.authAuthEa(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_3));
    }

    @Test
    public void testAuthEaEmptyReturnsEmpty() {
        Assertions.assertEquals("", BibtexKeyGenerator.authAuthEa(""));
    }

    /**
     * Tests the [auth.etal] and [authEtAl] patterns
     */
    @Test
    public void testAuthEtAl() {
        // tests taken from the comments
        // [auth.etal]
        String delim = ".";
        String append = ".etal";
        Assertions.assertEquals("Newton.etal", BibtexKeyGenerator.authEtal(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_3, delim, append));
        Assertions.assertEquals("Newton.Maxwell", BibtexKeyGenerator.authEtal(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2, delim, append));
        // [authEtAl]
        delim = "";
        append = "EtAl";
        Assertions.assertEquals("NewtonEtAl", BibtexKeyGenerator.authEtal(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_3, delim, append));
        Assertions.assertEquals("NewtonMaxwell", BibtexKeyGenerator.authEtal(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2, delim, append));
    }

    /**
     * Test the [authshort] pattern
     */
    @Test
    public void testAuthShort() {
        // tests taken from the comments
        Assertions.assertEquals("NME+", BibtexKeyGenerator.authshort(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4));
        Assertions.assertEquals("NME", BibtexKeyGenerator.authshort(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
        Assertions.assertEquals("NM", BibtexKeyGenerator.authshort(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("Newton", BibtexKeyGenerator.authshort(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
    }

    @Test
    public void testAuthShortEmptyReturnsEmpty() {
        Assertions.assertEquals("", BibtexKeyGenerator.authshort(""));
    }

    /**
     * Test the [authN_M] pattern
     */
    @Test
    public void authNM() {
        Assertions.assertEquals("N", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 1, 1));
        Assertions.assertEquals("Max", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2, 3, 2));
        Assertions.assertEquals("New", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3, 3, 1));
        Assertions.assertEquals("Bo", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4, 2, 4));
        Assertions.assertEquals("Bohr", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5, 6, 4));
        Assertions.assertEquals("Aal", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1, 3, 1));
        Assertions.assertEquals("Less", BibtexKeyGenerator.authNofMth(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2, 4, 2));
        Assertions.assertEquals("", BibtexKeyGenerator.authNofMth("", 2, 4));
    }

    @Test
    public void authNMThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.authNofMth(null, 2, 4));
    }

    /**
     * Tests [authForeIni]
     */
    @Test
    public void firstAuthorForenameInitials() {
        Assertions.assertEquals("I", BibtexKeyGenerator.firstAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("I", BibtexKeyGenerator.firstAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("I", BibtexKeyGenerator.firstAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("I", BibtexKeyGenerator.firstAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2));
    }

    /**
     * Tests [authFirstFull]
     */
    @Test
    public void firstAuthorVonAndLast() {
        Assertions.assertEquals("vanderAalst", BibtexKeyGenerator.firstAuthorVonAndLast(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1));
        Assertions.assertEquals("vanderAalst", BibtexKeyGenerator.firstAuthorVonAndLast(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2));
    }

    @Test
    public void firstAuthorVonAndLastNoVonInName() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.firstAuthorVonAndLast(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("Newton", BibtexKeyGenerator.firstAuthorVonAndLast(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_2));
    }

    /**
     * Tests [authors]
     */
    @Test
    public void testAllAuthors() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.allAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("NewtonMaxwell", BibtexKeyGenerator.allAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("NewtonMaxwellEinstein", BibtexKeyGenerator.allAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
    }

    /**
     * Tests [authorsAlpha]
     */
    @Test
    public void authorsAlpha() {
        Assertions.assertEquals("New", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("NM", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("NME", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
        Assertions.assertEquals("NMEB", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4));
        Assertions.assertEquals("NME+", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5));
        Assertions.assertEquals("vdAal", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1));
        Assertions.assertEquals("vdAvL", BibtexKeyGenerator.authorsAlpha(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2));
    }

    /**
     * Tests [authorLast]
     */
    @Test
    public void lastAuthor() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("Maxwell", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("Einstein", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
        Assertions.assertEquals("Bohr", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4));
        Assertions.assertEquals("Unknown", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5));
        Assertions.assertEquals("Aalst", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1));
        Assertions.assertEquals("Lessen", BibtexKeyGenerator.lastAuthor(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2));
    }

    /**
     * Tests [authorLastForeIni]
     */
    @Test
    public void lastAuthorForenameInitials() {
        Assertions.assertEquals("I", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("J", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("A", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
        Assertions.assertEquals("N", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4));
        Assertions.assertEquals("H", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5));
        Assertions.assertEquals("W", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1));
        Assertions.assertEquals("T", BibtexKeyGenerator.lastAuthorForenameInitials(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2));
    }

    /**
     * Tests [authorIni]
     */
    @Test
    public void oneAuthorPlusIni() {
        Assertions.assertEquals("Newto", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1));
        Assertions.assertEquals("NewtoM", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2));
        Assertions.assertEquals("NewtoME", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3));
        Assertions.assertEquals("NewtoMEB", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4));
        Assertions.assertEquals("NewtoMEBU", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_5));
        Assertions.assertEquals("Aalst", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_1));
        Assertions.assertEquals("AalstL", BibtexKeyGenerator.oneAuthorPlusIni(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_WITH_VAN_COUNT_2));
    }

    /**
     * Tests the [authorsN] pattern. -> [authors1]
     */
    @Test
    public void testNAuthors1() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 1));
        Assertions.assertEquals("NewtonEtAl", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2, 1));
        Assertions.assertEquals("NewtonEtAl", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3, 1));
        Assertions.assertEquals("NewtonEtAl", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4, 1));
    }

    @Test
    public void testNAuthors1EmptyReturnEmpty() {
        Assertions.assertEquals("", BibtexKeyGenerator.nAuthors("", 1));
    }

    /**
     * Tests the [authorsN] pattern. -> [authors3]
     */
    @Test
    public void testNAuthors3() {
        Assertions.assertEquals("Newton", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_1, 3));
        Assertions.assertEquals("NewtonMaxwell", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_2, 3));
        Assertions.assertEquals("NewtonMaxwellEinstein", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_3, 3));
        Assertions.assertEquals("NewtonMaxwellEinsteinEtAl", BibtexKeyGenerator.nAuthors(BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_INITIAL_LASTNAME_FULL_COUNT_4, 3));
    }

    @Test
    public void testFirstPage() {
        Assertions.assertEquals("7", BibtexKeyGenerator.firstPage("7--27"));
        Assertions.assertEquals("27", BibtexKeyGenerator.firstPage("--27"));
        Assertions.assertEquals("", BibtexKeyGenerator.firstPage(""));
        Assertions.assertEquals("42", BibtexKeyGenerator.firstPage("42--111"));
        Assertions.assertEquals("7", BibtexKeyGenerator.firstPage("7,41,73--97"));
        Assertions.assertEquals("7", BibtexKeyGenerator.firstPage("41,7,73--97"));
        Assertions.assertEquals("43", BibtexKeyGenerator.firstPage("43+"));
    }

    @Test
    public void testPagePrefix() {
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L7--27"));
        Assertions.assertEquals("L--", BibtexKeyGenerator.pagePrefix("L--27"));
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L"));
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L42--111"));
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L7,L41,L73--97"));
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L41,L7,L73--97"));
        Assertions.assertEquals("L", BibtexKeyGenerator.pagePrefix("L43+"));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix("7--27"));
        Assertions.assertEquals("--", BibtexKeyGenerator.pagePrefix("--27"));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix(""));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix("42--111"));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix("7,41,73--97"));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix("41,7,73--97"));
        Assertions.assertEquals("", BibtexKeyGenerator.pagePrefix("43+"));
    }

    @Test
    public void testPagePrefixNull() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.pagePrefix(null));
    }

    @Test
    public void testLastPage() {
        Assertions.assertEquals("27", BibtexKeyGenerator.lastPage("7--27"));
        Assertions.assertEquals("27", BibtexKeyGenerator.lastPage("--27"));
        Assertions.assertEquals("", BibtexKeyGenerator.lastPage(""));
        Assertions.assertEquals("111", BibtexKeyGenerator.lastPage("42--111"));
        Assertions.assertEquals("97", BibtexKeyGenerator.lastPage("7,41,73--97"));
        Assertions.assertEquals("97", BibtexKeyGenerator.lastPage("7,41,97--73"));
        Assertions.assertEquals("43", BibtexKeyGenerator.lastPage("43+"));
    }

    @Test
    public void testLastPageNull() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.lastPage(null));
    }

    /**
     * Tests [veryShortTitle]
     */
    @Test
    public void veryShortTitle() {
        // veryShortTitle is getTitleWords with "1" as count
        int count = 1;
        Assertions.assertEquals("application", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FOUR_SMALL_WORDS_ONE_EN_DASH)));
        Assertions.assertEquals("BPEL", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FIRST_WORD_IN_BRACKETS_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON)));
        Assertions.assertEquals("Process", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED)));
        Assertions.assertEquals("BPMN", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_ONE_UPPER_WORD_ONE_SMALL_WORD)));
        Assertions.assertEquals("Difference", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AT_THE_BEGINNING)));
        Assertions.assertEquals("Cloud", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON)));
        Assertions.assertEquals("Towards", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_ONE_CONNECTED_WORD)));
        Assertions.assertEquals("Measurement", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_FOUR_SMALL_WORDS_TWO_CONNECTED_WORDS)));
    }

    /**
     * Tests [shortTitle]
     */
    @Test
    public void shortTitle() {
        // shortTitle is getTitleWords with "3" as count and removed small words
        int count = 3;
        Assertions.assertEquals("application migration effort", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FOUR_SMALL_WORDS_ONE_EN_DASH)));
        Assertions.assertEquals("BPEL conformance open", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FIRST_WORD_IN_BRACKETS_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON)));
        Assertions.assertEquals("Process Viewing Patterns", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED)));
        Assertions.assertEquals("BPMN Conformance Open", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_ONE_UPPER_WORD_ONE_SMALL_WORD)));
        Assertions.assertEquals("Difference Graph Based", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AT_THE_BEGINNING)));
        Assertions.assertEquals("Cloud Computing: Next", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON)));
        Assertions.assertEquals("Towards Choreography based", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_ONE_CONNECTED_WORD)));
        Assertions.assertEquals("Measurement Design Time", BibtexKeyGenerator.getTitleWords(count, BibtexKeyGenerator.removeSmallWords(BibtexKeyGeneratorTest.TITLE_STRING_CASED_FOUR_SMALL_WORDS_TWO_CONNECTED_WORDS)));
    }

    /**
     * Tests [camel]
     */
    @Test
    public void camel() {
        // camel capitalises and concatenates all the words of the title
        Assertions.assertEquals("ApplicationMigrationEffortInTheCloudTheCaseOfCloudPlatforms", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FOUR_SMALL_WORDS_ONE_EN_DASH));
        Assertions.assertEquals("BPELConformanceInOpenSourceEnginesTheCaseOfStaticAnalysis", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FIRST_WORD_IN_BRACKETS_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON));
        Assertions.assertEquals("ProcessViewingPatterns", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED));
        Assertions.assertEquals("BPMNConformanceInOpenSourceEngines", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_ONE_UPPER_WORD_ONE_SMALL_WORD));
        Assertions.assertEquals("TheDifferenceBetweenGraphBasedAndBlockStructuredBusinessProcessModellingLanguages", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AT_THE_BEGINNING));
        Assertions.assertEquals("CloudComputingTheNextRevolutionInIT", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON));
        Assertions.assertEquals("TowardsChoreographyBasedProcessDistributionInTheCloud", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_ONE_CONNECTED_WORD));
        Assertions.assertEquals("OnTheMeasurementOfDesignTimeAdaptabilityForProcessBasedSystems", BibtexKeyGenerator.getCamelizedTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_FOUR_SMALL_WORDS_TWO_CONNECTED_WORDS));
    }

    /**
     * Tests [title]
     */
    @Test
    public void title() {
        // title capitalises the significant words of the title
        // for the title case the concatenation happens at formatting, which is tested in MakeLabelWithDatabaseTest.java
        Assertions.assertEquals("Application Migration Effort in the Cloud the Case of Cloud Platforms", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FOUR_SMALL_WORDS_ONE_EN_DASH));
        Assertions.assertEquals("BPEL Conformance in Open Source Engines: the Case of Static Analysis", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_ALL_LOWER_FIRST_WORD_IN_BRACKETS_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON));
        Assertions.assertEquals("Process Viewing Patterns", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED));
        Assertions.assertEquals("BPMN Conformance in Open Source Engines", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_ONE_UPPER_WORD_ONE_SMALL_WORD));
        Assertions.assertEquals("The Difference between Graph Based and Block Structured Business Process Modelling Languages", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AT_THE_BEGINNING));
        Assertions.assertEquals("Cloud Computing: the Next Revolution in IT", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_SMALL_WORD_AFTER_COLON));
        Assertions.assertEquals("Towards Choreography Based Process Distribution in the Cloud", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_TWO_SMALL_WORDS_ONE_CONNECTED_WORD));
        Assertions.assertEquals("On the Measurement of Design Time Adaptability for Process Based Systems", BibtexKeyGenerator.camelizeSignificantWordsInTitle(BibtexKeyGeneratorTest.TITLE_STRING_CASED_FOUR_SMALL_WORDS_TWO_CONNECTED_WORDS));
    }

    @Test
    public void keywordNKeywordsSeparatedBySpace() {
        BibEntry entry = new BibEntry();
        entry.setField("keywords", "w1, w2a w2b, w3");
        String result = BibtexKeyGenerator.generateKey(entry, "keyword1");
        Assertions.assertEquals("w1", result);
        // check keywords with space
        result = BibtexKeyGenerator.generateKey(entry, "keyword2");
        Assertions.assertEquals("w2aw2b", result);
        // check out of range
        result = BibtexKeyGenerator.generateKey(entry, "keyword4");
        Assertions.assertEquals("", result);
    }

    @Test
    public void crossrefkeywordNKeywordsSeparatedBySpace() {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        BibEntry entry2 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry1);
        entry2.setField("keywords", "w1, w2a w2b, w3");
        String result = BibtexKeyGenerator.generateKey(entry1, "keyword1", database);
        Assertions.assertEquals("w1", result);
    }

    @Test
    public void keywordsNKeywordsSeparatedBySpace() {
        BibEntry entry = new BibEntry();
        entry.setField("keywords", "w1, w2a w2b, w3");
        // all keywords
        String result = BibtexKeyGenerator.generateKey(entry, "keywords");
        Assertions.assertEquals("w1w2aw2bw3", result);
        // check keywords with space
        result = BibtexKeyGenerator.generateKey(entry, "keywords2");
        Assertions.assertEquals("w1w2aw2b", result);
        // check out of range
        result = BibtexKeyGenerator.generateKey(entry, "keywords55");
        Assertions.assertEquals("w1w2aw2bw3", result);
    }

    @Test
    public void crossrefkeywordsNKeywordsSeparatedBySpace() {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        BibEntry entry2 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry1);
        entry2.setField("keywords", "w1, w2a w2b, w3");
        String result = BibtexKeyGenerator.generateKey(entry1, "keywords", database);
        Assertions.assertEquals("w1w2aw2bw3", result);
    }

    @Test
    public void testCheckLegalKeyEnforceLegal() {
        Assertions.assertEquals("AAAA", BibtexKeyGenerator.cleanKey("AA AA", true));
        Assertions.assertEquals("SPECIALCHARS", BibtexKeyGenerator.cleanKey("SPECIAL CHARS#{\\\"}~,^", true));
        Assertions.assertEquals("", BibtexKeyGenerator.cleanKey("\n\t\r", true));
    }

    @Test
    public void testCheckLegalKeyDoNotEnforceLegal() {
        Assertions.assertEquals("AAAA", BibtexKeyGenerator.cleanKey("AA AA", false));
        Assertions.assertEquals("SPECIALCHARS#~^", BibtexKeyGenerator.cleanKey("SPECIAL CHARS#{\\\"}~,^", false));
        Assertions.assertEquals("", BibtexKeyGenerator.cleanKey("\n\t\r", false));
    }

    @Test
    public void testCheckLegalNullInNullOut() {
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.cleanKey(null, true));
        Assertions.assertThrows(NullPointerException.class, () -> BibtexKeyGenerator.cleanKey(null, false));
    }

    @Test
    public void testApplyModifiers() {
        BibEntry entry = new BibEntry();
        entry.setField("title", "Green Scheduling of Whatever");
        Assertions.assertEquals("GSo", BibtexKeyGenerator.generateKey(entry, "shorttitleINI"));
        Assertions.assertEquals("GreenSchedulingWhatever", BibtexKeyGenerator.generateKey(entry, "shorttitle", new BibDatabase()));
    }

    @Test
    public void testcrossrefShorttitle() {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        BibEntry entry2 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry1);
        entry2.setField("title", "Green Scheduling of Whatever");
        Assertions.assertEquals("GreenSchedulingWhatever", BibtexKeyGenerator.generateKey(entry1, "shorttitle", database));
    }

    @Test
    public void testcrossrefShorttitleInitials() {
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        BibEntry entry2 = new BibEntry();
        entry1.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry1);
        entry2.setField("title", "Green Scheduling of Whatever");
        Assertions.assertEquals("GSo", BibtexKeyGenerator.generateKey(entry1, "shorttitleINI", database));
    }

    @Test
    public void generateKeyStripsColonFromTitle() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "Green Scheduling of: Whatever");
        Assertions.assertEquals("GreenSchedulingOfWhatever", BibtexKeyGenerator.generateKey(entry, "title"));
    }

    @Test
    public void generateKeyStripsApostropheFromTitle() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "Green Scheduling of `Whatever`");
        Assertions.assertEquals("GreenSchedulingofWhatever", BibtexKeyGenerator.generateKey(entry, "title"));
    }

    @Test
    public void generateKeyWithOneModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "The Interesting Title");
        Assertions.assertEquals("theinterestingtitle", BibtexKeyGenerator.generateKey(entry, "title:lower"));
    }

    @Test
    public void generateKeyWithTwoModifiers() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "The Interesting Title");
        Assertions.assertEquals("theinterestingtitle", BibtexKeyGenerator.generateKey(entry, "title:lower:(_)"));
    }

    @Test
    public void generateKeyWithTitleCapitalizeModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "the InTeresting title longer than THREE words");
        Assertions.assertEquals("TheInterestingTitleLongerThanThreeWords", BibtexKeyGenerator.generateKey(entry, "title:capitalize"));
    }

    @Test
    public void generateKeyWithShortTitleCapitalizeModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "the InTeresting title longer than THREE words");
        Assertions.assertEquals("InterestingTitleLonger", BibtexKeyGenerator.generateKey(entry, "shorttitle:capitalize"));
    }

    @Test
    public void generateKeyWithTitleTitleCaseModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "A title WITH some of The key words");
        Assertions.assertEquals("ATitlewithSomeoftheKeyWords", BibtexKeyGenerator.generateKey(entry, "title:titlecase"));
    }

    @Test
    public void generateKeyWithShortTitleTitleCaseModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "the InTeresting title longer than THREE words");
        Assertions.assertEquals("InterestingTitleLonger", BibtexKeyGenerator.generateKey(entry, "shorttitle:titlecase"));
    }

    @Test
    public void generateKeyWithTitleSentenceCaseModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("title", "A title WITH some of The key words");
        Assertions.assertEquals("Atitlewithsomeofthekeywords", BibtexKeyGenerator.generateKey(entry, "title:sentencecase"));
    }

    @Test
    public void generateKeyWithAuthUpperYearShortTitleCapitalizeModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("author", BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_1);
        entry.setField("year", "2019");
        entry.setField("title", "the InTeresting title longer than THREE words");
        Assertions.assertEquals("NEWTON2019InterestingTitleLonger", BibtexKeyGenerator.generateKey(entry, "[auth:upper][year][shorttitle:capitalize]"));
    }

    @Test
    public void generateKeyWithYearAuthUpperTitleSentenceCaseModifier() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setField("author", BibtexKeyGeneratorTest.AUTHOR_STRING_FIRSTNAME_FULL_LASTNAME_FULL_COUNT_3);
        entry.setField("year", "2019");
        entry.setField("title", "the InTeresting title longer than THREE words");
        Assertions.assertEquals("NewtonMaxwellEtAl_2019_TheInterestingTitleLongerThanThreeWords", BibtexKeyGenerator.generateKey(entry, "[authors2]_[year]_[title:capitalize]"));
    }
}

