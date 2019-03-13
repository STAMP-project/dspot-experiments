package com.hankcs.hanlp.collection.trie.datrie;


import com.hankcs.hanlp.corpus.io.ByteArray;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;


public class IntArrayListTest extends TestCase {
    IntArrayList array = new IntArrayList();

    public void testSaveLoad() throws Exception {
        File tempFile = File.createTempFile("hanlp", ".intarray");
        array.save(new DataOutputStream(new FileOutputStream(tempFile.getAbsolutePath())));
        array.load(ByteArray.createByteArray(tempFile.getAbsolutePath()));
        for (int i = 0; i < 64; ++i) {
            TestCase.assertEquals(i, array.get(i));
        }
    }
}

