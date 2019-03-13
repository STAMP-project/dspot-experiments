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


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class CharSequenceEncoderTests extends AbstractEncoderTestCase<CharSequenceEncoder> {
    private final String foo = "foo";

    private final String bar = "bar";

    public CharSequenceEncoderTests() {
        super(CharSequenceEncoder.textPlainOnly());
    }

    @Test
    public void calculateCapacity() {
        String sequence = "Hello World!";
        Stream.of(StandardCharsets.UTF_8, StandardCharsets.UTF_16, StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, Charset.forName("BIG5")).forEach(( charset) -> {
            int capacity = this.encoder.calculateCapacity(sequence, charset);
            int length = sequence.length();
            Assert.assertTrue(String.format("%s has capacity %d; length %d", charset, capacity, length), (capacity >= length));
        });
    }
}

