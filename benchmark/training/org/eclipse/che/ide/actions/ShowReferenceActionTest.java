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
package org.eclipse.che.ide.actions;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.reference.ShowReferencePresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class ShowReferenceActionTest {
    @Mock
    private ShowReferencePresenter showReferencePresenter;

    @Mock
    private SelectionAgent selectionAgent;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private ActionEvent event;

    @Mock
    private Presentation presentation;

    @Mock
    private AppContext appContext;

    @Mock
    private Resource resource;

    @InjectMocks
    private ShowReferenceAction action;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(locale).showReference();
    }

    @Test
    public void presentationShouldNotBeVisibleWhenSelectedElementIsNull() {
        Mockito.when(appContext.getResource()).thenReturn(null);
        Mockito.when(appContext.getResources()).thenReturn(null);
        action.update(event);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(false);
    }

    @Test
    public void presentationShouldBeVisibleWhenSelectedElementIsHasStorablePathNode() {
        action.update(event);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(true);
    }

    @Test
    public void actionShouldBePerformed() {
        action.update(event);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(true);
        action.actionPerformed(event);
        Mockito.verify(showReferencePresenter).show(resource);
    }
}

