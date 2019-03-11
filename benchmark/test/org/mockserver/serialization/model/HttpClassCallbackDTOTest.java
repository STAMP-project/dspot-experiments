package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.HttpClassCallback;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpClassCallbackDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        String callbackClass = HttpClassCallbackDTOTest.class.getName();
        HttpClassCallback httpClassCallback = HttpClassCallback.callback(callbackClass);
        // when
        HttpClassCallbackDTO httpClassCallbackDTO = new HttpClassCallbackDTO(httpClassCallback);
        // then
        MatcherAssert.assertThat(httpClassCallbackDTO.getCallbackClass(), Is.is(callbackClass));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String callbackClass = HttpClassCallbackDTOTest.class.getName();
        HttpClassCallback httpClassCallback = new HttpClassCallback().withCallbackClass(callbackClass);
        // when
        HttpClassCallback builtHttpClassCallback = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpClassCallback.getCallbackClass(), Is.is(callbackClass));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        String callbackClass = HttpClassCallbackDTOTest.class.getName();
        HttpClassCallback httpClassCallback = new HttpClassCallback();
        // when
        HttpClassCallbackDTO httpClassCallbackDTO = new HttpClassCallbackDTO(httpClassCallback);
        httpClassCallbackDTO.setCallbackClass(callbackClass);
        // then
        MatcherAssert.assertThat(httpClassCallbackDTO.getCallbackClass(), Is.is(callbackClass));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpClassCallbackDTO httpClassCallbackDTO = new HttpClassCallbackDTO(null);
        // then
        MatcherAssert.assertThat(httpClassCallbackDTO.getCallbackClass(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpClassCallbackDTO httpClassCallbackDTO = new HttpClassCallbackDTO(new HttpClassCallback());
        // then
        MatcherAssert.assertThat(httpClassCallbackDTO.getCallbackClass(), Is.is(CoreMatchers.nullValue()));
    }
}

