package com.hankcs.hanlp.model.crf;


import HanLP.Config;
import com.hankcs.hanlp.corpus.io.ByteArray;
import com.hankcs.hanlp.utility.Predefine;
import junit.framework.TestCase;


// public void testRemoveSpace() throws Exception
// {
// String inputPath = "E:\\2014.txt";
// String outputPath = "E:\\2014f.txt";
// BufferedReader br = IOUtil.newBufferedReader(inputPath);
// BufferedWriter bw = IOUtil.newBufferedWriter(outputPath);
// String line = "";
// int preLength = 0;
// while ((line = br.readLine()) != null)
// {
// if (preLength == 0 && line.length() == 0) continue;
// bw.write(line);
// bw.newLine();
// preLength = line.length();
// }
// bw.close();
// }
public class CRFModelTest extends TestCase {
    public void testLoadModelWithBiGramFeature() throws Exception {
        String path = (Config.CRFSegmentModelPath) + (Predefine.BIN_EXT);
        CRFModel model = new CRFModel(new com.hankcs.hanlp.collection.trie.bintrie.BinTrie<FeatureFunction>());
        model.load(ByteArray.createByteArray(path));
        Table table = new Table();
        String text = "??????????";
        table.v = new String[text.length()][2];
        for (int i = 0; i < (text.length()); i++) {
            table.v[i][0] = String.valueOf(text.charAt(i));
        }
        model.tag(table);
        // System.out.println(table);
    }
}

