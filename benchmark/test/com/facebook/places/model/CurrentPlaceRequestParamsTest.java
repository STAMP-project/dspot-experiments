/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.places.model;


import CurrentPlaceRequestParams.Builder;
import CurrentPlaceRequestParams.ConfidenceLevel.MEDIUM;
import CurrentPlaceRequestParams.ScanMode.LOW_LATENCY;
import android.location.Location;
import com.facebook.FacebookTestCase;
import org.junit.Assert;
import org.junit.Test;


public class CurrentPlaceRequestParamsTest extends FacebookTestCase {
    @Test
    public void testBuilder() {
        Location location = new Location("dummy");
        location.setLatitude(1.0);
        location.setLongitude(2.0);
        CurrentPlaceRequestParams.Builder builder = new CurrentPlaceRequestParams.Builder();
        builder.setMinConfidenceLevel(MEDIUM);
        builder.setLimit(22);
        builder.setScanMode(LOW_LATENCY);
        builder.setLocation(location);
        builder.addField("field1");
        builder.addField("field2");
        CurrentPlaceRequestParams params = builder.build();
        Assert.assertEquals(22, params.getLimit());
        Assert.assertEquals(location, params.getLocation());
        Assert.assertEquals(MEDIUM, params.getMinConfidenceLevel());
        Assert.assertEquals(LOW_LATENCY, params.getScanMode());
        assertSetEqual(new String[]{ "field1", "field2" }, params.getFields());
    }
}

