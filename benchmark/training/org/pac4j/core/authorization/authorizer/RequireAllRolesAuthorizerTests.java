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
 * Tests {@link RequireAllRolesAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAllRolesAuthorizerTests {
    private static final String ROLE1 = "role1";

    private static final String ROLE2 = "role2";

    private static final String ROLE3 = "role3";

    private final JEEContext context = new JEEContext(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Test
    public void testHasAllRolesOkDifferentOrder() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder2() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(Arrays.asList(RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1));
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder3() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder4() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(new HashSet(Arrays.asList(RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1)));
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder5() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(Arrays.asList(RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1));
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesNull() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(((List<String>) (null)));
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesEmpty() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{  });
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesTwoRolesFail() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1 });
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE2);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesTwoRolesFailTwoProfiles() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ RequireAllRolesAuthorizerTests.ROLE3, RequireAllRolesAuthorizerTests.ROLE1 });
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE1);
        profile.addRole(RequireAllRolesAuthorizerTests.ROLE2);
        final CommonProfile profile2 = new CommonProfile();
        profile2.addRole(RequireAllRolesAuthorizerTests.ROLE3);
        profiles.add(profile2);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }
}

