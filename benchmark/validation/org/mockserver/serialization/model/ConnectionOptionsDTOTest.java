package org.mockserver.serialization.model;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.ConnectionOptions;


public class ConnectionOptionsDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        ConnectionOptionsDTO connectionOptions = new ConnectionOptionsDTO(new ConnectionOptions().withSuppressContentLengthHeader(true).withContentLengthHeaderOverride(50).withSuppressConnectionHeader(true).withKeepAliveOverride(true).withCloseSocket(true));
        // then
        MatcherAssert.assertThat(connectionOptions.getSuppressContentLengthHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getContentLengthHeaderOverride(), Is.is(50));
        MatcherAssert.assertThat(connectionOptions.getSuppressConnectionHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getKeepAliveOverride(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getCloseSocket(), Is.is(true));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // when
        ConnectionOptionsDTO connectionOptions = new ConnectionOptionsDTO();
        connectionOptions.setSuppressContentLengthHeader(true);
        connectionOptions.setContentLengthHeaderOverride(50);
        connectionOptions.setSuppressConnectionHeader(true);
        connectionOptions.setKeepAliveOverride(true);
        connectionOptions.setCloseSocket(true);
        // then
        MatcherAssert.assertThat(connectionOptions.getSuppressContentLengthHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getContentLengthHeaderOverride(), Is.is(50));
        MatcherAssert.assertThat(connectionOptions.getSuppressConnectionHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getKeepAliveOverride(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getCloseSocket(), Is.is(true));
    }

    @Test
    public void shouldHandleNullInput() {
        // when
        ConnectionOptionsDTO connectionOptions = new ConnectionOptionsDTO(null);
        // then
        MatcherAssert.assertThat(connectionOptions.getSuppressContentLengthHeader(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(connectionOptions.getContentLengthHeaderOverride(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(connectionOptions.getSuppressConnectionHeader(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(connectionOptions.getKeepAliveOverride(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(connectionOptions.getCloseSocket(), CoreMatchers.nullValue());
    }
}

