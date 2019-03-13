package org.whispersystems.textsecuregcm.tests.controllers;


import MediaType.APPLICATION_JSON_TYPE;
import OptionalAccess.UNIDENTIFIED;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.entities.PreKey;
import org.whispersystems.textsecuregcm.entities.PreKeyCount;
import org.whispersystems.textsecuregcm.entities.PreKeyResponse;
import org.whispersystems.textsecuregcm.entities.PreKeyState;
import org.whispersystems.textsecuregcm.entities.SignedPreKey;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.sqs.DirectoryQueue;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.storage.KeyRecord;
import org.whispersystems.textsecuregcm.storage.Keys;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;


public class KeyControllerTest {
    private static final String EXISTS_NUMBER = "+14152222222";

    private static String NOT_EXISTS_NUMBER = "+14152222220";

    private static int SAMPLE_REGISTRATION_ID = 999;

    private static int SAMPLE_REGISTRATION_ID2 = 1002;

    private static int SAMPLE_REGISTRATION_ID4 = 1555;

    private final KeyRecord SAMPLE_KEY = new KeyRecord(1, KeyControllerTest.EXISTS_NUMBER, Device.MASTER_ID, 1234, "test1", false);

    private final KeyRecord SAMPLE_KEY2 = new KeyRecord(2, KeyControllerTest.EXISTS_NUMBER, 2, 5667, "test3", false);

    private final KeyRecord SAMPLE_KEY3 = new KeyRecord(3, KeyControllerTest.EXISTS_NUMBER, 3, 334, "test5", false);

    private final KeyRecord SAMPLE_KEY4 = new KeyRecord(4, KeyControllerTest.EXISTS_NUMBER, 4, 336, "test6", false);

    private final SignedPreKey SAMPLE_SIGNED_KEY = new SignedPreKey(1111, "foofoo", "sig11");

    private final SignedPreKey SAMPLE_SIGNED_KEY2 = new SignedPreKey(2222, "foobar", "sig22");

    private final SignedPreKey SAMPLE_SIGNED_KEY3 = new SignedPreKey(3333, "barfoo", "sig33");

    private final Keys keys = Mockito.mock(Keys.class);

    private final AccountsManager accounts = Mockito.mock(AccountsManager.class);

    private final DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);

    private final Account existsAccount = Mockito.mock(Account.class);

    private RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.KeysController(rateLimiters, keys, accounts, directoryQueue)).build();

    @Test
    public void validKeyStatusTestV2() throws Exception {
        PreKeyCount result = resources.getJerseyTest().target("/v2/keys").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(PreKeyCount.class);
        assertThat(((result.getCount()) == 4));
        Mockito.verify(keys).getCount(ArgumentMatchers.eq(AuthHelper.VALID_NUMBER), ArgumentMatchers.eq(1L));
    }

    @Test
    public void getSignedPreKeyV2() throws Exception {
        SignedPreKey result = resources.getJerseyTest().target("/v2/keys/signed").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(SignedPreKey.class);
        assertThat(result.equals(SAMPLE_SIGNED_KEY));
    }

    @Test
    public void putSignedPreKeyV2() throws Exception {
        SignedPreKey test = new SignedPreKey(9999, "fooozzz", "baaarzzz");
        Response response = resources.getJerseyTest().target("/v2/keys/signed").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(test, APPLICATION_JSON_TYPE));
        assertThat(((response.getStatus()) == 204));
        Mockito.verify(AuthHelper.VALID_DEVICE).setSignedPreKey(ArgumentMatchers.eq(test));
        Mockito.verify(accounts).update(ArgumentMatchers.eq(AuthHelper.VALID_ACCOUNT));
    }

    @Test
    public void validSingleRequestTestV2() throws Exception {
        PreKeyResponse result = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(PreKeyResponse.class);
        assertThat(result.getIdentityKey()).isEqualTo(existsAccount.getIdentityKey());
        assertThat(result.getDevicesCount()).isEqualTo(1);
        assertThat(result.getDevice(1).getPreKey().getKeyId()).isEqualTo(SAMPLE_KEY.getKeyId());
        assertThat(result.getDevice(1).getPreKey().getPublicKey()).isEqualTo(SAMPLE_KEY.getPublicKey());
        assertThat(result.getDevice(1).getSignedPreKey()).isEqualTo(existsAccount.getDevice(1).get().getSignedPreKey());
        Mockito.verify(keys).get(ArgumentMatchers.eq(KeyControllerTest.EXISTS_NUMBER), ArgumentMatchers.eq(1L));
        Mockito.verifyNoMoreInteractions(keys);
    }

    @Test
    public void testUnidentifiedRequest() throws Exception {
        PreKeyResponse result = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().header(UNIDENTIFIED, AuthHelper.getUnidentifiedAccessHeader("1337".getBytes())).get(PreKeyResponse.class);
        assertThat(result.getIdentityKey()).isEqualTo(existsAccount.getIdentityKey());
        assertThat(result.getDevicesCount()).isEqualTo(1);
        assertThat(result.getDevice(1).getPreKey().getKeyId()).isEqualTo(SAMPLE_KEY.getKeyId());
        assertThat(result.getDevice(1).getPreKey().getPublicKey()).isEqualTo(SAMPLE_KEY.getPublicKey());
        assertThat(result.getDevice(1).getSignedPreKey()).isEqualTo(existsAccount.getDevice(1).get().getSignedPreKey());
        Mockito.verify(keys).get(ArgumentMatchers.eq(KeyControllerTest.EXISTS_NUMBER), ArgumentMatchers.eq(1L));
        Mockito.verifyNoMoreInteractions(keys);
    }

    @Test
    public void testUnauthorizedUnidentifiedRequest() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().header(UNIDENTIFIED, AuthHelper.getUnidentifiedAccessHeader("9999".getBytes())).get();
        assertThat(response.getStatus()).isEqualTo(401);
        Mockito.verifyNoMoreInteractions(keys);
    }

    @Test
    public void testMalformedUnidentifiedRequest() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().header(UNIDENTIFIED, "$$$$$$$$$").get();
        assertThat(response.getStatus()).isEqualTo(401);
        Mockito.verifyNoMoreInteractions(keys);
    }

    @Test
    public void validMultiRequestTestV2() throws Exception {
        PreKeyResponse results = resources.getJerseyTest().target(String.format("/v2/keys/%s/*", KeyControllerTest.EXISTS_NUMBER)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(PreKeyResponse.class);
        assertThat(results.getDevicesCount()).isEqualTo(3);
        assertThat(results.getIdentityKey()).isEqualTo(existsAccount.getIdentityKey());
        PreKey signedPreKey = results.getDevice(1).getSignedPreKey();
        PreKey preKey = results.getDevice(1).getPreKey();
        long registrationId = results.getDevice(1).getRegistrationId();
        long deviceId = results.getDevice(1).getDeviceId();
        assertThat(preKey.getKeyId()).isEqualTo(SAMPLE_KEY.getKeyId());
        assertThat(preKey.getPublicKey()).isEqualTo(SAMPLE_KEY.getPublicKey());
        assertThat(registrationId).isEqualTo(KeyControllerTest.SAMPLE_REGISTRATION_ID);
        assertThat(signedPreKey.getKeyId()).isEqualTo(SAMPLE_SIGNED_KEY.getKeyId());
        assertThat(signedPreKey.getPublicKey()).isEqualTo(SAMPLE_SIGNED_KEY.getPublicKey());
        assertThat(deviceId).isEqualTo(1);
        signedPreKey = results.getDevice(2).getSignedPreKey();
        preKey = results.getDevice(2).getPreKey();
        registrationId = results.getDevice(2).getRegistrationId();
        deviceId = results.getDevice(2).getDeviceId();
        assertThat(preKey.getKeyId()).isEqualTo(SAMPLE_KEY2.getKeyId());
        assertThat(preKey.getPublicKey()).isEqualTo(SAMPLE_KEY2.getPublicKey());
        assertThat(registrationId).isEqualTo(KeyControllerTest.SAMPLE_REGISTRATION_ID2);
        assertThat(signedPreKey.getKeyId()).isEqualTo(SAMPLE_SIGNED_KEY2.getKeyId());
        assertThat(signedPreKey.getPublicKey()).isEqualTo(SAMPLE_SIGNED_KEY2.getPublicKey());
        assertThat(deviceId).isEqualTo(2);
        signedPreKey = results.getDevice(4).getSignedPreKey();
        preKey = results.getDevice(4).getPreKey();
        registrationId = results.getDevice(4).getRegistrationId();
        deviceId = results.getDevice(4).getDeviceId();
        assertThat(preKey.getKeyId()).isEqualTo(SAMPLE_KEY4.getKeyId());
        assertThat(preKey.getPublicKey()).isEqualTo(SAMPLE_KEY4.getPublicKey());
        assertThat(registrationId).isEqualTo(KeyControllerTest.SAMPLE_REGISTRATION_ID4);
        assertThat(signedPreKey).isNull();
        assertThat(deviceId).isEqualTo(4);
        Mockito.verify(keys).get(ArgumentMatchers.eq(KeyControllerTest.EXISTS_NUMBER));
        Mockito.verifyNoMoreInteractions(keys);
    }

    @Test
    public void invalidRequestTestV2() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v2/keys/%s", KeyControllerTest.NOT_EXISTS_NUMBER)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void anotherInvalidRequestTestV2() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v2/keys/%s/22", KeyControllerTest.EXISTS_NUMBER)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void unauthorizedRequestTestV2() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.INVALID_PASSWORD)).get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(401);
        response = resources.getJerseyTest().target(String.format("/v2/keys/%s/1", KeyControllerTest.EXISTS_NUMBER)).request().get();
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(401);
    }

    @Test
    public void putKeysTestV2() throws Exception {
        final PreKey preKey = new PreKey(31337, "foobar");
        final PreKey lastResortKey = new PreKey(31339, "barbar");
        final SignedPreKey signedPreKey = new SignedPreKey(31338, "foobaz", "myvalidsig");
        final String identityKey = "barbar";
        List<PreKey> preKeys = new LinkedList<PreKey>() {
            {
                add(preKey);
            }
        };
        PreKeyState preKeyState = new PreKeyState(identityKey, signedPreKey, preKeys);
        Response response = resources.getJerseyTest().target("/v2/keys").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(preKeyState, APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(204);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(keys).store(ArgumentMatchers.eq(AuthHelper.VALID_NUMBER), ArgumentMatchers.eq(1L), listCaptor.capture());
        List<PreKey> capturedList = listCaptor.getValue();
        assertThat(((capturedList.size()) == 1));
        assertThat(((capturedList.get(0).getKeyId()) == 31337));
        assertThat(capturedList.get(0).getPublicKey().equals("foobar"));
        Mockito.verify(AuthHelper.VALID_ACCOUNT).setIdentityKey(ArgumentMatchers.eq("barbar"));
        Mockito.verify(AuthHelper.VALID_DEVICE).setSignedPreKey(ArgumentMatchers.eq(signedPreKey));
        Mockito.verify(accounts).update(AuthHelper.VALID_ACCOUNT);
    }
}

