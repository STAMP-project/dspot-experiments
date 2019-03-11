package org.web3j.codegen;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.AbiDefinition;


/**
 * Test that we can parse Truffle Contract from JSON file.
 */
public class ContractJsonParseTest {
    static final String BUILD_CONTRACTS = ("build" + (File.separator)) + "contracts";

    private String contractBaseDir;

    @Test
    public void testParseMetaCoin() throws Exception {
        TruffleJsonFunctionWrapperGenerator.Contract mc = ContractJsonParseTest.parseContractJson(contractBaseDir, "MetaCoin", "MetaCoin");
        Assert.assertEquals("Unexpected contract name", "MetaCoin", mc.getContractName());
    }

    @Test
    public void testParseConvertLib() throws Exception {
        TruffleJsonFunctionWrapperGenerator.Contract mc = ContractJsonParseTest.parseContractJson(contractBaseDir, "MetaCoin", "ConvertLib");
        Assert.assertEquals("Unexpected contract name", "ConvertLib", mc.getContractName());
        Assert.assertEquals("Unexpected number of functions", 1, mc.abi.size());
        AbiDefinition abi = mc.abi.get(0);
        Assert.assertEquals("Unexpected function name", "convert", abi.getName());
        Assert.assertTrue("Expected function to be 'constant'", abi.isConstant());
        Assert.assertFalse("Expected function to not be 'payable'", abi.isPayable());
        Assert.assertEquals("Expected abi to represent a function", "function", abi.getType());
        Assert.assertEquals("Expected the 'pure' for the state mutability setting", "pure", abi.getStateMutability());
    }
}

