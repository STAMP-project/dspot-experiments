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
package org.apache.camel.component.aws.mq;


import BrokerState.RUNNING;
import DeploymentMode.SINGLE_INSTANCE;
import EngineType.ACTIVEMQ;
import MQConstants.BROKER_DEPLOYMENT_MODE;
import MQConstants.BROKER_ENGINE;
import MQConstants.BROKER_ENGINE_VERSION;
import MQConstants.BROKER_ID;
import MQConstants.BROKER_INSTANCE_TYPE;
import MQConstants.BROKER_NAME;
import MQConstants.BROKER_PUBLICLY_ACCESSIBLE;
import MQConstants.BROKER_USERS;
import MQConstants.CONFIGURATION_ID;
import MQConstants.OPERATION;
import MQOperations.createBroker;
import MQOperations.deleteBroker;
import MQOperations.describeBroker;
import MQOperations.listBrokers;
import MQOperations.rebootBroker;
import MQOperations.updateBroker;
import com.amazonaws.services.mq.model.ConfigurationId;
import com.amazonaws.services.mq.model.CreateBrokerResult;
import com.amazonaws.services.mq.model.DeleteBrokerResult;
import com.amazonaws.services.mq.model.DescribeBrokerResult;
import com.amazonaws.services.mq.model.ListBrokersResult;
import com.amazonaws.services.mq.model.UpdateBrokerResult;
import com.amazonaws.services.mq.model.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class MQProducerTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mock;

    @Test
    public void mqListBrokersTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:listBrokers", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, listBrokers);
            }
        });
        assertMockEndpointsSatisfied();
        ListBrokersResult resultGet = ((ListBrokersResult) (exchange.getIn().getBody()));
        assertEquals(1, resultGet.getBrokerSummaries().size());
        assertEquals("mybroker", resultGet.getBrokerSummaries().get(0).getBrokerName());
        assertEquals(RUNNING.toString(), resultGet.getBrokerSummaries().get(0).getBrokerState());
    }

    @Test
    public void mqCreateBrokerTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createBroker", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createBroker);
                exchange.getIn().setHeader(BROKER_NAME, "test");
                exchange.getIn().setHeader(BROKER_DEPLOYMENT_MODE, SINGLE_INSTANCE);
                exchange.getIn().setHeader(BROKER_INSTANCE_TYPE, "mq.t2.micro");
                exchange.getIn().setHeader(BROKER_ENGINE, ACTIVEMQ.name());
                exchange.getIn().setHeader(BROKER_ENGINE_VERSION, "5.15.6");
                exchange.getIn().setHeader(BROKER_PUBLICLY_ACCESSIBLE, false);
                List<User> users = new ArrayList<>();
                User user = new User();
                user.setUsername("camel");
                user.setPassword("camelcamel12");
                users.add(user);
                exchange.getIn().setHeader(BROKER_USERS, users);
            }
        });
        assertMockEndpointsSatisfied();
        CreateBrokerResult resultGet = ((CreateBrokerResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getBrokerId(), "1");
        assertEquals(resultGet.getBrokerArn(), "test");
    }

    @Test
    public void mqDeleteBrokerTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createBroker", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, deleteBroker);
                exchange.getIn().setHeader(BROKER_ID, "1");
            }
        });
        assertMockEndpointsSatisfied();
        DeleteBrokerResult resultGet = ((DeleteBrokerResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getBrokerId(), "1");
    }

    @Test
    public void mqRebootBrokerTest() throws Exception {
        mock.expectedMessageCount(1);
        template.request("direct:rebootBroker", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, rebootBroker);
                exchange.getIn().setHeader(BROKER_ID, "1");
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void mqUpdateBrokerTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:updateBroker", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, updateBroker);
                exchange.getIn().setHeader(BROKER_ID, "1");
                ConfigurationId cId = new ConfigurationId();
                cId.setId("1");
                cId.setRevision(12);
                exchange.getIn().setHeader(CONFIGURATION_ID, cId);
            }
        });
        assertMockEndpointsSatisfied();
        UpdateBrokerResult resultGet = ((UpdateBrokerResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getBrokerId(), "1");
    }

    @Test
    public void mqDescribeBrokerTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describeBroker", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, describeBroker);
                exchange.getIn().setHeader(BROKER_ID, "1");
                ConfigurationId cId = new ConfigurationId();
                cId.setId("1");
                cId.setRevision(12);
                exchange.getIn().setHeader(CONFIGURATION_ID, cId);
            }
        });
        assertMockEndpointsSatisfied();
        DescribeBrokerResult resultGet = ((DescribeBrokerResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getBrokerId(), "1");
    }
}

