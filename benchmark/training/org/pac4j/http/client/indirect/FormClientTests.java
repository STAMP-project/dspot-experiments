package org.pac4j.http.client.indirect;


import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import static FormClient.ERROR_PARAMETER;
import static FormClient.MISSING_FIELD_ERROR;


/**
 * This class tests the {@link FormClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class FormClientTests implements TestsConstants {
    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final FormClient formClient = new FormClient(LOGIN_URL, null);
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create()), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.setProfileCreator(null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new <USERNAME, PASSWORD>UsernamePasswordCredentials(), MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.init();
    }

    @Test
    public void testMissingLoginUrl() {
        final FormClient formClient = new FormClient(null, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }

    @Test
    public void testRedirectionUrl() {
        final FormClient formClient = getFormClient();
        MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (formClient.redirect(context).get()));
        Assert.assertEquals(LOGIN_URL, action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (TestsHelper.expectException(() -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)))));
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals((((((((((LOGIN_URL) + "?") + (formClient.getUsernameParameter())) + "=") + (USERNAME)) + "&") + (ERROR_PARAMETER)) + "=") + (MISSING_FIELD_ERROR)), action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (TestsHelper.expectException(() -> formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD)))));
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals((((((((LOGIN_URL) + "?") + (formClient.getUsernameParameter())) + "=&") + (ERROR_PARAMETER)) + "=") + (MISSING_FIELD_ERROR)), action.getLocation());
    }

    @Test
    public void testGetCredentials() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = ((FoundAction) (TestsHelper.expectException(() -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME).addRequestParameter(formClient.getPasswordParameter(), PASSWORD)))));
        Assert.assertEquals(302, action.getCode());
        Assert.assertEquals((((((((((LOGIN_URL) + "?") + (formClient.getUsernameParameter())) + "=") + (USERNAME)) + "&") + (ERROR_PARAMETER)) + "=") + (CredentialsException.class.getSimpleName())), action.getLocation());
    }

    @Test
    public void testGetRightCredentials() {
        final FormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create().addRequestParameter(formClient.getUsernameParameter(), USERNAME).addRequestParameter(formClient.getPasswordParameter(), USERNAME)).get();
        Assert.assertEquals(USERNAME, credentials.getUsername());
        Assert.assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final FormClient formClient = getFormClient();
        formClient.setProfileCreator(( credentials, context) -> {
            String username = credentials.getUsername();
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return Optional.of(profile);
        });
        final MockWebContext context = MockWebContext.create();
        final CommonProfile profile = ((CommonProfile) (formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME), context).get()));
        Assert.assertEquals(USERNAME, profile.getId());
        Assert.assertEquals((((CommonProfile.class.getName()) + (CommonProfile.SEPARATOR)) + (USERNAME)), profile.getTypedId());
        Assert.assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        Assert.assertEquals(USERNAME, profile.getUsername());
        Assert.assertEquals(1, profile.getAttributes().size());
    }
}

