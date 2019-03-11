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


import DefaultAutoDeploymentStrategy.DEPLOYMENT_MODE;
import java.io.InputStream;
import org.flowable.form.spring.autodeployment.DefaultAutoDeploymentStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;


/**
 *
 *
 * @author Tiese Barrell
 */
@MockitoSettings(strictness = Strictness.LENIENT)
public class DefaultAutoDeploymentStrategyTest extends AbstractAutoDeploymentStrategyTest {
    private DefaultAutoDeploymentStrategy classUnderTest;

    @Test
    public void testHandlesMode() {
        Assertions.assertTrue(classUnderTest.handlesMode(DEPLOYMENT_MODE));
        Assertions.assertFalse(classUnderTest.handlesMode("other-mode"));
        Assertions.assertFalse(classUnderTest.handlesMode(null));
    }

    @Test
    public void testDeployResources() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2 };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name(deploymentNameHint);
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }

    @Test
    public void testDeployResourcesNoResources() {
        final Resource[] resources = new Resource[]{  };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name(deploymentNameHint);
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }
}

