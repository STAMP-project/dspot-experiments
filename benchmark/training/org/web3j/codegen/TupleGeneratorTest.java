package org.web3j.codegen;


import TupleGenerator.PACKAGE_NAME;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.web3j.TempFileProvider;


public class TupleGeneratorTest extends TempFileProvider {
    @Test
    public void testTuplesGeneration() throws IOException {
        TupleGenerator.main(new String[]{ tempDirPath });
        String baseDir = ((tempDirPath) + (File.separatorChar)) + (PACKAGE_NAME.replace('.', File.separatorChar));
        String fileNameBase = (baseDir + (File.separator)) + (TupleGenerator.CLASS_NAME);
        List<String> fileNames = new java.util.ArrayList(TupleGenerator.LIMIT);
        for (int i = 1; i <= (TupleGenerator.LIMIT); i++) {
            fileNames.add(((fileNameBase + i) + ".java"));
        }
        verifyGeneratedCode(fileNames);
    }
}

