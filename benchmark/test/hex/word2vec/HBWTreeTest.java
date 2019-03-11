package hex.word2vec;


import org.junit.Assert;
import org.junit.Test;


public class HBWTreeTest {
    @Test
    public void buildHuffmanBinaryWordTree() throws Exception {
        HBWTree t = HBWTree.buildHuffmanBinaryWordTree(new long[]{ 1, 2, 3 });
        Assert.assertNotNull(t);
    }
}

