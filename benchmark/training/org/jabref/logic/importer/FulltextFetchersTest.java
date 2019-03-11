package org.jabref.logic.importer;


import TrustLevel.SOURCE;
import TrustLevel.UNKNOWN;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class FulltextFetchersTest {
    private BibEntry entry;

    @Test
    public void acceptPdfUrls() throws MalformedURLException {
        URL pdfUrl = new URL("http://docs.oasis-open.org/wsbpel/2.0/OS/wsbpel-v2.0-OS.pdf");
        FulltextFetcher finder = ( e) -> Optional.of(pdfUrl);
        FulltextFetchers fetcher = new FulltextFetchers(Arrays.asList(finder));
        Assertions.assertEquals(Optional.of(pdfUrl), fetcher.findFullTextPDF(entry));
    }

    @Test
    public void rejectNonPdfUrls() throws MalformedURLException {
        URL pdfUrl = new URL("https://github.com/JabRef/jabref/blob/master/README.md");
        FulltextFetcher finder = ( e) -> Optional.of(pdfUrl);
        FulltextFetchers fetcher = new FulltextFetchers(Arrays.asList(finder));
        Assertions.assertEquals(Optional.empty(), fetcher.findFullTextPDF(entry));
    }

    @Test
    public void noTrustLevel() throws MalformedURLException {
        URL pdfUrl = new URL("http://docs.oasis-open.org/wsbpel/2.0/OS/wsbpel-v2.0-OS.pdf");
        FulltextFetcher finder = ( e) -> Optional.of(pdfUrl);
        FulltextFetchers fetcher = new FulltextFetchers(Arrays.asList(finder));
        Assertions.assertEquals(Optional.of(pdfUrl), fetcher.findFullTextPDF(entry));
    }

    @Test
    public void higherTrustLevelWins() throws IOException, MalformedURLException, FetcherException {
        final URL lowUrl = new URL("http://docs.oasis-open.org/opencsa/sca-bpel/sca-bpel-1.1-spec-cd-01.pdf");
        final URL highUrl = new URL("http://docs.oasis-open.org/wsbpel/2.0/OS/wsbpel-v2.0-OS.pdf");
        FulltextFetcher finderHigh = Mockito.mock(FulltextFetcher.class);
        FulltextFetcher finderLow = Mockito.mock(FulltextFetcher.class);
        Mockito.when(finderHigh.getTrustLevel()).thenReturn(SOURCE);
        Mockito.when(finderLow.getTrustLevel()).thenReturn(UNKNOWN);
        Mockito.when(finderHigh.findFullText(entry)).thenReturn(Optional.of(highUrl));
        Mockito.when(finderLow.findFullText(entry)).thenReturn(Optional.of(lowUrl));
        FulltextFetchers fetcher = new FulltextFetchers(Arrays.asList(finderLow, finderHigh));
        Assertions.assertEquals(Optional.of(highUrl), fetcher.findFullTextPDF(entry));
    }
}

