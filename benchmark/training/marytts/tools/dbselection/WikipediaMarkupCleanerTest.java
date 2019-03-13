package marytts.tools.dbselection;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.fest.assertions.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for WikipediaMarkupCleaner
 *
 * @author ingmar
 */
public class WikipediaMarkupCleanerTest {
    private WikipediaMarkupCleaner wikiCleaner;

    private URL markupResource;

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testRemoveMarkup() throws IOException, URISyntaxException {
        // read markup from test resource
        String page = Resources.toString(markupResource, Charsets.UTF_8);
        // process to extract markup-less text
        String pageWithoutMarkup = wikiCleaner.removeMarkup(page).firstElement();
        // write processed text to temp file
        File actualFile = tempDir.newFile("Autorack.txt");
        Files.write(pageWithoutMarkup, actualFile, Charsets.UTF_8);
        // get expected text and compare with actual processed text
        URL expectedResource = Resources.getResource(getClass(), "Autorack.txt");
        File expectedFile = new File(expectedResource.toURI());
        Assertions.assertThat(actualFile).hasSameContentAs(expectedFile);
    }
}

