/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.testing.integration;


import Metrics.GaugeRequest;
import MetricsServiceGrpc.MetricsServiceBlockingStub;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.testing.integration.Metrics.EmptyMessage;
import io.grpc.testing.integration.Metrics.GaugeResponse;
import io.grpc.testing.integration.StressTestClient.TestCaseWeightPair;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static TestCases.EMPTY_UNARY;
import static TestCases.LARGE_UNARY;
import static TestCases.SERVER_STREAMING;


/**
 * Unit tests for {@link StressTestClient}.
 */
@RunWith(JUnit4.class)
public class StressTestClientTest {
    @Rule
    public final Timeout globalTimeout = Timeout.seconds(5);

    @Test
    public void ipv6AddressesShouldBeSupported() {
        StressTestClient client = new StressTestClient();
        client.parseArgs(new String[]{ "--server_addresses=[0:0:0:0:0:0:0:1]:8080," + "[1:2:3:4:f:e:a:b]:8083" });
        Assert.assertEquals(2, client.addresses().size());
        Assert.assertEquals(new InetSocketAddress("0:0:0:0:0:0:0:1", 8080), client.addresses().get(0));
        Assert.assertEquals(new InetSocketAddress("1:2:3:4:f:e:a:b", 8083), client.addresses().get(1));
    }

    @Test
    public void defaults() {
        StressTestClient client = new StressTestClient();
        Assert.assertEquals(Collections.singletonList(new InetSocketAddress("localhost", 8080)), client.addresses());
        Assert.assertTrue(client.testCaseWeightPairs().isEmpty());
        Assert.assertEquals((-1), client.durationSecs());
        Assert.assertEquals(1, client.channelsPerServer());
        Assert.assertEquals(1, client.stubsPerChannel());
        Assert.assertEquals(8081, client.metricsPort());
    }

    @Test
    public void allCommandlineSwitchesAreSupported() {
        StressTestClient client = new StressTestClient();
        client.parseArgs(new String[]{ "--server_addresses=localhost:8080,localhost:8081,localhost:8082", "--test_cases=empty_unary:20,large_unary:50,server_streaming:30", "--test_duration_secs=20", "--num_channels_per_server=10", "--num_stubs_per_channel=5", "--metrics_port=9090", "--server_host_override=foo.test.google.fr", "--use_tls=true", "--use_test_ca=true" });
        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("localhost", 8080), new InetSocketAddress("localhost", 8081), new InetSocketAddress("localhost", 8082));
        Assert.assertEquals(addresses, client.addresses());
        List<TestCaseWeightPair> testCases = Arrays.asList(new TestCaseWeightPair(EMPTY_UNARY, 20), new TestCaseWeightPair(LARGE_UNARY, 50), new TestCaseWeightPair(SERVER_STREAMING, 30));
        Assert.assertEquals(testCases, client.testCaseWeightPairs());
        Assert.assertEquals("foo.test.google.fr", client.serverHostOverride());
        Assert.assertTrue(client.useTls());
        Assert.assertTrue(client.useTestCa());
        Assert.assertEquals(20, client.durationSecs());
        Assert.assertEquals(10, client.channelsPerServer());
        Assert.assertEquals(5, client.stubsPerChannel());
        Assert.assertEquals(9090, client.metricsPort());
    }

    @Test
    public void serverHostOverrideShouldBeApplied() {
        StressTestClient client = new StressTestClient();
        client.parseArgs(new String[]{ "--server_addresses=localhost:8080", "--server_host_override=foo.test.google.fr" });
        Assert.assertEquals("foo.test.google.fr", client.addresses().get(0).getHostName());
    }

    @Test
    public void gaugesShouldBeExported() throws Exception {
        TestServiceServer server = new TestServiceServer();
        server.parseArgs(new String[]{ "--port=" + 0, "--use_tls=false" });
        server.start();
        StressTestClient client = new StressTestClient();
        client.parseArgs(new String[]{ "--test_cases=empty_unary:1", "--server_addresses=localhost:" + (server.getPort()), "--metrics_port=" + 0, "--num_stubs_per_channel=2" });
        client.startMetricsService();
        client.runStressTest();
        // Connect to the metrics service
        ManagedChannel ch = ManagedChannelBuilder.forAddress("localhost", client.getMetricServerPort()).usePlaintext().build();
        MetricsServiceGrpc.MetricsServiceBlockingStub stub = MetricsServiceGrpc.newBlockingStub(ch);
        // Wait until gauges have been exported
        Set<String> gaugeNames = Sets.newHashSet("/stress_test/server_0/channel_0/stub_0/qps", "/stress_test/server_0/channel_0/stub_1/qps");
        List<GaugeResponse> allGauges = ImmutableList.copyOf(stub.getAllGauges(EmptyMessage.getDefaultInstance()));
        while ((allGauges.size()) < (gaugeNames.size())) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            allGauges = ImmutableList.copyOf(stub.getAllGauges(EmptyMessage.getDefaultInstance()));
        } 
        for (GaugeResponse gauge : allGauges) {
            String gaugeName = gauge.getName();
            Assert.assertTrue(("gaugeName: " + gaugeName), gaugeNames.contains(gaugeName));
            Assert.assertTrue(("qps: " + (gauge.getLongValue())), ((gauge.getLongValue()) > 0));
            gaugeNames.remove(gauge.getName());
            GaugeResponse gauge1 = stub.getGauge(GaugeRequest.newBuilder().setName(gaugeName).build());
            Assert.assertEquals(gaugeName, gauge1.getName());
            Assert.assertTrue(("qps: " + (gauge1.getLongValue())), ((gauge1.getLongValue()) > 0));
        }
        Assert.assertTrue(("gauges: " + gaugeNames), gaugeNames.isEmpty());
        client.shutdown();
        server.stop();
    }
}

