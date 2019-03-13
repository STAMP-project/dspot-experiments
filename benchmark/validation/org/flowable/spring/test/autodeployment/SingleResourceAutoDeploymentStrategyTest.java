/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.spring.test.autodeployment;


import SingleResourceAutoDeploymentStrategy.DEPLOYMENT_MODE;
import java.io.InputStream;
import org.flowable.dmn.spring.autodeployment.SingleResourceAutoDeploymentStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;


/**
 *
 *
 * @author Tiese Barrell
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SingleResourceAutoDeploymentStrategyTest extends AbstractAutoDeploymentStrategyTest {
    private SingleResourceAutoDeploymentStrategy classUnderTest;

    @Test
    public void testHandlesMode() {
        Assert.assertTrue(classUnderTest.handlesMode(DEPLOYMENT_MODE));
        Assert.assertFalse(classUnderTest.handlesMode("other-mode"));
        Assert.assertFalse(classUnderTest.handlesMode(null));
    }

    @Test
    public void testDeployResources() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(3)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(3)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name(resourceName1);
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name(resourceName2);
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name(resourceName3);
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(3)).deploy();
    }

    @Test
    public void testDeployResourcesNoResources() {
        final Resource[] resources = new Resource[]{  };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.never()).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.never()).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.never()).name(deploymentNameHint);
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).deploy();
    }
}

