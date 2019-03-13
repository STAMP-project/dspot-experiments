package com.hankcs.hanlp.dictionary.stopword;


import com.hankcs.hanlp.collection.MDAG.MDAGSet;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;


// public void testRemoveDuplicateEntries() throws Exception
// {
// StopWordDictionary dictionary = new StopWordDictionary(new File(HanLP.Config.CoreStopWordDictionaryPath));
// BufferedWriter bw = IOUtil.newBufferedWriter(HanLP.Config.CoreStopWordDictionaryPath);
// for (String word : dictionary)
// {
// bw.write(word);
// bw.newLine();
// }
// bw.close();
// }
public class CoreStopWordDictionaryTest extends TestCase {
    public void testContains() throws Exception {
        TestCase.assertTrue(CoreStopWordDictionary.contains("????"));
    }

    public void testContainsSomeWords() throws Exception {
        TestCase.assertEquals(true, CoreStopWordDictionary.contains("??"));
    }

    public void testMDAG() throws Exception {
        List<String> wordList = new LinkedList<String>();
        wordList.add("zoo");
        wordList.add("hello");
        wordList.add("world");
        MDAGSet set = new MDAGSet(wordList);
        set.add("bee");
        TestCase.assertEquals(true, set.contains("bee"));
        set.remove("bee");
        TestCase.assertEquals(false, set.contains("bee"));
    }
}

