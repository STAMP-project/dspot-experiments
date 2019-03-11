package com.hankcs.hanlp.corpus.io;


import HanLP.Config;
import Predefine.BIN_EXT;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;


public class IIOAdapterTest extends TestCase {
    /**
     * ????????IOAdapter??HanLP???????
     *
     * @throws Exception
     * 		
     */
    public void testReturnNullInIOAdapter() throws Exception {
        Config.IOAdapter = new FileIOAdapter() {
            @Override
            public InputStream open(String path) throws FileNotFoundException {
                if (path.endsWith(BIN_EXT))
                    return null;

                return super.open(path);
            }

            @Override
            public OutputStream create(String path) throws FileNotFoundException {
                if (path.endsWith(BIN_EXT))
                    return null;

                return super.create(path);
            }
        };
        Config.enableDebug(false);
        TestCase.assertEquals(true, CoreStopWordDictionary.contains("?"));
    }
}

