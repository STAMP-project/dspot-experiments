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
package bt.tracker.http;


import bt.tracker.TrackerResponse;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class HttpResponseHandlerTest {
    private HttpResponseHandler responseHandler;

    private Charset defaultCharset = Charset.forName("ISO-8859-1");

    @Test
    public void handleResponse_Success1() throws Exception {
        TrackerResponse trackerResponse = responseHandler.handleResponse(HttpResponseHandlerTest.class.getResourceAsStream("tracker_response_success1"), defaultCharset);
        Assert.assertNotNull(trackerResponse);
        Assert.assertTrue(trackerResponse.isSuccess());
        Assert.assertEquals(3591, trackerResponse.getInterval());
        Assert.assertEquals(3591, trackerResponse.getMinInterval());
        assertHasPeers(trackerResponse.getPeers().iterator(), createPeer(null, new byte[]{ 77, ((byte) (245)), 113, 10 }, 13933), createPeer(null, new byte[]{ 37, 17, 49, ((byte) (228)) }, 6881), createPeer(null, new byte[]{ ((byte) (178)), ((byte) (215)), 98, 37 }, 16881), createPeer(null, new byte[]{ 46, ((byte) (167)), 127, 46 }, 44264), createPeer(null, new byte[]{ 91, ((byte) (247)), ((byte) (233)), ((byte) (253)) }, 6881), createPeer(null, new byte[]{ ((byte) (176)), 117, ((byte) (218)), ((byte) (136)) }, 39638), createPeer(null, new byte[]{ 37, ((byte) (190)), ((byte) (197)), ((byte) (178)) }, 62354), createPeer(null, new byte[]{ 77, 105, ((byte) (190)), 5 }, 15418), createPeer(null, new byte[]{ ((byte) (176)), 99, 67, 43 }, 28391));
    }

    @Test
    public void handleResponse_Failure1() {
        TrackerResponse trackerResponse = responseHandler.handleResponse(HttpResponseHandlerTest.class.getResourceAsStream("tracker_response_failure1"), defaultCharset);
        Assert.assertNotNull(trackerResponse);
        Assert.assertFalse(trackerResponse.isSuccess());
        Assert.assertFalse(trackerResponse.getError().isPresent());
        Assert.assertEquals("Invalid info_hash (0 - )", trackerResponse.getErrorMessage());
    }
}

