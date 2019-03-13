/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.authorization;


import Mode.Bits;
import Mode.Bits.ALL;
import Mode.Bits.EXECUTE;
import Mode.Bits.NONE;
import Mode.Bits.READ;
import Mode.Bits.READ_EXECUTE;
import Mode.Bits.READ_WRITE;
import Mode.Bits.WRITE;
import Mode.Bits.WRITE_EXECUTE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link Mode.Bits} class.
 */
public final class ModeBitsTest {
    /**
     * Tests the {@link Mode.Bits#toString()} method.
     */
    @Test
    public void getSymbol() {
        Assert.assertEquals("---", NONE.toString());
        Assert.assertEquals("r--", READ.toString());
        Assert.assertEquals("-w-", WRITE.toString());
        Assert.assertEquals("--x", EXECUTE.toString());
        Assert.assertEquals("rw-", READ_WRITE.toString());
        Assert.assertEquals("r-x", READ_EXECUTE.toString());
        Assert.assertEquals("-wx", WRITE_EXECUTE.toString());
        Assert.assertEquals("rwx", ALL.toString());
    }

    /**
     * Tests the {@link Mode.Bits#imply(Mode.Bits)} method.
     */
    @Test
    public void implies() {
        Assert.assertTrue(ALL.imply(READ));
        Assert.assertTrue(ALL.imply(WRITE));
        Assert.assertTrue(ALL.imply(EXECUTE));
        Assert.assertTrue(ALL.imply(READ_EXECUTE));
        Assert.assertTrue(ALL.imply(WRITE_EXECUTE));
        Assert.assertTrue(ALL.imply(ALL));
        Assert.assertTrue(READ_EXECUTE.imply(READ));
        Assert.assertTrue(READ_EXECUTE.imply(EXECUTE));
        Assert.assertFalse(READ_EXECUTE.imply(WRITE));
        Assert.assertTrue(WRITE_EXECUTE.imply(WRITE));
        Assert.assertTrue(WRITE_EXECUTE.imply(EXECUTE));
        Assert.assertFalse(WRITE_EXECUTE.imply(READ));
        Assert.assertTrue(READ_WRITE.imply(WRITE));
        Assert.assertTrue(READ_WRITE.imply(READ));
        Assert.assertFalse(READ_WRITE.imply(EXECUTE));
    }

    /**
     * Tests the {@link Mode.Bits#not()} method.
     */
    @Test
    public void notOperation() {
        Assert.assertEquals(WRITE, READ_EXECUTE.not());
        Assert.assertEquals(READ, WRITE_EXECUTE.not());
        Assert.assertEquals(EXECUTE, READ_WRITE.not());
    }

    /**
     * Tests the {@link Mode.Bits#or(Mode.Bits)} method.
     */
    @Test
    public void orOperation() {
        Assert.assertEquals(WRITE_EXECUTE, WRITE.or(EXECUTE));
        Assert.assertEquals(READ_EXECUTE, READ.or(EXECUTE));
        Assert.assertEquals(READ_WRITE, WRITE.or(READ));
    }

    /**
     * Tests the {@link Mode.Bits#and(Mode.Bits)} method.
     */
    @Test
    public void andOperation() {
        Assert.assertEquals(NONE, READ.and(WRITE));
        Assert.assertEquals(READ, READ_EXECUTE.and(READ));
        Assert.assertEquals(WRITE, READ_WRITE.and(WRITE));
    }

    /**
     * Tests {@link Mode.Bits#toAclActionSet()}.
     */
    @Test
    public void toAclActions() {
        for (Mode.Bits bits : Bits.values()) {
            Assert.assertEquals(bits, toModeBits());
        }
    }
}

