package com.hankcs.hanlp.dictionary;


import com.hankcs.hanlp.utility.Predefine;
import junit.framework.TestCase;


// public void testDump() throws Exception
// {
// DictionaryMaker dictionaryMaker = new DictionaryMaker();
// for (Map.Entry<String, Integer> entry : PlaceSuffixDictionary.dictionary.entrySet())
// {
// dictionaryMaker.add(entry.getKey(), NS.H.toString());
// }
// dictionaryMaker.saveTxtTo("data/dictionary/place/suffix.txt");
// }
public class SuffixDictionaryTest extends TestCase {
    SuffixDictionary dictionary = new SuffixDictionary();

    public void testGet() throws Exception {
        String total = Predefine.POSTFIX_SINGLE;
        for (int i = 0; i < (total.length()); ++i) {
            String single = String.valueOf(total.charAt(i));
            TestCase.assertEquals(1, dictionary.get(single));
        }
        for (String single : Predefine.POSTFIX_MUTIPLE) {
            TestCase.assertEquals(single.length(), dictionary.get(single));
        }
    }

    public void testEndsWith() throws Exception {
        TestCase.assertEquals(true, dictionary.endsWith("???"));
        TestCase.assertEquals(false, dictionary.endsWith("?????"));
    }

    public void testLongest() throws Exception {
        TestCase.assertEquals(2, dictionary.getLongestSuffixLength("?????"));
    }
}

