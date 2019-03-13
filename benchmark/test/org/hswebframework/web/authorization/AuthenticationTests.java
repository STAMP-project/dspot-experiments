package org.hswebframework.web.authorization;


import java.util.Collections;
import java.util.Set;
import org.hswebframework.web.authorization.builder.AuthenticationBuilder;
import org.junit.Assert;
import org.junit.Test;


public class AuthenticationTests {
    private AuthenticationBuilder builder;

    /**
     * ????????????
     */
    @Test
    public void testInitUserRoleAndPermission() {
        Authentication authentication = builder.user("{\"id\":\"admin\",\"username\":\"admin\",\"name\":\"Administrator\",\"type\":\"default\"}").role("[{\"id\":\"admin-role\",\"name\":\"admin\"}]").permission(("[{\"id\":\"user-manager\",\"actions\":[\"query\",\"get\",\"update\"]" + ",\"dataAccesses\":[{\"action\":\"query\",\"field\":\"test\",\"fields\":[\"1\",\"2\",\"3\"],\"scopeType\":\"CUSTOM_SCOPE\",\"type\":\"DENY_FIELDS\"}]}]")).build();
        // test user
        Assert.assertEquals(authentication.getUser().getId(), "admin");
        Assert.assertEquals(authentication.getUser().getUsername(), "admin");
        Assert.assertEquals(authentication.getUser().getName(), "Administrator");
        Assert.assertEquals(authentication.getUser().getType(), "default");
        // test role
        Assert.assertNotNull(authentication.getRole("admin-role").orElse(null));
        Assert.assertEquals(authentication.getRole("admin-role").get().getName(), "admin");
        Assert.assertTrue(authentication.hasRole("admin-role"));
        // test permission
        Assert.assertEquals(authentication.getPermissions().size(), 1);
        Assert.assertTrue(authentication.hasPermission("user-manager"));
        Assert.assertTrue(authentication.hasPermission("user-manager", "get"));
        Assert.assertTrue((!(authentication.hasPermission("user-manager", "delete"))));
        boolean has = AuthenticationPredicate.has("permission:user-manager").or(AuthenticationPredicate.role("admin-role")).test(authentication);
        Assert.assertTrue(has);
        has = AuthenticationPredicate.has("permission:user-manager:test").and(AuthenticationPredicate.role("admin-role")).test(authentication);
        Assert.assertFalse(has);
        has = AuthenticationPredicate.has("permission:user-manager:get and role:admin-role").test(authentication);
        Assert.assertTrue(has);
        has = AuthenticationPredicate.has("permission:user-manager:test or role:admin-role").test(authentication);
        Assert.assertTrue(has);
        // ????????
        Set<String> fields = authentication.getPermission("user-manager").map(( permission) -> permission.findDenyFields(Permission.ACTION_QUERY)).orElseGet(Collections::emptySet);
        Assert.assertEquals(fields.size(), 3);
        System.out.println(fields);
    }

    /**
     * ????????????
     */
    @Test
    public void testGetSetCurrentUser() {
        Authentication authentication = builder.user("{\"id\":\"admin\",\"username\":\"admin\",\"name\":\"Administrator\",\"type\":\"default\"}").build();
        // ????????,???????????
        AuthenticationManager authenticationManager = new AuthenticationManager() {
            @Override
            public Authentication authenticate(AuthenticationRequest request) {
                return null;
            }

            @Override
            public Authentication getByUserId(String userId) {
                if (userId.equals("admin")) {
                    return authentication;
                }
                return null;
            }

            @Override
            public Authentication sync(Authentication authentication) {
                return authentication;
            }
        };
        AuthenticationHolder.addSupplier(new UserTokenAuthenticationSupplier(authenticationManager));
        // ????token
        UserTokenManager userTokenManager = new DefaultUserTokenManager();
        UserToken token = userTokenManager.signIn("test", "token-test", "admin", (-1));
        UserTokenHolder.setCurrent(token);
        // ????????
        Authentication current = Authentication.current().orElseThrow(UnAuthorizedException::new);
        Assert.assertEquals(current.getUser().getId(), "admin");
    }
}

