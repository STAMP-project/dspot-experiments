/**
 * Copyright (c) 2003-present, Jodd Team (http://jodd.org)
 */
/**
 * All rights reserved.
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are met:
 */
/**
 *
 */
/**
 * 1. Redistributions of source code must retain the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer.
 */
/**
 *
 */
/**
 * 2. Redistributions in binary form must reproduce the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer in the
 */
/**
 * documentation and/or other materials provided with the distribution.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 */
/**
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 */
/**
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 */
/**
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 */
/**
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 */
/**
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 */
/**
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 */
/**
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 */
/**
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 */
/**
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 */
/**
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jodd.json;


import java.util.function.Consumer;
import jodd.json.fixtures.JsonParser;
import jodd.json.fixtures.JsonParsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NansInfTest {
    @Test
    public void testDouble_NaN() {
        JsonParsers.forEachParser(( jsonParser) -> {
            String json = JsonSerializer.create().serialize(Double.NaN);
            Assertions.assertEquals("\"NaN\"", json);
            Double d = jsonParser.parse(json, Double.class);
            Assertions.assertTrue(d.isNaN());
        });
    }

    @Test
    public void testFloat_NaN() {
        JsonParsers.forEachParser(( jsonParser) -> {
            String json = JsonSerializer.create().serialize(Float.NaN);
            Assertions.assertEquals("\"NaN\"", json);
            Float d = jsonParser.parse(json, Float.class);
            Assertions.assertTrue(d.isNaN());
        });
    }

    @Test
    public void testDouble_Infinity() {
        JsonParsers.forEachParser(( jsonParser) -> {
            String json = JsonSerializer.create().serialize(Double.POSITIVE_INFINITY);
            Assertions.assertEquals("\"+Infinity\"", json);
            Double d = jsonParser.parse(json, Double.class);
            Assertions.assertEquals(Double.POSITIVE_INFINITY, d.doubleValue());
            json = JsonSerializer.create().serialize(Double.NEGATIVE_INFINITY);
            Assertions.assertEquals("\"-Infinity\"", json);
            d = jsonParser.parse(json, Double.class);
            Assertions.assertEquals(Double.NEGATIVE_INFINITY, d.doubleValue());
        });
    }

    @Test
    public void testFloat_Infinity() {
        JsonParsers.forEachParser(( jsonParser) -> {
            String json = JsonSerializer.create().serialize(Float.POSITIVE_INFINITY);
            Assertions.assertEquals("\"+Infinity\"", json);
            Float d = jsonParser.parse(json, Float.class);
            Assertions.assertEquals(Float.POSITIVE_INFINITY, d.floatValue());
            json = JsonSerializer.create().serialize(Float.NEGATIVE_INFINITY);
            Assertions.assertEquals("\"-Infinity\"", json);
            d = jsonParser.parse(json, Float.class);
            Assertions.assertEquals(Float.NEGATIVE_INFINITY, d.floatValue());
        });
    }
}

