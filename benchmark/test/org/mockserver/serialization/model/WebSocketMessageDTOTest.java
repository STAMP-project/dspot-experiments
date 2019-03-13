package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class WebSocketMessageDTOTest {
    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        String type = "some_type";
        String value = "some_value";
        // when
        WebSocketMessageDTO webSocketMessageDTO = new WebSocketMessageDTO();
        webSocketMessageDTO.setType(type);
        webSocketMessageDTO.setValue(value);
        // then
        MatcherAssert.assertThat(webSocketMessageDTO.getType(), Is.is(type));
        MatcherAssert.assertThat(webSocketMessageDTO.getValue(), Is.is(value));
    }

    @Test
    public void shouldHandleNullValuesSetInSetter() {
        // given
        String type = null;
        String value = null;
        // when
        WebSocketMessageDTO webSocketMessageDTO = new WebSocketMessageDTO();
        webSocketMessageDTO.setType(type);
        webSocketMessageDTO.setValue(value);
        // then
        MatcherAssert.assertThat(webSocketMessageDTO.getType(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(webSocketMessageDTO.getValue(), CoreMatchers.nullValue());
    }
}

