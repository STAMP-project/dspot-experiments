package org.jabref.logic.importer.fetcher;


import BiblatexEntryTypes.ARTICLE;
import FieldName.DOI;
import FieldName.EPRINT;
import FieldName.TITLE;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.identifier.ArXivIdentifier;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


@FetcherTest
public class ArXivTest {
    private ArXiv finder;

    private BibEntry entry;

    private BibEntry sliceTheoremPaper;

    @Test
    public void findFullTextForEmptyEntryResultsEmptyOptional() throws IOException {
        Assertions.assertEquals(Optional.empty(), finder.findFullText(entry));
    }

    @Test
    public void findFullTextRejectsNullParameter() {
        Assertions.assertThrows(NullPointerException.class, () -> finder.findFullText(null));
    }

    @Test
    public void findFullTextByDOI() throws IOException {
        entry.setField(DOI, "10.1529/biophysj.104.047340");
        entry.setField(TITLE, "Pause Point Spectra in DNA Constant-Force Unzipping");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/cond-mat/0406246v1")), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByEprint() throws IOException {
        entry.setField("eprint", "1603.06570");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/1603.06570v1")), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByEprintWithPrefix() throws IOException {
        entry.setField("eprint", "arXiv:1603.06570");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/1603.06570v1")), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByEprintWithUnknownDOI() throws IOException {
        entry.setField("doi", "10.1529/unknown");
        entry.setField("eprint", "1603.06570");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/1603.06570v1")), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByTitle() throws IOException {
        entry.setField("title", "Pause Point Spectra in DNA Constant-Force Unzipping");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/cond-mat/0406246v1")), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByTitleAndPartOfAuthor() throws IOException {
        entry.setField("title", "Pause Point Spectra in DNA Constant-Force Unzipping");
        entry.setField("author", "Weeks and Lucks");
        Assertions.assertEquals(Optional.of(new URL("http://arxiv.org/pdf/cond-mat/0406246v1")), finder.findFullText(entry));
    }

    @Test
    public void notFindFullTextByUnknownDOI() throws IOException {
        entry.setField("doi", "10.1529/unknown");
        Assertions.assertEquals(Optional.empty(), finder.findFullText(entry));
    }

    @Test
    public void notFindFullTextByUnknownId() throws IOException {
        entry.setField("eprint", "1234.12345");
        Assertions.assertEquals(Optional.empty(), finder.findFullText(entry));
    }

    @Test
    public void findFullTextByDOINotAvailableInCatalog() throws IOException {
        entry.setField(DOI, "10.1016/0370-2693(77)90015-6");
        entry.setField(TITLE, "Superspace formulation of supergravity");
        Assertions.assertEquals(Optional.empty(), finder.findFullText(entry));
    }

    @Test
    public void searchEntryByPartOfTitle() throws Exception {
        Assertions.assertEquals(Collections.singletonList(sliceTheoremPaper), finder.performSearch("ti:\"slice theorem for Frechet\""));
    }

    @Test
    public void searchEntryByPartOfTitleWithAcuteAccent() throws Exception {
        Assertions.assertEquals(Collections.singletonList(sliceTheoremPaper), finder.performSearch("ti:\"slice theorem for Fr\u00e9chet\""));
    }

    @Test
    public void searchEntryByOldId() throws Exception {
        BibEntry expected = new BibEntry();
        expected.setType(ARTICLE);
        expected.setField("author", "H1 Collaboration");
        expected.setField("title", "Multi-Electron Production at High Transverse Momenta in ep Collisions at HERA");
        expected.setField("date", "2003-07-07");
        expected.setField("abstract", "Multi-electron production is studied at high electron transverse momentum in positron- and electron-proton collisions using the H1 detector at HERA. The data correspond to an integrated luminosity of 115 pb-1. Di-electron and tri-electron event yields are measured. Cross sections are derived in a restricted phase space region dominated by photon-photon collisions. In general good agreement is found with the Standard Model predictions. However, for electron pair invariant masses above 100 GeV, three di-electron events and three tri-electron events are observed, compared to Standard Model expectations of 0.30 \\pm 0.04 and 0.23 \\pm 0.04, respectively.");
        expected.setField("eprint", "hep-ex/0307015v1");
        expected.setField("file", ":http\\://arxiv.org/pdf/hep-ex/0307015v1:PDF");
        expected.setField("eprinttype", "arXiv");
        expected.setField("eprintclass", "hep-ex");
        expected.setField("keywords", "hep-ex");
        expected.setField("doi", "10.1140/epjc/s2003-01326-x");
        expected.setField("journaltitle", "Eur.Phys.J.C31:17-29,2003");
        Assertions.assertEquals(Optional.of(expected), finder.performSearchById("hep-ex/0307015"));
    }

    @Test
    public void searchEntryByIdWith4DigitsAndVersion() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("1405.2249v1"));
    }

    @Test
    public void searchEntryByIdWith4Digits() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("1405.2249"));
    }

    @Test
    public void searchEntryByIdWith4DigitsAndPrefix() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("arXiv:1405.2249"));
    }

    @Test
    public void searchEntryByIdWith4DigitsAndPrefixAndNotTrimmed() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("arXiv : 1405. 2249"));
    }

    @Test
    public void searchEntryByIdWith5Digits() throws Exception {
        Assertions.assertEquals(Optional.of("An Optimal Convergence Theorem for Mean Curvature Flow of Arbitrary Codimension in Hyperbolic Spaces"), finder.performSearchById("1503.06747").flatMap(( entry) -> entry.getField("title")));
    }

    @Test
    public void searchWithMalformedIdThrowsException() throws Exception {
        Assertions.assertThrows(FetcherException.class, () -> finder.performSearchById("123412345"));
    }

    @Test
    public void searchIdentifierForSlicePaper() throws Exception {
        sliceTheoremPaper.clearField(EPRINT);
        Assertions.assertEquals(ArXivIdentifier.parse("1405.2249v1"), finder.findIdentifier(sliceTheoremPaper));
    }

    @Test
    public void searchEmptyId() throws Exception {
        Assertions.assertEquals(Optional.empty(), finder.performSearchById(""));
    }

    @Test
    public void searchWithHttpUrl() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("http://arxiv.org/abs/1405.2249"));
    }

    @Test
    public void searchWithHttpsUrl() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("https://arxiv.org/abs/1405.2249"));
    }

    @Test
    public void searchWithHttpsUrlNotTrimmed() throws Exception {
        Assertions.assertEquals(Optional.of(sliceTheoremPaper), finder.performSearchById("https : // arxiv . org / abs / 1405 . 2249 "));
    }
}

