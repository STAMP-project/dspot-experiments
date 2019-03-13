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
package alluxio.master.file.meta;


import Constants.KB;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_UMASK;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.BlockInfoException;
import alluxio.security.authorization.Mode;
import alluxio.util.ModeUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link MutableInodeFile}.
 */
public final class MutableInodeFileTest extends AbstractInodeTest {
    private static final long LENGTH = 100;

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    /**
     * Tests the {@link MutableInodeFile#equals(Object)} method.
     */
    @Test
    public void equals() {
        MutableInodeFile inode1 = createInodeFile(1);
        // self equal
        Assert.assertEquals(inode1, inode1);
        MutableInodeFile inode2 = createInodeFile(1);
        // equal with same id
        Assert.assertEquals(inode1, inode2);
        MutableInodeFile inode3 = createInodeFile(3);
        Assert.assertFalse(inode1.equals(inode3));
    }

    /**
     * Tests the {@link MutableInodeFile#getId()} method.
     */
    @Test
    public void getId() {
        MutableInodeFile inode1 = createInodeFile(1);
        Assert.assertEquals(createInodeFileId(1), inode1.getId());
    }

    /**
     * Tests the {@link MutableInodeFile#setLength(long)} method.
     */
    @Test
    public void setLength() {
        MutableInodeFile inodeFile = createInodeFile(1);
        inodeFile.setLength(MutableInodeFileTest.LENGTH);
        Assert.assertEquals(MutableInodeFileTest.LENGTH, inodeFile.getLength());
    }

    /**
     * Tests the {@link MutableInodeFile#getBlockSizeBytes()} method.
     */
    @Test
    public void getBlockSizeBytes() {
        MutableInodeFile inode1 = createInodeFile(1);
        Assert.assertEquals(KB, inode1.getBlockSizeBytes());
    }

    /**
     * Tests the {@link MutableInodeFile#getBlockIdByIndex(int)} method.
     */
    @Test
    public void getBlockIdByIndex() throws Exception {
        MutableInodeFile inodeFile = createInodeFile(1);
        List<Long> blockIds = new ArrayList<>();
        final int NUM_BLOCKS = 3;
        for (int i = 0; i < NUM_BLOCKS; i++) {
            blockIds.add(inodeFile.getNewBlockId());
        }
        for (int i = 0; i < NUM_BLOCKS; i++) {
            Assert.assertEquals(blockIds.get(i), ((Long) (inodeFile.getBlockIdByIndex(i))));
        }
        try {
            inodeFile.getBlockIdByIndex((-1));
            Assert.fail();
        } catch (BlockInfoException e) {
            Assert.assertEquals(String.format("blockIndex -1 is out of range. File blocks: %d", NUM_BLOCKS), e.getMessage());
        }
        try {
            inodeFile.getBlockIdByIndex(NUM_BLOCKS);
            Assert.fail();
        } catch (BlockInfoException e) {
            Assert.assertEquals(String.format("blockIndex %d is out of range. File blocks: %d", NUM_BLOCKS, NUM_BLOCKS), e.getMessage());
        }
    }

    /**
     * Tests the {@link MutableInodeFile#setCompleted(boolean)} method.
     */
    @Test
    public void setCompleted() {
        MutableInodeFile inode1 = createInodeFile(1);
        Assert.assertFalse(inode1.isCompleted());
        inode1.setCompleted(true);
        Assert.assertTrue(inode1.isCompleted());
    }

    /**
     * Tests the {@link MutableInodeFile#getMode()} method.
     */
    @Test
    public void permissionStatus() {
        MutableInodeFile inode1 = createInodeFile(1);
        Assert.assertEquals(AbstractInodeTest.TEST_OWNER, inode1.getOwner());
        Assert.assertEquals(AbstractInodeTest.TEST_GROUP, inode1.getGroup());
        Assert.assertEquals(ModeUtils.applyFileUMask(Mode.defaults(), ServerConfiguration.get(SECURITY_AUTHORIZATION_PERMISSION_UMASK)).toShort(), inode1.getMode());
    }
}

