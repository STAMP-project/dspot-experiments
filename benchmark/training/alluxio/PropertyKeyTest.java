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
package alluxio;


import ExceptionMessage.INVALID_CONFIGURATION_KEY;
import PropertyKey.CONF_DIR;
import PropertyKey.DEBUG;
import PropertyKey.HOME;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_ALLUXIO;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_OPTION;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_READONLY;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_SHARED;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.MASTER_TIERED_STORE_GLOBAL_LEVEL0_ALIAS;
import PropertyKey.MASTER_TIERED_STORE_GLOBAL_LEVEL1_ALIAS;
import PropertyKey.MASTER_TIERED_STORE_GLOBAL_LEVEL2_ALIAS;
import PropertyKey.Template.MASTER_IMPERSONATION_GROUPS_OPTION;
import PropertyKey.Template.MASTER_MOUNT_TABLE_ALLUXIO;
import PropertyKey.Template.MASTER_MOUNT_TABLE_OPTION;
import PropertyKey.Template.MASTER_MOUNT_TABLE_READONLY;
import PropertyKey.Template.MASTER_MOUNT_TABLE_SHARED;
import PropertyKey.Template.MASTER_MOUNT_TABLE_UFS;
import PropertyKey.Template.MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_ALIAS;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_PATH;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_HIGH_WATERMARK_RATIO;
import PropertyKey.VERSION;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_ALIAS;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_DIRS_PATH;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_DIRS_QUOTA;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_HIGH_WATERMARK_RATIO;
import PropertyKey.WORKER_TIERED_STORE_LEVEL1_ALIAS;
import PropertyKey.WORKER_TIERED_STORE_LEVEL1_DIRS_PATH;
import PropertyKey.WORKER_TIERED_STORE_LEVEL1_DIRS_QUOTA;
import PropertyKey.WORKER_TIERED_STORE_LEVEL1_HIGH_WATERMARK_RATIO;
import PropertyKey.WORKER_TIERED_STORE_LEVEL2_ALIAS;
import PropertyKey.WORKER_TIERED_STORE_LEVEL2_DIRS_PATH;
import PropertyKey.WORKER_TIERED_STORE_LEVEL2_DIRS_QUOTA;
import PropertyKey.WORKER_TIERED_STORE_LEVEL2_HIGH_WATERMARK_RATIO;
import Template.LOCALITY_TIER;
import Template.MASTER_IMPERSONATION_USERS_OPTION;
import alluxio.conf.PropertyKey;
import alluxio.conf.PropertyKey.Builder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests enum type {@link PropertyKey}.
 */
public final class PropertyKeyTest {
    private PropertyKey mTestProperty = new Builder("alluxio.test.property").setAlias(new String[]{ "alluxio.test.property.alias1", "alluxio.test.property.alias2" }).setDescription("test").setDefaultValue(false).setIsHidden(false).setIgnoredSiteProperty(false).build();

    /**
     * Tests parsing string to PropertyKey by {@link PropertyKey#fromString}.
     */
    @Test
    public void fromString() throws Exception {
        Assert.assertEquals(VERSION, PropertyKey.fromString(VERSION.toString()));
        PropertyKey.fromString(VERSION.toString());
        Assert.assertEquals(mTestProperty, PropertyKey.fromString("alluxio.test.property.alias1"));
        Assert.assertEquals(mTestProperty, PropertyKey.fromString("alluxio.test.property.alias2"));
        Assert.assertEquals(mTestProperty, PropertyKey.fromString(mTestProperty.toString()));
    }

    @Test
    public void equalsTest() throws Exception {
        Assert.assertEquals(MASTER_MOUNT_TABLE_ALLUXIO.format("foo"), MASTER_MOUNT_TABLE_ALLUXIO.format("foo"));
        Assert.assertEquals(HOME, HOME);
    }

    @Test
    public void length() throws Exception {
        Assert.assertEquals(PropertyKey.Name.HOME.length(), HOME.length());
    }

    @Test
    public void isValid() throws Exception {
        Assert.assertTrue(PropertyKey.isValid(HOME.toString()));
        Assert.assertTrue(PropertyKey.isValid(MASTER_MOUNT_TABLE_ALLUXIO.format("foo").toString()));
        Assert.assertFalse(PropertyKey.isValid(""));
        Assert.assertFalse(PropertyKey.isValid(" "));
        Assert.assertFalse(PropertyKey.isValid("foo"));
        Assert.assertFalse(PropertyKey.isValid(((HOME.toString()) + "1")));
        Assert.assertFalse(PropertyKey.isValid(HOME.toString().toUpperCase()));
    }

    @Test
    public void aliasIsValid() throws Exception {
        Assert.assertTrue(PropertyKey.isValid(mTestProperty.toString()));
        Assert.assertTrue(PropertyKey.isValid("alluxio.test.property.alias1"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.test.property.alias2"));
    }

    @Test
    public void fromStringExceptionThrown() throws Exception {
        String[] wrongKeys = new String[]{ "", " ", "foo", "alluxio.foo", "alluxio.HOME", "alluxio.master.mount.table.root.alluxio1", "alluxio.master.mount.table.alluxio", "alluxio.master.mount.table.foo" };
        for (String key : wrongKeys) {
            try {
                PropertyKey.fromString(key);
                Assert.fail();
            } catch (IllegalArgumentException e) {
                Assert.assertEquals(e.getMessage(), INVALID_CONFIGURATION_KEY.getMessage(key));
            }
        }
    }

    @Test
    public void formatMasterTieredStoreGlobalAlias() throws Exception {
        Assert.assertEquals(MASTER_TIERED_STORE_GLOBAL_LEVEL0_ALIAS, MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS.format(0));
        Assert.assertEquals(MASTER_TIERED_STORE_GLOBAL_LEVEL1_ALIAS, MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS.format(1));
        Assert.assertEquals(MASTER_TIERED_STORE_GLOBAL_LEVEL2_ALIAS, MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS.format(2));
    }

    @Test
    public void formatWorkerTieredStoreAlias() throws Exception {
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL0_ALIAS, WORKER_TIERED_STORE_LEVEL_ALIAS.format(0));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL1_ALIAS, WORKER_TIERED_STORE_LEVEL_ALIAS.format(1));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL2_ALIAS, WORKER_TIERED_STORE_LEVEL_ALIAS.format(2));
    }

    @Test
    public void formatWorkerTieredStoreDirsPath() throws Exception {
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL0_DIRS_PATH, WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(0));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL1_DIRS_PATH, WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(1));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL2_DIRS_PATH, WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(2));
    }

    @Test
    public void formatWorkerTieredStoreDirsQuota() throws Exception {
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL0_DIRS_QUOTA, WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(0));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL1_DIRS_QUOTA, WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(1));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL2_DIRS_QUOTA, WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(2));
    }

    @Test
    public void formatWorkerTieredStoreReservedRatio() throws Exception {
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL0_HIGH_WATERMARK_RATIO, WORKER_TIERED_STORE_LEVEL_HIGH_WATERMARK_RATIO.format(0));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL1_HIGH_WATERMARK_RATIO, WORKER_TIERED_STORE_LEVEL_HIGH_WATERMARK_RATIO.format(1));
        Assert.assertEquals(WORKER_TIERED_STORE_LEVEL2_HIGH_WATERMARK_RATIO, WORKER_TIERED_STORE_LEVEL_HIGH_WATERMARK_RATIO.format(2));
    }

    @Test
    public void mountTableRootProperties() throws Exception {
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_ALLUXIO, MASTER_MOUNT_TABLE_ALLUXIO.format("root"));
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_UFS, MASTER_MOUNT_TABLE_UFS.format("root"));
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_READONLY, MASTER_MOUNT_TABLE_READONLY.format("root"));
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_SHARED, MASTER_MOUNT_TABLE_SHARED.format("root"));
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_OPTION, MASTER_MOUNT_TABLE_OPTION.format("root"));
    }

    @Test
    public void isValidParameterized() throws Exception {
        // String parameter
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.root.alluxio"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.foo.alluxio"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.FoO.alluxio"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.Fo123.alluxio"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.FoO.alluxio"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.root.option"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.root.option.foo"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.master.mount.table.root.option.alluxio.foo"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table.alluxio"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table..alluxio"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table. .alluxio"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table.foo.alluxio1"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table.root.option."));
        Assert.assertFalse(PropertyKey.isValid("alluxio.master.mount.table.root.option.foo."));
        // Numeric parameter
        Assert.assertTrue(PropertyKey.isValid("alluxio.worker.tieredstore.level1.alias"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.worker.tieredstore.level99.alias"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.worker.tieredstore.level.alias"));
        Assert.assertFalse(PropertyKey.isValid("alluxio.worker.tieredstore.levela.alias"));
    }

    @Test
    public void fromStringParameterized() throws Exception {
        Assert.assertEquals(MASTER_MOUNT_TABLE_ROOT_ALLUXIO, PropertyKey.fromString("alluxio.master.mount.table.root.alluxio"));
        Assert.assertEquals(MASTER_MOUNT_TABLE_ALLUXIO.format("foo"), PropertyKey.fromString("alluxio.master.mount.table.foo.alluxio"));
    }

    @Test
    public void fromStringParameterizedExceptionThrown() throws Exception {
        String[] wrongKeys = new String[]{ "alluxio.master.mount.table.root.alluxio1", "alluxio.master.mount.table.alluxio", "alluxio.master.mount.table.foo" };
        for (String key : wrongKeys) {
            try {
                PropertyKey.fromString(key);
                Assert.fail();
            } catch (IllegalArgumentException e) {
                Assert.assertEquals(e.getMessage(), INVALID_CONFIGURATION_KEY.getMessage(key));
            }
        }
    }

    @Test
    public void defaultSupplier() throws Exception {
        AtomicInteger x = new AtomicInteger(100);
        PropertyKey key = new Builder("test").setDefaultSupplier(new DefaultSupplier(() -> x.get(), "test description")).build();
        Assert.assertEquals("100", key.getDefaultValue());
        x.set(20);
        Assert.assertEquals("20", key.getDefaultValue());
        Assert.assertEquals("test description", key.getDefaultSupplier().getDescription());
    }

    @Test
    public void isDeprecated() throws Exception {
        Assert.assertFalse(PropertyKey.isDeprecated("VERSION"));
    }

    @Test
    public void isDeprecatedExceptionThrown() throws Exception {
        Assert.assertFalse(PropertyKey.isDeprecated("foo"));
    }

    @Test
    public void compare() throws Exception {
        Assert.assertTrue(((CONF_DIR.compareTo(DEBUG)) < 0));
        Assert.assertTrue(((DEBUG.compareTo(CONF_DIR)) > 0));
        Assert.assertEquals(0, DEBUG.compareTo(DEBUG));
    }

    @Test
    public void templateMatches() throws Exception {
        Assert.assertTrue(MASTER_MOUNT_TABLE_ALLUXIO.matches("alluxio.master.mount.table.root.alluxio"));
        Assert.assertTrue(MASTER_MOUNT_TABLE_ALLUXIO.matches("alluxio.master.mount.table.ufs123.alluxio"));
        Assert.assertFalse(MASTER_MOUNT_TABLE_ALLUXIO.matches("alluxio.master.mount.table..alluxio"));
        Assert.assertFalse(MASTER_MOUNT_TABLE_ALLUXIO.matches("alluxio.master.mount.table.alluxio"));
    }

    @Test
    public void localityTemplates() throws Exception {
        Assert.assertTrue(PropertyKey.isValid("alluxio.locality.node"));
        Assert.assertTrue(PropertyKey.isValid("alluxio.locality.custom"));
        Assert.assertEquals("alluxio.locality.custom", LOCALITY_TIER.format("custom").toString());
    }

    @Test
    public void isBuiltIn() {
        Assert.assertTrue(mTestProperty.isBuiltIn());
    }

    @Test
    public void impersonationRegex() {
        // test groups
        String name = "a-A_1.b-B_2@.groups";
        String groups = String.format("alluxio.master.security.impersonation.%s.groups", name);
        Matcher matcher = MASTER_IMPERSONATION_GROUPS_OPTION.match(groups);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(name, matcher.group(1));
        // test users
        name = "a-A_1.b-B_2@.users";
        String users = String.format("alluxio.master.security.impersonation.%s.users", name);
        matcher = MASTER_IMPERSONATION_USERS_OPTION.match(users);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(name, matcher.group(1));
    }

    @Test
    public void testEmptyKeyDefaults() {
        for (PropertyKey key : PropertyKey.defaultKeys()) {
            Assert.assertNotEquals(String.format("Property keys cannot have a default value of \"\". Offending key: %s", key.getName()), key.getDefaultValue(), "");
        }
    }
}

