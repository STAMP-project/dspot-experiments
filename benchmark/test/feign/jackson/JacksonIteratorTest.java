/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.jackson;


import HttpMethod.GET;
import Util.UTF_8;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.jackson.JacksonIteratorDecoder.JacksonIterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JacksonIteratorTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldDecodePrimitiveArrays() throws IOException {
        assertThat(iterator(Integer.class, "[0,1,2,3]")).containsExactly(0, 1, 2, 3);
    }

    @Test
    public void shouldDecodeObjects() throws IOException {
        assertThat(iterator(JacksonIteratorTest.User.class, "[{\"login\":\"bob\"},{\"login\":\"joe\"}]")).containsExactly(new JacksonIteratorTest.User("bob"), new JacksonIteratorTest.User("joe"));
    }

    @Test
    public void malformedObjectThrowsDecodeException() throws IOException {
        thrown.expect(DecodeException.class);
        thrown.expectCause(Is.isA(IOException.class));
        assertThat(iterator(JacksonIteratorTest.User.class, "[{\"login\":\"bob\"},{\"login\":\"joe...")).containsOnly(new JacksonIteratorTest.User("bob"));
    }

    @Test
    public void emptyBodyDecodesToEmptyIterator() throws IOException {
        assertThat(iterator(String.class, "")).isEmpty();
    }

    @Test
    public void unmodifiable() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        JacksonIterator<String> it = iterator(String.class, "[\"test\"]");
        assertThat(it).containsExactly("test");
        it.remove();
    }

    @Test
    public void responseIsClosedAfterIteration() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        byte[] jsonBytes = "[false, true]".getBytes(Util.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(jsonBytes) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(inputStream, jsonBytes.length).build();
        assertThat(iterator(Boolean.class, response)).hasSize(2);
        assertThat(closed.get()).isTrue();
    }

    @Test
    public void responseIsClosedOnParseError() throws IOException {
        final AtomicBoolean closed = new AtomicBoolean();
        byte[] jsonBytes = "[error".getBytes(Util.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(jsonBytes) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(inputStream, jsonBytes.length).build();
        try {
            thrown.expect(DecodeException.class);
            assertThat(iterator(Boolean.class, response)).hasSize(1);
        } finally {
            assertThat(closed.get()).isTrue();
        }
    }

    static class User extends LinkedHashMap<String, Object> {
        private static final long serialVersionUID = 1L;

        User() {
            // for reflective instantiation.
        }

        User(String login) {
            put("login", login);
        }
    }
}

