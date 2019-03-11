package org.jabref.logic.citationstyle;


import org.jabref.logic.util.TestEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CitationStyleTest {
    @Test
    public void getDefault() throws Exception {
        Assertions.assertNotNull(CitationStyle.getDefault());
    }

    @Test
    public void testDefaultCitation() {
        String citation = CitationStyleGenerator.generateCitation(TestEntry.getTestEntry(), CitationStyle.getDefault());
        // if the default citation style changes this has to be modified
        String expected = "  <div class=\"csl-entry\">\n" + ((("    <div class=\"csl-left-margin\">[1]</div><div class=\"csl-right-inline\">" + "B. Smith, B. Jones, and J. Williams, ?Title of the test entry,? ") + "<i>BibTeX Journal</i>, vol. 34, no. 3, pp. 45\u201367, Jul. 2016.</div>\n") + "  </div>\n");
        Assertions.assertEquals(expected, citation);
    }
}

