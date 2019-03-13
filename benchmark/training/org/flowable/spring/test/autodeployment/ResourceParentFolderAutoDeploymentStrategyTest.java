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


import ResourceParentFolderAutoDeploymentStrategy.DEPLOYMENT_MODE;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.flowable.form.spring.autodeployment.ResourceParentFolderAutoDeploymentStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
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
public class ResourceParentFolderAutoDeploymentStrategyTest extends AbstractAutoDeploymentStrategyTest {
    private ResourceParentFolderAutoDeploymentStrategy classUnderTest;

    @Mock
    private File parentFile1Mock;

    @Mock
    private File parentFile2Mock;

    private final String parentFilename1 = "parentFilename1";

    private final String parentFilename2 = "parentFilename2";

    @Test
    public void testHandlesMode() {
        Assertions.assertTrue(classUnderTest.handlesMode(DEPLOYMENT_MODE));
        Assertions.assertFalse(classUnderTest.handlesMode("other-mode"));
        Assertions.assertFalse(classUnderTest.handlesMode(null));
    }

    @Test
    public void testDeployResources_Separate() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile2Mock);
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(2)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).deploy();
    }

    @Test
    public void testDeployResources_Joined() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile1Mock);
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }

    @Test
    public void testDeployResources_AllInOne() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock3.getParentFile()).thenReturn(parentFile1Mock);
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }

    @Test
    public void testDeployResources_Mixed() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile2Mock);
        Mockito.when(fileMock3.getParentFile()).thenReturn(parentFile1Mock);
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(2)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).deploy();
    }

    @Test
    public void testDeployResources_NoParent() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(3)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(3)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName3)));
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

    @Test
    public void testDeployResourcesIOExceptionWhenCreatingMapFallsBackToResourceName() throws Exception {
        Mockito.when(resourceMock3.getFile()).thenThrow(new IOException());
        Mockito.when(resourceMock3.getFilename()).thenReturn(resourceName3);
        final Resource[] resources = new Resource[]{ resourceMock3 };
        classUnderTest.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName3)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }
}

