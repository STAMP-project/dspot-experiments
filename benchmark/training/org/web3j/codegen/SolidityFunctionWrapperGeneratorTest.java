package org.web3j.codegen;


import java.util.Arrays;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.TempFileProvider;


public class SolidityFunctionWrapperGeneratorTest extends TempFileProvider {
    private String solidityBaseDir;

    @Test
    public void testGetFileNoExtension() {
        Assert.assertThat(SolidityFunctionWrapperGenerator.getFileNameNoExtension(""), Is.is(""));
        Assert.assertThat(SolidityFunctionWrapperGenerator.getFileNameNoExtension("file"), Is.is("file"));
        Assert.assertThat(SolidityFunctionWrapperGenerator.getFileNameNoExtension("file."), Is.is("file"));
        Assert.assertThat(SolidityFunctionWrapperGenerator.getFileNameNoExtension("file.txt"), Is.is("file"));
    }

    @Test
    public void testGreeterGeneration() throws Exception {
        testCodeGenerationJvmTypes("greeter", "Greeter");
        testCodeGenerationSolidityTypes("greeter", "Greeter");
    }

    @Test
    public void testHumanStandardTokenGeneration() throws Exception {
        testCodeGenerationJvmTypes("contracts", "HumanStandardToken");
        testCodeGenerationSolidityTypes("contracts", "HumanStandardToken");
    }

    @Test
    public void testSimpleStorageGeneration() throws Exception {
        testCodeGenerationJvmTypes("simplestorage", "SimpleStorage");
        testCodeGenerationSolidityTypes("simplestorage", "SimpleStorage");
    }

    @Test
    public void testFibonacciGeneration() throws Exception {
        testCodeGenerationJvmTypes("fibonacci", "Fibonacci");
        testCodeGenerationSolidityTypes("fibonacci", "Fibonacci");
    }

    @Test
    public void testArrays() throws Exception {
        testCodeGenerationJvmTypes("arrays", "Arrays");
        testCodeGenerationSolidityTypes("arrays", "Arrays");
    }

    @Test
    public void testShipIt() throws Exception {
        testCodeGenerationJvmTypes("shipit", "ShipIt");
        testCodeGenerationSolidityTypes("shipit", "ShipIt");
    }

    @Test
    public void testMisc() throws Exception {
        testCodeGenerationJvmTypes("misc", "Misc");
        testCodeGenerationSolidityTypes("misc", "Misc");
    }

    @Test
    public void testContractsNoBin() throws Exception {
        testCodeGeneration("contracts", "HumanStandardToken", FunctionWrapperGenerator.JAVA_TYPES_ARG, false);
        testCodeGeneration("contracts", "HumanStandardToken", FunctionWrapperGenerator.SOLIDITY_TYPES_ARG, false);
    }

    @Test
    public void testGenerationCommandPrefixes() throws Exception {
        testCodeGeneration(Arrays.asList(SolidityFunctionWrapperGenerator.COMMAND_SOLIDITY, SolidityFunctionWrapperGenerator.COMMAND_GENERATE), "contracts", "HumanStandardToken", FunctionWrapperGenerator.JAVA_TYPES_ARG, true);
        testCodeGeneration(Arrays.asList(SolidityFunctionWrapperGenerator.COMMAND_GENERATE), "contracts", "HumanStandardToken", FunctionWrapperGenerator.SOLIDITY_TYPES_ARG, true);
    }
}

