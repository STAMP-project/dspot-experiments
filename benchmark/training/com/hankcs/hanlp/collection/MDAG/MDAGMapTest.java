package com.hankcs.hanlp.collection.MDAG;


import java.util.Set;
import junit.framework.TestCase;


// public void testBenchmark() throws Exception
// {
// testPut();
// BinTrie<Integer> binTrie = new BinTrie<Integer>();
// for (String key : validKeySet)
// {
// binTrie.put(key, key.length());
// }
// mdagMap.simplify();
// for (String key : validKeySet)
// {
// assertEquals(binTrie.commonPrefixSearchWithValue(key).size(), mdagMap.commonPrefixSearchWithValue(key).size());
// }
// 
// long start;
// start = System.currentTimeMillis();
// for (String key : validKeySet)
// {
// binTrie.commonPrefixSearchWithValue(key);
// }
// System.out.printf("binTrie: %d ms\n", System.currentTimeMillis() - start);
// 
// start = System.currentTimeMillis();
// for (String key : validKeySet)
// {
// mdagMap.commonPrefixSearchWithValue(key);
// }
// System.out.printf("mdagMap: %d ms\n", System.currentTimeMillis() - start);
// }
public class MDAGMapTest extends TestCase {
    MDAGMap<Integer> mdagMap = new MDAGMap<Integer>();

    Set<String> validKeySet;

    public void testGet() throws Exception {
        testPut();
        mdagMap.simplify();
        // mdagMap.unSimplify();
        for (String word : validKeySet) {
            TestCase.assertEquals(word.length(), ((int) (mdagMap.get(word))));
        }
    }

    public void testSingle() throws Exception {
        testPut();
        mdagMap.simplify();
        TestCase.assertEquals(null, mdagMap.get("???"));
    }

    public void testCommonPrefixSearch() throws Exception {
        testPut();
        TestCase.assertEquals("[hankcs=6]", mdagMap.commonPrefixSearchWithValue("hankcs").toString());
    }
}

