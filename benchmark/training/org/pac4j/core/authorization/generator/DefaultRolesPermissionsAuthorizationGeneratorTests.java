package org.pac4j.core.authorization.generator;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 * This class tests {@link DefaultRolesPermissionsAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultRolesPermissionsAuthorizationGeneratorTests {
    private static final String[] DEFAULT_ROLES_ARRAY = new String[]{ "R1", "R2" };

    private static final String[] DEFAULT_PERMISSIONS_ARRAY = new String[]{ "P1", "P2" };

    private static final List<String> DEFAULT_ROLES_LIST = Arrays.asList(DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_ROLES_ARRAY);

    private static final List<String> DEFAULT_PERMISSIONS_LIST = Arrays.asList(DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_PERMISSIONS_ARRAY);

    @Test
    public void testNullArrays() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator(((String[]) (null)), null);
        checkEmptyProfile(generator);
    }

    @Test
    public void testNullLists() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator(((List<String>) (null)), null);
        checkEmptyProfile(generator);
    }

    @Test
    public void testDefaultValuesArrays() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator(DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_ROLES_ARRAY, DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_PERMISSIONS_ARRAY);
        checkProfile(generator);
    }

    @Test
    public void testDefaultValuesLists() {
        final DefaultRolesPermissionsAuthorizationGenerator generator = new DefaultRolesPermissionsAuthorizationGenerator(DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_ROLES_LIST, DefaultRolesPermissionsAuthorizationGeneratorTests.DEFAULT_PERMISSIONS_LIST);
        checkProfile(generator);
    }
}

