/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.twitter;


import StreamingType.FILTER;
import StreamingType.SAMPLE;
import StreamingType.USER;
import TimelineType.HOME;
import TimelineType.MENTIONS;
import TimelineType.RETWEETSOFME;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.twitter.directmessage.TwitterDirectMessageEndpoint;
import org.apache.camel.component.twitter.search.TwitterSearchEndpoint;
import org.apache.camel.component.twitter.streaming.TwitterStreamingEndpoint;
import org.apache.camel.component.twitter.timeline.TwitterTimelineEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


public class UriConfigurationTest extends Assert {
    private CamelContext context = new DefaultCamelContext();

    private CamelTwitterTestSupport support = new CamelTwitterTestSupport();

    @Test
    public void testBasicAuthentication() throws Exception {
        Endpoint endpoint = context.getEndpoint(("twitter-search:foo?" + (support.getUriTokens())));
        Assert.assertTrue(("Endpoint not a TwitterSearchEndpoint: " + endpoint), (endpoint instanceof TwitterSearchEndpoint));
        TwitterSearchEndpoint twitterEndpoint = ((TwitterSearchEndpoint) (endpoint));
        Assert.assertTrue((!(twitterEndpoint.getProperties().getConsumerKey().isEmpty())));
        Assert.assertTrue((!(twitterEndpoint.getProperties().getConsumerSecret().isEmpty())));
        Assert.assertTrue((!(twitterEndpoint.getProperties().getAccessToken().isEmpty())));
        Assert.assertTrue((!(twitterEndpoint.getProperties().getAccessTokenSecret().isEmpty())));
    }

    @Test
    public void testPageSetting() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-search:foo?count=50&numberOfPages=2");
        Assert.assertTrue(("Endpoint not a TwitterSearchEndpoint: " + endpoint), (endpoint instanceof TwitterSearchEndpoint));
        TwitterSearchEndpoint twitterEndpoint = ((TwitterSearchEndpoint) (endpoint));
        Assert.assertEquals(new Integer(50), twitterEndpoint.getProperties().getCount());
        Assert.assertEquals(new Integer(2), twitterEndpoint.getProperties().getNumberOfPages());
    }

    @Test
    public void testHttpProxySetting() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-search:foo?httpProxyHost=example.com&httpProxyPort=3338&httpProxyUser=test&httpProxyPassword=pwd");
        Assert.assertTrue(("Endpoint not a TwitterSearchEndpoint: " + endpoint), (endpoint instanceof TwitterSearchEndpoint));
        TwitterSearchEndpoint twitterEndpoint = ((TwitterSearchEndpoint) (endpoint));
        Assert.assertEquals("example.com", twitterEndpoint.getProperties().getHttpProxyHost());
        Assert.assertEquals(Integer.valueOf(3338), twitterEndpoint.getProperties().getHttpProxyPort());
        Assert.assertEquals("test", twitterEndpoint.getProperties().getHttpProxyUser());
        Assert.assertEquals("pwd", twitterEndpoint.getProperties().getHttpProxyPassword());
    }

    @Test
    public void testDirectMessageEndpoint() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-directmessage:foo");
        Assert.assertTrue(("Endpoint not a TwitterDirectMessageEndpoint: " + endpoint), (endpoint instanceof TwitterDirectMessageEndpoint));
    }

    @Test
    public void testSearchEndpoint() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-search:foo");
        Assert.assertTrue(("Endpoint not a TwitterSearchEndpoint: " + endpoint), (endpoint instanceof TwitterSearchEndpoint));
    }

    @Test
    public void testStreamingEndpoint() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-streaming:filter");
        Assert.assertTrue(("Endpoint not a TwitterStreamingEndpoint: " + endpoint), (endpoint instanceof TwitterStreamingEndpoint));
        TwitterStreamingEndpoint streamingEndpoint = ((TwitterStreamingEndpoint) (endpoint));
        Assert.assertEquals(FILTER, streamingEndpoint.getStreamingType());
        endpoint = context.getEndpoint("twitter-streaming:sample");
        Assert.assertTrue(("Endpoint not a TwitterStreamingEndpoint: " + endpoint), (endpoint instanceof TwitterStreamingEndpoint));
        streamingEndpoint = ((TwitterStreamingEndpoint) (endpoint));
        Assert.assertEquals(SAMPLE, streamingEndpoint.getStreamingType());
        endpoint = context.getEndpoint("twitter-streaming:user");
        Assert.assertTrue(("Endpoint not a TwitterStreamingEndpoint: " + endpoint), (endpoint instanceof TwitterStreamingEndpoint));
        streamingEndpoint = ((TwitterStreamingEndpoint) (endpoint));
        Assert.assertEquals(USER, streamingEndpoint.getStreamingType());
    }

    @Test
    public void testTimelineEndpoint() throws Exception {
        Endpoint endpoint = context.getEndpoint("twitter-timeline:home");
        Assert.assertTrue(("Endpoint not a TwitterTimelineEndpoint: " + endpoint), (endpoint instanceof TwitterTimelineEndpoint));
        TwitterTimelineEndpoint timelineEndpoint = ((TwitterTimelineEndpoint) (endpoint));
        Assert.assertEquals(HOME, timelineEndpoint.getTimelineType());
        endpoint = context.getEndpoint("twitter-timeline:mentions");
        Assert.assertTrue(("Endpoint not a TwitterTimelineEndpoint: " + endpoint), (endpoint instanceof TwitterTimelineEndpoint));
        timelineEndpoint = ((TwitterTimelineEndpoint) (endpoint));
        Assert.assertEquals(MENTIONS, timelineEndpoint.getTimelineType());
        endpoint = context.getEndpoint("twitter-timeline:retweetsofme");
        Assert.assertTrue(("Endpoint not a TwitterTimelineEndpoint: " + endpoint), (endpoint instanceof TwitterTimelineEndpoint));
        timelineEndpoint = ((TwitterTimelineEndpoint) (endpoint));
        Assert.assertEquals(RETWEETSOFME, timelineEndpoint.getTimelineType());
        endpoint = context.getEndpoint("twitter-timeline:user");
        Assert.assertTrue(("Endpoint not a TwitterTimelineEndpoint: " + endpoint), (endpoint instanceof TwitterTimelineEndpoint));
        timelineEndpoint = ((TwitterTimelineEndpoint) (endpoint));
        Assert.assertEquals(TimelineType.USER, timelineEndpoint.getTimelineType());
    }
}

