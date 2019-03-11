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
package alluxio.underfs.options;


import AuthType.SIMPLE;
import PropertyKey.SECURITY_AUTHENTICATION_TYPE;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_UMASK;
import PropertyKey.SECURITY_GROUP_MAPPING_CLASS;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import alluxio.conf.InstancedConfiguration;
import alluxio.security.authorization.Mode;
import alluxio.security.group.provider.IdentityUserGroupsMapping;
import alluxio.util.CommonUtils;
import alluxio.util.ConfigurationUtils;
import alluxio.util.ModeUtils;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link MkdirsOptions} class.
 */
public final class MkdirsOptionsTest {
    private InstancedConfiguration mConfiguration;

    /**
     * Tests for default {@link MkdirsOptions}.
     */
    @Test
    public void defaults() throws IOException {
        MkdirsOptions options = MkdirsOptions.defaults(mConfiguration);
        // Verify the default createParent is true.
        Assert.assertTrue(options.getCreateParent());
        // Verify that the owner and group are not set.
        Assert.assertNull(options.getOwner());
        Assert.assertNull(options.getGroup());
        String umask = mConfiguration.get(SECURITY_AUTHORIZATION_PERMISSION_UMASK);
        Assert.assertEquals(ModeUtils.applyDirectoryUMask(Mode.defaults(), umask), options.getMode());
    }

    /**
     * Tests for building an {@link MkdirsOptions} with a security enabled
     * configuration.
     */
    @Test
    public void securityEnabled() throws IOException {
        InstancedConfiguration conf = new InstancedConfiguration(ConfigurationUtils.defaults());
        conf.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName());
        conf.set(SECURITY_LOGIN_USERNAME, "foo");
        // Use IdentityUserGroupMapping to map user "foo" to group "foo".
        conf.set(SECURITY_GROUP_MAPPING_CLASS, IdentityUserGroupsMapping.class.getName());
        MkdirsOptions options = MkdirsOptions.defaults(mConfiguration);
        // Verify the default createParent is true.
        Assert.assertTrue(options.getCreateParent());
        // Verify that the owner and group are not set.
        Assert.assertNull(options.getOwner());
        Assert.assertNull(options.getGroup());
        String umask = mConfiguration.get(SECURITY_AUTHORIZATION_PERMISSION_UMASK);
        Assert.assertEquals(ModeUtils.applyDirectoryUMask(Mode.defaults(), umask), options.getMode());
    }

    /**
     * Tests getting and setting fields.
     */
    @Test
    public void fields() {
        Random random = new Random();
        boolean createParent = random.nextBoolean();
        String owner = CommonUtils.randomAlphaNumString(10);
        String group = CommonUtils.randomAlphaNumString(10);
        Mode mode = new Mode(((short) (random.nextInt())));
        MkdirsOptions options = MkdirsOptions.defaults(mConfiguration);
        options.setCreateParent(createParent);
        options.setOwner(owner);
        options.setGroup(group);
        options.setMode(mode);
        Assert.assertEquals(createParent, options.getCreateParent());
        Assert.assertEquals(owner, options.getOwner());
        Assert.assertEquals(group, options.getGroup());
        Assert.assertEquals(mode, options.getMode());
    }

    @Test
    public void equalsTest() throws Exception {
        new EqualsTester().addEqualityGroup(MkdirsOptions.defaults(mConfiguration), MkdirsOptions.defaults(mConfiguration)).testEquals();
    }
}

