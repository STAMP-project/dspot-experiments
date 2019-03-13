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
package alluxio.client.file.options;


import Constants.LAST_TIER;
import Constants.NO_TTL;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_UMASK;
import PropertyKey.SECURITY_GROUP_MAPPING_CLASS;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_WRITE_TIER_DEFAULT;
import PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT;
import TtlAction.DELETE;
import TtlAction.FREE;
import WriteType.CACHE_THROUGH;
import alluxio.ConfigurationRule;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.LoginUserRule;
import alluxio.client.AlluxioStorageType;
import alluxio.client.UnderStorageType;
import alluxio.client.WriteType;
import alluxio.client.file.policy.FileWriteLocationPolicy;
import alluxio.client.file.policy.LocalFirstPolicy;
import alluxio.conf.InstancedConfiguration;
import alluxio.security.authorization.Mode;
import alluxio.security.group.GroupMappingService;
import alluxio.util.CommonUtils;
import alluxio.util.ModeUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests for the {@link OutStreamOptions} class.
 */
public class OutStreamOptionsTest {
    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    /**
     * A mapping from a user to its corresponding group.
     */
    // TODO(binfan): create MockUserGroupsMapping class
    public static class FakeUserGroupsMapping implements GroupMappingService {
        public FakeUserGroupsMapping() {
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            return Lists.newArrayList("test_group");
        }
    }

    @Rule
    public ConfigurationRule mConfiguration = new ConfigurationRule(ImmutableMap.of(SECURITY_GROUP_MAPPING_CLASS, OutStreamOptionsTest.FakeUserGroupsMapping.class.getName()), mConf);

    @Rule
    public LoginUserRule mRule = new LoginUserRule("test_user", mConf);

    /**
     * Tests that building an {@link OutStreamOptions} with the defaults works.
     */
    @Test
    public void defaults() throws IOException {
        AlluxioStorageType alluxioType = AlluxioStorageType.STORE;
        UnderStorageType ufsType = UnderStorageType.SYNC_PERSIST;
        mConf.set(USER_BLOCK_SIZE_BYTES_DEFAULT, "64MB");
        mConf.set(USER_FILE_WRITE_TYPE_DEFAULT, CACHE_THROUGH.toString());
        mConf.set(USER_FILE_WRITE_TIER_DEFAULT, LAST_TIER);
        OutStreamOptions options = OutStreamOptions.defaults(mConf);
        Assert.assertEquals(alluxioType, options.getAlluxioStorageType());
        Assert.assertEquals((64 * (Constants.MB)), options.getBlockSizeBytes());
        Assert.assertTrue(((options.getLocationPolicy()) instanceof LocalFirstPolicy));
        Assert.assertEquals("test_user", options.getOwner());
        Assert.assertEquals("test_group", options.getGroup());
        Assert.assertEquals(ModeUtils.applyFileUMask(Mode.defaults(), mConf.get(SECURITY_AUTHORIZATION_PERMISSION_UMASK)), options.getMode());
        Assert.assertEquals(NO_TTL, options.getTtl());
        Assert.assertEquals(DELETE, options.getTtlAction());
        Assert.assertEquals(ufsType, options.getUnderStorageType());
        Assert.assertEquals(CACHE_THROUGH, options.getWriteType());
        Assert.assertEquals(LAST_TIER, options.getWriteTier());
    }

    /**
     * Tests getting and setting fields.
     */
    @Test
    public void fields() throws Exception {
        Random random = new Random();
        long blockSize = random.nextLong();
        FileWriteLocationPolicy locationPolicy = new alluxio.client.file.policy.RoundRobinPolicy(mConf);
        String owner = CommonUtils.randomAlphaNumString(10);
        String group = CommonUtils.randomAlphaNumString(10);
        Mode mode = new Mode(((short) (random.nextInt())));
        long ttl = random.nextLong();
        int writeTier = random.nextInt();
        WriteType writeType = WriteType.NONE;
        OutStreamOptions options = OutStreamOptions.defaults(mConf);
        options.setBlockSizeBytes(blockSize);
        options.setLocationPolicy(locationPolicy);
        options.setOwner(owner);
        options.setGroup(group);
        options.setMode(mode);
        options.setTtl(ttl);
        options.setTtlAction(FREE);
        options.setWriteTier(writeTier);
        options.setWriteType(writeType);
        Assert.assertEquals(blockSize, options.getBlockSizeBytes());
        Assert.assertEquals(locationPolicy, options.getLocationPolicy());
        Assert.assertEquals(owner, options.getOwner());
        Assert.assertEquals(group, options.getGroup());
        Assert.assertEquals(mode, options.getMode());
        Assert.assertEquals(ttl, options.getTtl());
        Assert.assertEquals(FREE, options.getTtlAction());
        Assert.assertEquals(writeTier, options.getWriteTier());
        Assert.assertEquals(writeType.getAlluxioStorageType(), options.getAlluxioStorageType());
        Assert.assertEquals(writeType.getUnderStorageType(), options.getUnderStorageType());
    }

    @Test
    public void equalsTest() throws Exception {
        new EqualsTester().addEqualityGroup(OutStreamOptions.defaults(mConf), OutStreamOptions.defaults(mConf)).testEquals();
    }
}

