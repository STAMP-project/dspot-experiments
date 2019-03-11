package com.hankcs.hanlp.collection.trie.bintrie;


import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import java.io.File;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


// public void testCustomDictionary() throws Exception
// {
// HanLP.Config.enableDebug(true);
// System.out.println(CustomDictionary.get("????"));
// }
// 
// public void testSortCustomDictionary() throws Exception
// {
// DictionaryUtil.sortDictionary(HanLP.Config.CustomDictionaryPath[0]);
// }
public class BinTrieTest extends TestCase {
    static String DATA_TEST_OUT_BIN;

    private File tempFile;

    public void testParseText() throws Exception {
        BinTrie<String> trie = new BinTrie<String>();
        String[] keys = new String[]{ "he", "her", "his" };
        for (String key : keys) {
            trie.put(key, key);
        }
        final String text = " her4he7his ";
        AhoCorasickDoubleArrayTrie.IHit<String> processor = new AhoCorasickDoubleArrayTrie.IHit<String>() {
            @Override
            public void hit(int begin, int end, String value) {
                // System.out.printf("[%d, %d)=%s\n", begin, end, value);
                assertEquals(value, text.substring(begin, end));
            }
        };
        // trie.parseLongestText(text, processor);
        trie.parseText(text, processor);
    }

    public void testPut() throws Exception {
        BinTrie<Boolean> trie = new BinTrie<Boolean>();
        trie.put("??", true);
        trie.put("??", false);
        TestCase.assertEquals(new Boolean(false), trie.get("??"));
    }

    public void testArrayIndexOutOfBoundsException() throws Exception {
        BinTrie<Boolean> trie = new BinTrie<Boolean>();
        trie.put(new char[]{ '\uffff' }, true);
    }

    public void testSaveAndLoad() throws Exception {
        BinTrie<Integer> trie = new BinTrie<Integer>();
        trie.put("haha", 0);
        trie.put("hankcs", 1);
        trie.put("hello", 2);
        trie.put("za", 3);
        trie.put("zb", 4);
        trie.put("zzz", 5);
        TestCase.assertTrue(trie.save(BinTrieTest.DATA_TEST_OUT_BIN));
        trie = new BinTrie<Integer>();
        Integer[] value = new Integer[100];
        for (int i = 0; i < (value.length); ++i) {
            value[i] = i;
        }
        TestCase.assertTrue(trie.load(BinTrieTest.DATA_TEST_OUT_BIN, value));
        Set<Map.Entry<String, Integer>> entrySet = trie.entrySet();
        TestCase.assertEquals("[haha=0, hankcs=1, hello=2, za=3, zb=4, zzz=5]", entrySet.toString());
    }
}

