/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.serialization;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * StringSerializerTest
 */
public class StringSerializerTest {
    @Test(expected = NullPointerException.class)
    public void testSerializeThrowsOnNull() {
        new StringSerializer().serialize(null);
    }

    @Test(expected = NullPointerException.class)
    public void testReadThrowsOnNull() throws ClassNotFoundException {
        new StringSerializer().read(null);
    }

    @Test
    public void testSimpleString() throws ClassNotFoundException {
        StringSerializerTest.testString("eins");
    }

    @Test
    public void testAllCharacters() throws ClassNotFoundException {
        char c = Character.MIN_VALUE;
        do {
            StringSerializerTest.testString(String.valueOf((c++)));
        } while (c != (Character.MIN_VALUE) );
    }

    @Test
    public void testBackwardsCompatibility() throws UnsupportedEncodingException, ClassNotFoundException {
        StringSerializer serializer = new StringSerializer();
        int codepoint = 65536;
        do {
            if ((Character.isValidCodePoint(codepoint)) && (!((Character.isHighSurrogate(((char) (codepoint)))) || (Character.isLowSurrogate(((char) (codepoint))))))) {
                String s = new String(Character.toChars(codepoint));
                ByteBuffer bytes = ByteBuffer.wrap(s.getBytes("UTF-8"));
                Assert.assertThat(("Codepoint : 0x" + (Integer.toHexString(codepoint))), serializer.read(bytes), Matchers.is(s));
                Assert.assertThat(("Codepoint : 0x" + (Integer.toHexString(codepoint))), serializer.equals(s, bytes), Matchers.is(true));
            }
        } while ((++codepoint) != (Integer.MIN_VALUE) );
    }

    @Test
    public void testEqualsMismatchOnMissingFinalSurrogateAgainstOldFormat() throws UnsupportedEncodingException, ClassNotFoundException {
        StringSerializer serializer = new StringSerializer();
        String string = "?????";
        String trimmed = string.substring(0, ((string.length()) - 1));
        ByteBuffer bytes = ByteBuffer.wrap(string.getBytes("UTF-8"));
        Assert.assertThat(serializer.equals(trimmed, bytes), Matchers.is(false));
    }
}

