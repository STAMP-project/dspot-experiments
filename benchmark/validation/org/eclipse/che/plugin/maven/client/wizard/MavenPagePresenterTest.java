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
package org.eclipse.che.plugin.maven.client.wizard;


import com.google.common.base.Optional;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Map;
import org.eclipse.che.api.project.shared.dto.SourceEstimation;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.plugin.maven.client.MavenLocalizationConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(MockitoJUnitRunner.class)
public class MavenPagePresenterTest {
    private static final String TEXT = "to be or not to be";

    @Mock
    private MavenPageView view;

    @Mock
    private EventBus eventBus;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private AppContext appContext;

    @Mock
    private MavenLocalizationConstant localization;

    @InjectMocks
    private MavenPagePresenter mavenPagePresenter;

    @Mock
    private MutableProjectConfig projectConfig;

    @Mock
    private Container workspaceRoot;

    @Mock
    private Promise<Optional<Container>> containerPromise;

    @Mock
    private Optional<Container> optionalContainer;

    @Mock
    private Promise<SourceEstimation> sourceEstimationPromise;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> containerArgumentErrorCapture;

    @Captor
    private ArgumentCaptor<Operation<Optional<Container>>> optionContainerCapture;

    private Map<String, String> context;

    @Test
    public void constructorShouldBePerformed() throws Exception {
        Mockito.verify(view).setDelegate(mavenPagePresenter);
    }

    @Test
    public void warningWindowShouldBeShowedIfProjectEstimationHasSomeError() throws Exception {
        final String dialogTitle = "Not valid Maven project";
        PromiseError promiseError = Mockito.mock(PromiseError.class);
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        context.put(WIZARD_MODE_KEY, UPDATE.toString());
        Mockito.when(promiseError.getMessage()).thenReturn(MavenPagePresenterTest.TEXT);
        Mockito.when(localization.mavenPageErrorDialogTitle()).thenReturn(dialogTitle);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyObject())).thenReturn(messageDialog);
        Mockito.when(sourceEstimationPromise.then(ArgumentMatchers.<Operation<SourceEstimation>>anyObject())).thenReturn(sourceEstimationPromise);
        Mockito.when(sourceEstimationPromise.catchError(ArgumentMatchers.<Operation<PromiseError>>anyObject())).thenReturn(sourceEstimationPromise);
        mavenPagePresenter.init(projectConfig);
        Mockito.verify(containerPromise).then(optionContainerCapture.capture());
        optionContainerCapture.getValue().apply(optionalContainer);
        Mockito.verify(sourceEstimationPromise).catchError(containerArgumentErrorCapture.capture());
        containerArgumentErrorCapture.getValue().apply(promiseError);
        Mockito.verify(promiseError).getMessage();
        Mockito.verify(dialogFactory).createMessageDialog(dialogTitle, MavenPagePresenterTest.TEXT, null);
        Mockito.verify(messageDialog).show();
    }
}

