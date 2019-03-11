package org.mockserver.serialization.model;


import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.HttpObjectCallback;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpObjectCallbackDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        String clientId = UUID.randomUUID().toString();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback().withClientId(clientId);
        // when
        HttpObjectCallbackDTO httpObjectCallbackDTO = new HttpObjectCallbackDTO(httpObjectCallback);
        // then
        MatcherAssert.assertThat(httpObjectCallbackDTO.getClientId(), Is.is(clientId));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String clientId = UUID.randomUUID().toString();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback().withClientId(clientId);
        // when
        HttpObjectCallback builtHttpObjectCallback = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpObjectCallback.getClientId(), Is.is(clientId));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        String clientId = UUID.randomUUID().toString();
        HttpObjectCallback httpObjectCallback = new HttpObjectCallback();
        // when
        HttpObjectCallbackDTO httpObjectCallbackDTO = new HttpObjectCallbackDTO(httpObjectCallback);
        httpObjectCallbackDTO.setClientId(clientId);
        // then
        MatcherAssert.assertThat(httpObjectCallbackDTO.getClientId(), Is.is(clientId));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpObjectCallbackDTO httpObjectCallbackDTO = new HttpObjectCallbackDTO(null);
        // then
        MatcherAssert.assertThat(httpObjectCallbackDTO.getClientId(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpObjectCallbackDTO httpObjectCallbackDTO = new HttpObjectCallbackDTO(new HttpObjectCallback());
        // then
        MatcherAssert.assertThat(httpObjectCallbackDTO.getClientId(), Is.is(CoreMatchers.nullValue()));
    }
}

