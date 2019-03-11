package org.jabref.logic.importer.fileformat;


import StandardFileType.ISI;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test cases for the IsiImporter
 */
public class IsiImporterTest {
    private static final String FILE_ENDING = ".isi";

    private final IsiImporter importer = new IsiImporter();

    @Test
    public void testParseMonthException() {
        IsiImporter.parseMonth("20l06 06-07");
    }

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("ISI", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("isi", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(ISI, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Importer for the ISI Web of Science, INSPEC and Medline format.", importer.getDescription());
    }

    @Test
    public void testProcessSubSup() {
        HashMap<String, String> subs = new HashMap<>();
        subs.put("title", "/sub 3/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_3$", subs.get("title"));
        subs.put("title", "/sub   3   /");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_3$", subs.get("title"));
        subs.put("title", "/sub 31/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_{31}$", subs.get("title"));
        subs.put("abstract", "/sub 3/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_3$", subs.get("abstract"));
        subs.put("review", "/sub 31/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_{31}$", subs.get("review"));
        subs.put("title", "/sup 3/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$^3$", subs.get("title"));
        subs.put("title", "/sup 31/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$^{31}$", subs.get("title"));
        subs.put("abstract", "/sup 3/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$^3$", subs.get("abstract"));
        subs.put("review", "/sup 31/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$^{31}$", subs.get("review"));
        subs.put("title", "/sub $Hello/");
        IsiImporter.processSubSup(subs);
        Assertions.assertEquals("$_{\\$Hello}$", subs.get("title"));
    }

    @Test
    public void testImportEntries1() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTest1.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry entry = entries.get(0);
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals(Optional.of("Optical properties of MgO doped LiNbO$_3$ single crystals"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("James Brown and James Marc Brown and Brown, J. M. and Brown, J. and Brown, J. M. and Brown, J."), entry.getField("author"));
        Assertions.assertEquals("article", entry.getType());
        Assertions.assertEquals(Optional.of("Optical Materials"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of("2006"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("28"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("5"), entry.getField("number"));
        Assertions.assertEquals(Optional.of("467--72"), entry.getField("pages"));
    }

    @Test
    public void testImportEntries2() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTest2.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry entry = entries.get(0);
        Assertions.assertEquals(3, entries.size());
        Assertions.assertEquals(Optional.of("Optical properties of MgO doped LiNbO$_3$ single crystals"), entry.getField("title"));
        Assertions.assertEquals("misc", entry.getType());
        Assertions.assertEquals(Optional.of("Optical Materials"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of("2006"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("28"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("5"), entry.getField("number"));
        Assertions.assertEquals(Optional.of("467-72"), entry.getField("pages"));
    }

    @Test
    public void testImportEntriesINSPEC() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTestInspec.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry first = entries.get(0);
        BibEntry second = entries.get(1);
        if (first.getField("title").equals(Optional.of("Optical and photoelectric spectroscopy of photorefractive Sn$_2$P$_2$S$_6$ crystals"))) {
            BibEntry tmp = first;
            first = second;
            second = tmp;
        }
        Assertions.assertEquals(2, entries.size());
        Assertions.assertEquals(Optional.of("Second harmonic generation of continuous wave ultraviolet light and production of beta -BaB$_2$O$_4$ optical waveguides"), first.getField("title"));
        Assertions.assertEquals("article", first.getType());
        Assertions.assertEquals(Optional.of("Degl'Innocenti, R. and Guarino, A. and Poberaj, G. and Gunter, P."), first.getField("author"));
        Assertions.assertEquals(Optional.of("Applied Physics Letters"), first.getField("journal"));
        Assertions.assertEquals(Optional.of("2006"), first.getField("year"));
        Assertions.assertEquals(Optional.of("#jul#"), first.getField("month"));
        Assertions.assertEquals(Optional.of("89"), first.getField("volume"));
        Assertions.assertEquals(Optional.of("4"), first.getField("number"));
        Assertions.assertEquals(Optional.of("Lorem ipsum abstract"), first.getField("abstract"));
        Assertions.assertEquals(Optional.of("Aip"), first.getField("publisher"));
        Assertions.assertEquals(Optional.of("Optical and photoelectric spectroscopy of photorefractive Sn$_2$P$_2$S$_6$ crystals"), second.getField("title"));
        Assertions.assertEquals("article", second.getType());
    }

    @Test
    public void testImportEntriesWOS() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTestWOS.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry first = entries.get(0);
        BibEntry second = entries.get(1);
        Assertions.assertEquals(2, entries.size());
        Assertions.assertEquals(Optional.of("Optical and photoelectric spectroscopy of photorefractive Sn2P2S6 crystals"), first.getField("title"));
        Assertions.assertEquals(Optional.of("Optical waveguides in Sn2P2S6 by low fluence MeV He+ ion implantation"), second.getField("title"));
        Assertions.assertEquals(Optional.of("Journal of Physics-condensed Matter"), first.getField("journal"));
    }

    @Test
    public void testIsiAuthorsConvert() {
        Assertions.assertEquals("James Brown and James Marc Brown and Brown, J. M. and Brown, J. and Brown, J. M. and Brown, J.", IsiImporter.isiAuthorsConvert("James Brown and James Marc Brown and Brown, J.M. and Brown, J. and Brown, J.M. and Brown, J."));
        Assertions.assertEquals("Joffe, Hadine and Hall, Janet E. and Gruber, Staci and Sarmiento, Ingrid A. and Cohen, Lee S. and Yurgelun-Todd, Deborah and Martin, Kathryn A.", IsiImporter.isiAuthorsConvert("Joffe, Hadine; Hall, Janet E; Gruber, Staci; Sarmiento, Ingrid A; Cohen, Lee S; Yurgelun-Todd, Deborah; Martin, Kathryn A"));
    }

    @Test
    public void testMonthConvert() {
        Assertions.assertEquals("#jun#", IsiImporter.parseMonth("06"));
        Assertions.assertEquals("#jun#", IsiImporter.parseMonth("JUN"));
        Assertions.assertEquals("#jun#", IsiImporter.parseMonth("jUn"));
        Assertions.assertEquals("#may#", IsiImporter.parseMonth("MAY-JUN"));
        Assertions.assertEquals("#jun#", IsiImporter.parseMonth("2006 06"));
        Assertions.assertEquals("#jun#", IsiImporter.parseMonth("2006 06-07"));
        Assertions.assertEquals("#jul#", IsiImporter.parseMonth("2006 07 03"));
        Assertions.assertEquals("#may#", IsiImporter.parseMonth("2006 May-Jun"));
    }

    @Test
    public void testIsiAuthorConvert() {
        Assertions.assertEquals("James Brown", IsiImporter.isiAuthorConvert("James Brown"));
        Assertions.assertEquals("James Marc Brown", IsiImporter.isiAuthorConvert("James Marc Brown"));
        Assertions.assertEquals("Brown, J. M.", IsiImporter.isiAuthorConvert("Brown, J.M."));
        Assertions.assertEquals("Brown, J.", IsiImporter.isiAuthorConvert("Brown, J."));
        Assertions.assertEquals("Brown, J. M.", IsiImporter.isiAuthorConvert("Brown, JM"));
        Assertions.assertEquals("Brown, J.", IsiImporter.isiAuthorConvert("Brown, J"));
        Assertions.assertEquals("Brown, James", IsiImporter.isiAuthorConvert("Brown, James"));
        Assertions.assertEquals("Hall, Janet E.", IsiImporter.isiAuthorConvert("Hall, Janet E"));
        Assertions.assertEquals("", IsiImporter.isiAuthorConvert(""));
    }

    @Test
    public void testImportIEEEExport() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IEEEImport1.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry entry = entries.get(0);
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals("article", entry.getType());
        Assertions.assertEquals(Optional.of("Geoscience and Remote Sensing Letters, IEEE"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of(("Improving Urban Road Extraction in High-Resolution " + ("Images Exploiting Directional Filtering, Perceptual " + "Grouping, and Simple Topological Concepts"))), entry.getField("title"));
        Assertions.assertEquals(Optional.of("4"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("3"), entry.getField("number"));
        Assertions.assertEquals(Optional.of("1545-598X"), entry.getField("SN"));
        Assertions.assertEquals(Optional.of("387--391"), entry.getField("pages"));
        Assertions.assertEquals(Optional.of("Gamba, P. and Dell'Acqua, F. and Lisini, G."), entry.getField("author"));
        Assertions.assertEquals(Optional.of("2006"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("Perceptual grouping, street extraction, urban remote sensing"), entry.getField("keywords"));
        Assertions.assertEquals(Optional.of("Lorem ipsum abstract"), entry.getField("abstract"));
    }

    @Test
    public void testIEEEImport() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IEEEImport1.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry entry = entries.get(0);
        Assertions.assertEquals(1, entries.size());
        Assertions.assertEquals("article", entry.getType());
        Assertions.assertEquals(Optional.of("Geoscience and Remote Sensing Letters, IEEE"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of("Improving Urban Road Extraction in High-Resolution Images Exploiting Directional Filtering, Perceptual Grouping, and Simple Topological Concepts"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("4"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("3"), entry.getField("number"));
        Assertions.assertEquals(Optional.of("1545-598X"), entry.getField("SN"));
        Assertions.assertEquals(Optional.of("387--391"), entry.getField("pages"));
        Assertions.assertEquals(Optional.of("Gamba, P. and Dell'Acqua, F. and Lisini, G."), entry.getField("author"));
        Assertions.assertEquals(Optional.of("2006"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("Perceptual grouping, street extraction, urban remote sensing"), entry.getField("keywords"));
        Assertions.assertEquals(Optional.of("Lorem ipsum abstract"), entry.getField("abstract"));
    }

    @Test
    public void testImportEntriesMedline() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTestMedline.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry first = entries.get(0);
        BibEntry second = entries.get(1);
        Assertions.assertEquals(2, entries.size());
        Assertions.assertEquals(Optional.of("Effects of modafinil on cognitive performance and alertness during sleep deprivation."), first.getField("title"));
        Assertions.assertEquals(Optional.of("Wesensten, Nancy J."), first.getField("author"));
        Assertions.assertEquals(Optional.of("Curr Pharm Des"), first.getField("journal"));
        Assertions.assertEquals(Optional.of("2006"), first.getField("year"));
        Assertions.assertEquals(Optional.empty(), first.getField("month"));
        Assertions.assertEquals(Optional.of("12"), first.getField("volume"));
        Assertions.assertEquals(Optional.of("20"), first.getField("number"));
        Assertions.assertEquals(Optional.of("2457--71"), first.getField("pages"));
        Assertions.assertEquals("article", first.getType());
        Assertions.assertEquals(Optional.of("Estrogen therapy selectively enhances prefrontal cognitive processes: a randomized, double-blind, placebo-controlled study with functional magnetic resonance imaging in perimenopausal and recently postmenopausal women."), second.getField("title"));
        Assertions.assertEquals(Optional.of("Joffe, Hadine and Hall, Janet E. and Gruber, Staci and Sarmiento, Ingrid A. and Cohen, Lee S. and Yurgelun-Todd, Deborah and Martin, Kathryn A."), second.getField("author"));
        Assertions.assertEquals(Optional.of("2006"), second.getField("year"));
        Assertions.assertEquals(Optional.of("#may#"), second.getField("month"));
        Assertions.assertEquals(Optional.of("13"), second.getField("volume"));
        Assertions.assertEquals(Optional.of("3"), second.getField("number"));
        Assertions.assertEquals(Optional.of("411--22"), second.getField("pages"));
        Assertions.assertEquals("article", second.getType());
    }

    @Test
    public void testImportEntriesEmpty() throws IOException, URISyntaxException {
        Path file = Paths.get(IsiImporterTest.class.getResource("IsiImporterTestEmpty.isi").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(1, entries.size());
    }
}

