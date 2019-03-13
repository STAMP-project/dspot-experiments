package com.hankcs.hanlp.mining.word2vec;


import com.hankcs.hanlp.corpus.io.IOUtil;
import java.io.BufferedWriter;
import java.io.File;
import junit.framework.TestCase;


public class VectorsReaderTest extends TestCase {
    public void testReadVectorFile() throws Exception {
        File tempFile = File.createTempFile("hanlp-vector", ".txt");
        tempFile.deleteOnExit();
        BufferedWriter bw = IOUtil.newBufferedWriter(tempFile.getAbsolutePath());
        bw.write(("3 1\n" + (("cat 1.1\n" + " 2.2\n") + "dog 3.3\n")));
        bw.close();
        VectorsReader reader = new VectorsReader(tempFile.getAbsolutePath());
        reader.readVectorFile();
        TestCase.assertEquals(2, reader.words);
        TestCase.assertEquals(2, reader.vocab.length);
        TestCase.assertEquals(2, reader.matrix.length);
        TestCase.assertEquals(1.0F, reader.matrix[1][0]);
    }
}

