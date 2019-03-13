package com.hankcs.hanlp.model.perceptron;


import Nature.nr;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.hankcs.hanlp.seg.common.Term;
import java.util.List;
import junit.framework.TestCase;


public class PerceptronLexicalAnalyzerTest extends TestCase {
    PerceptronLexicalAnalyzer analyzer;

    public void testIssue() throws Exception {
        // System.out.println(analyzer.seg(""));
        for (Term term : analyzer.seg("??????????????????????????????????????")) {
            if ((term.nature) == (Nature.w))
                continue;

            TestCase.assertEquals(nr, term.nature);
        }
    }

    public void testLearn() throws Exception {
        analyzer.learn("?/r ?/p ??/ns ??/ns ??/v");
        TestCase.assertTrue(analyzer.analyze("????????").toString().contains("??/ns"));
        TestCase.assertTrue(analyzer.analyze("???????").toString().contains("??/nr"));
    }

    public void testEmptyInput() throws Exception {
        analyzer.segment("");
        analyzer.seg("");
    }

    public void testCustomDictionary() throws Exception {
        analyzer.enableCustomDictionary(true);
        TestCase.assertTrue(CustomDictionary.contains("?????"));
        final String text = "?????????????????????";
        // System.out.println(analyzer.analyze(text));
        TestCase.assertTrue(analyzer.analyze(text).toString().contains(" ?????/"));
    }

    public void testCustomNature() throws Exception {
        TestCase.assertTrue(CustomDictionary.insert("???", "ntc 1"));
        analyzer.enableCustomDictionaryForcing(true);
        TestCase.assertEquals("??/n ?/p ???/ntc ??/v ??/v ??/n", analyzer.analyze("????????????").toString());
    }

    public void testIndexMode() throws Exception {
        analyzer.enableIndexMode(true);
        String text = "???????????????";
        List<Term> termList = analyzer.seg(text);
        TestCase.assertEquals("[??/v, ???????????/ns, ??/ns, ??/ns, ??/t, ??/n, ???/n, ??/v]", termList.toString());
        for (Term term : termList) {
            TestCase.assertEquals(term.word, text.substring(term.offset, ((term.offset) + (term.length()))));
        }
        analyzer.enableIndexMode(false);
    }

    public void testOffset() throws Exception {
        analyzer.enableIndexMode(false);
        String text = "???????????????";
        List<Term> termList = analyzer.seg(text);
        for (Term term : termList) {
            TestCase.assertEquals(term.word, text.substring(term.offset, ((term.offset) + (term.length()))));
        }
    }

    public void testNormalization() throws Exception {
        analyzer.enableCustomDictionary(false);
        String text = "????????????????";
        Sentence sentence = analyzer.analyze(text);
        // System.out.println(sentence);
        TestCase.assertEquals("??/v [??/ns ??/ns ??/t ??/n ???/n]/ns ??/v ?/w", sentence.toString());
        List<Term> termList = analyzer.seg(text);
        // System.out.println(termList);
        TestCase.assertEquals("[??/v, ???????????/ns, ??/v, ?/w]", termList.toString());
    }

    public void testWhiteSpace() throws Exception {
        CharTable.CONVERT[' '] = '!';
        CharTable.CONVERT['\t'] = '!';
        Sentence sentence = analyzer.analyze("\"\u4f60\u597d\uff0c \u6211\u60f3\u77e5\u9053\uff1a \u98ce\u662f\u4ece\u54ea\u91cc\u6765; \t\u96f7\u662f\u4ece\u54ea\u91cc\u6765\uff1b \u96e8\u662f\u4ece\u54ea\u91cc\u6765\uff1f\"");
        for (IWord word : sentence) {
            if (!(word.getLabel().equals("w"))) {
                TestCase.assertFalse(word.getValue().contains(" "));
                TestCase.assertFalse(word.getValue().contains("\t"));
            }
        }
    }

    public void testCustomDictionaryForcing() throws Exception {
        String text = "?????????????????";
        CustomDictionary.insert("??", "NRF 1");
        analyzer.enableCustomDictionaryForcing(false);
        System.out.println(analyzer.analyze(text));
        analyzer.enableCustomDictionaryForcing(true);
        System.out.println(analyzer.analyze(text));
    }

    public void testRules() throws Exception {
        analyzer.enableRuleBasedSegment(true);
        System.out.println(analyzer.analyze("????????1975????????????????18???????????????????"));
    }
}

