/**
 *
 */
package org.fnlp.nlp.tag;


import org.junit.Test;


/**
 *
 *
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version ?????2014?11?14? ??10:27:23
 */
public class TaggerTest {
    /**
     * Test method for {@link org.fnlp.nlp.tag.Tagger#main(java.lang.String[])}.
     */
    @Test
    public void testMain() {
        try {
            Tagger.main("-train ../example-data/sequence/template ../example-data/sequence/train.txt ../tmp/tmp.m".split("\\s+"));
            Tagger.main("../tmp/tmp.m ../example-data/sequence/test.txt ../tmp/res.txt".split("\\s+"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

