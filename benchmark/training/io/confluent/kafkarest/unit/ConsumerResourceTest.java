/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.unit;


import Response.Status.NOT_FOUND;
import Response.Status.NO_CONTENT;
import io.confluent.kafkarest.BinaryConsumerState;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.Versions;
import io.confluent.kafkarest.entities.ConsumerInstanceConfig;
import io.confluent.kafkarest.entities.CreateConsumerInstanceResponse;
import io.confluent.rest.RestConfigException;
import io.confluent.rest.exceptions.RestNotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


// This test evaluates non-consume functionality for ConsumerResource, exercising functionality
// that isn't really dependent on the EmbeddedFormat, e.g. invalid requests, commit offsets,
// delete consumer. It ends up using the BINARY default since it is sometimes required to make
// those operations possible, but the operations shouldn't be affected by it. See
// ConsumerResourceBinaryTest and ConsumerResourceAvroTest for format-specific tests.
public class ConsumerResourceTest extends AbstractConsumerResourceTest {
    public ConsumerResourceTest() throws RestConfigException {
        super();
    }

    @Test
    public void testCreateInstanceRequestsNewInstance() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES) {
                expectCreateGroup(new ConsumerInstanceConfig());
                EasyMock.replay(consumerManager);
                Response response = request(("/consumers/" + (AbstractConsumerResourceTest.groupName)), mediatype.header).post(Entity.entity(null, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final CreateConsumerInstanceResponse ciResponse = TestUtils.tryReadEntityOrLog(response, CreateConsumerInstanceResponse.class);
                Assert.assertEquals(AbstractConsumerResourceTest.instanceId, ciResponse.getInstanceId());
                Assert.assertThat(ciResponse.getBaseUri(), CoreMatchers.allOf(CoreMatchers.startsWith("http://"), CoreMatchers.containsString(AbstractConsumerResourceTest.instancePath)));
                EasyMock.verify(consumerManager);
                EasyMock.reset(consumerManager);
            }
        }
    }

    @Test
    public void testCreateInstanceWithConfig() {
        ConsumerInstanceConfig config = new ConsumerInstanceConfig();
        config.setId("testid");
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES) {
                expectCreateGroup(config);
                EasyMock.replay(consumerManager);
                Response response = request(("/consumers/" + (AbstractConsumerResourceTest.groupName)), mediatype.header).post(Entity.entity(config, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final CreateConsumerInstanceResponse ciResponse = TestUtils.tryReadEntityOrLog(response, CreateConsumerInstanceResponse.class);
                Assert.assertEquals(AbstractConsumerResourceTest.instanceId, ciResponse.getInstanceId());
                Assert.assertThat(ciResponse.getBaseUri(), CoreMatchers.allOf(CoreMatchers.startsWith("http://"), CoreMatchers.containsString(AbstractConsumerResourceTest.instancePath)));
                EasyMock.verify(consumerManager);
                EasyMock.reset(consumerManager);
            }
        }
    }

    @Test
    public void testInvalidInstanceOrTopic() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES) {
                // Trying to access either an invalid consumer instance or a missing topic should trigger
                // an error
                expectCreateGroup(new ConsumerInstanceConfig());
                expectReadTopic(AbstractConsumerResourceTest.topicName, BinaryConsumerState.class, null, new RestNotFoundException(AbstractConsumerResourceTest.not_found_message, 1000));
                EasyMock.replay(consumerManager);
                Response response = request(("/consumers/" + (AbstractConsumerResourceTest.groupName)), mediatype.header).post(Entity.entity(null, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final CreateConsumerInstanceResponse createResponse = TestUtils.tryReadEntityOrLog(response, CreateConsumerInstanceResponse.class);
                final Response readResponse = request((((instanceBasePath(createResponse)) + "/topics/") + (AbstractConsumerResourceTest.topicName)), mediatype.header).get();
                // Most specific default is different when retrieving embedded data
                String expectedMediatype = ((mediatype.header) != null) ? mediatype.expected : Versions.KAFKA_V1_JSON_BINARY;
                TestUtils.assertErrorResponse(NOT_FOUND, readResponse, 1000, AbstractConsumerResourceTest.not_found_message, expectedMediatype);
                EasyMock.verify(consumerManager);
                EasyMock.reset(consumerManager);
            }
        }
    }

    @Test
    public void testDeleteInstance() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            for (String requestMediatype : TestUtils.V1_REQUEST_ENTITY_TYPES) {
                expectCreateGroup(new ConsumerInstanceConfig());
                expectDeleteGroup(false);
                EasyMock.replay(consumerManager);
                Response response = request(("/consumers/" + (AbstractConsumerResourceTest.groupName)), mediatype.header).post(Entity.entity(null, requestMediatype));
                TestUtils.assertOKResponse(response, mediatype.expected);
                final CreateConsumerInstanceResponse createResponse = TestUtils.tryReadEntityOrLog(response, CreateConsumerInstanceResponse.class);
                final Response deleteResponse = request(instanceBasePath(createResponse), mediatype.header).delete();
                TestUtils.assertErrorResponse(NO_CONTENT, deleteResponse, 0, null, mediatype.expected);
                EasyMock.verify(consumerManager);
                EasyMock.reset(consumerManager);
            }
        }
    }

    @Test
    public void testDeleteInvalidInstance() {
        for (TestUtils.RequestMediaType mediatype : TestUtils.V1_ACCEPT_MEDIATYPES) {
            expectDeleteGroup(true);
            EasyMock.replay(consumerManager);
            final Response deleteResponse = request(((("/consumers/" + (AbstractConsumerResourceTest.groupName)) + "/instances/") + (AbstractConsumerResourceTest.instanceId)), mediatype.header).delete();
            TestUtils.assertErrorResponse(NOT_FOUND, deleteResponse, 1000, AbstractConsumerResourceTest.not_found_message, mediatype.expected);
            EasyMock.verify(consumerManager);
            EasyMock.reset(consumerManager);
        }
    }
}

