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
 * Tests {@link RequireAnyRoleAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyRoleAuthorizerTests {
    private static final String ROLE1 = "role1";

    private static final String ROLE2 = "role2";

    private static final String ROLE3 = "role3";

    private final JEEContext context = new JEEContext(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Test
    public void testHasAnyRoleOneRole() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(RequireAnyRoleAuthorizerTests.ROLE1);
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole2() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(RequireAnyRoleAuthorizerTests.ROLE1);
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleTwoProfiles() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(RequireAnyRoleAuthorizerTests.ROLE1);
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        profiles.add(new CommonProfile());
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole3() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(Arrays.asList(RequireAnyRoleAuthorizerTests.ROLE1));
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole4() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(new HashSet<String>(Arrays.asList(RequireAnyRoleAuthorizerTests.ROLE1)));
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[]{ RequireAnyRoleAuthorizerTests.ROLE1 });
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE2);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleNull() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(((List<String>) (null)));
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleEmpty() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[]{  });
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOkTwoRoles() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(RequireAnyRoleAuthorizerTests.ROLE2, RequireAnyRoleAuthorizerTests.ROLE1);
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleProfileTwoRolesFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[]{ RequireAnyRoleAuthorizerTests.ROLE2 });
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE1);
        profile.addRole(RequireAnyRoleAuthorizerTests.ROLE3);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }
}

