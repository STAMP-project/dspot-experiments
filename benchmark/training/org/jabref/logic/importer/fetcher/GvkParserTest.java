package org.jabref.logic.importer.fetcher;


import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.logic.importer.fileformat.GvkParser;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class GvkParserTest {
    @Test
    public void emptyResult() throws Exception {
        doTest("gvk_empty_result_because_of_bad_query.xml", 0, Collections.emptyList());
    }

    @Test
    public void resultFor797485368() throws Exception {
        doTest("gvk_result_for_797485368.xml", 1, Collections.singletonList("gvk_result_for_797485368.bib"));
    }

    @Test
    public void testGMP() throws Exception {
        doTest("gvk_gmp.xml", 2, Arrays.asList("gvk_gmp.1.bib", "gvk_gmp.2.bib"));
    }

    @Test
    public void subTitleTest() throws Exception {
        try (InputStream is = GvkParserTest.class.getResourceAsStream("gvk_artificial_subtitle_test.xml")) {
            GvkParser parser = new GvkParser();
            List<BibEntry> entries = parser.parseEntries(is);
            Assertions.assertNotNull(entries);
            Assertions.assertEquals(5, entries.size());
            BibEntry entry = entries.get(0);
            Assertions.assertEquals(Optional.empty(), entry.getField("subtitle"));
            entry = entries.get(1);
            Assertions.assertEquals(Optional.of("C"), entry.getField("subtitle"));
            entry = entries.get(2);
            Assertions.assertEquals(Optional.of("Word"), entry.getField("subtitle"));
            entry = entries.get(3);
            Assertions.assertEquals(Optional.of("Word1 word2"), entry.getField("subtitle"));
            entry = entries.get(4);
            Assertions.assertEquals(Optional.of("Word1 word2"), entry.getField("subtitle"));
        }
    }
}

