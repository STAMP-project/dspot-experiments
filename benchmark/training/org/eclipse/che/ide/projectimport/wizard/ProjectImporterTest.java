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
package org.eclipse.che.ide.projectimport.wizard;


import MutableProjectConfig.MutableSourceStorage;
import Project.ProjectRequest;
import Wizard.CompleteCallback;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.auth.OAuthServiceClient;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Dmitry Shnurenko
 * @author Vlad Zhukovskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectImporterTest {
    // constructors mocks
    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Mock
    private ImportProjectNotificationSubscriberFactory subscriberFactory;

    @Mock
    private AppContext appContext;

    @Mock
    private ProjectResolver resolver;

    // additional mocks
    @Mock
    private ProjectNotificationSubscriber subscriber;

    @Mock
    private MutableProjectConfig projectConfig;

    @Mock
    private MutableSourceStorage source;

    @Mock
    private CompleteCallback completeCallback;

    @Mock
    private Container workspaceRoot;

    @Mock
    private ProjectRequest importRequest;

    @Mock
    private Promise<Project> importPromise;

    @Mock
    private Project importedProject;

    @Mock
    private OAuthServiceClient oAuthServiceClient;

    @Mock
    private DtoUnmarshallerFactory unmarshallerFactory;

    @Captor
    private ArgumentCaptor<Function<Project, Promise<Project>>> importProjectCaptor;

    private ProjectImporter importer;

    @Test
    public void importShouldBeSuccessAndProjectStartsResolving() throws Exception {
        importer.importProject(completeCallback, projectConfig);
        Mockito.verify(importPromise).thenPromise(importProjectCaptor.capture());
        importProjectCaptor.getValue().apply(importedProject);
        Mockito.verify(subscriber).onSuccess();
        Mockito.verify(resolver).resolve(ArgumentMatchers.eq(importedProject));
    }
}

