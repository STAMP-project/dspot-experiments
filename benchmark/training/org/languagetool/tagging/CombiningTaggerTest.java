package org.languagetool.tagging;


import java.io.IOException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


public class CombiningTaggerTest {
    @Test
    public void testTagNoOverwrite() throws Exception {
        CombiningTagger tagger = getCombiningTagger(false, null);
        Assert.assertThat(tagger.tag("nosuchword").size(), CoreMatchers.is(0));
        List<TaggedWord> result = tagger.tag("fullform");
        Assert.assertThat(result.size(), CoreMatchers.is(2));
        String asString = getAsString(result);
        Assert.assertTrue(asString.contains("baseform1/POSTAG1"));
        Assert.assertTrue(asString.contains("baseform2/POSTAG2"));
    }

    @Test
    public void testTagOverwrite() throws Exception {
        CombiningTagger tagger = getCombiningTagger(true, null);
        Assert.assertThat(tagger.tag("nosuchword").size(), CoreMatchers.is(0));
        List<TaggedWord> result = tagger.tag("fullform");
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        String asString = getAsString(result);
        Assert.assertTrue(asString.contains("baseform2/POSTAG2"));
    }

    @Test
    public void testTagRemoval() throws Exception {
        CombiningTagger tagger = getCombiningTagger(false, "/xx/removed.txt");
        Assert.assertThat(tagger.tag("nosuchword").size(), CoreMatchers.is(0));
        List<TaggedWord> result = tagger.tag("fullform");
        String asString = getAsString(result);
        Assert.assertFalse(asString.contains("baseform1/POSTAG1"));// first tagged, but in removed.txt

        Assert.assertTrue(asString.contains("baseform2/POSTAG2"));
    }

    @Test(expected = IOException.class)
    public void testInvalidFile() throws Exception {
        new ManualTagger(JLanguageTool.getDataBroker().getFromResourceDirAsStream("/xx/added-invalid.txt"));
    }
}

