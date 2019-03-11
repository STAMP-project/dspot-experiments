package com.hankcs.hanlp.collection.trie.datrie;


import com.hankcs.hanlp.corpus.io.ByteArray;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;


public class MutableDoubleArrayTrieIntegerTest extends TestCase {
    MutableDoubleArrayTrieInteger mdat;

    private int size;

    public void testSaveLoad() throws Exception {
        File tempFile = File.createTempFile("hanlp", ".mdat");
        mdat.save(new DataOutputStream(new FileOutputStream(tempFile)));
        mdat = new MutableDoubleArrayTrieInteger();
        mdat.load(ByteArray.createByteArray(tempFile.getAbsolutePath()));
        TestCase.assertEquals(size, mdat.size());
        for (int i = 0; i < (size); ++i) {
            TestCase.assertEquals(i, mdat.get(String.valueOf(i)));
        }
        for (int i = size; i < (2 * (size)); ++i) {
            TestCase.assertEquals((-1), mdat.get(String.valueOf(i)));
        }
    }
}

