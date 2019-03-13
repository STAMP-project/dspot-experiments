/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.yarn;


import ExceptionMessage.YARN_NOT_ENOUGH_HOSTS;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ContainerAllocator}.
 */
public final class ContainerAllocatorTest {
    private static final String CONTAINER_NAME = "test";

    private Resource mResource;

    private YarnClient mYarnClient;

    private AMRMClientAsync<ContainerRequest> mRMClient;

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test(timeout = 10000)
    public void oneContainerPerHostFullAllocation() throws Exception {
        int numHosts = 10;
        int maxContainersPerHost = 1;
        testFullAllocation(numHosts, maxContainersPerHost);
    }

    @Test(timeout = 10000)
    public void fiveContainersPerHostFullAllocation() throws Exception {
        int numHosts = 10;
        int maxContainersPerHost = 5;
        testFullAllocation(numHosts, maxContainersPerHost);
    }

    @Test(timeout = 10000)
    public void fiveContainersPerHostHalfAllocation() throws Exception {
        int numHosts = 10;
        int maxContainersPerHost = 5;
        int numContainers = (numHosts * maxContainersPerHost) / 2;
        ContainerAllocator containerAllocator = setup(numHosts, maxContainersPerHost, numContainers);
        List<Container> containers = containerAllocator.allocateContainers();
        Assert.assertEquals(numContainers, containers.size());
        checkMaxHostsLimitNotExceeded(containers, maxContainersPerHost);
    }

    @Test(timeout = 10000)
    public void notEnoughHosts() throws Exception {
        int numHosts = 10;
        int maxContainersPerHost = 5;
        int numContainers = (numHosts * maxContainersPerHost) + 1;// one container too many

        ContainerAllocator containerAllocator = setup(numHosts, maxContainersPerHost, numContainers);
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(YARN_NOT_ENOUGH_HOSTS.getMessage(numContainers, ContainerAllocatorTest.CONTAINER_NAME, numHosts));
        containerAllocator.allocateContainers();
    }

    @Test(timeout = 1000)
    public void allocateMasterInAnyHost() throws Exception {
        ContainerAllocator containerAllocator = new ContainerAllocator(ContainerAllocatorTest.CONTAINER_NAME, 1, 1, mResource, mYarnClient, mRMClient, "any");
        Mockito.doAnswer(allocateFirstHostAnswer(containerAllocator)).when(mRMClient).addContainerRequest(Matchers.argThat(new ArgumentMatcher<ContainerRequest>() {
            @Override
            public boolean matches(Object o) {
                ContainerRequest request = ((ContainerRequest) (o));
                if ((((request.getRelaxLocality()) == true) && ((request.getNodes().size()) == 1)) && (request.getNodes().get(0).equals("any"))) {
                    return true;
                }
                return false;
            }
        }));
        containerAllocator.allocateContainers();
    }
}

