package org.jabref.logic.importer.fileformat;


import java.nio.file.Paths;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CustomImporterTest {
    private CustomImporter importer;

    @Test
    public void testGetName() {
        Assertions.assertEquals("Copac", importer.getName());
    }

    @Test
    public void testGetId() {
        Assertions.assertEquals("cpc", importer.getId());
    }

    @Test
    public void testGetClassName() {
        Assertions.assertEquals("org.jabref.logic.importer.fileformat.CopacImporter", importer.getClassName());
    }

    @Test
    public void testGetBasePath() {
        Assertions.assertEquals(Paths.get("src/main/java/org/jabref/logic/importer/fileformat/CopacImporter.java"), importer.getBasePath());
    }

    @Test
    public void testGetAsStringList() {
        Assertions.assertEquals(Arrays.asList("src/main/java/org/jabref/logic/importer/fileformat/CopacImporter.java", "org.jabref.logic.importer.fileformat.CopacImporter"), importer.getAsStringList());
    }

    @Test
    public void equalsWithSameReference() {
        Assertions.assertEquals(importer, importer);
    }

    @Test
    public void equalsIsBasedOnName() {
        // noinspection AssertEqualsBetweenInconvertibleTypes
        Assertions.assertEquals(new CopacImporter(), importer);
    }

    @Test
    public void testCompareToSmaller() throws Exception {
        CustomImporter ovidImporter = asCustomImporter(new OvidImporter());
        TestCase.assertTrue(((importer.compareTo(ovidImporter)) < 0));
    }

    @Test
    public void testCompareToGreater() throws Exception {
        CustomImporter bibtexmlImporter = asCustomImporter(new BibTeXMLImporter());
        CustomImporter ovidImporter = asCustomImporter(new OvidImporter());
        TestCase.assertTrue(((ovidImporter.compareTo(bibtexmlImporter)) > 0));
    }

    @Test
    public void testCompareToEven() throws Exception {
        Assertions.assertEquals(0, importer.compareTo(asCustomImporter(new CopacImporter())));
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("Copac", importer.toString());
    }

    @Test
    public void testClassicConstructor() throws Exception {
        CustomImporter customImporter = new CustomImporter("src/main/java/org/jabref/logic/importer/fileformat/CopacImporter.java", "org.jabref.logic.importer.fileformat.CopacImporter");
        Assertions.assertEquals(importer, customImporter);
    }
}

