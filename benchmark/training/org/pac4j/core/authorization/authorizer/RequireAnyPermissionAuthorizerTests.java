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
 * Tests {@link RequireAnyPermissionAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyPermissionAuthorizerTests {
    private static final String PERMISSION1 = "permission1";

    private static final String PERMISSION2 = "permission2";

    private static final String PERMISSION3 = "permission3";

    private final JEEContext context = new JEEContext(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Test
    public void testHasAnyPermissionOnePermission() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission2() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission3() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(Arrays.asList(RequireAnyPermissionAuthorizerTests.PERMISSION1));
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission4() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(new HashSet(Arrays.asList(RequireAnyPermissionAuthorizerTests.PERMISSION1)));
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[]{ RequireAnyPermissionAuthorizerTests.PERMISSION1 });
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION2);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionTwoProfiles() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[]{ RequireAnyPermissionAuthorizerTests.PERMISSION1 });
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION2);
        final CommonProfile profile2 = new CommonProfile();
        profile2.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        profiles.add(profile2);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionNull() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(((List<String>) (null)));
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionEmpty() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[]{  });
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOkTwoPermissions() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(RequireAnyPermissionAuthorizerTests.PERMISSION2, RequireAnyPermissionAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionProfileTwoPermissionsFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[]{ RequireAnyPermissionAuthorizerTests.PERMISSION2 });
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION1);
        profile.addPermission(RequireAnyPermissionAuthorizerTests.PERMISSION3);
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }
}

