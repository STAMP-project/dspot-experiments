/**
 * Copyright ? 2013-2016 Esko Luontola and other Retrolambda contributors
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.lambdas;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;


public class HandlesTest {
    @Test
    public void testGetOpcode() {
        Assert.assertThat(getOpcode(handle(Opcodes.H_INVOKEVIRTUAL)), Matchers.is(Opcodes.INVOKEVIRTUAL));
        Assert.assertThat(getOpcode(handle(Opcodes.H_INVOKESTATIC)), Matchers.is(Opcodes.INVOKESTATIC));
        Assert.assertThat(getOpcode(handle(Opcodes.H_INVOKESPECIAL)), Matchers.is(Opcodes.INVOKESPECIAL));
        Assert.assertThat(getOpcode(handle(Opcodes.H_NEWINVOKESPECIAL)), Matchers.is(Opcodes.INVOKESPECIAL));
        Assert.assertThat(getOpcode(handle(Opcodes.H_INVOKEINTERFACE)), Matchers.is(Opcodes.INVOKEINTERFACE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOpcodeNegative() {
        getOpcode(handle(0));
    }

    @Test
    public void testOpcodeToTag() {
        Assert.assertThat(opcodeToTag(Opcodes.INVOKEVIRTUAL), Matchers.is(Opcodes.H_INVOKEVIRTUAL));
        Assert.assertThat(opcodeToTag(Opcodes.INVOKESTATIC), Matchers.is(Opcodes.H_INVOKESTATIC));
        Assert.assertThat(opcodeToTag(Opcodes.INVOKESPECIAL), Matchers.is(Opcodes.H_INVOKESPECIAL));
        Assert.assertThat(opcodeToTag(Opcodes.INVOKEINTERFACE), Matchers.is(Opcodes.H_INVOKEINTERFACE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpcodeToTagNegative() {
        opcodeToTag(0);
    }

    @Test
    public void testAccessToTag() {
        Assert.assertThat(accessToTag(Opcodes.ACC_STATIC, true), Matchers.is(Opcodes.H_INVOKESTATIC));
        Assert.assertThat(accessToTag(Opcodes.ACC_STATIC, false), Matchers.is(Opcodes.H_INVOKESTATIC));
        Assert.assertThat(accessToTag(Opcodes.ACC_PRIVATE, true), Matchers.is(Opcodes.H_INVOKESPECIAL));
        Assert.assertThat(accessToTag(Opcodes.ACC_PRIVATE, false), Matchers.is(Opcodes.H_INVOKESPECIAL));
        Assert.assertThat(accessToTag(Opcodes.ACC_PUBLIC, true), Matchers.is(Opcodes.H_INVOKEINTERFACE));
        Assert.assertThat(accessToTag(Opcodes.ACC_PUBLIC, false), Matchers.is(Opcodes.H_INVOKEVIRTUAL));
    }
}

