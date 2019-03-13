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
package org.apache.camel.component.aws.ec2;


import EC2Constants.IMAGE_ID;
import EC2Constants.INSTANCES_IDS;
import EC2Constants.INSTANCES_KEY_PAIR;
import EC2Constants.INSTANCES_TAGS;
import EC2Constants.INSTANCE_MAX_COUNT;
import EC2Constants.INSTANCE_MIN_COUNT;
import EC2Constants.INSTANCE_SECURITY_GROUPS;
import EC2Constants.INSTANCE_TYPE;
import EC2Constants.OPERATION;
import EC2Operations.createAndRunInstances;
import InstanceStateName.Running;
import InstanceStateName.Stopped;
import InstanceStateName.Terminated;
import InstanceType.T2Micro;
import MonitoringState.Disabled;
import MonitoringState.Enabled;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DeleteTagsResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.MonitorInstancesResult;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.amazonaws.services.ec2.model.UnmonitorInstancesResult;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class EC2ProducerTest extends CamelTestSupport {
    @BindToRegistry("amazonEc2Client")
    AmazonEC2ClientMock amazonEc2Client = new AmazonEC2ClientMock();

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mock;

    @Test
    public void ec2CreateAndRunTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
        RunInstancesResult resultGet = ((RunInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getReservation().getInstances().get(0).getImageId(), "test-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceType(), T2Micro.toString());
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceId(), "instance-1");
    }

    @Test
    public void ec2CreateAndRunTestWithSecurityGroups() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
                Collection<String> secGroups = new ArrayList<>();
                secGroups.add("secgroup-1");
                secGroups.add("secgroup-2");
                exchange.getIn().setHeader(INSTANCE_SECURITY_GROUPS, secGroups);
            }
        });
        assertMockEndpointsSatisfied();
        RunInstancesResult resultGet = ((RunInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getReservation().getInstances().get(0).getImageId(), "test-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceType(), T2Micro.toString());
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceId(), "instance-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().size(), 2);
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().get(0).getGroupId(), "id-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().get(1).getGroupId(), "id-2");
    }

    @Test
    public void ec2CreateAndRunImageIdNotSpecifiedTest() throws Exception {
        mock.expectedMessageCount(0);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
        assertTrue("Should be failed", exchange.isFailed());
        assertTrue("Should be IllegalArgumentException", ((exchange.getException()) instanceof IllegalArgumentException));
        assertEquals("AMI must be specified", exchange.getException().getMessage());
    }

    @Test
    public void ec2CreateAndRunInstanceTypeNotSpecifiedTest() throws Exception {
        mock.expectedMessageCount(0);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
        assertTrue("Should be failed", exchange.isFailed());
        assertTrue("Should be IllegalArgumentException", ((exchange.getException()) instanceof IllegalArgumentException));
        assertEquals("Instance Type must be specified", exchange.getException().getMessage());
    }

    @Test
    public void ec2CreateAndRunMinCountNotSpecifiedTest() throws Exception {
        mock.expectedMessageCount(0);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
        assertTrue("Should be failed", exchange.isFailed());
        assertTrue("Should be IllegalArgumentException", ((exchange.getException()) instanceof IllegalArgumentException));
        assertEquals("Min instances count must be specified", exchange.getException().getMessage());
    }

    @Test
    public void ec2CreateAndRunMaxCountNotSpecifiedTest() throws Exception {
        mock.expectedMessageCount(0);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
        assertTrue("Should be failed", exchange.isFailed());
        assertTrue("Should be IllegalArgumentException", ((exchange.getException()) instanceof IllegalArgumentException));
        assertEquals("Max instances count must be specified", exchange.getException().getMessage());
    }

    @Test
    public void ec2CreateAndRunTestWithKeyPair() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-1");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
                exchange.getIn().setHeader(INSTANCES_KEY_PAIR, "keypair-1");
            }
        });
        assertMockEndpointsSatisfied();
        RunInstancesResult resultGet = ((RunInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getReservation().getInstances().get(0).getImageId(), "test-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceType(), T2Micro.toString());
        assertEquals(resultGet.getReservation().getInstances().get(0).getInstanceId(), "instance-1");
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().size(), 2);
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().get(0).getGroupId(), "id-3");
        assertEquals(resultGet.getReservation().getInstances().get(0).getSecurityGroups().get(1).getGroupId(), "id-4");
    }

    @Test
    public void ec2CreateAndRunKoTest() throws Exception {
        mock.expectedMessageCount(0);
        template.request("direct:createAndRun", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(OPERATION, createAndRunInstances);
                exchange.getIn().setHeader(IMAGE_ID, "test-2");
                exchange.getIn().setHeader(INSTANCE_TYPE, T2Micro);
                exchange.getIn().setHeader(INSTANCE_MIN_COUNT, 1);
                exchange.getIn().setHeader(INSTANCE_MAX_COUNT, 1);
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void ec2StartTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        StartInstancesResult resultGet = ((StartInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getStartingInstances().get(0).getInstanceId(), "test-1");
        assertEquals(resultGet.getStartingInstances().get(0).getPreviousState().getName(), Stopped.toString());
        assertEquals(resultGet.getStartingInstances().get(0).getCurrentState().getName(), Running.toString());
    }

    @Test
    public void ec2StopTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:stop", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        StopInstancesResult resultGet = ((StopInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getStoppingInstances().get(0).getInstanceId(), "test-1");
        assertEquals(resultGet.getStoppingInstances().get(0).getPreviousState().getName(), Running.toString());
        assertEquals(resultGet.getStoppingInstances().get(0).getCurrentState().getName(), Stopped.toString());
    }

    @Test
    public void ec2TerminateTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:terminate", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        TerminateInstancesResult resultGet = ((TerminateInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getTerminatingInstances().get(0).getInstanceId(), "test-1");
        assertEquals(resultGet.getTerminatingInstances().get(0).getPreviousState().getName(), Running.toString());
        assertEquals(resultGet.getTerminatingInstances().get(0).getCurrentState().getName(), Terminated.toString());
    }

    @Test
    public void ec2DescribeInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describe", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertMockEndpointsSatisfied();
        DescribeInstancesResult resultGet = ((DescribeInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getReservations().size(), 1);
        assertEquals(resultGet.getReservations().get(0).getInstances().size(), 2);
    }

    @Test
    public void ec2DescribeSpecificInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describe", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("instance-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        DescribeInstancesResult resultGet = ((DescribeInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getReservations().size(), 1);
        assertEquals(resultGet.getReservations().get(0).getInstances().size(), 1);
    }

    @Test
    public void ec2DescribeInstancesStatusTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describeStatus", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertMockEndpointsSatisfied();
        DescribeInstanceStatusResult resultGet = ((DescribeInstanceStatusResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getInstanceStatuses().size(), 2);
    }

    @Test
    public void ec2DescribeStatusSpecificInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describeStatus", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        DescribeInstanceStatusResult resultGet = ((DescribeInstanceStatusResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getInstanceStatuses().size(), 1);
        assertEquals(resultGet.getInstanceStatuses().get(0).getInstanceState().getName(), Running.toString());
    }

    @Test
    public void ec2RebootInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        template.request("direct:reboot", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void ec2MonitorInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:monitor", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        MonitorInstancesResult resultGet = ((MonitorInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getInstanceMonitorings().size(), 1);
        assertEquals(resultGet.getInstanceMonitorings().get(0).getInstanceId(), "test-1");
        assertEquals(resultGet.getInstanceMonitorings().get(0).getMonitoring().getState(), Enabled.toString());
    }

    @Test
    public void ec2UnmonitorInstancesTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:unmonitor", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
            }
        });
        assertMockEndpointsSatisfied();
        UnmonitorInstancesResult resultGet = ((UnmonitorInstancesResult) (exchange.getIn().getBody()));
        assertEquals(resultGet.getInstanceMonitorings().size(), 1);
        assertEquals(resultGet.getInstanceMonitorings().get(0).getInstanceId(), "test-1");
        assertEquals(resultGet.getInstanceMonitorings().get(0).getMonitoring().getState(), Disabled.toString());
    }

    @Test
    public void ec2CreateTagsTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createTags", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
                Collection<String> tags = new ArrayList<>();
                tags.add("pacific");
                exchange.getIn().setHeader(INSTANCES_TAGS, tags);
            }
        });
        assertMockEndpointsSatisfied();
        CreateTagsResult resultGet = ((CreateTagsResult) (exchange.getIn().getBody()));
        assertNotNull(resultGet);
    }

    @Test
    public void ec2DeleteTagsTest() throws Exception {
        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:deleteTags", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Collection<String> l = new ArrayList<>();
                l.add("test-1");
                exchange.getIn().setHeader(INSTANCES_IDS, l);
                Collection<String> tags = new ArrayList<>();
                tags.add("pacific");
                exchange.getIn().setHeader(INSTANCES_TAGS, tags);
            }
        });
        assertMockEndpointsSatisfied();
        DeleteTagsResult resultGet = ((DeleteTagsResult) (exchange.getIn().getBody()));
        assertNotNull(resultGet);
    }
}

