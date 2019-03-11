package org.jabref.logic.cleanup;


import BibEntry.KEY_FIELD;
import java.util.Map;
import org.jabref.logic.formatter.bibtexfields.UnicodeToLatexFormatter;
import org.jabref.logic.formatter.casechanger.UpperCaseFormatter;
import org.jabref.model.cleanup.FieldFormatterCleanup;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.FieldName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldFormatterCleanupTest {
    private BibEntry entry;

    private Map<String, String> fieldMap;

    @Test
    public void testInternalAllField() throws Exception {
        FieldFormatterCleanup cleanup = new FieldFormatterCleanup(FieldName.INTERNAL_ALL_FIELD, new UpperCaseFormatter());
        cleanup.cleanup(entry);
        Assertions.assertEquals(fieldMap.get("title").toUpperCase(), entry.getField("title").get());
        Assertions.assertEquals(fieldMap.get("booktitle").toUpperCase(), entry.getField("booktitle").get());
        Assertions.assertEquals(fieldMap.get("year").toUpperCase(), entry.getField("year").get());
        Assertions.assertEquals(fieldMap.get("month").toUpperCase(), entry.getField("month").get());
        Assertions.assertEquals(fieldMap.get("abstract").toUpperCase(), entry.getField("abstract").get());
        Assertions.assertEquals(fieldMap.get("doi").toUpperCase(), entry.getField("doi").get());
        Assertions.assertEquals(fieldMap.get("issn").toUpperCase(), entry.getField("issn").get());
    }

    @Test
    public void testInternalAllTextFieldsField() throws Exception {
        FieldFormatterCleanup cleanup = new FieldFormatterCleanup(FieldName.INTERNAL_ALL_TEXT_FIELDS_FIELD, new UpperCaseFormatter());
        cleanup.cleanup(entry);
        Assertions.assertEquals(fieldMap.get("title").toUpperCase(), entry.getField("title").get());
        Assertions.assertEquals(fieldMap.get("booktitle").toUpperCase(), entry.getField("booktitle").get());
        Assertions.assertEquals(fieldMap.get("year"), entry.getField("year").get());
        Assertions.assertEquals(fieldMap.get("month"), entry.getField("month").get());
        Assertions.assertEquals(fieldMap.get("abstract").toUpperCase(), entry.getField("abstract").get());
        Assertions.assertEquals(fieldMap.get("doi"), entry.getField("doi").get());
        Assertions.assertEquals(fieldMap.get("issn"), entry.getField("issn").get());
    }

    @Test
    public void testCleanupAllFieldsIgnoresKeyField() throws Exception {
        FieldFormatterCleanup cleanup = new FieldFormatterCleanup(FieldName.INTERNAL_ALL_FIELD, new UnicodeToLatexFormatter());
        entry.setField(KEY_FIELD, "Fran?ois-Marie Arouet");// Contains ?, not in Basic Latin

        cleanup.cleanup(entry);
        Assertions.assertEquals("Fran?ois-Marie Arouet", entry.getField(KEY_FIELD).get());
    }

    @Test
    public void testCleanupAllTextFieldsIgnoresKeyField() throws Exception {
        FieldFormatterCleanup cleanup = new FieldFormatterCleanup(FieldName.INTERNAL_ALL_TEXT_FIELDS_FIELD, new UnicodeToLatexFormatter());
        entry.setField(KEY_FIELD, "Fran?ois-Marie Arouet");// Contains ?, not in Basic Latin

        cleanup.cleanup(entry);
        Assertions.assertEquals("Fran?ois-Marie Arouet", entry.getField(KEY_FIELD).get());
    }

    @Test
    public void testCleanupKeyFieldCleansUpKeyField() throws Exception {
        FieldFormatterCleanup cleanup = new FieldFormatterCleanup(BibEntry.KEY_FIELD, new UnicodeToLatexFormatter());
        entry.setField(KEY_FIELD, "Fran?ois-Marie Arouet");// Contains ?, not in Basic Latin

        cleanup.cleanup(entry);
        Assertions.assertEquals("Fran{\\c{c}}ois-Marie Arouet", entry.getField(KEY_FIELD).get());
    }
}

