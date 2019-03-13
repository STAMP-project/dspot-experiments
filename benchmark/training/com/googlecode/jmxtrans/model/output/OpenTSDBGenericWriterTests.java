/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.googlecode.jmxtrans.exceptions.LifecycleException;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.ServerFixtures;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link OpenTSDBGenericWriter}.
 */
public class OpenTSDBGenericWriterTests {
    protected Query mockQuery;

    protected Result mockResult;

    // Interactions with the custom, test subclass of OpenTSDBGenericWriter.
    protected boolean tvAddHostnameTagDefault;

    protected boolean prepareSenderCalled;

    protected boolean shutdownSenderCalled;

    protected boolean startOutputCalled;

    protected boolean finishOutputCalled;

    protected List<String> tvMetricLinesSent;

    @Test
    public void testEmptyTagSetting() throws Exception {
        Map<String, String> tagMap = Maps.newHashMap();
        Map<String, Object> settings = Maps.newHashMap();
        settings.put("tags", tagMap);
        OpenTSDBGenericWriter writer = new OpenTSDBGenericWriterTests.TestOpenTSDBGenericWriter(ImmutableList.<String>of(), false, settings);
        Mockito.when(this.mockResult.getAttributeName()).thenReturn("X-ATT-X");
        Mockito.when(this.mockResult.getValue()).thenReturn("120021");
        // Verify empty tag map.
        Assertions.assertThat(writer.getTypeNames()).isEmpty();
        writer.start();
        writer.doWrite(ServerFixtures.dummyServer(), this.mockQuery, ImmutableList.of(this.mockResult));
        writer.close();
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches("^X-DOMAIN.PKG.CLASS-X\\.X-ATT-X 0 120021 host=[^ ]*$"));
    }

    @Test
    public void testTagSetting() throws Exception {
        Map<String, String> tagMap;
        // Verify tag map with multiple values.
        tagMap = Maps.newHashMap();
        tagMap.put("x-tag1-x", "x-tag1val-x");
        tagMap.put("x-tag2-x", "x-tag2val-x");
        tagMap.put("x-tag3-x", "x-tag3val-x");
        OpenTSDBGenericWriter writer = createWriter("tags", tagMap);
        writer.start();
        writer.doWrite(ServerFixtures.dummyServer(), this.mockQuery, ImmutableList.of(this.mockResult));
        writer.close();
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches("^X-DOMAIN.PKG.CLASS-X\\.X-ATT-X 0 120021.*"));
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches(".*\\bhost=.*"));
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches(".*\\bx-tag1-x=x-tag1val-x\\b.*"));
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches(".*\\bx-tag2-x=x-tag2val-x\\b.*"));
        Assert.assertTrue(this.tvMetricLinesSent.get(0).matches(".*\\bx-tag3-x=x-tag3val-x\\b.*"));
    }

    @Test
    public void testValidateValidHostPort() throws Exception {
        OpenTSDBGenericWriter writer = createWriter(ImmutableMap.of("host", ((Object) ("localhost")), "port", 4242));
        writer.start();
        writer.validateSetup(null, this.mockQuery);
    }

    @Test
    public void testPortNumberAsString() throws Exception {
        OpenTSDBGenericWriter writer = createWriter(ImmutableMap.of("host", ((Object) ("localhost")), "port", "4242"));
        writer.start();
        writer.validateSetup(null, this.mockQuery);
    }

    @Test
    public void testHooksCalled() throws Exception {
        OpenTSDBGenericWriter writer = createWriter();
        writer.start();
        Assert.assertTrue(prepareSenderCalled);
        Assert.assertFalse(shutdownSenderCalled);
        Assert.assertFalse(startOutputCalled);
        Assert.assertFalse(finishOutputCalled);
        writer.doWrite(ServerFixtures.dummyServer(), this.mockQuery, ImmutableList.of(this.mockResult));
        Assert.assertTrue(prepareSenderCalled);
        Assert.assertFalse(shutdownSenderCalled);
        Assert.assertTrue(startOutputCalled);
        Assert.assertTrue(finishOutputCalled);
        writer.close();
        Assert.assertTrue(prepareSenderCalled);
        Assert.assertTrue(shutdownSenderCalled);
        Assert.assertTrue(startOutputCalled);
        Assert.assertTrue(finishOutputCalled);
    }

    @Test
    public void testDebugEnabled() throws Exception {
        OpenTSDBGenericWriter writer = createWriter(ImmutableMap.of("host", ((Object) ("localhost")), "port", 4242, "debug", true));
        writer.start();
        writer.validateSetup(null, this.mockQuery);
        writer.doWrite(ServerFixtures.dummyServer(), this.mockQuery, ImmutableList.of(this.mockResult));
    }

    private class TestOpenTSDBGenericWriter extends OpenTSDBGenericWriter {
        public TestOpenTSDBGenericWriter(@JsonProperty("typeNames")
        ImmutableList<String> typeNames, @JsonProperty("debug")
        Boolean debugEnabled, @JsonProperty("settings")
        Map<String, Object> settings) throws LifecycleException {
            super(typeNames, false, debugEnabled, "localhost", 1234, null, null, null, null, true, settings);
        }

        protected void prepareSender() throws LifecycleException {
            OpenTSDBGenericWriterTests.this.prepareSenderCalled = true;
        }

        protected void shutdownSender() throws LifecycleException {
            OpenTSDBGenericWriterTests.this.shutdownSenderCalled = true;
        }

        protected void startOutput() throws IOException {
            OpenTSDBGenericWriterTests.this.startOutputCalled = true;
        }

        protected void finishOutput() throws IOException {
            OpenTSDBGenericWriterTests.this.finishOutputCalled = true;
        }

        protected boolean getAddHostnameTagDefault() {
            return tvAddHostnameTagDefault;
        }

        protected void sendOutput(String metricLine) {
            OpenTSDBGenericWriterTests.this.tvMetricLinesSent.add(metricLine);
        }
    }
}

