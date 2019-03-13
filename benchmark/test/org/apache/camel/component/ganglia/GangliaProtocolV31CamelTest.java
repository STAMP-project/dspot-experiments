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
package org.apache.camel.component.ganglia;


import info.ganglia.gmetric4j.gmetric.GMetricSlope;
import info.ganglia.gmetric4j.gmetric.GMetricType;
import info.ganglia.gmetric4j.xdr.v31x.Ganglia_metadata_msg;
import info.ganglia.gmetric4j.xdr.v31x.Ganglia_value_msg;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrBufferDecodingStream;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * {@code GangliaProtocolV31CamelTest} is not shipped with an embedded gmond
 * agent. The gmond agent is mocked with the help of camel-netty4 codecs and a
 * mock endpoint. As underlying UDP packets are not guaranteed to be delivered,
 * loose assertions are performed.
 */
public class GangliaProtocolV31CamelTest extends CamelGangliaTestSupport {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @EndpointInject(uri = "mock:gmond")
    private MockEndpoint mockGmond;

    @Test
    public void sendDefaultConfigurationShouldSucceed() throws Exception {
        mockGmond.setMinimumExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        mockGmond.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Ganglia_metadata_msg metadataMessage = exchange.getIn().getBody(Ganglia_metadata_msg.class);
                if (metadataMessage != null) {
                    assertEquals(GangliaConfiguration.DEFAULT_METRIC_NAME, metadataMessage.gfull.metric.name);
                    assertEquals(GangliaConfiguration.DEFAULT_TYPE.getGangliaType(), metadataMessage.gfull.metric.type);
                    assertEquals(GangliaConfiguration.DEFAULT_SLOPE.getGangliaSlope(), metadataMessage.gfull.metric.slope);
                    assertEquals(GangliaConfiguration.DEFAULT_UNITS, metadataMessage.gfull.metric.units);
                    assertEquals(GangliaConfiguration.DEFAULT_TMAX, metadataMessage.gfull.metric.tmax);
                    assertEquals(GangliaConfiguration.DEFAULT_DMAX, metadataMessage.gfull.metric.dmax);
                } else {
                    Ganglia_value_msg valueMessage = exchange.getIn().getBody(Ganglia_value_msg.class);
                    if (valueMessage != null) {
                        assertEquals("28.0", valueMessage.gstr.str);
                        assertEquals("%s", valueMessage.gstr.fmt);
                    } else {
                        fail("The gmond mock should only receive non-null metadata or value messages");
                    }
                }
            }
        });
        template.sendBody(getTestUri(), "28.0");
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendMessageHeadersShouldOverrideDefaultConfiguration() throws Exception {
        mockGmond.setMinimumExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        mockGmond.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Ganglia_metadata_msg metadataMessage = exchange.getIn().getBody(Ganglia_metadata_msg.class);
                if (metadataMessage != null) {
                    assertEquals("depth", metadataMessage.gfull.metric.name);
                    assertEquals(GMetricType.FLOAT.getGangliaType(), metadataMessage.gfull.metric.type);
                    assertEquals(GMetricSlope.NEGATIVE.getGangliaSlope(), metadataMessage.gfull.metric.slope);
                    assertEquals("cm", metadataMessage.gfull.metric.units);
                    assertEquals(100, metadataMessage.gfull.metric.tmax);
                    assertEquals(10, metadataMessage.gfull.metric.dmax);
                } else {
                    Ganglia_value_msg valueMessage = exchange.getIn().getBody(Ganglia_value_msg.class);
                    if (valueMessage != null) {
                        assertEquals("-3.0", valueMessage.gstr.str);
                        assertEquals("%s", valueMessage.gstr.fmt);
                    } else {
                        fail("The gmond mock should only receive non-null metadata or value messages");
                    }
                }
            }
        });
        Map<String, Object> headers = new HashMap<>();
        headers.put(GangliaConstants.GROUP_NAME, "sea-mesure");
        headers.put(GangliaConstants.METRIC_NAME, "depth");
        headers.put(GangliaConstants.METRIC_TYPE, GMetricType.FLOAT);
        headers.put(GangliaConstants.METRIC_SLOPE, GMetricSlope.NEGATIVE);
        headers.put(GangliaConstants.METRIC_UNITS, "cm");
        headers.put(GangliaConstants.METRIC_TMAX, 100);
        headers.put(GangliaConstants.METRIC_DMAX, 10);
        template.sendBodyAndHeaders(getTestUri(), (-3.0F), headers);
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendWrongMetricTypeShouldThrow() throws Exception {
        thrown.expect(CamelExecutionException.class);
        mockGmond.setExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        template.sendBodyAndHeader(getTestUri(), "28.0", GangliaConstants.METRIC_TYPE, "NotAGMetricType");
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendWrongMetricSlopeShouldThrow() throws Exception {
        thrown.expect(CamelExecutionException.class);
        mockGmond.setExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        template.sendBodyAndHeader(getTestUri(), "28.0", GangliaConstants.METRIC_SLOPE, "NotAGMetricSlope");
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendWrongMetricTMaxShouldThrow() throws Exception {
        thrown.expect(CamelExecutionException.class);
        mockGmond.setExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        template.sendBodyAndHeader(getTestUri(), "28.0", GangliaConstants.METRIC_TMAX, new Object());
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendWrongMetricDMaxShouldThrow() throws Exception {
        thrown.expect(CamelExecutionException.class);
        mockGmond.setExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        template.sendBodyAndHeader(getTestUri(), "28.0", GangliaConstants.METRIC_DMAX, new Object());
        mockGmond.assertIsSatisfied();
    }

    @Test
    public void sendWithWrongTypeShouldThrow() throws Exception {
        thrown.expect(ResolveEndpointFailedException.class);
        mockGmond.setExpectedMessageCount(0);
        mockGmond.setAssertPeriod(100L);
        template.sendBody(((getTestUri()) + "&type=wrong"), "");
        mockGmond.assertIsSatisfied();
    }

    @Sharable
    public static class ProtocolV31Decoder extends MessageToMessageDecoder<DatagramPacket> {
        @Override
        protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws IOException, OncRpcException {
            byte[] bytes = new byte[packet.content().readableBytes()];
            packet.content().readBytes(bytes);
            // Determine what kind of object the datagram is handling
            XdrBufferDecodingStream xbds = new XdrBufferDecodingStream(bytes);
            xbds.beginDecoding();
            int id = (xbds.xdrDecodeInt()) & 191;
            xbds.endDecoding();
            XdrAble outMsg = null;
            if (id == (gmetadata_full)) {
                outMsg = new Ganglia_metadata_msg();
            } else
                if (id == (gmetric_string)) {
                    outMsg = new Ganglia_value_msg();
                } else {
                    fail("During those tests, the gmond mock should only receive metadata or string messages");
                }

            // Unmarshall the incoming datagram
            xbds = new XdrBufferDecodingStream(bytes);
            xbds.beginDecoding();
            outMsg.xdrDecode(xbds);
            xbds.endDecoding();
            out.add(outMsg);
        }
    }
}

