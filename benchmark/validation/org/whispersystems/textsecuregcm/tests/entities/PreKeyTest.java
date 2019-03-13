package org.whispersystems.textsecuregcm.tests.entities;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.whispersystems.textsecuregcm.entities.ClientContact;
import org.whispersystems.textsecuregcm.entities.PreKey;
import org.whispersystems.textsecuregcm.tests.util.JsonHelpers;
import org.whispersystems.textsecuregcm.util.Util;


public class PreKeyTest {
    @Test
    public void deserializeFromJSONV() throws Exception {
        ClientContact contact = new ClientContact(Util.getContactToken("+14152222222"), "whisper", false, false);
        MatcherAssert.assertThat("a ClientContact can be deserialized from JSON", JsonHelpers.fromJson(JsonHelpers.jsonFixture("fixtures/contact.relay.json"), ClientContact.class), CoreMatchers.is(contact));
    }

    @Test
    public void serializeToJSONV2() throws Exception {
        PreKey preKey = new PreKey(1234, "test");
        MatcherAssert.assertThat("PreKeyV2 Serialization works", JsonHelpers.asJson(preKey), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/prekey_v2.json"))));
    }
}

