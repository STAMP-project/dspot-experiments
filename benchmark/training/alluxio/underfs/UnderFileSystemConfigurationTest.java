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
package alluxio.underfs;


import PropertyKey.S3A_ACCESS_KEY;
import PropertyKey.S3A_SECRET_KEY;
import PropertyKey.UNDERFS_LISTING_LENGTH;
import alluxio.ConfigurationRule;
import alluxio.conf.InstancedConfiguration;
import alluxio.conf.PropertyKey;
import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public final class UnderFileSystemConfigurationTest {
    private InstancedConfiguration mConfiguration;

    @Test
    public void getValueWhenGlobalConfHasProperty() throws Exception {
        // Set property in global configuration
        try (Closeable c = new ConfigurationRule(PropertyKey.S3A_ACCESS_KEY, "bar", mConfiguration).toResource()) {
            Random random = new Random();
            boolean readOnly = random.nextBoolean();
            boolean shared = random.nextBoolean();
            UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults().setReadOnly(readOnly).setShared(shared);
            Assert.assertEquals(readOnly, conf.isReadOnly());
            Assert.assertEquals(shared, conf.isShared());
            Assert.assertEquals("bar", mConfiguration.get(S3A_ACCESS_KEY));
            conf = UnderFileSystemConfiguration.defaults().setReadOnly(readOnly).setShared(shared).createMountSpecificConf(ImmutableMap.of(S3A_ACCESS_KEY.toString(), "foo"));
            Assert.assertEquals(readOnly, conf.isReadOnly());
            Assert.assertEquals(shared, conf.isShared());
            Assert.assertEquals("foo", conf.get(S3A_ACCESS_KEY));
        }
    }

    @Test
    public void getValueWhenGlobalConfOverridesPropertyWithDefaultValue() throws Exception {
        // Set property in global configuration
        try (Closeable c = new ConfigurationRule(PropertyKey.UNDERFS_LISTING_LENGTH, "2000", mConfiguration).toResource()) {
            UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults(mConfiguration);
            Assert.assertEquals("2000", conf.get(UNDERFS_LISTING_LENGTH));
        }
    }

    @Test
    public void getValueWhenGlobalConfHasNotProperty() throws Exception {
        // Set property in global configuration
        try (Closeable c = new ConfigurationRule(PropertyKey.S3A_ACCESS_KEY, null, mConfiguration).toResource()) {
            Random random = new Random();
            boolean readOnly = random.nextBoolean();
            boolean shared = random.nextBoolean();
            UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults(mConfiguration).setReadOnly(readOnly).setShared(shared);
            try {
                conf.get(S3A_ACCESS_KEY);
                Assert.fail("this key should not exist");
            } catch (Exception e) {
                // expect to pass
            }
            UnderFileSystemConfiguration conf2 = conf.createMountSpecificConf(ImmutableMap.of(S3A_ACCESS_KEY.toString(), "foo"));
            Assert.assertEquals(readOnly, conf2.isReadOnly());
            Assert.assertEquals(shared, conf2.isShared());
            Assert.assertEquals("foo", conf2.get(S3A_ACCESS_KEY));
        }
    }

    @Test
    public void containsWhenGlobalConfHasProperty() throws Exception {
        // Unset property in global configuration
        try (Closeable c = new ConfigurationRule(PropertyKey.S3A_ACCESS_KEY, "bar", mConfiguration).toResource()) {
            Random random = new Random();
            boolean readOnly = random.nextBoolean();
            boolean shared = random.nextBoolean();
            UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults(mConfiguration).setReadOnly(readOnly).setShared(shared);
            Assert.assertTrue(conf.isSet(S3A_ACCESS_KEY));
            conf.createMountSpecificConf(ImmutableMap.of(S3A_ACCESS_KEY.toString(), "foo"));
            Assert.assertEquals(readOnly, conf.isReadOnly());
            Assert.assertEquals(shared, conf.isShared());
            Assert.assertTrue(conf.isSet(S3A_ACCESS_KEY));
        }
    }

    @Test
    public void containsWhenGlobalConfHasNotProperty() throws Exception {
        // Unset property in global configuration
        try (Closeable c = new ConfigurationRule(PropertyKey.S3A_ACCESS_KEY, null, mConfiguration).toResource()) {
            Random random = new Random();
            boolean readOnly = random.nextBoolean();
            boolean shared = random.nextBoolean();
            UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults(mConfiguration).setReadOnly(readOnly).setShared(shared);
            Assert.assertFalse(conf.isSet(S3A_ACCESS_KEY));
            UnderFileSystemConfiguration conf2 = conf.createMountSpecificConf(ImmutableMap.of(S3A_ACCESS_KEY.toString(), "foo"));
            Assert.assertEquals(readOnly, conf2.isReadOnly());
            Assert.assertEquals(shared, conf2.isShared());
            Assert.assertTrue(conf2.isSet(S3A_ACCESS_KEY));
        }
    }

    @Test
    public void setUserSpecifiedConfRepeatedly() throws Exception {
        UnderFileSystemConfiguration conf = UnderFileSystemConfiguration.defaults(mConfiguration);
        UnderFileSystemConfiguration conf2 = conf.createMountSpecificConf(ImmutableMap.of(S3A_ACCESS_KEY.toString(), "foo"));
        Assert.assertEquals("foo", conf2.get(S3A_ACCESS_KEY));
        Assert.assertEquals(1, conf2.getMountSpecificConf().size());
        conf2 = conf.createMountSpecificConf(ImmutableMap.of(S3A_SECRET_KEY.toString(), "bar"));
        Assert.assertEquals("bar", conf2.get(S3A_SECRET_KEY));
        Assert.assertFalse(conf2.isSet(S3A_ACCESS_KEY));
        Assert.assertEquals(1, conf2.getMountSpecificConf().size());
        Assert.assertEquals(0, conf.getMountSpecificConf().size());
    }
}

