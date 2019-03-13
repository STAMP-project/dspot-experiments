package org.pac4j.core.authorization.generator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;


/**
 * This class tests {@link FromAttributesAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public final class FromAttributesAuthorizationGeneratorTests {
    private static final String ATTRIB1 = "attrib1";

    private static final String VALUE1 = "info11,info12";

    private static final String ATTRIB2 = "attrib2";

    private static final String VALUE2 = "info21,info22";

    private static final String ATTRIB3 = "attrib3";

    private static final String ATTRIB4 = "attrib4";

    private static final String ATTRIB5 = "attrib5";

    private static final String[] ATTRIB_ARRAY = new String[]{ "infoA1", "infoA2", "infoA3" };

    private static final List<String> ATTRIB_LIST = new ArrayList<>();

    static {
        FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST.add("infoL1");
        FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST.add("infoL2");
        FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST.add("infoL3");
    }

    private CommonProfile profile;

    @Test
    public void testNoConfigWithCollections() {
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(new ArrayList(), new HashSet());
        generator.generate(null, this.profile);
        Assert.assertEquals(0, this.profile.getRoles().size());
        Assert.assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testNoConfig() {
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(null, ((String[]) (null)));
        generator.generate(null, this.profile);
        Assert.assertEquals(0, this.profile.getRoles().size());
        Assert.assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testRolePermission() {
        final String[] roleAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB1 };
        final String[] permissionAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB2 };
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("info11"));
        Assert.assertTrue(roles.contains("info12"));
        final Set<String> permissions = this.profile.getPermissions();
        Assert.assertEquals(2, permissions.size());
        Assert.assertTrue(permissions.contains("info21"));
        Assert.assertTrue(permissions.contains("info22"));
    }

    @Test
    public void testNoRolePermission() {
        final String[] roleAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB5 };
        final String[] permissionAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB2 };
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.generate(null, this.profile);
        Assert.assertEquals(0, this.profile.getRoles().size());
        final Set<String> permissions = this.profile.getPermissions();
        Assert.assertEquals(2, permissions.size());
        Assert.assertTrue(permissions.contains("info21"));
        Assert.assertTrue(permissions.contains("info22"));
    }

    @Test
    public void testRoleNoPermission() {
        final String[] roleAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB1 };
        final String[] permissionAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB5 };
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains("info11"));
        Assert.assertTrue(roles.contains("info12"));
        Assert.assertEquals(0, this.profile.getPermissions().size());
    }

    @Test
    public void testRolePermissionChangeSplit() {
        final String[] roleAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB1 };
        final String[] permissionAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB2 };
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.setSplitChar("|");
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains(FromAttributesAuthorizationGeneratorTests.VALUE1));
        final Set<String> permissions = this.profile.getPermissions();
        Assert.assertEquals(1, permissions.size());
        Assert.assertTrue(permissions.contains(FromAttributesAuthorizationGeneratorTests.VALUE2));
    }

    @Test
    public void testListRolesPermissions() {
        final String[] roleAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB3, FromAttributesAuthorizationGeneratorTests.ATTRIB4 };
        final String[] permissionAttributes = new String[]{ FromAttributesAuthorizationGeneratorTests.ATTRIB3, FromAttributesAuthorizationGeneratorTests.ATTRIB4 };
        final FromAttributesAuthorizationGenerator generator = new FromAttributesAuthorizationGenerator(roleAttributes, permissionAttributes);
        generator.generate(null, this.profile);
        final Set<String> roles = this.profile.getRoles();
        Assert.assertEquals(((FromAttributesAuthorizationGeneratorTests.ATTRIB_ARRAY.length) + (FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST.size())), roles.size());
        for (String value : FromAttributesAuthorizationGeneratorTests.ATTRIB_ARRAY) {
            Assert.assertTrue(roles.contains(value));
        }
        for (String value : FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST) {
            Assert.assertTrue(roles.contains(value));
        }
        final Set<String> permissions = this.profile.getPermissions();
        Assert.assertEquals(((FromAttributesAuthorizationGeneratorTests.ATTRIB_ARRAY.length) + (FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST.size())), roles.size());
        for (String value : FromAttributesAuthorizationGeneratorTests.ATTRIB_ARRAY) {
            Assert.assertTrue(permissions.contains(value));
        }
        for (String value : FromAttributesAuthorizationGeneratorTests.ATTRIB_LIST) {
            Assert.assertTrue(permissions.contains(value));
        }
    }
}

