package org.jabref.logic.importer.fileformat;


import StandardFileType.BIBTEX_DB;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This class tests the BibtexImporter. That importer is only used for --importToOpen, which is currently untested
 * <p>
 * TODO: 1. Add test for --importToOpen 2. Move these tests to the code opening a bibtex file
 */
public class BibtexImporterTest {
    private BibtexImporter importer;

    @Test
    public void testIsRecognizedFormat() throws IOException, URISyntaxException {
        Path file = Paths.get(BibtexImporterTest.class.getResource("BibtexImporter.examples.bib").toURI());
        Assertions.assertTrue(importer.isRecognizedFormat(file, StandardCharsets.UTF_8));
    }

    @Test
    public void testImportEntries() throws IOException, URISyntaxException {
        Path file = Paths.get(BibtexImporterTest.class.getResource("BibtexImporter.examples.bib").toURI());
        List<BibEntry> bibEntries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(4, bibEntries.size());
        for (BibEntry entry : bibEntries) {
            if (entry.getCiteKeyOptional().get().equals("aksin")) {
                Assertions.assertEquals(Optional.of(("Aks{\\i}n, {\\\"O}zge and T{\\\"u}rkmen, Hayati and Artok, Levent and {\\c{C}}etinkaya, " + "Bekir and Ni, Chaoying and B{\\\"u}y{\\\"u}kg{\\\"u}ng{\\\"o}r, Orhan and {\\\"O}zkal, Erhan")), entry.getField("author"));
                Assertions.assertEquals(Optional.of("aksin"), entry.getField("bibtexkey"));
                Assertions.assertEquals(Optional.of("2006"), entry.getField("date"));
                Assertions.assertEquals(Optional.of("Effect of immobilization on catalytic characteristics"), entry.getField("indextitle"));
                Assertions.assertEquals(Optional.of("#jomch#"), entry.getField("journal"));
                Assertions.assertEquals(Optional.of("13"), entry.getField("number"));
                Assertions.assertEquals(Optional.of("3027-3036"), entry.getField("pages"));
                Assertions.assertEquals(Optional.of(("Effect of immobilization on catalytic characteristics of saturated {Pd-N}-heterocyclic " + "carbenes in {Mizoroki-Heck} reactions")), entry.getField("title"));
                Assertions.assertEquals(Optional.of("691"), entry.getField("volume"));
            } else
                if (entry.getCiteKeyOptional().get().equals("stdmodel")) {
                    Assertions.assertEquals(Optional.of(("A \\texttt{set} with three members discussing the standard model of particle physics. " + ("The \\texttt{crossref} field in the \\texttt{@set} entry and the \\texttt{entryset} field in " + "each set member entry is needed only when using BibTeX as the backend"))), entry.getField("annotation"));
                    Assertions.assertEquals(Optional.of("stdmodel"), entry.getField("bibtexkey"));
                    Assertions.assertEquals(Optional.of("glashow,weinberg,salam"), entry.getField("entryset"));
                } else
                    if (entry.getCiteKeyOptional().get().equals("set")) {
                        Assertions.assertEquals(Optional.of(("A \\texttt{set} with three members. The \\texttt{crossref} field in the \\texttt{@set} " + ("entry and the \\texttt{entryset} field in each set member entry is needed only when using " + "BibTeX as the backend"))), entry.getField("annotation"));
                        Assertions.assertEquals(Optional.of("set"), entry.getField("bibtexkey"));
                        Assertions.assertEquals(Optional.of("herrmann,aksin,yoon"), entry.getField("entryset"));
                    } else
                        if (entry.getCiteKeyOptional().get().equals("Preissel2016")) {
                            Assertions.assertEquals(Optional.of("Heidelberg"), entry.getField("address"));
                            Assertions.assertEquals(Optional.of("Prei?el, Ren?"), entry.getField("author"));
                            Assertions.assertEquals(Optional.of("Preissel2016"), entry.getField("bibtexkey"));
                            Assertions.assertEquals(Optional.of("3., aktualisierte und erweiterte Auflage"), entry.getField("edition"));
                            Assertions.assertEquals(Optional.of("978-3-86490-311-3"), entry.getField("isbn"));
                            Assertions.assertEquals(Optional.of("Versionsverwaltung"), entry.getField("keywords"));
                            Assertions.assertEquals(Optional.of("XX, 327 Seiten"), entry.getField("pages"));
                            Assertions.assertEquals(Optional.of("dpunkt.verlag"), entry.getField("publisher"));
                            Assertions.assertEquals(Optional.of("Git: dezentrale Versionsverwaltung im Team : Grundlagen und Workflows"), entry.getField("title"));
                            Assertions.assertEquals(Optional.of("http://d-nb.info/107601965X"), entry.getField("url"));
                            Assertions.assertEquals(Optional.of("2016"), entry.getField("year"));
                        }



        }
    }

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("BibTeX", importer.getName());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(BIBTEX_DB, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals(("This importer exists only to enable `--importToOpen someEntry.bib`\n" + ("It is NOT intended to import a BIB file. This is done via the option action, which treats the metadata fields.\n" + "The metadata is not required to be read here, as this class is NOT called at --import.")), importer.getDescription());
    }

    @Test
    public void testRecognizesDatabaseID() throws Exception {
        Path file = Paths.get(BibtexImporterTest.class.getResource("AutosavedSharedDatabase.bib").toURI());
        String sharedDatabaseID = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getSharedDatabaseID().get();
        Assertions.assertEquals("13ceoc8dm42f5g1iitao3dj2ap", sharedDatabaseID);
    }
}

