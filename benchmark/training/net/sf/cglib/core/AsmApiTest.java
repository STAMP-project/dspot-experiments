package net.sf.cglib.core;


import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;


public class AsmApiTest {
    @Test
    public void testValue() {
        Assert.assertEquals(ASM7, AsmApi.value());
    }

    /**
     * With the release of ASM 7.0 beta, Opcodes.ASM7_EXPERIMENTAL
     * has been replaced by Opcodes.ASM7 so we simply ignore
     * the system property and default to the newest stable
     * version.
     */
    @Test
    public void testValueWithAsm7Experimental() {
        int asmApi = setAsm7ExperimentalAndGetValue("true");
        Assert.assertEquals(ASM7, asmApi);
        asmApi = setAsm7ExperimentalAndGetValue("");
        Assert.assertEquals(ASM7, asmApi);
        asmApi = setAsm7ExperimentalAndGetValue("false");
        Assert.assertEquals(ASM7, asmApi);
    }
}

