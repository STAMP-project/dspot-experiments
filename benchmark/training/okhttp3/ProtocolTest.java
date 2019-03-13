/**
 * Copyright (C) 2018 Square, Inc.
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
package okhttp3;


import Protocol.H2_PRIOR_KNOWLEDGE;
import Protocol.HTTP_1_0;
import Protocol.HTTP_1_1;
import Protocol.HTTP_2;
import Protocol.QUIC;
import Protocol.SPDY_3;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ProtocolTest {
    @Test
    public void testGetKnown() throws IOException {
        Assert.assertEquals(HTTP_1_0, Protocol.get("http/1.0"));
        Assert.assertEquals(HTTP_1_1, Protocol.get("http/1.1"));
        Assert.assertEquals(SPDY_3, Protocol.get("spdy/3.1"));
        Assert.assertEquals(HTTP_2, Protocol.get("h2"));
        Assert.assertEquals(H2_PRIOR_KNOWLEDGE, Protocol.get("h2_prior_knowledge"));
        Assert.assertEquals(QUIC, Protocol.get("quic"));
    }

    @Test(expected = IOException.class)
    public void testGetUnknown() throws IOException {
        Protocol.get("tcp");
    }

    @Test
    public void testToString() throws IOException {
        Assert.assertEquals("http/1.0", HTTP_1_0.toString());
        Assert.assertEquals("http/1.1", HTTP_1_1.toString());
        Assert.assertEquals("spdy/3.1", SPDY_3.toString());
        Assert.assertEquals("h2", HTTP_2.toString());
        Assert.assertEquals("h2_prior_knowledge", H2_PRIOR_KNOWLEDGE.toString());
        Assert.assertEquals("quic", QUIC.toString());
    }
}

