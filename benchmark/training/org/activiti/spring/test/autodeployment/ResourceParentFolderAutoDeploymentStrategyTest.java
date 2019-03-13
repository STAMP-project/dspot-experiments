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
package org.activiti.spring.test.autodeployment;


import ResourceParentFolderAutoDeploymentStrategy.DEPLOYMENT_MODE;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import org.activiti.spring.autodeployment.ResourceParentFolderAutoDeploymentStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;


public class ResourceParentFolderAutoDeploymentStrategyTest extends AbstractAutoDeploymentStrategyTest {
    private ResourceParentFolderAutoDeploymentStrategy deploymentStrategy;

    @Mock
    private File parentFile1Mock;

    @Mock
    private File parentFile2Mock;

    private final String parentFilename1 = "parentFilename1";

    private final String parentFilename2 = "parentFilename2";

    @Test
    public void testHandlesMode() {
        Assert.assertTrue(deploymentStrategy.handlesMode(DEPLOYMENT_MODE));
        Assert.assertFalse(deploymentStrategy.handlesMode("other-mode"));
        Assert.assertFalse(deploymentStrategy.handlesMode(null));
    }

    @Test
    public void testDeployResources_Separate() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile2Mock);
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(2)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).deploy();
    }

    @Test
    public void testDeployResources_Joined() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile1Mock);
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(1)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).deploy();
    }

    @Test
    public void testDeployResources_AllInOne() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3, resourceMock4, resourceMock5 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock3.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock4.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock5.getParentFile()).thenReturn(parentFile1Mock);
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock).createDeployment();
        Mockito.verify(deploymentBuilderMock).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName3), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName4), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName5), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock).deploy();
    }

    @Test
    public void testDeployResources_Mixed() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        Mockito.when(fileMock1.getParentFile()).thenReturn(parentFile1Mock);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile2Mock);
        Mockito.when(fileMock3.getParentFile()).thenReturn(parentFile1Mock);
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.times(2)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (parentFilename2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName3), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(2)).deploy();
    }

    @Test
    public void testDeployResources_NoParent() {
        final Resource[] resources = new Resource[]{ resourceMock1, resourceMock2, resourceMock3 };
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.when(fileMock1.getParentFile()).thenReturn(null);
        Mockito.when(fileMock2.getParentFile()).thenReturn(parentFile2Mock);
        Mockito.when(parentFile2Mock.isDirectory()).thenReturn(false);
        Mockito.when(fileMock3.getParentFile()).thenReturn(null);
        Mockito.verify(repositoryServiceMock, Mockito.times(3)).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.times(3)).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName1)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName2)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).name((((deploymentNameHint) + ".") + (resourceName3)));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName1), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(1)).addInputStream(ArgumentMatchers.eq(resourceName3), ArgumentMatchers.isA(Resource.class));
        Mockito.verify(deploymentBuilderMock, Mockito.times(3)).deploy();
    }

    @Test
    public void testDeployResourcesNoResources() {
        final Resource[] resources = new Resource[]{  };
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock, Mockito.never()).createDeployment();
        Mockito.verify(deploymentBuilderMock, Mockito.never()).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock, Mockito.never()).name(deploymentNameHint);
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addInputStream(ArgumentMatchers.eq(resourceName2), ArgumentMatchers.isA(InputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).addZipInputStream(ArgumentMatchers.isA(ZipInputStream.class));
        Mockito.verify(deploymentBuilderMock, Mockito.never()).deploy();
    }

    @Test
    public void testDeployResourcesIOExceptionWhenCreatingMapFallsBackToResourceName() throws Exception {
        Mockito.when(resourceMock3.getFile()).thenThrow(new IOException());
        Mockito.when(resourceMock3.getFilename()).thenReturn(resourceName3);
        final Resource[] resources = new Resource[]{ resourceMock3 };
        deploymentStrategy.deployResources(deploymentNameHint, resources, repositoryServiceMock);
        Mockito.verify(repositoryServiceMock).createDeployment();
        Mockito.verify(deploymentBuilderMock).enableDuplicateFiltering();
        Mockito.verify(deploymentBuilderMock).name((((deploymentNameHint) + ".") + (resourceName3)));
        Mockito.verify(deploymentBuilderMock).addInputStream(ArgumentMatchers.eq(resourceName3), ArgumentMatchers.any(Resource.class));
        Mockito.verify(deploymentBuilderMock).deploy();
    }
}

