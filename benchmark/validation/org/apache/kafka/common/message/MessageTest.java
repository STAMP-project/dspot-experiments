/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.message;


import Type.NULLABLE_BYTES;
import Type.RECORDS;
import java.util.Arrays;
import java.util.Collections;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.message.AddPartitionsToTxnRequestData.AddPartitionsToTxnTopic;
import org.apache.kafka.common.message.CreateTopicsRequestData.CreatableTopicSet;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Message;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


@Ignore
public final class MessageTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    /**
     * Test serializing and deserializing some messages.
     */
    @Test
    public void testRoundTrips() throws Exception {
        testMessageRoundTrips(new MetadataRequestData().setTopics(Arrays.asList(new MetadataRequestData.MetadataRequestTopic().setName("foo"), new MetadataRequestData.MetadataRequestTopic().setName("bar"))), ((short) (6)));
        testMessageRoundTrips(new AddOffsetsToTxnRequestData().setTransactionalId("foobar").setProducerId(52596993799604990L).setProducerEpoch(((short) (123))).setGroupId("baaz"), ((short) (1)));
        testMessageRoundTrips(new AddOffsetsToTxnResponseData().setThrottleTimeMs(42).setErrorCode(((short) (0))), ((short) (0)));
        testMessageRoundTrips(new AddPartitionsToTxnRequestData().setTransactionalId("blah").setProducerId(52596993799604990L).setProducerEpoch(((short) (30000))).setTopics(new org.apache.kafka.common.message.AddPartitionsToTxnRequestData.AddPartitionsToTxnTopicSet(Collections.singletonList(new AddPartitionsToTxnTopic().setName("Topic").setPartitions(Collections.singletonList(1))).iterator())));
        testMessageRoundTrips(new CreateTopicsRequestData().setTimeoutMs(1000).setTopics(new CreatableTopicSet()));
        testMessageRoundTrips(new DescribeAclsRequestData().setResourceType(((byte) (42))).setResourceNameFilter(null).setResourcePatternType(((byte) (3))).setPrincipalFilter("abc").setHostFilter(null).setOperation(((byte) (0))).setPermissionType(((byte) (0))), ((short) (0)));
    }

    /**
     * Verify that the JSON files support the same message versions as the
     * schemas accessible through the ApiKey class.
     */
    @Test
    public void testMessageVersions() throws Exception {
        for (ApiKeys apiKey : ApiKeys.values()) {
            Message message = null;
            try {
                message = ApiMessageFactory.newRequest(apiKey.id);
            } catch (UnsupportedVersionException e) {
                Assert.fail(("No request message spec found for API " + apiKey));
            }
            Assert.assertTrue((((("Request message spec for " + apiKey) + " only ") + "supports versions up to ") + (message.highestSupportedVersion())), ((apiKey.latestVersion()) <= (message.highestSupportedVersion())));
            try {
                message = ApiMessageFactory.newResponse(apiKey.id);
            } catch (UnsupportedVersionException e) {
                Assert.fail(("No response message spec found for API " + apiKey));
            }
            Assert.assertTrue((((("Response message spec for " + apiKey) + " only ") + "supports versions up to ") + (message.highestSupportedVersion())), ((apiKey.latestVersion()) <= (message.highestSupportedVersion())));
        }
    }

    /**
     * Test that the JSON request files match the schemas accessible through the ApiKey class.
     */
    @Test
    public void testRequestSchemas() throws Exception {
        for (ApiKeys apiKey : ApiKeys.values()) {
            Schema[] manualSchemas = apiKey.requestSchemas;
            Schema[] generatedSchemas = ApiMessageFactory.requestSchemas(apiKey.id);
            Assert.assertEquals((("Mismatching request SCHEMAS lengths " + "for api key ") + apiKey), manualSchemas.length, generatedSchemas.length);
            for (int v = 0; v < (manualSchemas.length); v++) {
                try {
                    if ((generatedSchemas[v]) != null) {
                        MessageTest.compareTypes(manualSchemas[v], generatedSchemas[v]);
                    }
                } catch (Exception e) {
                    throw new RuntimeException((((("Failed to compare request schemas " + "for version ") + v) + " of ") + apiKey), e);
                }
            }
        }
    }

    /**
     * Test that the JSON response files match the schemas accessible through the ApiKey class.
     */
    @Test
    public void testResponseSchemas() throws Exception {
        for (ApiKeys apiKey : ApiKeys.values()) {
            Schema[] manualSchemas = apiKey.responseSchemas;
            Schema[] generatedSchemas = ApiMessageFactory.responseSchemas(apiKey.id);
            Assert.assertEquals((("Mismatching response SCHEMAS lengths " + "for api key ") + apiKey), manualSchemas.length, generatedSchemas.length);
            for (int v = 0; v < (manualSchemas.length); v++) {
                try {
                    if ((generatedSchemas[v]) != null) {
                        MessageTest.compareTypes(manualSchemas[v], generatedSchemas[v]);
                    }
                } catch (Exception e) {
                    throw new RuntimeException((((("Failed to compare response schemas " + "for version ") + v) + " of ") + apiKey), e);
                }
            }
        }
    }

    private static class NamedType {
        final String name;

        final Type type;

        NamedType(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        boolean hasSimilarType(MessageTest.NamedType other) {
            if (type.getClass().equals(other.type.getClass())) {
                return true;
            }
            if (type.getClass().equals(RECORDS.getClass())) {
                if (other.type.getClass().equals(NULLABLE_BYTES.getClass())) {
                    return true;
                }
            } else
                if (type.getClass().equals(NULLABLE_BYTES.getClass())) {
                    if (other.type.getClass().equals(RECORDS.getClass())) {
                        return true;
                    }
                }

            return false;
        }

        @Override
        public String toString() {
            return (((name) + "[") + (type)) + "]";
        }
    }

    @Test
    public void testDefaultValues() throws Exception {
        verifySizeRaisesUve(((short) (0)), "validateOnly", new CreateTopicsRequestData().setValidateOnly(true));
        verifySizeSucceeds(((short) (0)), new CreateTopicsRequestData().setValidateOnly(false));
        verifySizeSucceeds(((short) (0)), new OffsetCommitRequestData().setRetentionTimeMs(123));
        verifySizeRaisesUve(((short) (5)), "forgotten", new FetchRequestData().setForgotten(Collections.singletonList(new FetchRequestData.ForgottenTopic().setName("foo"))));
    }
}

