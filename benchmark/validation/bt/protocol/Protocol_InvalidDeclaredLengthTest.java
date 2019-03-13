/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.protocol;


import bt.test.protocol.ProtocolTest;
import org.junit.Test;

import static StandardBittorrentProtocol.CHOKE_ID;
import static StandardBittorrentProtocol.INTERESTED_ID;
import static StandardBittorrentProtocol.NOT_INTERESTED_ID;
import static StandardBittorrentProtocol.UNCHOKE_ID;


public class Protocol_InvalidDeclaredLengthTest {
    private static final ProtocolTest TEST = ProtocolTest.forBittorrentProtocol().build();

    private byte[] CHOKE_INVALID_DATA = new byte[]{ 0, 0, 0, 3, CHOKE_ID, 1, 2 };

    private byte[] UNCHOKE_INVALID_DATA = new byte[]{ 0, 0, 0, 2, UNCHOKE_ID, 1 };

    private byte[] INTERESTED_INVALID_DATA = new byte[]{ 0, 0, 0, 4, INTERESTED_ID, 1, 2, 3 };

    private byte[] NOT_INTERESTED_INVALID_DATA = new byte[]{ 0, 0, 0, 9, NOT_INTERESTED_ID, 1, 2, 3, 4, 5, 6, 7, 8 };

    @Test
    public void testProtocol_Choke_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Choke.class, 3, 0, CHOKE_INVALID_DATA);
    }

    @Test
    public void testProtocol_Unchoke_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Unchoke.class, 2, 0, UNCHOKE_INVALID_DATA);
    }

    @Test
    public void testProtocol_Interested_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Interested.class, 4, 0, INTERESTED_INVALID_DATA);
    }

    @Test
    public void testProtocol_NotInterested_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(NotInterested.class, 9, 0, NOT_INTERESTED_INVALID_DATA);
    }

    private byte[] HAVE_INVALID_DATA = new byte[]{ 0, 0, 0, 1, 4, 0, 0, 16, 127 };

    private byte[] REQUEST_INVALID_DATA = new byte[]{ 0, 0, 0, 12, 6, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 64, 0 };

    private byte[] CANCEL_INVALID_DATA = new byte[]{ 0, 0, 0, 3, 8, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 64, 0 };

    @Test
    public void testProtocol_Have_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Have.class, 1, 4, HAVE_INVALID_DATA);
    }

    @Test
    public void testProtocol_Request_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Request.class, 12, 12, REQUEST_INVALID_DATA);
    }

    @Test
    public void testProtocol_Cancel_InvalidLength() throws Exception {
        Protocol_InvalidDeclaredLengthTest.assertHasInvalidLength(Cancel.class, 3, 12, CANCEL_INVALID_DATA);
    }
}

