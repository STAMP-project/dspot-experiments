/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.core.codec;


import MimeTypeUtils.APPLICATION_JSON;
import MimeTypeUtils.TEXT_HTML;
import MimeTypeUtils.TEXT_PLAIN;
import StringDecoder.DEFAULT_DELIMITERS;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link StringDecoder}.
 *
 * @author Sebastien Deleuze
 * @author Brian Clozel
 * @author Mark Paluch
 */
public class StringDecoderTests extends AbstractDecoderTestCase<StringDecoder> {
    private static final ResolvableType TYPE = ResolvableType.forClass(String.class);

    public StringDecoderTests() {
        super(StringDecoder.allMimeTypes());
    }

    @Override
    @Test
    public void canDecode() {
        Assert.assertTrue(this.decoder.canDecode(StringDecoderTests.TYPE, TEXT_PLAIN));
        Assert.assertTrue(this.decoder.canDecode(StringDecoderTests.TYPE, TEXT_HTML));
        Assert.assertTrue(this.decoder.canDecode(StringDecoderTests.TYPE, APPLICATION_JSON));
        Assert.assertTrue(this.decoder.canDecode(StringDecoderTests.TYPE, MimeTypeUtils.parseMimeType("text/plain;charset=utf-8")));
        Assert.assertFalse(this.decoder.canDecode(ResolvableType.forClass(Integer.class), TEXT_PLAIN));
        Assert.assertFalse(this.decoder.canDecode(ResolvableType.forClass(Object.class), APPLICATION_JSON));
    }

    @Override
    @Test
    public void decode() {
        String u = "?";
        String e = "?";
        String o = "?";
        String s = String.format("%s\n%s\n%s", u, e, o);
        Flux<DataBuffer> input = toDataBuffers(s, 1, StandardCharsets.UTF_8);
        testDecodeAll(input, ResolvableType.forClass(String.class), ( step) -> step.expectNext(u, e, o).verifyComplete(), null, null);
    }

    @Test
    public void decodeMultibyteCharacterUtf16() {
        String u = "?";
        String e = "?";
        String o = "?";
        String s = String.format("%s\n%s\n%s", u, e, o);
        Flux<DataBuffer> source = toDataBuffers(s, 2, StandardCharsets.UTF_16BE);
        MimeType mimeType = MimeTypeUtils.parseMimeType("text/plain;charset=utf-16be");
        testDecode(source, StringDecoderTests.TYPE, ( step) -> step.expectNext(u, e, o).verifyComplete(), mimeType, null);
    }

    @Test
    public void decodeNewLine() {
        Flux<DataBuffer> input = Flux.just(stringBuffer("\r\nabc\n"), stringBuffer("def"), stringBuffer("ghi\r\n\n"), stringBuffer("jkl"), stringBuffer("mno\npqr\n"), stringBuffer("stu"), stringBuffer("vw"), stringBuffer("xyz"));
        testDecode(input, String.class, ( step) -> step.expectNext("").expectNext("abc").expectNext("defghi").expectNext("").expectNext("jklmno").expectNext("pqr").expectNext("stuvwxyz").expectComplete().verify());
    }

    @Test
    public void decodeNewLineIncludeDelimiters() {
        this.decoder = StringDecoder.allMimeTypes(DEFAULT_DELIMITERS, false);
        Flux<DataBuffer> input = Flux.just(stringBuffer("\r\nabc\n"), stringBuffer("def"), stringBuffer("ghi\r\n\n"), stringBuffer("jkl"), stringBuffer("mno\npqr\n"), stringBuffer("stu"), stringBuffer("vw"), stringBuffer("xyz"));
        testDecode(input, String.class, ( step) -> step.expectNext("\r\n").expectNext("abc\n").expectNext("defghi\r\n").expectNext("\n").expectNext("jklmno\n").expectNext("pqr\n").expectNext("stuvwxyz").expectComplete().verify());
    }

    @Test
    public void decodeEmptyFlux() {
        Flux<DataBuffer> input = Flux.empty();
        testDecode(input, String.class, ( step) -> step.expectComplete().verify());
    }

    @Test
    public void decodeEmptyDataBuffer() {
        Flux<DataBuffer> input = Flux.just(stringBuffer(""));
        Flux<String> output = this.decoder.decode(input, StringDecoderTests.TYPE, null, Collections.emptyMap());
        StepVerifier.create(output).expectNext("").expectComplete().verify();
    }

    @Override
    @Test
    public void decodeToMono() {
        Flux<DataBuffer> input = Flux.just(stringBuffer("foo"), stringBuffer("bar"), stringBuffer("baz"));
        testDecodeToMonoAll(input, String.class, ( step) -> step.expectNext("foobarbaz").expectComplete().verify());
    }

    @Test
    public void decodeToMonoWithEmptyFlux() {
        Flux<DataBuffer> input = Flux.empty();
        testDecodeToMono(input, String.class, ( step) -> step.expectComplete().verify());
    }
}

