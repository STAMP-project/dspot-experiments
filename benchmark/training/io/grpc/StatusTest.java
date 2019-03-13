/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc;


import Status.CANCELLED;
import Status.MESSAGE_KEY;
import Status.UNKNOWN;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Status}.
 */
@RunWith(JUnit4.class)
public class StatusTest {
    private final Charset ascii = Charset.forName("US-ASCII");

    @Test
    public void verifyExceptionMessage() {
        Assert.assertEquals("UNKNOWN", UNKNOWN.asRuntimeException().getMessage());
        Assert.assertEquals("CANCELLED: This is a test", CANCELLED.withDescription("This is a test").asRuntimeException().getMessage());
        Assert.assertEquals("UNKNOWN", UNKNOWN.asException().getMessage());
        Assert.assertEquals("CANCELLED: This is a test", CANCELLED.withDescription("This is a test").asException().getMessage());
    }

    @Test
    public void impossibleCodeValue() {
        Assert.assertEquals(Code.UNKNOWN, Status.fromCodeValue((-1)).getCode());
    }

    @Test
    public void sameCauseReturnsSelf() {
        Assert.assertSame(CANCELLED, CANCELLED.withCause(null));
    }

    @Test
    public void sameDescriptionReturnsSelf() {
        Assert.assertSame(CANCELLED, CANCELLED.withDescription(null));
        Assert.assertSame(CANCELLED, CANCELLED.augmentDescription(null));
    }

    @Test
    public void useObjectHashCode() {
        Assert.assertEquals(CANCELLED.hashCode(), System.identityHashCode(CANCELLED));
    }

    @Test
    public void metadataEncode_lowAscii() {
        byte[] b = MESSAGE_KEY.toBytes("my favorite character is \u0000");
        Assert.assertEquals("my favorite character is %00", new String(b, ascii));
    }

    @Test
    public void metadataEncode_percent() {
        byte[] b = MESSAGE_KEY.toBytes("my favorite character is %");
        Assert.assertEquals("my favorite character is %25", new String(b, ascii));
    }

    @Test
    public void metadataEncode_surrogatePair() {
        byte[] b = MESSAGE_KEY.toBytes("my favorite character is ?");
        Assert.assertEquals("my favorite character is %F0%90%80%81", new String(b, ascii));
    }

    @Test
    public void metadataEncode_unmatchedHighSurrogate() {
        byte[] b = MESSAGE_KEY.toBytes(("my favorite character is " + ((char) (55297))));
        Assert.assertEquals("my favorite character is ?", new String(b, ascii));
    }

    @Test
    public void metadataEncode_unmatchedLowSurrogate() {
        byte[] b = MESSAGE_KEY.toBytes(("my favorite character is " + ((char) (56375))));
        Assert.assertEquals("my favorite character is ?", new String(b, ascii));
    }

    @Test
    public void metadataEncode_maxSurrogatePair() {
        byte[] b = MESSAGE_KEY.toBytes((("my favorite character is " + ((char) (56319))) + ((char) (57343))));
        Assert.assertEquals("my favorite character is %F4%8F%BF%BF", new String(b, ascii));
    }

    @Test
    public void metadataDecode_ascii() {
        String s = MESSAGE_KEY.parseBytes(new byte[]{ 'H', 'e', 'l', 'l', 'o' });
        Assert.assertEquals("Hello", s);
    }

    @Test
    public void metadataDecode_percent() {
        String s = MESSAGE_KEY.parseBytes(new byte[]{ 'H', '%', '6', '1', 'o' });
        Assert.assertEquals("Hao", s);
    }

    @Test
    public void metadataDecode_percentUnderflow() {
        String s = MESSAGE_KEY.parseBytes(new byte[]{ 'H', '%', '6' });
        Assert.assertEquals("H%6", s);
    }

    @Test
    public void metadataDecode_surrogate() {
        String s = MESSAGE_KEY.parseBytes(new byte[]{ '%', 'F', '0', '%', '9', '0', '%', '8', '0', '%', '8', '1' });
        Assert.assertEquals("?", s);
    }

    @Test
    public void metadataDecode_badEncoding() {
        String s = MESSAGE_KEY.parseBytes(new byte[]{ '%', 'G', '0' });
        Assert.assertEquals("%G0", s);
    }
}

