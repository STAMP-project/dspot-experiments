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


import Constants.DEFAULT_FILE_SYSTEM_MODE;
import ExceptionMessage.INVALID_CONFIGURATION_VALUE;
import Mode.Bits;
import Mode.Bits.ALL;
import Mode.Bits.NONE;
import Mode.Bits.READ;
import Mode.Bits.READ_EXECUTE;
import Mode.Bits.READ_WRITE;
import Mode.Bits.WRITE;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_UMASK;
import alluxio.conf.InstancedConfiguration;
import alluxio.util.ModeUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests the {@link Mode} class.
 */
public final class ModeTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private InstancedConfiguration mConfiguration;

    @Test
    public void defaults() {
        Mode mode = Mode.defaults();
        Assert.assertEquals(DEFAULT_FILE_SYSTEM_MODE, mode.toShort());
    }

    /**
     * Tests the {@link Mode#toShort()} method.
     */
    @Test
    public void toShort() {
        Mode mode = new Mode(Bits.ALL, Bits.READ_EXECUTE, Bits.READ_EXECUTE);
        Assert.assertEquals(493, mode.toShort());
        mode = Mode.defaults();
        Assert.assertEquals(511, mode.toShort());
        mode = new Mode(Bits.READ_WRITE, Bits.READ, Bits.READ);
        Assert.assertEquals(420, mode.toShort());
    }

    /**
     * Tests the {@link Mode#fromShort(short)} method.
     */
    @Test
    public void fromShort() {
        Mode mode = new Mode(((short) (511)));
        Assert.assertEquals(ALL, mode.getOwnerBits());
        Assert.assertEquals(ALL, mode.getGroupBits());
        Assert.assertEquals(ALL, mode.getOtherBits());
        mode = new Mode(((short) (420)));
        Assert.assertEquals(READ_WRITE, mode.getOwnerBits());
        Assert.assertEquals(READ, mode.getGroupBits());
        Assert.assertEquals(READ, mode.getOtherBits());
        mode = new Mode(((short) (493)));
        Assert.assertEquals(ALL, mode.getOwnerBits());
        Assert.assertEquals(READ_EXECUTE, mode.getGroupBits());
        Assert.assertEquals(READ_EXECUTE, mode.getOtherBits());
    }

    /**
     * Tests the {@link Mode#Mode(Mode)} constructor.
     */
    @Test
    public void copyConstructor() {
        Mode mode = new Mode(Mode.defaults());
        Assert.assertEquals(ALL, mode.getOwnerBits());
        Assert.assertEquals(ALL, mode.getGroupBits());
        Assert.assertEquals(ALL, mode.getOtherBits());
        Assert.assertEquals(511, mode.toShort());
    }

    /**
     * Tests the {@link Mode#createNoAccess()} method.
     */
    @Test
    public void createNoAccess() {
        Mode mode = Mode.createNoAccess();
        Assert.assertEquals(NONE, mode.getOwnerBits());
        Assert.assertEquals(NONE, mode.getGroupBits());
        Assert.assertEquals(NONE, mode.getOtherBits());
        Assert.assertEquals(0, mode.toShort());
    }

    /**
     * Tests the {@link Mode#equals(Object)} method.
     */
    @Test
    public void equals() {
        Mode allAccess = new Mode(((short) (511)));
        Assert.assertTrue(allAccess.equals(Mode.defaults()));
        Mode noAccess = new Mode(((short) (0)));
        Assert.assertTrue(noAccess.equals(Mode.createNoAccess()));
        Assert.assertFalse(allAccess.equals(noAccess));
    }

    /**
     * Tests the {@link Mode#toString()} method.
     */
    @Test
    public void toStringTest() {
        Assert.assertEquals("rwxrwxrwx", new Mode(((short) (511))).toString());
        Assert.assertEquals("rw-r-----", new Mode(((short) (416))).toString());
        Assert.assertEquals("rw-------", new Mode(((short) (384))).toString());
        Assert.assertEquals("---------", new Mode(((short) (0))).toString());
    }

    /**
     * Tests the {@link Mode#applyUMask(Mode)} method.
     */
    @Test
    public void applyUMask() {
        String umask = "0022";
        Mode mode = ModeUtils.applyDirectoryUMask(Mode.defaults(), umask);
        Assert.assertEquals(ALL, mode.getOwnerBits());
        Assert.assertEquals(READ_EXECUTE, mode.getGroupBits());
        Assert.assertEquals(READ_EXECUTE, mode.getOtherBits());
        Assert.assertEquals(493, mode.toShort());
    }

    /**
     * Tests the {@link Mode#getUMask()} method.
     */
    @Test
    public void umask() {
        Assert.assertEquals(448, ModeUtils.getUMask("0700").toShort());
        Assert.assertEquals(493, ModeUtils.getUMask("0755").toShort());
        Assert.assertEquals(420, ModeUtils.getUMask("0644").toShort());
    }

    /**
     * Tests the {@link Mode#getUMask()} method to thrown an exception when it exceeds the length.
     */
    @Test
    public void umaskExceedLength() {
        String umask = "00022";
        mConfiguration.set(SECURITY_AUTHORIZATION_PERMISSION_UMASK, umask);
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_CONFIGURATION_VALUE.getMessage(umask, SECURITY_AUTHORIZATION_PERMISSION_UMASK));
        ModeUtils.applyDirectoryUMask(Mode.defaults(), mConfiguration.get(SECURITY_AUTHORIZATION_PERMISSION_UMASK));
    }

    /**
     * Tests the {@link Mode#getUMask()} method to thrown an exception when it is not an integer.
     */
    @Test
    public void umaskNotInteger() {
        String umask = "NotInteger";
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_CONFIGURATION_VALUE.getMessage(umask, SECURITY_AUTHORIZATION_PERMISSION_UMASK));
        ModeUtils.applyDirectoryUMask(Mode.defaults(), umask);
    }

    @Test
    public void setOwnerBits() {
        Mode mode = new Mode(((short) (0)));
        mode.setOwnerBits(READ_EXECUTE);
        Assert.assertEquals(READ_EXECUTE, mode.getOwnerBits());
        mode.setOwnerBits(WRITE);
        Assert.assertEquals(WRITE, mode.getOwnerBits());
        mode.setOwnerBits(ALL);
        Assert.assertEquals(ALL, mode.getOwnerBits());
    }

    @Test
    public void setGroupBits() {
        Mode mode = new Mode(((short) (0)));
        mode.setGroupBits(READ_EXECUTE);
        Assert.assertEquals(READ_EXECUTE, mode.getGroupBits());
        mode.setGroupBits(WRITE);
        Assert.assertEquals(WRITE, mode.getGroupBits());
        mode.setGroupBits(ALL);
        Assert.assertEquals(ALL, mode.getGroupBits());
    }

    @Test
    public void setOtherBits() {
        Mode mode = new Mode(((short) (0)));
        mode.setOtherBits(READ_EXECUTE);
        Assert.assertEquals(READ_EXECUTE, mode.getOtherBits());
        mode.setOtherBits(WRITE);
        Assert.assertEquals(WRITE, mode.getOtherBits());
        mode.setOtherBits(ALL);
        Assert.assertEquals(ALL, mode.getOtherBits());
    }
}

