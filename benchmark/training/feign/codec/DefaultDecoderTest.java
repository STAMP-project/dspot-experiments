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
package feign.codec;


import feign.Response;
import feign.Util;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;


public class DefaultDecoderTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Decoder decoder = new Decoder.Default();

    @Test
    public void testDecodesToString() throws Exception {
        Response response = knownResponse();
        Object decodedObject = decoder.decode(response, String.class);
        Assert.assertEquals(String.class, decodedObject.getClass());
        Assert.assertEquals("response body", decodedObject.toString());
    }

    @Test
    public void testDecodesToByteArray() throws Exception {
        Response response = knownResponse();
        Object decodedObject = decoder.decode(response, byte[].class);
        Assert.assertEquals(byte[].class, decodedObject.getClass());
        Assert.assertEquals("response body", new String(((byte[]) (decodedObject)), Util.UTF_8));
    }

    @Test
    public void testDecodesNullBodyToNull() throws Exception {
        Assert.assertNull(decoder.decode(nullBodyResponse(), Document.class));
    }

    @Test
    public void testRefusesToDecodeOtherTypes() throws Exception {
        thrown.expect(DecodeException.class);
        thrown.expectMessage(" is not a type supported by this decoder.");
        decoder.decode(knownResponse(), Document.class);
    }
}

