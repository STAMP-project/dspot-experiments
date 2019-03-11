package org.web3j.codegen;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.TempFileProvider;


public class AbiTypesGeneratorTest extends TempFileProvider {
    @Test
    public void testGetPackageName() {
        Assert.assertThat(AbiTypesGenerator.getPackageName(String.class), Is.is("java.lang"));
    }

    @Test
    public void testCreatePackageName() {
        Assert.assertThat(AbiTypesGenerator.createPackageName(String.class), Is.is("java.lang.generated"));
    }

    @Test
    public void testGeneration() throws Exception {
        AbiTypesGenerator.main(new String[]{ tempDirPath });
    }
}

