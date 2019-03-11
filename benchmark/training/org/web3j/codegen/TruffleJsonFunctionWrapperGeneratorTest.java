package org.web3j.codegen;


import org.junit.Test;
import org.web3j.TempFileProvider;


public class TruffleJsonFunctionWrapperGeneratorTest extends TempFileProvider {
    private static final String PackageName = "org.web3j.unittests.truffle.java";

    private String contractBaseDir;

    @Test
    public void testLibGeneration() throws Exception {
        testCodeGenerationJvmTypes("MetaCoin", "ConvertLib");
        testCodeGenerationSolidtyTypes("MetaCoin", "ConvertLib");
    }

    @Test
    public void testContractGeneration() throws Exception {
        testCodeGenerationJvmTypes("MetaCoin", "MetaCoin");
        testCodeGenerationSolidtyTypes("MetaCoin", "MetaCoin");
    }
}

