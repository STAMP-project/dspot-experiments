package org.pac4j.core.authorization.checker;


import DefaultAuthorizers.ALLOW_AJAX_REQUESTS;
import DefaultAuthorizers.CSRF;
import DefaultAuthorizers.CSRF_CHECK;
import DefaultAuthorizers.HSTS;
import DefaultAuthorizers.IS_ANONYMOUS;
import DefaultAuthorizers.IS_AUTHENTICATED;
import DefaultAuthorizers.IS_FULLY_AUTHENTICATED;
import DefaultAuthorizers.IS_REMEMBERED;
import DefaultAuthorizers.NOCACHE;
import DefaultAuthorizers.NOFRAME;
import DefaultAuthorizers.NOSNIFF;
import DefaultAuthorizers.SECURITYHEADERS;
import DefaultAuthorizers.XSSPROTECTION;
import HTTP_METHOD.DELETE;
import HTTP_METHOD.GET;
import HTTP_METHOD.OPTIONS;
import HTTP_METHOD.POST;
import HTTP_METHOD.PUT;
import Pac4jConstants.CSRF_TOKEN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import static Pac4jConstants.ELEMENT_SEPARATOR;


/**
 * Tests the {@link DefaultAuthorizationChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultAuthorizationCheckerTests implements TestsConstants {
    private final DefaultAuthorizationChecker checker = new DefaultAuthorizationChecker();

    private List<UserProfile> profiles;

    private BasicUserProfile profile;

    private static class IdAuthorizer implements Authorizer<UserProfile> {
        @Override
        public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
            return TestsConstants.VALUE.equals(profiles.get(0).getId());
        }
    }

    @Test
    public void testBlankAuthorizerNameAProfile() {
        Assert.assertTrue(checker.isAuthorized(null, profiles, "", null));
    }

    @Test
    public void testNullAuthorizerNameAProfileGetRequest() {
        Assert.assertTrue(checker.isAuthorized(MockWebContext.create(), profiles, null, null));
    }

    @Test
    public void testNullAuthorizerNameAProfilePostRequest() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        Assert.assertFalse(checker.isAuthorized(context, profiles, null, null));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch() {
        profile.setId(TestsConstants.VALUE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(TestsConstants.NAME, new DefaultAuthorizationCheckerTests.IdAuthorizer());
        Assert.assertTrue(checker.isAuthorized(null, profiles, TestsConstants.NAME, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch() {
        internalTestOneExistingAuthorizerProfileDoesNotMatch(TestsConstants.NAME);
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatchCasTrim() {
        internalTestOneExistingAuthorizerProfileDoesNotMatch("   NaME       ");
    }

    @Test(expected = TechnicalException.class)
    public void testOneAuthorizerDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(TestsConstants.NAME, new DefaultAuthorizationCheckerTests.IdAuthorizer());
        checker.isAuthorized(null, profiles, TestsConstants.VALUE, authorizers);
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch() {
        profile.setId(TestsConstants.VALUE);
        profile.addRole(TestsConstants.ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(TestsConstants.NAME, new DefaultAuthorizationCheckerTests.IdAuthorizer());
        authorizers.put(TestsConstants.VALUE, new RequireAnyRoleAuthorizer(TestsConstants.ROLE));
        Assert.assertTrue(checker.isAuthorized(null, profiles, (((TestsConstants.NAME) + (ELEMENT_SEPARATOR)) + (TestsConstants.VALUE)), authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch() {
        profile.addRole(TestsConstants.ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(TestsConstants.NAME, new DefaultAuthorizationCheckerTests.IdAuthorizer());
        authorizers.put(TestsConstants.VALUE, new RequireAnyRoleAuthorizer(TestsConstants.ROLE));
        Assert.assertFalse(checker.isAuthorized(null, profiles, (((TestsConstants.NAME) + (ELEMENT_SEPARATOR)) + (TestsConstants.VALUE)), authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testTwoAuthorizerOneDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(TestsConstants.NAME, new DefaultAuthorizationCheckerTests.IdAuthorizer());
        checker.isAuthorized(null, profiles, (((TestsConstants.NAME) + (ELEMENT_SEPARATOR)) + (TestsConstants.VALUE)), authorizers);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizers() {
        Assert.assertTrue(checker.isAuthorized(null, profiles, null));
        checker.isAuthorized(null, profiles, "auth1", null);
    }

    @Test
    public void testZeroAuthorizers() {
        Assert.assertTrue(checker.isAuthorized(null, profiles, new ArrayList()));
        Assert.assertTrue(checker.isAuthorized(null, profiles, "", new HashMap()));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch2() {
        profile.setId(TestsConstants.VALUE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new DefaultAuthorizationCheckerTests.IdAuthorizer());
        Assert.assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch2() {
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new DefaultAuthorizationCheckerTests.IdAuthorizer());
        Assert.assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch2() {
        profile.setId(TestsConstants.VALUE);
        profile.addRole(TestsConstants.ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new DefaultAuthorizationCheckerTests.IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(TestsConstants.ROLE));
        Assert.assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch2() {
        profile.addRole(TestsConstants.ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new DefaultAuthorizationCheckerTests.IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(TestsConstants.ROLE));
        Assert.assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testNullProfile() {
        checker.isAuthorized(null, null, new ArrayList());
    }

    @Test
    public void testHsts() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.isAuthorized(context, profiles, HSTS, null);
        Assert.assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.isAuthorized(context, profiles, "  HSTS ", null);
        Assert.assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, NOSNIFF, null);
        Assert.assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, NOFRAME, null);
        Assert.assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, XSSPROTECTION, null);
        Assert.assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, NOCACHE, null);
        Assert.assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        Assert.assertNotNull(context.getResponseHeaders().get("Pragma"));
        Assert.assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testAllowAjaxRequests() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, ALLOW_AJAX_REQUESTS, null);
        Assert.assertEquals("*", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        Assert.assertEquals("true", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        final String methods = context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_METHODS_HEADER);
        final List<String> methodArray = Arrays.asList(methods.split(",")).stream().map(String::trim).collect(Collectors.toList());
        Assert.assertTrue(methodArray.contains(POST.name()));
        Assert.assertTrue(methodArray.contains(PUT.name()));
        Assert.assertTrue(methodArray.contains(DELETE.name()));
        Assert.assertTrue(methodArray.contains(OPTIONS.name()));
        Assert.assertTrue(methodArray.contains(GET.name()));
    }

    @Test
    public void testSecurityHeaders() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.isAuthorized(context, profiles, SECURITYHEADERS, null);
        Assert.assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
        Assert.assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        Assert.assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        Assert.assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
        Assert.assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        Assert.assertNotNull(context.getResponseHeaders().get("Pragma"));
        Assert.assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testCsrf() {
        final MockWebContext context = MockWebContext.create();
        Assert.assertTrue(checker.isAuthorized(context, profiles, CSRF, null));
        Assert.assertNotNull(context.getRequestAttribute(CSRF_TOKEN));
        Assert.assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), CSRF_TOKEN));
    }

    @Test
    public void testCsrfToken() {
        final MockWebContext context = MockWebContext.create();
        Assert.assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_TOKEN, null));
        Assert.assertNotNull(context.getRequestAttribute(CSRF_TOKEN));
        Assert.assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), CSRF_TOKEN));
    }

    @Test
    public void testCsrfPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertFalse(checker.isAuthorized(context, profiles, CSRF, null));
        Assert.assertNotNull(context.getRequestAttribute(CSRF_TOKEN));
        Assert.assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_TOKEN, null));
        Assert.assertNotNull(context.getRequestAttribute(CSRF_TOKEN));
        Assert.assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), CSRF_TOKEN));
    }

    @Test
    public void testCsrfPostTokenParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(CSRF_TOKEN, token);
        Assert.assertTrue(checker.isAuthorized(context, profiles, CSRF, null));
        Assert.assertTrue(context.getRequestAttribute(CSRF_TOKEN).isPresent());
        Assert.assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), CSRF_TOKEN));
    }

    @Test
    public void testCsrfCheckPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        generator.get(context);
        Assert.assertFalse(checker.isAuthorized(context, profiles, CSRF_CHECK, null));
    }

    @Test
    public void testCsrfCheckPostTokenParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(CSRF_TOKEN, token);
        Assert.assertTrue(checker.isAuthorized(context, profiles, CSRF_CHECK, null));
    }

    @Test
    public void testIsAnonymous() {
        profiles.clear();
        profiles.add(new AnonymousProfile());
        Assert.assertTrue(checker.isAuthorized(null, profiles, IS_ANONYMOUS, null));
    }

    @Test
    public void testIsAuthenticated() {
        Assert.assertTrue(checker.isAuthorized(null, profiles, IS_AUTHENTICATED, null));
    }

    @Test
    public void testIsFullyAuthenticated() {
        Assert.assertTrue(checker.isAuthorized(null, profiles, IS_FULLY_AUTHENTICATED, null));
    }

    @Test
    public void testIsRemembered() {
        profile.setRemembered(true);
        Assert.assertTrue(checker.isAuthorized(null, profiles, IS_REMEMBERED, null));
    }
}

