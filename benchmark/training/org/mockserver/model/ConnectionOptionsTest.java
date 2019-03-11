package org.mockserver.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ConnectionOptionsTest {
    @Test
    public void shouldReturnValuesSetInWithMethods() {
        // when
        ConnectionOptions connectionOptions = new ConnectionOptions().withSuppressContentLengthHeader(true).withContentLengthHeaderOverride(50).withSuppressConnectionHeader(true).withKeepAliveOverride(true).withCloseSocket(true);
        // then
        MatcherAssert.assertThat(connectionOptions.getSuppressContentLengthHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getContentLengthHeaderOverride(), Is.is(50));
        MatcherAssert.assertThat(connectionOptions.getSuppressConnectionHeader(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getKeepAliveOverride(), Is.is(true));
        MatcherAssert.assertThat(connectionOptions.getCloseSocket(), Is.is(true));
    }

    @Test
    public void shouldTestFalseOrNull() {
        MatcherAssert.assertThat(ConnectionOptions.isFalseOrNull(false), Is.is(true));
        MatcherAssert.assertThat(ConnectionOptions.isFalseOrNull(null), Is.is(true));
        MatcherAssert.assertThat(ConnectionOptions.isFalseOrNull(true), Is.is(false));
    }
}

