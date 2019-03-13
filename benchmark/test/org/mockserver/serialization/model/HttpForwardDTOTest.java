package org.mockserver.serialization.model;


import HttpForward.Scheme;
import HttpForward.Scheme.HTTP;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.HttpForward;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        String host = "some_host";
        int port = 9090;
        HttpForward.Scheme scheme = Scheme.HTTPS;
        HttpForward httpForward = new HttpForward().withHost(host).withPort(port).withScheme(scheme);
        // when
        HttpForwardDTO httpForwardDTO = new HttpForwardDTO(httpForward);
        // then
        MatcherAssert.assertThat(httpForwardDTO.getHost(), Is.is(host));
        MatcherAssert.assertThat(httpForwardDTO.getPort(), Is.is(port));
        MatcherAssert.assertThat(httpForwardDTO.getScheme(), Is.is(scheme));
    }

    @Test
    public void shouldBuildObject() {
        // given
        String host = "some_host";
        int port = 9090;
        HttpForward.Scheme scheme = Scheme.HTTPS;
        HttpForward httpForward = new HttpForward().withHost(host).withPort(port).withScheme(scheme);
        // when
        HttpForward builtHttpForward = buildObject();
        // then
        MatcherAssert.assertThat(builtHttpForward.getHost(), Is.is(host));
        MatcherAssert.assertThat(builtHttpForward.getPort(), Is.is(port));
        MatcherAssert.assertThat(builtHttpForward.getScheme(), Is.is(scheme));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // given
        String host = "some_host";
        int port = 9090;
        HttpForward.Scheme scheme = Scheme.HTTPS;
        HttpForward httpForward = new HttpForward();
        // when
        HttpForwardDTO httpForwardDTO = new HttpForwardDTO(httpForward);
        httpForwardDTO.setHost(host);
        httpForwardDTO.setPort(port);
        httpForwardDTO.setScheme(scheme);
        // then
        MatcherAssert.assertThat(httpForwardDTO.getHost(), Is.is(host));
        MatcherAssert.assertThat(httpForwardDTO.getPort(), Is.is(port));
        MatcherAssert.assertThat(httpForwardDTO.getScheme(), Is.is(scheme));
    }

    @Test
    public void shouldHandleNullObjectInput() {
        // when
        HttpForwardDTO httpForwardDTO = new HttpForwardDTO(null);
        // then
        MatcherAssert.assertThat(httpForwardDTO.getHost(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpForwardDTO.getPort(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpForwardDTO.getScheme(), Is.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldHandleNullFieldInput() {
        // when
        HttpForwardDTO httpForwardDTO = new HttpForwardDTO(new HttpForward());
        // then
        MatcherAssert.assertThat(httpForwardDTO.getHost(), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(httpForwardDTO.getPort(), Is.is(80));
        MatcherAssert.assertThat(httpForwardDTO.getScheme(), Is.is(HTTP));
    }
}

