package org.ansj.app.crf;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;


// @Test
// public void test() throws Exception {
// Model model = Model.load("src/main/resources/crf.model");
// System.out.println(new SplitWord(model).cut("?????????"));
// 
// String path = "/Users/sunjian/Documents/src/CRF++-0.58/test/model.txt";
// 
// if (Check.checkFileExit(path)) {
// model = Model.load(path);
// System.out.println(new SplitWord(model).cut("?????????"));
// }
// 
// path = "/Users/sunjian/Documents/src/Wapiti/test/model.dat";
// if (Check.checkFileExit(path)) {
// model = Model.load("/Users/sunjian/Documents/src/Wapiti/test/model.dat");
// System.out.println(new SplitWord(model).cut("?????????"));
// }
// 
// }
public class ModelTest {
    @Test
    public void CRFSplitTest() {
        List<String> cut = org.ansj.library.CrfLibrary.get().cut("???????????????1990????????????");
        Set<String> words = new HashSet<String>(cut);
        Assert.assertTrue(words.contains("??????"));
    }
}

