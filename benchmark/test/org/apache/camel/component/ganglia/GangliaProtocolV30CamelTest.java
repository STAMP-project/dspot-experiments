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
import info.ganglia.gmetric4j.xdr.v30x.Ganglia_message;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrBufferDecodingStream;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * {@code GangliaProtocolV30CamelTest} is not shipped with an embedded gmond
 * agent. The gmond agent is mocked with the help of camel-netty4 codecs and a
 * mock endpoint. As underlying UDP packets are not guaranteed to be delivered,
 * loose assertions are performed.
 */
public class GangliaProtocolV30CamelTest extends CamelGangliaTestSupport {
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
                Ganglia_message gangliaMessage = exchange.getIn().getBody(Ganglia_message.class);
                assertNotNull("The gmond mock should only receive a non-null ganglia message", gangliaMessage);
                assertEquals(GangliaConfiguration.DEFAULT_METRIC_NAME, gangliaMessage.gmetric.name);
                assertEquals(GangliaConfiguration.DEFAULT_TYPE.getGangliaType(), gangliaMessage.gmetric.type);
                assertEquals(GangliaConfiguration.DEFAULT_SLOPE.getGangliaSlope(), gangliaMessage.gmetric.slope);
                assertEquals(GangliaConfiguration.DEFAULT_UNITS, gangliaMessage.gmetric.units);
                assertEquals(GangliaConfiguration.DEFAULT_TMAX, gangliaMessage.gmetric.tmax);
                assertEquals(GangliaConfiguration.DEFAULT_DMAX, gangliaMessage.gmetric.dmax);
                assertEquals("28.0", gangliaMessage.gmetric.value);
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
                Ganglia_message gangliaMessage = exchange.getIn().getBody(Ganglia_message.class);
                assertNotNull("The gmond mock should only receive a non-null ganglia message", gangliaMessage);
                assertEquals("depth", gangliaMessage.gmetric.name);
                assertEquals("float", gangliaMessage.gmetric.type);
                assertEquals(2, gangliaMessage.gmetric.slope);
                assertEquals("cm", gangliaMessage.gmetric.units);
                assertEquals(100, gangliaMessage.gmetric.tmax);
                assertEquals(10, gangliaMessage.gmetric.dmax);
                assertEquals("-3.0", gangliaMessage.gmetric.value);
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

    @Sharable
    public static class ProtocolV30Decoder extends MessageToMessageDecoder<DatagramPacket> {
        @Override
        protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws IOException, OncRpcException {
            byte[] bytes = new byte[packet.content().readableBytes()];
            packet.content().readBytes(bytes);
            // Unmarshall the incoming datagram
            XdrBufferDecodingStream xbds = new XdrBufferDecodingStream(bytes);
            Ganglia_message outMsg = new Ganglia_message();
            xbds.beginDecoding();
            outMsg.xdrDecode(xbds);
            xbds.endDecoding();
            out.add(outMsg);
        }
    }
}

