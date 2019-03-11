package com.baeldung.jackson.inheritance;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ItemIdRemovedFromUserUnitTest {
    @Test
    public void givenRemoveItemJson_whenDeserialize_shouldHaveProperClassType() throws IOException {
        // given
        Event event = new ItemIdRemovedFromUser("1", 12345567L, "item_1", 2L);
        ObjectMapper objectMapper = new ObjectMapper();
        String eventJson = objectMapper.writeValueAsString(event);
        // when
        Event result = new ObjectMapper().readValue(eventJson, Event.class);
        // then
        Assert.assertTrue((result instanceof ItemIdRemovedFromUser));
        Assert.assertEquals("item_1", getItemId());
    }

    @Test
    public void givenAdddItemJson_whenSerialize_shouldIgnoreIdPropertyFromSuperclass() throws IOException {
        // given
        Event event = new ItemIdAddedToUser("1", 12345567L, "item_1", 2L);
        ObjectMapper objectMapper = new ObjectMapper();
        // when
        String eventJson = objectMapper.writeValueAsString(event);
        // then
        Assert.assertFalse(eventJson.contains("id"));
    }
}

