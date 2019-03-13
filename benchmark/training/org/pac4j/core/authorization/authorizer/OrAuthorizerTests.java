package org.pac4j.core.authorization.authorizer;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link OrAuthorizer}
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class OrAuthorizerTests {
    private List<CommonProfile> profiles = new ArrayList<>();

    @Test
    public void testDisjunctionAuthorizer1() {
        final Authorizer<CommonProfile> authorizer = OrAuthorizer.or(RequireAnyRoleAuthorizer.requireAnyRole("profile_role2"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission2"));
        Assert.assertFalse(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testDisjunctionAuthorizer2() {
        final Authorizer<CommonProfile> authorizer = OrAuthorizer.or(RequireAnyRoleAuthorizer.requireAnyRole("profile_role2"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission"));
        Assert.assertTrue(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testDisjunctionAuthorizer3() {
        final Authorizer<CommonProfile> authorizer = OrAuthorizer.or(RequireAnyRoleAuthorizer.requireAnyRole("profile_role"), RequireAnyPermissionAuthorizer.requireAnyPermission("profile_permission2"));
        Assert.assertTrue(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }
}

