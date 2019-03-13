/**
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/9/18 16:23</create-date>
 *
 * <copyright file="TestSegment.java" company="????????????">
 * Copyright (c) 2003-2014, ????????????. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact ???????????? to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.seg;


import CharType.CT_DELIMITER;
import CoreDictionary.Attribute;
import HanLP.Config;
import StandardTokenizer.SEGMENT;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.hankcs.hanlp.dictionary.other.CharType;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.Other.CommonAhoCorasickSegmentUtil;
import com.hankcs.hanlp.seg.Other.DoubleArrayTrieSegment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.ResultTerm;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.common.wrapper.SegmentWrapper;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.tokenizer.TraditionalChineseTokenizer;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import junit.framework.TestCase;


/**
 *
 *
 * @author hankcs
 */
public class SegmentTest extends TestCase {
    public void testSeg() throws Exception {
        // HanLP.Config.enableDebug();
        Segment segment = new DijkstraSegment();
        // System.out.println(segment.seg(
        // "????????"
        // ));
    }

    public void testIssue1054() {
        System.out.println(HanLP.segment("????"));
    }

    public void testIssue880() throws Exception {
        // HanLP.Config.enableDebug();
        Segment segment = new DijkstraSegment();
        System.out.println(segment.seg("???????????????"));
        System.out.println(segment.seg("?????????"));
    }

    public void testViterbi() throws Exception {
        // HanLP.Config.enableDebug(true);
        CustomDictionary.add("??");
        Segment seg = new DijkstraSegment();
        List<Term> termList = seg.seg("???????????2015????????????????????????????");
        // System.out.println(termList);
    }

    public void testExtendViterbi() throws Exception {
        Config.enableDebug(false);
        String path = (((((System.getProperty("user.dir")) + "/") + "data/dictionary/custom/CustomDictionary.txt;") + (System.getProperty("user.dir"))) + "/") + "data/dictionary/custom/??????.txt";
        path = path.replace("\\", "/");
        String text = "????????????????";
        Segment segment = HanLP.newSegment().enableCustomDictionary(false);
        Segment seg = new ViterbiSegment(path);
        System.out.println(("???????????" + (segment.seg(text))));
        System.out.println(("???????" + (HanLP.segment(text))));
        seg.enableCustomDictionaryForcing(true).enableCustomDictionary(true);
        List<Term> termList = seg.seg(text);
        System.out.println(("???????????" + termList));
    }

    public void testNotional() throws Exception {
        // System.out.println(NotionalTokenizer.segment("???????????"));
    }

    public void testNGram() throws Exception {
        // System.out.println(CoreBiGramTableDictionary.getBiFrequency("?", "?"));
    }

    public void testShortest() throws Exception {
        // HanLP.Config.enableDebug();
        // Segment segment = new ViterbiSegment().enableAllNamedEntityRecognize(true);
        // System.out.println(segment.seg("??????????????????????????????"));
    }

    public void testIndexSeg() throws Exception {
        // System.out.println(IndexTokenizer.segment("????????????????"));
    }

    public void testOffset() throws Exception {
        String text = "??????????";
        // for (int i = 0; i < text.length(); ++i)
        // {
        // System.out.print(text.charAt(i) + "" + i + " ");
        // }
        // System.out.println();
        List<Term> termList = IndexTokenizer.segment(text);
        for (Term term : termList) {
            TestCase.assertEquals(term.word, text.substring(term.offset, ((term.offset) + (term.length()))));
        }
    }

    public void testWrapper() throws Exception {
        SegmentWrapper wrapper = new SegmentWrapper(new BufferedReader(new StringReader("\u4e2d\u79d1\u9662\u9884\u6d4b\u79d1\u5b66\u7814\u7a76\u4e2d\u5fc3\u5b66\u672f\u59d4\u5458\u4f1a\nhaha")), StandardTokenizer.SEGMENT);
        Term fullTerm;
        while ((fullTerm = wrapper.next()) != null) {
            // System.out.println(fullTerm);
        } 
    }

    public void testSpeechTagging() throws Exception {
        // HanLP.Config.enableDebug();
        String text = "??????????????";
        DijkstraSegment segment = new DijkstraSegment();
        // System.out.println("????" + segment.seg(text));
        segment.enablePartOfSpeechTagging(true);
        // System.out.println("????" + segment.seg(text));
    }

    public void testFactory() throws Exception {
        Segment segment = HanLP.newSegment();
    }

    public void testCustomDictionary() throws Exception {
        CustomDictionary.insert("???", "ns 1000");
        Segment segment = new ViterbiSegment();
        // System.out.println(segment.seg("???"));
    }

    public void testNT() throws Exception {
        // HanLP.Config.enableDebug();
        Segment segment = new DijkstraSegment().enableOrganizationRecognize(true);
        // System.out.println(segment.seg("??????????????"));
    }

    public void testACSegment() throws Exception {
        Segment segment = new DoubleArrayTrieSegment();
        segment.enablePartOfSpeechTagging(true);
        // System.out.println(segment.seg("????????????????????"));
    }

    public void testIssue2() throws Exception {
        // HanLP.Config.enableDebug();
        String text = "BENQphone";
        // System.out.println(HanLP.segment(text));
        CustomDictionary.insert("BENQ");
        // System.out.println(HanLP.segment(text));
    }

    public void testIssue3() throws Exception {
        TestCase.assertEquals(CT_DELIMITER, CharType.get('*'));
        // System.out.println(HanLP.segment("300g*2"));
        // System.out.println(HanLP.segment("??????"));
        // System.out.println(HanLP.segment("?300?*2/?"));
    }

    public void testIssue313() throws Exception {
        // System.out.println(HanLP.segment("hello\n" + "world"));
    }

    public void testQuickAtomSegment() throws Exception {
        String text = "??1234abc Good????3.14";
        // System.out.println(Segment.quickAtomSegment(text.toCharArray(), 0, text.length()));
    }

    public void testJP() throws Exception {
        String text = "??8.9??abc??";
        Segment segment = new ViterbiSegment().enableCustomDictionary(false).enableAllNamedEntityRecognize(false);
        // System.out.println(segment.seg(text));
    }

    // public void testSpeedOfSecondViterbi() throws Exception
    // {
    // String text = "????????";
    // Segment segment = new ViterbiSegment().enableAllNamedEntityRecognize(false)
    // .enableNameRecognize(false) // ???????????????
    // .enableCustomDictionary(false);
    // System.out.println(segment.seg(text));
    // long start = System.currentTimeMillis();
    // int pressure = 1000000;
    // for (int i = 0; i < pressure; ++i)
    // {
    // segment.seg(text);
    // }
    // double costTime = (System.currentTimeMillis() - start) / (double) 1000;
    // System.out.printf("?????%.2f???", text.length() * pressure / costTime);
    // }
    public void testNumberAndQuantifier() throws Exception {
        SEGMENT.enableNumberQuantifierRecognize(true);
        String[] testCase = new String[]{ "?????????", "??????????", "????????", "?????????????" };
        for (String sentence : testCase) {
            // System.out.println(StandardTokenizer.segment(sentence));
        }
    }

    public void testIssue10() throws Exception {
        SEGMENT.enableNumberQuantifierRecognize(true);
        IndexTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        List termList = StandardTokenizer.segment("???????????");
        // System.out.println(termList);
        termList = IndexTokenizer.segment("???????????");
        // System.out.println(termList);
        termList = StandardTokenizer.segment("15307971214??????");
        // System.out.println(termList);
        termList = IndexTokenizer.segment("15307971214??????");
        // System.out.println(termList);
    }

    // public void testIssue199() throws Exception
    // {
    // Segment segment = new CRFSegment();
    // segment.enableCustomDictionary(false);// ???????
    // segment.enablePartOfSpeechTagging(true);
    // List<Term> termList = segment.seg("????");
    // //        System.out.println(termList);
    // for (Term term : termList)
    // {
    // if (term.nature == null)
    // {
    // //                System.out.println("??????" + term.word);
    // }
    // }
    // }
    // public void testMultiThreading() throws Exception
    // {
    // Segment segment = BasicTokenizer.SEGMENT;
    // // ????
    // String text = "?????????????????????";
    // System.out.println(segment.seg(text));
    // int pressure = 100000;
    // StringBuilder sbBigText = new StringBuilder(text.length() * pressure);
    // for (int i = 0; i < pressure; i++)
    // {
    // sbBigText.append(text);
    // }
    // text = sbBigText.toString();
    // long start = System.currentTimeMillis();
    // List<Term> termList1 = segment.seg(text);
    // double costTime = (System.currentTimeMillis() - start) / (double) 1000;
    // System.out.printf("????????%.2f???\n", text.length() / costTime);
    // 
    // segment.enableMultithreading(4);
    // start = System.currentTimeMillis();
    // List<Term> termList2 = segment.seg(text);
    // costTime = (System.currentTimeMillis() - start) / (double) 1000;
    // System.out.printf("????????%.2f???\n", text.length() / costTime);
    // 
    // assertEquals(termList1.size(), termList2.size());
    // Iterator<Term> iterator1 = termList1.iterator();
    // Iterator<Term> iterator2 = termList2.iterator();
    // while (iterator1.hasNext())
    // {
    // Term term1 = iterator1.next();
    // Term term2 = iterator2.next();
    // assertEquals(term1.word, term2.word);
    // assertEquals(term1.nature, term2.nature);
    // assertEquals(term1.offset, term2.offset);
    // }
    // }
    // public void testTryToCrashSegment() throws Exception
    // {
    // String text = "???????";
    // Segment segment = new ViterbiSegment().enableMultithreading(100);
    // System.out.println(segment.seg(text));
    // }
    // public void testCRFSegment() throws Exception
    // {
    // HanLP.Config.enableDebug();
    // //        HanLP.Config.ShowTermNature = false;
    // Segment segment = new CRFSegment();
    // System.out.println(segment.seg("??????????????"));
    // }
    public void testIssue16() throws Exception {
        CustomDictionary.insert("??4g", "nz 1000");
        Segment segment = new ViterbiSegment();
        // System.out.println(segment.seg("??4g"));
        // System.out.println(segment.seg("??4G"));
        // System.out.println(segment.seg("???G"));
        // System.out.println(segment.seg("????"));
        // System.out.println(segment.seg("????"));
    }

    public void testIssuse17() throws Exception {
        // System.out.println(CharType.get('\u0000'));
        // System.out.println(CharType.get(' '));
        TestCase.assertEquals(CharTable.convert(' '), ' ');
        // System.out.println(CharTable.convert('?'));
        Config.Normalization = true;
        // System.out.println(StandardTokenizer.segment("? "));
    }

    public void testIssue22() throws Exception {
        SEGMENT.enableNumberQuantifierRecognize(false);
        CoreDictionary.Attribute attribute = CoreDictionary.get("?");
        // System.out.println(attribute);
        List<Term> termList = StandardTokenizer.segment("??");
        // System.out.println(termList);
        TestCase.assertEquals(attribute.nature[0], termList.get(1).nature);
        // System.out.println(StandardTokenizer.segment("??"));
        SEGMENT.enableNumberQuantifierRecognize(true);
        // System.out.println(StandardTokenizer.segment("??"));
    }

    public void testIssue71() throws Exception {
        Segment segment = HanLP.newSegment();
        segment = segment.enableAllNamedEntityRecognize(true);
        segment = segment.enableNumberQuantifierRecognize(true);
        // System.out.println(segment.seg("???????????????????"));
    }

    public void testIssue193() throws Exception {
        String[] testCase = new String[]{ "????200????????????????????????????????", "???2500~2800??????", "3700?????????????????", "????????????????????", "?????????", "????????5?????", "?1974????5178?????????", "?????18???????", "?????????????????", "??????????????????????" };
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true).enableNumberQuantifierRecognize(true);
        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            // System.out.println(termList);
        }
    }

    public void testTime() throws Exception {
        TraditionalChineseTokenizer.segment("????");
    }

    public void testBuildASimpleSegment() throws Exception {
        TreeMap<String, String> dictionary = new TreeMap<String, String>();
        dictionary.put("HanLP", "??");
        dictionary.put("??", "??");
        dictionary.put("??", "???");
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
        acdat.build(dictionary);
        LinkedList<ResultTerm<String>> termList = CommonAhoCorasickSegmentUtil.segment("HanLP????????", acdat);
        // System.out.println(termList);
    }

    public void testNLPSegment() throws Exception {
        String text = "2013?4?27?11?54?";
        // System.out.println(NLPTokenizer.segment(text));
    }

    public void testTraditionalSegment() throws Exception {
        String text = "??????????";
        // System.out.println(TraditionalChineseTokenizer.segment(text));
    }

    public void testIssue290() throws Exception {
        // HanLP.Config.enableDebug();
        String txt = "????????????????????????????????";
        Segment seg_viterbi = new ViterbiSegment().enablePartOfSpeechTagging(true).enableOffset(true).enableNameRecognize(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).enableNumberQuantifierRecognize(true);
        // System.out.println(seg_viterbi.seg(txt));
    }

    public void testIssue343() throws Exception {
        CustomDictionary.insert("??");
        CustomDictionary.insert("????");
        Segment segment = HanLP.newSegment().enableIndexMode(true);
        // System.out.println(segment.seg("1????2????3??4????6?7????"));
    }

    public void testIssue358() throws Exception {
        // HanLP.Config.enableDebug();
        String text = "?????????????????????????????????????????";
        Segment segment = SEGMENT.enableAllNamedEntityRecognize(false).enableCustomDictionary(false).enableOrganizationRecognize(true);
        // System.out.println(segment.seg(text));
    }

    public void testIssue496() throws Exception {
        Segment segment = HanLP.newSegment().enableIndexMode(true);
        // System.out.println(segment.seg("???"));
        // System.out.println(segment.seg("?????"));
    }

    public void testIssue513() throws Exception {
        List<Term> termList = IndexTokenizer.segment("???????");
        for (Term term : termList) {
            // System.out.println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]");
        }
    }

    public void testIssue519() throws Exception {
        String[] testCase = new String[]{ "?????", "???????", "??????", "????????????????" };
        for (String sentence : testCase) {
            // System.out.println(sentence);
            List<Term> termList = IndexTokenizer.segment(sentence);
            for (Term term : termList) {
                // System.out.println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]");
            }
            // System.out.println();
        }
    }

    public void testIssue542() throws Exception {
        Segment seg = HanLP.newSegment();
        seg.enableAllNamedEntityRecognize(true);
        seg.enableNumberQuantifierRecognize(true);
        // System.out.println(seg.seg("??????"));
    }

    public void testIssue623() throws Exception {
        SEGMENT.enableCustomDictionary(false);
        // System.out.println(HanLP.segment("??158?????"));
        // System.out.println(HanLP.segment("???18:00??????"));
    }

    public void testIssue633() throws Exception {
        CustomDictionary.add("???");
        SEGMENT.enableCustomDictionaryForcing(true);
        // System.out.println(HanLP.segment("??????????"));
    }

    public void testIssue784() throws Exception {
        String s = "????????????";
        CustomDictionary.add("??");
        SEGMENT.enableCustomDictionaryForcing(true);
        TestCase.assertTrue(HanLP.segment(s).toString().contains("??"));
    }

    public void testIssue790() throws Exception {
        Segment seg = HanLP.newSegment();
        seg.enableOrganizationRecognize(true);
        seg.enableNumberQuantifierRecognize(true);
        String raw = "1????????????????";
        // System.out.println(seg.seg(raw));
        seg.seg(raw);
    }

    public void testTimeIssue() throws Exception {
        TestCase.assertTrue(HanLP.segment("1??????????").toString().contains("1?"));
    }

    public void testIssue932() throws Exception {
        Segment segment = new DijkstraSegment().enableOrganizationRecognize(true);
        Config.enableDebug();
        System.out.println(segment.seg("??????????"));
    }
}

