/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.project;


import Path.EMPTY;
import RequestBuilder.Method;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.core.jsonrpc.commons.RequestTransmitter;
import org.eclipse.che.api.core.model.workspace.config.ProjectConfig;
import org.eclipse.che.api.project.shared.dto.CopyOptions;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.MoveOptions;
import org.eclipse.che.api.project.shared.dto.NewProjectConfigDto;
import org.eclipse.che.api.project.shared.dto.ProjectSearchResponseDto;
import org.eclipse.che.api.project.shared.dto.SourceEstimation;
import org.eclipse.che.api.project.shared.dto.TreeElement;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.api.workspace.shared.dto.SourceStorageDto;
import org.eclipse.che.ide.MimeType;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.QueryExpression;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.rest.AsyncRequest;
import org.eclipse.che.ide.rest.AsyncRequestFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.loaders.request.LoaderFactory;
import org.eclipse.che.ide.ui.loaders.request.MessageLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit test for {@link ProjectServiceClient}.
 *
 * @author Vlad Zhukovskyi
 * @author Oleksandr Andriienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectServiceClientTest {
    private static final String TEXT = "to be or not to be.";

    private static final Path resourcePath = Path.valueOf("TestPrj/http%253A%252F%252F.org%252Fte st ");

    private static final Path targetPath = Path.valueOf("TestPrj/target here* ");

    @Mock
    private LoaderFactory loaderFactory;

    @Mock
    private AsyncRequestFactory requestFactory;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private DtoUnmarshallerFactory unmarshaller;

    @Mock
    private RequestTransmitter transmitter;

    @Mock
    private AppContext appContext;

    @Mock
    private AsyncRequest asyncRequest;

    @Mock
    private Unmarshallable<ItemReference> unmarshallableItemRef;

    @Mock
    private Unmarshallable<List<ProjectConfigDto>> unmarshallablePrjsConf;

    @Mock
    private Unmarshallable<ProjectConfigDto> unmarshallablePrjConf;

    @Mock
    private Unmarshallable<ProjectSearchResponseDto> unmarshallableSearch;

    @Mock
    private Promise<ProjectSearchResponseDto> searchPromise;

    @Mock
    private Unmarshallable<List<SourceEstimation>> unmarshallbleSourcesEstimation;

    @Mock
    private Unmarshallable<SourceEstimation> unmarshallbleSourceEstimation;

    @Mock
    private Unmarshallable<TreeElement> unmarshallableTreeElem;

    @Mock
    private Promise<ItemReference> itemRefPromise;

    @Mock
    private MessageLoader messageLoader;

    @Mock
    private NewProjectConfigDto prjConfig1;

    @Mock
    private NewProjectConfigDto prjConfig2;

    @Mock
    private SourceStorageDto source;

    @Captor
    private ArgumentCaptor<List<NewProjectConfigDto>> prjsArgCaptor;

    private ProjectServiceClient client;

    @Test
    public void testShouldNotSetupLoaderForTheGetTreeMethod() throws Exception {
        Mockito.when(asyncRequest.header(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(asyncRequest);
        client.getTree(EMPTY, 1, true);
        Mockito.verify(asyncRequest, Mockito.never()).loader(ArgumentMatchers.any(AsyncRequestLoader.class));// see CHE-3467

    }

    @Test
    public void shouldReturnListProjects() {
        client.getProjects();
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(unmarshaller).newListUnmarshaller(ProjectConfigDto.class);
        Mockito.verify(asyncRequest).send(unmarshallablePrjsConf);
    }

    @Test
    public void shouldEncodeUrlAndEstimateProject() {
        String prjType = "java";
        client.estimate(ProjectServiceClientTest.resourcePath, prjType);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Estimating project...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send(unmarshallbleSourceEstimation);
    }

    @Test
    public void shouldEncodeUrlAndResolveProjectSources() {
        client.resolveSources(ProjectServiceClientTest.resourcePath);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Resolving sources...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(unmarshaller).newListUnmarshaller(SourceEstimation.class);
        Mockito.verify(asyncRequest).send(unmarshallbleSourcesEstimation);
    }

    @Test
    public void shouldEncodeUrlAndImportProject() {
        client.importProject(ProjectServiceClientTest.resourcePath, source);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.any(), ArgumentMatchers.eq(source));
        Mockito.verify(asyncRequest).header(CONTENT_TYPE, MimeType.APPLICATION_JSON);
        Mockito.verify(asyncRequest).send();
    }

    @Test
    public void shouldEncodeUrlAndSearchResourceReferences() {
        QueryExpression expression = new QueryExpression();
        expression.setName(ProjectServiceClientTest.TEXT);
        expression.setText(ProjectServiceClientTest.TEXT);
        expression.setPath(ProjectServiceClientTest.resourcePath.toString());
        expression.setMaxItems(100);
        expression.setSkipCount(10);
        Mockito.when(asyncRequest.send(unmarshallableSearch)).thenReturn(searchPromise);
        client.search(expression);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Searching...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(unmarshaller).newUnmarshaller(ProjectSearchResponseDto.class);
        Mockito.verify(asyncRequest).send(unmarshallableSearch);
    }

    @Test
    public void shouldCreateOneProjectByBatch() {
        List<NewProjectConfigDto> configs = Collections.singletonList(prjConfig1);
        client.createBatchProjects(configs);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.anyString(), prjsArgCaptor.capture());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Creating project...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send(unmarshallablePrjsConf);
        Mockito.verify(unmarshaller).newListUnmarshaller(ProjectConfigDto.class);
        Assert.assertEquals(1, prjsArgCaptor.getValue().size());
    }

    @Test
    public void shouldCreateFewProjectByBatch() {
        List<NewProjectConfigDto> configs = Arrays.asList(prjConfig1, prjConfig2);
        client.createBatchProjects(configs);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.anyString(), prjsArgCaptor.capture());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Creating the batch of projects...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send(unmarshallablePrjsConf);
        Mockito.verify(unmarshaller).newListUnmarshaller(ProjectConfigDto.class);
        Assert.assertEquals(2, prjsArgCaptor.getValue().size());
    }

    @Test
    public void shouldEncodeUrlAndCreateFile() {
        client.createFile(ProjectServiceClientTest.resourcePath, ProjectServiceClientTest.TEXT);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(asyncRequest).data(ProjectServiceClientTest.TEXT);
        Mockito.verify(loaderFactory).newLoader("Creating file...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send(unmarshallableItemRef);
    }

    @Test
    public void shouldEncodeUrlAndGetFileContent() {
        client.getFileContent(ProjectServiceClientTest.resourcePath);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).send(ArgumentMatchers.any(StringUnmarshaller.class));
    }

    @Test
    public void shouldEncodeUrlAndSetFileContent() {
        client.setFileContent(ProjectServiceClientTest.resourcePath, ProjectServiceClientTest.TEXT);
        Mockito.verify(requestFactory).createRequest(ArgumentMatchers.eq(PUT), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        Mockito.verify(asyncRequest).data(ProjectServiceClientTest.TEXT);
        Mockito.verify(asyncRequest).send();
    }

    @Test
    public void shouldEncodeUrlAndCreateFolder() {
        client.createFolder(ProjectServiceClientTest.resourcePath);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(loaderFactory).newLoader("Creating folder...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(unmarshaller).newUnmarshaller(ItemReference.class);
        Mockito.verify(asyncRequest).send(unmarshallableItemRef);
    }

    @Test
    public void shouldEncodeUrlAndDeleteFolder() {
        client.deleteItem(ProjectServiceClientTest.resourcePath);
        Mockito.verify(requestFactory).createRequest(ArgumentMatchers.eq(DELETE), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(false));
        Mockito.verify(loaderFactory).newLoader("Deleting resource...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send();
    }

    @Test
    public void shouldEncodeUrlAndCopyResource() {
        CopyOptions copyOptions = Mockito.mock(CopyOptions.class);
        Mockito.when(dtoFactory.createDto(CopyOptions.class)).thenReturn(copyOptions);
        client.copy(ProjectServiceClientTest.resourcePath, ProjectServiceClientTest.targetPath, ProjectServiceClientTest.TEXT, true);
        Mockito.verify(dtoFactory).createDto(CopyOptions.class);
        Mockito.verify(copyOptions).setName(ArgumentMatchers.any());
        Mockito.verify(copyOptions).setOverWrite(true);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.any(), ArgumentMatchers.eq(copyOptions));
        Mockito.verify(loaderFactory).newLoader("Copying...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send();
    }

    @Test
    public void shouldEncodeUrlAndMoveResource() {
        MoveOptions moveOptions = Mockito.mock(MoveOptions.class);
        Mockito.when(dtoFactory.createDto(MoveOptions.class)).thenReturn(moveOptions);
        client.move(ProjectServiceClientTest.resourcePath, ProjectServiceClientTest.targetPath, ProjectServiceClientTest.TEXT, true);
        Mockito.verify(dtoFactory).createDto(MoveOptions.class);
        Mockito.verify(moveOptions).setName(ArgumentMatchers.any());
        Mockito.verify(moveOptions).setOverWrite(true);
        Mockito.verify(requestFactory).createPostRequest(ArgumentMatchers.any(), ArgumentMatchers.eq(moveOptions));
        Mockito.verify(loaderFactory).newLoader("Moving...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(asyncRequest).send();
    }

    @Test
    public void shouldEncodeUrlAndGetTree() {
        client.getTree(ProjectServiceClientTest.resourcePath, 2, true);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(unmarshaller).newUnmarshaller(TreeElement.class);
        Mockito.verify(asyncRequest).send(unmarshallableTreeElem);
    }

    @Test
    public void shouldEncodeUrlAndGetItem() {
        client.getItem(ProjectServiceClientTest.resourcePath);
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Getting item...");
        Mockito.verify(unmarshaller).newUnmarshaller(ItemReference.class);
        Mockito.verify(asyncRequest).send(unmarshallableItemRef);
    }

    @Test
    public void shouldEncodeUrlAndGetProject() {
        client.getProject(Path.valueOf(ProjectServiceClientTest.TEXT));
        Mockito.verify(requestFactory).createGetRequest(ArgumentMatchers.any());
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Getting project...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(unmarshaller).newUnmarshaller(ProjectConfigDto.class);
        Mockito.verify(asyncRequest).send(unmarshallablePrjConf);
    }

    @Test
    public void shouldEncodeUrlAndUpdateProject() {
        Mockito.when(requestFactory.createRequest(ArgumentMatchers.any(Method.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(ProjectConfig.class), ArgumentMatchers.anyBoolean())).thenReturn(asyncRequest);
        Mockito.when(prjConfig1.getPath()).thenReturn(ProjectServiceClientTest.TEXT);
        client.updateProject(prjConfig1);
        Mockito.verify(requestFactory).createRequest(ArgumentMatchers.eq(PUT), ArgumentMatchers.anyString(), ArgumentMatchers.eq(prjConfig1), ArgumentMatchers.eq(false));
        Mockito.verify(asyncRequest).header(CONTENT_TYPE, MimeType.APPLICATION_JSON);
        Mockito.verify(asyncRequest).header(ACCEPT, MimeType.APPLICATION_JSON);
        Mockito.verify(loaderFactory).newLoader("Updating project...");
        Mockito.verify(asyncRequest).loader(messageLoader);
        Mockito.verify(unmarshaller).newUnmarshaller(ProjectConfigDto.class);
        Mockito.verify(asyncRequest).send(unmarshallablePrjConf);
    }
}

