package aima.test.core.unit.nlp.rank;


import aima.core.nlp.ranking.Page;
import aima.core.nlp.ranking.PagesDataset;
import java.io.File;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PagesDatasetTest {
    Map<String, Page> pageTable;

    // resource folder of .txt files to test with
    String testFilesFolderPath = "src/main/resources/aima/core/ranking/data/pages/test_pages";

    @Test
    public void testGetPageName() {
        File file = new File("test/file/path.txt");
        File fileTwo = new File("test/file/PATHTWO.txt");
        String p = PagesDataset.getPageName(file);
        Assert.assertEquals(p, "/wiki/path");
        Assert.assertEquals(PagesDataset.getPageName(fileTwo), "/wiki/pathtwo");
    }
}

