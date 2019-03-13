package org.whispersystems.textsecuregcm.tests.controllers;


import Envelope.Type;
import MediaType.APPLICATION_JSON_TYPE;
import OptionalAccess.UNIDENTIFIED;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.entities.IncomingMessageList;
import org.whispersystems.textsecuregcm.entities.MessageProtos.Envelope;
import org.whispersystems.textsecuregcm.entities.MismatchedDevices;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntity;
import org.whispersystems.textsecuregcm.entities.OutgoingMessageEntityList;
import org.whispersystems.textsecuregcm.entities.StaleDevices;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.push.ApnFallbackManager;
import org.whispersystems.textsecuregcm.push.PushSender;
import org.whispersystems.textsecuregcm.push.ReceiptSender;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.Device;
import org.whispersystems.textsecuregcm.storage.MessagesManager;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.tests.util.JsonHelpers;
import org.whispersystems.textsecuregcm.util.Base64;


public class MessageControllerTest {
    private static final String SINGLE_DEVICE_RECIPIENT = "+14151111111";

    private static final String MULTI_DEVICE_RECIPIENT = "+14152222222";

    private final PushSender pushSender = Mockito.mock(PushSender.class);

    private final ReceiptSender receiptSender = Mockito.mock(ReceiptSender.class);

    private final AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private final MessagesManager messagesManager = Mockito.mock(MessagesManager.class);

    private final RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private final RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private final ApnFallbackManager apnFallbackManager = Mockito.mock(ApnFallbackManager.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.MessageController(rateLimiters, pushSender, receiptSender, accountsManager, messagesManager, apnFallbackManager)).build();

    @Test
    public synchronized void testSingleDeviceCurrent() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.SINGLE_DEVICE_RECIPIENT)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_single_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        Mockito.verify(pushSender, Mockito.times(1)).sendMessage(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(Device.class), captor.capture(), ArgumentMatchers.eq(false));
        Assert.assertTrue(captor.getValue().hasSource());
        Assert.assertTrue(captor.getValue().hasSourceDevice());
    }

    @Test
    public synchronized void testSingleDeviceCurrentUnidentified() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.SINGLE_DEVICE_RECIPIENT)).request().header(UNIDENTIFIED, Base64.encodeBytes("1234".getBytes())).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_single_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        ArgumentCaptor<Envelope> captor = ArgumentCaptor.forClass(Envelope.class);
        Mockito.verify(pushSender, Mockito.times(1)).sendMessage(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(Device.class), captor.capture(), ArgumentMatchers.eq(false));
        Assert.assertFalse(captor.getValue().hasSource());
        Assert.assertFalse(captor.getValue().hasSourceDevice());
    }

    @Test
    public synchronized void testSendBadAuth() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.SINGLE_DEVICE_RECIPIENT)).request().put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_single_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(401)));
    }

    @Test
    public synchronized void testMultiDeviceMissing() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.MULTI_DEVICE_RECIPIENT)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_single_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(409)));
        MatcherAssert.assertThat("Good Response Body", JsonHelpers.asJson(response.readEntity(MismatchedDevices.class)), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/missing_device_response.json"))));
        Mockito.verifyNoMoreInteractions(pushSender);
    }

    @Test
    public synchronized void testMultiDeviceExtra() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.MULTI_DEVICE_RECIPIENT)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_extra_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(409)));
        MatcherAssert.assertThat("Good Response Body", JsonHelpers.asJson(response.readEntity(MismatchedDevices.class)), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/missing_device_response2.json"))));
        Mockito.verifyNoMoreInteractions(pushSender);
    }

    @Test
    public synchronized void testMultiDevice() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.MULTI_DEVICE_RECIPIENT)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_multi_device.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Mockito.verify(pushSender, Mockito.times(2)).sendMessage(ArgumentMatchers.any(Account.class), ArgumentMatchers.any(Device.class), ArgumentMatchers.any(Envelope.class), ArgumentMatchers.eq(false));
    }

    @Test
    public synchronized void testRegistrationIdMismatch() throws Exception {
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s", MessageControllerTest.MULTI_DEVICE_RECIPIENT)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).put(Entity.entity(mapper.readValue(JsonHelpers.jsonFixture("fixtures/current_message_registration_id.json"), IncomingMessageList.class), APPLICATION_JSON_TYPE));
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(410)));
        MatcherAssert.assertThat("Good Response Body", JsonHelpers.asJson(response.readEntity(StaleDevices.class)), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/mismatched_registration_id.json"))));
        Mockito.verifyNoMoreInteractions(pushSender);
    }

    @Test
    public synchronized void testGetMessages() throws Exception {
        final long timestampOne = 313377;
        final long timestampTwo = 313388;
        final UUID uuidOne = UUID.randomUUID();
        List<OutgoingMessageEntity> messages = new LinkedList<OutgoingMessageEntity>() {
            {
                add(new OutgoingMessageEntity(1L, false, uuidOne, Type.CIPHERTEXT_VALUE, null, timestampOne, "+14152222222", 2, "hi there".getBytes(), null, 0));
                add(new OutgoingMessageEntity(2L, false, null, Type.RECEIPT_VALUE, null, timestampTwo, "+14152222222", 2, null, null, 0));
            }
        };
        OutgoingMessageEntityList messagesList = new OutgoingMessageEntityList(messages, false);
        Mockito.when(messagesManager.getMessagesForDevice(ArgumentMatchers.eq(AuthHelper.VALID_NUMBER), ArgumentMatchers.eq(1L))).thenReturn(messagesList);
        OutgoingMessageEntityList response = resources.getJerseyTest().target("/v1/messages/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).accept(APPLICATION_JSON_TYPE).get(OutgoingMessageEntityList.class);
        Assert.assertEquals(response.getMessages().size(), 2);
        Assert.assertEquals(response.getMessages().get(0).getId(), 0);
        Assert.assertEquals(response.getMessages().get(1).getId(), 0);
        Assert.assertEquals(response.getMessages().get(0).getTimestamp(), timestampOne);
        Assert.assertEquals(response.getMessages().get(1).getTimestamp(), timestampTwo);
        Assert.assertEquals(response.getMessages().get(0).getGuid(), uuidOne);
        Assert.assertEquals(response.getMessages().get(1).getGuid(), null);
    }

    @Test
    public synchronized void testGetMessagesBadAuth() throws Exception {
        final long timestampOne = 313377;
        final long timestampTwo = 313388;
        List<OutgoingMessageEntity> messages = new LinkedList<OutgoingMessageEntity>() {
            {
                add(new OutgoingMessageEntity(1L, false, UUID.randomUUID(), Type.CIPHERTEXT_VALUE, null, timestampOne, "+14152222222", 2, "hi there".getBytes(), null, 0));
                add(new OutgoingMessageEntity(2L, false, UUID.randomUUID(), Type.RECEIPT_VALUE, null, timestampTwo, "+14152222222", 2, null, null, 0));
            }
        };
        OutgoingMessageEntityList messagesList = new OutgoingMessageEntityList(messages, false);
        Mockito.when(messagesManager.getMessagesForDevice(ArgumentMatchers.eq(AuthHelper.VALID_NUMBER), ArgumentMatchers.eq(1L))).thenReturn(messagesList);
        Response response = resources.getJerseyTest().target("/v1/messages/").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.INVALID_PASSWORD)).accept(APPLICATION_JSON_TYPE).get();
        MatcherAssert.assertThat("Unauthorized response", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(401)));
    }

    @Test
    public synchronized void testDeleteMessages() throws Exception {
        long timestamp = System.currentTimeMillis();
        Mockito.when(messagesManager.delete(AuthHelper.VALID_NUMBER, 1, "+14152222222", 31337)).thenReturn(Optional.of(new OutgoingMessageEntity(31337L, true, null, Type.CIPHERTEXT_VALUE, null, timestamp, "+14152222222", 1, "hi".getBytes(), null, 0)));
        Mockito.when(messagesManager.delete(AuthHelper.VALID_NUMBER, 1, "+14152222222", 31338)).thenReturn(Optional.of(new OutgoingMessageEntity(31337L, true, null, Type.RECEIPT_VALUE, null, System.currentTimeMillis(), "+14152222222", 1, null, null, 0)));
        Mockito.when(messagesManager.delete(AuthHelper.VALID_NUMBER, 1, "+14152222222", 31339)).thenReturn(Optional.empty());
        Response response = resources.getJerseyTest().target(String.format("/v1/messages/%s/%d", "+14152222222", 31337)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).delete();
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(204)));
        Mockito.verify(receiptSender).sendReceipt(ArgumentMatchers.any(Account.class), ArgumentMatchers.eq("+14152222222"), ArgumentMatchers.eq(timestamp));
        response = resources.getJerseyTest().target(String.format("/v1/messages/%s/%d", "+14152222222", 31338)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).delete();
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(204)));
        Mockito.verifyNoMoreInteractions(receiptSender);
        response = resources.getJerseyTest().target(String.format("/v1/messages/%s/%d", "+14152222222", 31339)).request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).delete();
        MatcherAssert.assertThat("Good Response Code", response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(204)));
        Mockito.verifyNoMoreInteractions(receiptSender);
    }
}

