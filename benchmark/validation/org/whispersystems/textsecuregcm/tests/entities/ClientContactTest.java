package org.whispersystems.textsecuregcm.tests.entities;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.whispersystems.textsecuregcm.entities.ClientContact;
import org.whispersystems.textsecuregcm.tests.util.JsonHelpers;
import org.whispersystems.textsecuregcm.util.Util;


public class ClientContactTest {
    @Test
    public void serializeToJSON() throws Exception {
        byte[] token = Util.getContactToken("+14152222222");
        ClientContact contact = new ClientContact(token, null, false, false);
        ClientContact contactWithRelay = new ClientContact(token, "whisper", false, false);
        ClientContact contactWithRelayVox = new ClientContact(token, "whisper", true, false);
        ClientContact contactWithRelayVid = new ClientContact(token, "whisper", true, true);
        MatcherAssert.assertThat("Basic Contact Serialization works", JsonHelpers.asJson(contact), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/contact.json"))));
        MatcherAssert.assertThat("Contact Relay Serialization works", JsonHelpers.asJson(contactWithRelay), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/contact.relay.json"))));
        MatcherAssert.assertThat("Contact Relay Vox Serializaton works", JsonHelpers.asJson(contactWithRelayVox), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/contact.relay.voice.json"))));
        MatcherAssert.assertThat("Contact Relay Video Serializaton works", JsonHelpers.asJson(contactWithRelayVid), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/contact.relay.video.json"))));
    }

    @Test
    public void deserializeFromJSON() throws Exception {
        ClientContact contact = new ClientContact(Util.getContactToken("+14152222222"), "whisper", false, false);
        MatcherAssert.assertThat("a ClientContact can be deserialized from JSON", JsonHelpers.fromJson(JsonHelpers.jsonFixture("fixtures/contact.relay.json"), ClientContact.class), CoreMatchers.is(contact));
    }
}

