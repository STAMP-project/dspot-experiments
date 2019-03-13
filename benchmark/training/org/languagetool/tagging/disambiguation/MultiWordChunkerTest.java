package org.languagetool.tagging.disambiguation;


import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.FakeLanguage;
import org.languagetool.JLanguageTool;
import org.languagetool.tagging.Tagger;
import org.languagetool.tagging.xx.DemoTagger;


public class MultiWordChunkerTest {
    private final JLanguageTool lt = new JLanguageTool(new FakeLanguage() {
        public Tagger getTagger() {
            return new DemoTagger() {
                public List<AnalyzedTokenReadings> tag(List<String> sentenceTokens) {
                    List<AnalyzedTokenReadings> tokenReadings = super.tag(sentenceTokens);
                    for (AnalyzedTokenReadings readings : tokenReadings) {
                        if (readings.isWhitespace())
                            continue;

                        readings.addReading(new org.languagetool.AnalyzedToken(readings.getToken(), "FakePosTag", readings.getToken()));
                    }
                    return tokenReadings;
                }
            };
        }
    });

    @Test
    public void testDisambiguate1() throws IOException {
        MultiWordChunker multiWordChunker = new MultiWordChunker("/yy/multiwords.txt", true);
        AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence("ah for shame");
        AnalyzedSentence disambiguated = multiWordChunker.disambiguate(analyzedSentence);
        AnalyzedTokenReadings[] tokens = disambiguated.getTokens();
        Assert.assertTrue(tokens[1].getReadings().toString().contains("<adv>"));
        Assert.assertFalse(tokens[3].getReadings().toString().contains("adv"));
        Assert.assertTrue(tokens[5].getReadings().toString().contains("</adv>"));
        Assert.assertTrue(tokens[1].getReadings().toString().contains("FakePosTag"));
        Assert.assertTrue(tokens[3].getReadings().toString().contains("FakePosTag"));
        Assert.assertTrue(tokens[5].getReadings().toString().contains("FakePosTag"));
    }

    @Test
    public void testDisambiguate2() throws IOException {
        MultiWordChunker2 multiWordChunker = new MultiWordChunker2("/yy/multiwords.txt", true);
        AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence("Ah for shame");
        AnalyzedSentence disambiguated = multiWordChunker.disambiguate(analyzedSentence);
        AnalyzedTokenReadings[] tokens = disambiguated.getTokens();
        Assert.assertTrue(tokens[1].getReadings().toString().contains("<adv>"));
        Assert.assertTrue(tokens[3].getReadings().toString().contains("<adv>"));
        Assert.assertTrue(tokens[5].getReadings().toString().contains("<adv>"));
        Assert.assertTrue(tokens[1].getReadings().toString().contains("FakePosTag"));
        Assert.assertTrue(tokens[3].getReadings().toString().contains("FakePosTag"));
        Assert.assertTrue(tokens[5].getReadings().toString().contains("FakePosTag"));
    }

    @Test
    public void testDisambiguate2NoMatch() throws IOException {
        MultiWordChunker2 multiWordChunker = new MultiWordChunker2("/yy/multiwords.txt", true);
        AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence("ahh for shame");
        AnalyzedSentence disambiguated = multiWordChunker.disambiguate(analyzedSentence);
        AnalyzedTokenReadings[] tokens = disambiguated.getTokens();
        Assert.assertFalse(tokens[1].getReadings().toString().contains("<adv>"));
    }

    @Test
    public void testDisambiguate2RemoveOtherReadings() throws IOException {
        MultiWordChunker2 multiWordChunker = new MultiWordChunker2("/yy/multiwords.txt", true);
        multiWordChunker.setRemoveOtherReadings(true);
        multiWordChunker.setWrapTag(false);
        AnalyzedSentence analyzedSentence = lt.getAnalyzedSentence("ah for shame");
        AnalyzedSentence disambiguated = multiWordChunker.disambiguate(analyzedSentence);
        AnalyzedTokenReadings[] tokens = disambiguated.getTokens();
        Assert.assertTrue(tokens[1].getReadings().toString().contains("adv"));
        Assert.assertTrue(tokens[3].getReadings().toString().contains("adv"));
        Assert.assertTrue(tokens[5].getReadings().toString().contains("adv"));
        Assert.assertFalse(tokens[1].getReadings().toString().contains("FakePosTag"));
        Assert.assertFalse(tokens[3].getReadings().toString().contains("FakePosTag"));
        Assert.assertFalse(tokens[5].getReadings().toString().contains("FakePosTag"));
    }
}

