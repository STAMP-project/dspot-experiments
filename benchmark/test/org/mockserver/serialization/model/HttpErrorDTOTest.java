package org.mockserver.serialization.model;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpError;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpErrorDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        Delay delay = new Delay(TimeUnit.HOURS, 1);
        Boolean dropConnection = Boolean.TRUE;
        byte[] responseBytes = "some_bytes".getBytes(StandardCharsets.UTF_8);
        HttpError httpError = new HttpError().withDelay(delay).withDropConnection(dropConnection).withResponseBytes(responseBytes);
        // when
        HttpErrorDTO httpErrorDTO = new HttpErrorDTO(httpError);
        // then
        MatcherAssert.assertThat(httpErrorDTO.getDelay(), Is.is(new DelayDTO(delay)));
        MatcherAssert.assertThat(httpErrorDTO.getDropConnection(), Is.is(dropConnection));
        MatcherAssert.assertThat(httpErrorDTO.getResponseBytes(), Is.is(responseBytes));
    }

    @Test
    public void shouldBuildObject() {
        // given
        Delay delay = new Delay(TimeUnit.HOURS, 1);
        Boolean dropConnection = Boolean.TRUE;
        byte[] responseBytes = "some_bytes".getBytes(StandardCharsets.UTF_8);
        HttpError httpError = new HttpError().withDelay(delay).withDropConnection(dropConnection).withResponseBytes(responseBytes);
        // when
        HttpError builtHttpError = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpError.getDelay(), Is.is(delay));
        MatcherAssert.assertThat(builtHttpError.getDropConnection(), Is.is(dropConnection));
        MatcherAssert.assertThat(builtHttpError.getResponseBytes(), Is.is(responseBytes));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        DelayDTO delay = new DelayDTO(new Delay(TimeUnit.HOURS, 1));
        Boolean dropConnection = Boolean.TRUE;
        byte[] responseBytes = "some_bytes".getBytes(StandardCharsets.UTF_8);
        HttpError httpError = new HttpError();
        // when
        HttpErrorDTO httpErrorDTO = new HttpErrorDTO(httpError);
        httpErrorDTO.setDelay(delay);
        httpErrorDTO.setDropConnection(dropConnection);
        httpErrorDTO.setResponseBytes(responseBytes);
        // then
        MatcherAssert.assertThat(httpErrorDTO.getDelay(), Is.is(delay));
        MatcherAssert.assertThat(httpErrorDTO.getDropConnection(), Is.is(dropConnection));
        MatcherAssert.assertThat(httpErrorDTO.getResponseBytes(), Is.is(responseBytes));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpErrorDTO httpErrorDTO = new HttpErrorDTO(null);
        // then
        MatcherAssert.assertThat(httpErrorDTO.getDelay(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpErrorDTO.getDropConnection(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpErrorDTO.getResponseBytes(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpErrorDTO httpErrorDTO = new HttpErrorDTO(new HttpError());
        // then
        MatcherAssert.assertThat(httpErrorDTO.getDelay(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpErrorDTO.getDropConnection(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpErrorDTO.getResponseBytes(), Is.is(CoreMatchers.nullValue()));
    }
}

