/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws;


import Marker.USER_AGENT;
import com.amazonaws.RequestClientOptions.Marker;
import org.junit.Assert;
import org.junit.Test;


public class RequestClientOptionsTest {
    @Test
    public void test() {
        RequestClientOptions opts = new RequestClientOptions();
        for (Marker m : Marker.values()) {
            Assert.assertNull(opts.getClientMarker(m));
        }
        for (Marker m : Marker.values()) {
            opts.putClientMarker(m, m.name());
        }
        for (Marker m : Marker.values()) {
            Assert.assertEquals(m.name(), opts.getClientMarker(m));
        }
        Assert.assertEquals(USER_AGENT.name(), opts.getClientMarker(USER_AGENT));
        opts.appendUserAgent("2nd-agent");
        String ua = opts.getClientMarker(USER_AGENT);
        Assert.assertEquals(ua, ((USER_AGENT.name()) + " 2nd-agent"), ua);
        opts.appendUserAgent("3rd-agent");
        ua = opts.getClientMarker(USER_AGENT);
        Assert.assertEquals(ua, ((USER_AGENT.name()) + " 2nd-agent 3rd-agent"), ua);
        Assert.assertEquals(ua, opts.getClientMarker(USER_AGENT));
    }

    @Test
    public void copyTo() {
        RequestClientOptions from = new RequestClientOptions();
        from.appendUserAgent("useragent");
        from.setReadLimit(1234);
        Assert.assertNotNull(from.getClientMarker(USER_AGENT));
        Assert.assertTrue((1234 == (from.getReadLimit())));
        // Before copy
        RequestClientOptions to = new RequestClientOptions();
        Assert.assertNull(to.getClientMarker(USER_AGENT));
        Assert.assertFalse((1234 == (to.getReadLimit())));
        // After copy
        from.copyTo(to);
        Assert.assertEquals(from.getClientMarker(USER_AGENT), to.getClientMarker(USER_AGENT));
        Assert.assertTrue((1234 == (to.getReadLimit())));
    }
}

