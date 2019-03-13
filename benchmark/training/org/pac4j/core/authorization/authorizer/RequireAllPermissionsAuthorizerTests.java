package org.pac4j.core.authorization.authorizer;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link RequireAllPermissionsAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAllPermissionsAuthorizerTests {
    private static final String PERMISSION1 = "permission1";

    private static final String PERMISSION2 = "permission2";

    private static final String PERMISSION3 = "permission3";

    private final JEEContext context = new JEEContext(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Test
    public void testHasAllPermissionsOkDifferentOrder() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrderTwoProfiles() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        profiles.add(new CommonProfile());
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder2() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(Arrays.asList(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1));
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder3() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder4() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(new HashSet(Arrays.asList(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1)));
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder5() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(Arrays.asList(RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1));
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsNull() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(((List<String>) (null)));
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsEmpty() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{  });
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsTwoPermissionsFail() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{ RequireAllPermissionsAuthorizerTests.PERMISSION3, RequireAllPermissionsAuthorizerTests.PERMISSION1 });
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAllPermissionsAuthorizerTests.PERMISSION2);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }
}

