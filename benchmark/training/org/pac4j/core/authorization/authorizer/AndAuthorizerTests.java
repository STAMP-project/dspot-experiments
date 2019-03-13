package org.pac4j.core.authorization.authorizer;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link AndAuthorizer}
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class AndAuthorizerTests {
    private List<CommonProfile> profiles = new ArrayList<>();

    @Test
    public void testAuthorizerConstraint1() {
        final Authorizer<CommonProfile> authorizer = AndAuthorizer.and(IsAuthenticatedAuthorizer.isAuthenticated(), RequireAnyRoleAuthorizer.requireAnyRole("profile_role"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission"));
        Assert.assertTrue(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testAuthorizerConstraint2() {
        final Authorizer<CommonProfile> authorizer = AndAuthorizer.and(RequireAnyRoleAuthorizer.requireAnyRole("profile_role2"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission"));
        Assert.assertFalse(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testAuthorizerConstraint3() {
        final Authorizer<CommonProfile> authorizer = AndAuthorizer.and(RequireAnyRoleAuthorizer.requireAnyRole("profile_role"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission2"));
        Assert.assertFalse(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }
}

