/**
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.codec.json;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.AbstractLeakCheckingTestCase;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class Jackson2TokenizerTests extends AbstractLeakCheckingTestCase {
    private JsonFactory jsonFactory;

    private ObjectMapper objectMapper;

    @Test
    public void doNotTokenizeArrayElements() {
        testTokenize(Collections.singletonList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}"), Collections.singletonList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}"), false);
        testTokenize(Arrays.asList("{\"foo\": \"foofoo\"", ", \"bar\": \"barbar\"}"), Collections.singletonList("{\"foo\":\"foofoo\",\"bar\":\"barbar\"}"), false);
        testTokenize(Collections.singletonList(("[" + ("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]"))), Collections.singletonList(("[" + ("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]"))), false);
        testTokenize(Collections.singletonList("[{\"foo\": \"bar\"},{\"foo\": \"baz\"}]"), Collections.singletonList("[{\"foo\": \"bar\"},{\"foo\": \"baz\"}]"), false);
        testTokenize(Arrays.asList(("[" + "{\"foo\": \"foofoo\", \"bar\""), (": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]")), Collections.singletonList(("[" + ("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]"))), false);
        testTokenize(Arrays.asList("[", "{\"id\":1,\"name\":\"Robert\"}", ",", "{\"id\":2,\"name\":\"Raide\"}", ",", "{\"id\":3,\"name\":\"Ford\"}", "]"), Collections.singletonList(("[" + (("{\"id\":1,\"name\":\"Robert\"}," + "{\"id\":2,\"name\":\"Raide\"},") + "{\"id\":3,\"name\":\"Ford\"}]"))), false);
        // SPR-16166: top-level JSON values
        testTokenize(Arrays.asList("\"foo", "bar\""), Collections.singletonList("\"foobar\""), false);
        testTokenize(Arrays.asList("12", "34"), Collections.singletonList("1234"), false);
        testTokenize(Arrays.asList("12.", "34"), Collections.singletonList("12.34"), false);
        // note that we do not test for null, true, or false, which are also valid top-level values,
        // but are unsupported by JSONassert
    }

    @Test
    public void tokenizeArrayElements() {
        testTokenize(Collections.singletonList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}"), Collections.singletonList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}"), true);
        testTokenize(Arrays.asList("{\"foo\": \"foofoo\"", ", \"bar\": \"barbar\"}"), Collections.singletonList("{\"foo\":\"foofoo\",\"bar\":\"barbar\"}"), true);
        testTokenize(Collections.singletonList(("[" + ("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]"))), Arrays.asList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}", "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}"), true);
        testTokenize(Collections.singletonList("[{\"foo\": \"bar\"},{\"foo\": \"baz\"}]"), Arrays.asList("{\"foo\": \"bar\"}", "{\"foo\": \"baz\"}"), true);
        // SPR-15803: nested array
        testTokenize(Collections.singletonList(("[" + ((("{\"id\":\"0\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]}," + "{\"id\":\"1\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]},") + "{\"id\":\"2\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]}") + "]"))), Arrays.asList("{\"id\":\"0\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]}", "{\"id\":\"1\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]}", "{\"id\":\"2\",\"start\":[-999999999,1,1],\"end\":[999999999,12,31]}"), true);
        // SPR-15803: nested array, no top-level array
        testTokenize(Collections.singletonList("{\"speakerIds\":[\"tastapod\"],\"language\":\"ENGLISH\"}"), Collections.singletonList("{\"speakerIds\":[\"tastapod\"],\"language\":\"ENGLISH\"}"), true);
        testTokenize(Arrays.asList(("[" + "{\"foo\": \"foofoo\", \"bar\""), (": \"barbar\"}," + "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}]")), Arrays.asList("{\"foo\": \"foofoo\", \"bar\": \"barbar\"}", "{\"foo\": \"foofoofoo\", \"bar\": \"barbarbar\"}"), true);
        testTokenize(Arrays.asList("[", "{\"id\":1,\"name\":\"Robert\"}", ",", "{\"id\":2,\"name\":\"Raide\"}", ",", "{\"id\":3,\"name\":\"Ford\"}", "]"), Arrays.asList("{\"id\":1,\"name\":\"Robert\"}", "{\"id\":2,\"name\":\"Raide\"}", "{\"id\":3,\"name\":\"Ford\"}"), true);
        // SPR-16166: top-level JSON values
        testTokenize(Arrays.asList("\"foo", "bar\""), Collections.singletonList("\"foobar\""), true);
        testTokenize(Arrays.asList("12", "34"), Collections.singletonList("1234"), true);
        testTokenize(Arrays.asList("12.", "34"), Collections.singletonList("12.34"), true);
        // SPR-16407
        testTokenize(Arrays.asList("[1", ",2,", "3]"), Arrays.asList("1", "2", "3"), true);
    }

    @Test
    public void errorInStream() {
        DataBuffer buffer = stringBuffer("{\"id\":1,\"name\":");
        Flux<DataBuffer> source = Flux.just(buffer).concatWith(Flux.error(new RuntimeException()));
        Flux<TokenBuffer> result = Jackson2Tokenizer.tokenize(source, this.jsonFactory, this.objectMapper.getDeserializationContext(), true);
        StepVerifier.create(result).expectError(RuntimeException.class).verify();
    }

    // SPR-16521
    @Test
    public void jsonEOFExceptionIsWrappedAsDecodingError() {
        Flux<DataBuffer> source = Flux.just(stringBuffer("{\"status\": \"noClosingQuote}"));
        Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize(source, this.jsonFactory, this.objectMapper.getDeserializationContext(), false);
        StepVerifier.create(tokens).expectError(DecodingException.class).verify();
    }

    private static class JSONAssertConsumer implements Consumer<String> {
        private final String expected;

        JSONAssertConsumer(String expected) {
            this.expected = expected;
        }

        @Override
        public void accept(String s) {
            try {
                JSONAssert.assertEquals(this.expected, s, true);
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

