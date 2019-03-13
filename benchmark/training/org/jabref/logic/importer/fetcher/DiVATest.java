package org.jabref.logic.importer.fetcher;


import HelpFile.FETCHER_DIVA;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class DiVATest {
    private DiVA fetcher;

    @Test
    public void testGetName() {
        Assertions.assertEquals("DiVA", fetcher.getName());
    }

    @Test
    public void testGetHelpPage() {
        Assertions.assertEquals(FETCHER_DIVA, FETCHER_DIVA);
    }

    @Test
    public void testPerformSearchById() throws Exception {
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "Gustafsson, Oscar");
        entry.setField("institution", "Link?ping University, The Institute of Technology");
        entry.setCiteKey("Gustafsson260746");
        entry.setField("journal", "IEEE transactions on circuits and systems. 2, Analog and digital signal processing (Print)");
        entry.setField("number", "11");
        entry.setField("pages", "974--978");
        entry.setField("title", "Lower bounds for constant multiplication problems");
        entry.setField("volume", "54");
        entry.setField("year", "2007");
        entry.setField("abstract", "Lower bounds for problems related to realizing multiplication by constants with shifts, adders, and subtracters are presented. These lower bounds are straightforwardly calculated and have applications in proving the optimality of solutions obtained by heuristics. ");
        entry.setField("doi", "10.1109/TCSII.2007.903212");
        Assertions.assertEquals(Optional.of(entry), fetcher.performSearchById("diva2:260746"));
    }

    @Test
    public void testValidIdentifier() {
        Assertions.assertTrue(fetcher.isValidId("diva2:260746"));
    }

    @Test
    public void testInvalidIdentifier() {
        Assertions.assertFalse(fetcher.isValidId("banana"));
    }

    @Test
    public void testEmptyId() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById(""));
    }
}

