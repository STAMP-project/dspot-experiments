package com.hankcs.hanlp.dictionary;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import junit.framework.TestCase;


public class CustomDictionaryTest extends TestCase {
    // public void testReload() throws Exception
    // {
    // assertEquals(true, CustomDictionary.reload());
    // assertEquals(true, CustomDictionary.contains("?????"));
    // }
    public void testGet() throws Exception {
        TestCase.assertEquals("nz 1 ", CustomDictionary.get("?????").toString());
    }

    /**
     * ????????
     *
     * @throws Exception
     * 		
     */
    // public void testRemoveShortWord() throws Exception
    // {
    // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/dictionary/CustomDictionary.txt")));
    // String line;
    // Set<String> fixedDictionary = new TreeSet<String>();
    // while ((line = br.readLine()) != null)
    // {
    // String[] param = line.split("\\s");
    // if (param[0].length() == 1 || CoreDictionary.contains(param[0])) continue;
    // fixedDictionary.add(line);
    // }
    // br.close();
    // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/dictionary/CustomDictionary.txt")));
    // for (String word : fixedDictionary)
    // {
    // bw.write(word);
    // bw.newLine();
    // }
    // bw.close();
    // }
    /**
     * ?????nr?????????
     *
     * @throws Exception
     * 		
     */
    // public void testRemoveNR() throws Exception
    // {
    // BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/dictionary/CustomDictionary.txt")));
    // String line;
    // Set<String> fixedDictionary = new TreeSet<String>();
    // while ((line = br.readLine()) != null)
    // {
    // String[] param = line.split("\\s");
    // if (param[1].equals("nr")) continue;
    // fixedDictionary.add(line);
    // }
    // br.close();
    // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/dictionary/CustomDictionary.txt")));
    // for (String word : fixedDictionary)
    // {
    // bw.write(word);
    // bw.newLine();
    // }
    // bw.close();
    // }
    // public void testNext() throws Exception
    // {
    // BaseSearcher searcher = CustomDictionary.getSearcher("????");
    // Map.Entry<String, CoreDictionary.Attribute> entry;
    // while ((entry = searcher.next()) != null)
    // {
    // int offset = searcher.getOffset();
    // System.out.println(offset + 1 + " " + entry);
    // }
    // }
    // public void testRemoveJunkWord() throws Exception
    // {
    // DictionaryMaker dictionaryMaker = DictionaryMaker.load("data/dictionary/custom/CustomDictionary.txt");
    // dictionaryMaker.saveTxtTo("data/dictionary/custom/CustomDictionary.txt", new DictionaryMaker.Filter()
    // {
    // @Override
    // public boolean onSave(Item item)
    // {
    // if (item.containsLabel("mq") || item.containsLabel("m") || item.containsLabel("t"))
    // {
    // return false;
    // }
    // return true;
    // }
    // });
    // }
    /**
     * data/dictionary/custom/??????.txt???????????
     *
     * @throws Exception
     * 		
     */
    // public void testRemoveNotNS() throws Exception
    // {
    // String path = "data/dictionary/custom/??????.txt";
    // final Set<Character> suffixSet = new TreeSet<Character>();
    // for (char c : Predefine.POSTFIX_SINGLE.toCharArray())
    // {
    // suffixSet.add(c);
    // }
    // DictionaryMaker.load(path).saveTxtTo(path, new DictionaryMaker.Filter()
    // {
    // Segment segment = HanLP.newSegment().enableCustomDictionary(false);
    // @Override
    // public boolean onSave(Item item)
    // {
    // if (suffixSet.contains(item.key.charAt(item.key.length() - 1))) return true;
    // List<Term> termList = segment.seg(item.key);
    // if (termList.size() == 1 && termList.get(0).nature == Nature.nr)
    // {
    // System.out.println(item);
    // return false;
    // }
    // return true;
    // }
    // });
    // }
    public void testCustomNature() throws Exception {
        Nature pcNature1 = Nature.create("????");
        Nature pcNature2 = Nature.create("????");
        TestCase.assertEquals(pcNature1, pcNature2);
    }

    // public void testIssue234() throws Exception
    // {
    // String customTerm = "???";
    // String text = "?????????????????????";
    // System.out.println("??????");
    // System.out.println("CustomDictionary.get(customTerm)=" + CustomDictionary.get(customTerm));
    // System.out.println(HanLP.segment(text));
    // // ????
    // CustomDictionary.add(customTerm);
    // System.out.println("???????????");
    // System.out.println("CustomDictionary.get(customTerm)=" + CustomDictionary.get(customTerm));
    // System.out.println(HanLP.segment(text));
    // // ????
    // CustomDictionary.remove(customTerm);
    // System.out.println("???????????");
    // System.out.println("CustomDictionary.get(customTerm)=" + CustomDictionary.get(customTerm));
    // System.out.println(HanLP.segment(text));
    // }
    public void testIssue540() throws Exception {
        CustomDictionary.add("123");
        CustomDictionary.add("??");
        CustomDictionary.remove("123");
        CustomDictionary.remove("??");
    }

    public void testReload() {
        CustomDictionary.reload();
        System.out.println(HanLP.segment("??????"));
    }
}

