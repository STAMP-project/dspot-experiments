package org.whispersystems.textsecuregcm.tests.controllers;


import MediaType.APPLICATION_JSON_TYPE;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.auth.TurnTokenGenerator;
import org.whispersystems.textsecuregcm.entities.AccountAttributes;
import org.whispersystems.textsecuregcm.entities.RegistrationLock;
import org.whispersystems.textsecuregcm.entities.RegistrationLockFailure;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.mappers.RateLimitExceededExceptionMapper;
import org.whispersystems.textsecuregcm.providers.TimeProvider;
import org.whispersystems.textsecuregcm.sms.SmsSender;
import org.whispersystems.textsecuregcm.sqs.DirectoryQueue;
import org.whispersystems.textsecuregcm.storage.AbusiveHostRules;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.MessagesManager;
import org.whispersystems.textsecuregcm.storage.PendingAccountsManager;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class AccountControllerTest {
    private static final String SENDER = "+14152222222";

    private static final String SENDER_OLD = "+14151111111";

    private static final String SENDER_PIN = "+14153333333";

    private static final String SENDER_OVER_PIN = "+14154444444";

    private static final String ABUSIVE_HOST = "192.168.1.1";

    private static final String RESTRICTED_HOST = "192.168.1.2";

    private static final String NICE_HOST = "127.0.0.1";

    private PendingAccountsManager pendingAccountsManager = Mockito.mock(PendingAccountsManager.class);

    private AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private AbusiveHostRules abusiveHostRules = Mockito.mock(AbusiveHostRules.class);

    private RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private RateLimiter pinLimiter = Mockito.mock(RateLimiter.class);

    private RateLimiter smsVoiceIpLimiter = Mockito.mock(RateLimiter.class);

    private SmsSender smsSender = Mockito.mock(SmsSender.class);

    private DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);

    private MessagesManager storedMessages = Mockito.mock(MessagesManager.class);

    private TimeProvider timeProvider = Mockito.mock(TimeProvider.class);

    private TurnTokenGenerator turnTokenGenerator = Mockito.mock(TurnTokenGenerator.class);

    private Account senderPinAccount = Mockito.mock(Account.class);

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).addProvider(new RateLimitExceededExceptionMapper()).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.AccountController(pendingAccountsManager, accountsManager, abusiveHostRules, rateLimiters, smsSender, directoryQueue, storedMessages, turnTokenGenerator, new HashMap())).build();

    @Test
    public void testSendCode() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).request().header("X-Forwarded-For", AccountControllerTest.NICE_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(smsSender).deliverSmsVerification(ArgumentMatchers.eq(AccountControllerTest.SENDER), ArgumentMatchers.eq(Optional.empty()), ArgumentMatchers.anyString());
        Mockito.verify(abusiveHostRules).getAbusiveHostRulesFor(ArgumentMatchers.eq(AccountControllerTest.NICE_HOST));
    }

    @Test
    public void testSendiOSCode() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).queryParam("client", "ios").request().header("X-Forwarded-For", AccountControllerTest.NICE_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(smsSender).deliverSmsVerification(ArgumentMatchers.eq(AccountControllerTest.SENDER), ArgumentMatchers.eq(Optional.of("ios")), ArgumentMatchers.anyString());
    }

    @Test
    public void testSendAndroidNgCode() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).queryParam("client", "android-ng").request().header("X-Forwarded-For", AccountControllerTest.NICE_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(smsSender).deliverSmsVerification(ArgumentMatchers.eq(AccountControllerTest.SENDER), ArgumentMatchers.eq(Optional.of("android-ng")), ArgumentMatchers.anyString());
    }

    @Test
    public void testSendAbusiveHost() {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).request().header("X-Forwarded-For", AccountControllerTest.ABUSIVE_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(abusiveHostRules).getAbusiveHostRulesFor(ArgumentMatchers.eq(AccountControllerTest.ABUSIVE_HOST));
        Mockito.verifyNoMoreInteractions(smsSender);
    }

    @Test
    public void testSendMultipleHost() {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).request().header("X-Forwarded-For", (((AccountControllerTest.NICE_HOST) + ", ") + (AccountControllerTest.ABUSIVE_HOST))).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(abusiveHostRules, Mockito.times(1)).getAbusiveHostRulesFor(ArgumentMatchers.eq(AccountControllerTest.ABUSIVE_HOST));
        Mockito.verify(abusiveHostRules, Mockito.times(1)).getAbusiveHostRulesFor(ArgumentMatchers.eq(AccountControllerTest.NICE_HOST));
        Mockito.verifyNoMoreInteractions(abusiveHostRules);
        Mockito.verifyNoMoreInteractions(smsSender);
    }

    @Test
    public void testSendRestrictedHostOut() {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", AccountControllerTest.SENDER)).request().header("X-Forwarded-For", AccountControllerTest.RESTRICTED_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(abusiveHostRules).getAbusiveHostRulesFor(ArgumentMatchers.eq(AccountControllerTest.RESTRICTED_HOST));
        Mockito.verifyNoMoreInteractions(smsSender);
    }

    @Test
    public void testSendRestrictedIn() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/sms/code/%s", "+12345678901")).request().header("X-Forwarded-For", AccountControllerTest.RESTRICTED_HOST).get();
        assertThat(response.getStatus()).isEqualTo(200);
        Mockito.verify(smsSender).deliverSmsVerification(ArgumentMatchers.eq("+12345678901"), ArgumentMatchers.eq(Optional.empty()), ArgumentMatchers.anyString());
    }

    @Test
    public void testVerifyCode() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "1234")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 2222, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(204);
        Mockito.verify(accountsManager, Mockito.times(1)).create(ArgumentMatchers.isA(Account.class));
        Mockito.verify(directoryQueue, Mockito.times(1)).deleteRegisteredUser(ArgumentMatchers.eq(AccountControllerTest.SENDER));
    }

    @Test
    public void testVerifyCodeOld() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "1234")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_OLD, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 2222, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(403);
        Mockito.verifyNoMoreInteractions(accountsManager);
    }

    @Test
    public void testVerifyBadCode() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "1111")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(403);
        Mockito.verifyNoMoreInteractions(accountsManager);
    }

    @Test
    public void testVerifyPin() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "333333")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_PIN, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, "31337"), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(204);
        Mockito.verify(pinLimiter).validate(ArgumentMatchers.eq(AccountControllerTest.SENDER_PIN));
    }

    @Test
    public void testVerifyWrongPin() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "333333")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_PIN, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, "31338"), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(423);
        Mockito.verify(pinLimiter).validate(ArgumentMatchers.eq(AccountControllerTest.SENDER_PIN));
    }

    @Test
    public void testVerifyNoPin() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "333333")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_PIN, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(423);
        RegistrationLockFailure failure = response.readEntity(RegistrationLockFailure.class);
        Mockito.verifyNoMoreInteractions(pinLimiter);
    }

    @Test
    public void testVerifyLimitPin() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "444444")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_OVER_PIN, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, "31337"), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(413);
        Mockito.verify(rateLimiter).clear(ArgumentMatchers.eq(AccountControllerTest.SENDER_OVER_PIN));
    }

    @Test
    public void testVerifyOldPin() throws Exception {
        try {
            Mockito.when(senderPinAccount.getLastSeen()).thenReturn(((System.currentTimeMillis()) - (TimeUnit.DAYS.toMillis(7))));
            Response response = resources.getJerseyTest().target(String.format("/v1/accounts/code/%s", "444444")).request().header("Authorization", AuthHelper.getAuthHeader(AccountControllerTest.SENDER_OVER_PIN, "bar")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 3333, null), APPLICATION_JSON_TYPE));
            assertThat(response.getStatus()).isEqualTo(204);
        } finally {
            Mockito.when(senderPinAccount.getLastSeen()).thenReturn(System.currentTimeMillis());
        }
    }

    @Test
    public void testSetPin() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/accounts/pin/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.json(new RegistrationLock("31337")));
        assertThat(response.getStatus()).isEqualTo(204);
        Mockito.verify(AuthHelper.VALID_ACCOUNT).setPin(ArgumentMatchers.eq("31337"));
    }

    @Test
    public void testSetPinUnauthorized() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/accounts/pin/").request().put(Entity.json(new RegistrationLock("31337")));
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    public void testSetShortPin() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/accounts/pin/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.json(new RegistrationLock("313")));
        assertThat(response.getStatus()).isEqualTo(422);
        Mockito.verify(AuthHelper.VALID_ACCOUNT, Mockito.never()).setPin(ArgumentMatchers.anyString());
    }
}

