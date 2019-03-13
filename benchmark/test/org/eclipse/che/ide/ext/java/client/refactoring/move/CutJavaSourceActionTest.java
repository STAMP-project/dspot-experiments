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
package org.eclipse.che.ide.ext.java.client.refactoring.move;


import Constants.LANGUAGE;
import SourceFolderMarker.ID;
import com.google.common.base.Optional;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class CutJavaSourceActionTest {
    @Mock
    private JavaLocalizationConstant locale;

    @Mock
    private MoveAction moveAction;

    @Mock
    private EventBus eventBus;

    @Mock
    private FileTypeRegistry fileTypeRegistry;

    @Mock
    private AppContext appContext;

    @Mock
    private ActionEvent updateActionEvent;

    @Mock
    private Presentation presentation;

    @Mock
    private File resource;

    @Mock
    private Project project;

    @Mock
    private Resource srcFolder;

    @Mock
    private FileType fileType;

    private CutJavaSourceAction action;

    @Test
    public void actionShouldBeEnabledWhenFolderInContext() throws Exception {
        final Container container = Mockito.mock(Container.class);
        Mockito.when(updateActionEvent.getPresentation()).thenReturn(presentation);
        Mockito.when(appContext.getResources()).thenReturn(new Resource[]{ container });
        Mockito.when(container.getRelatedProject()).thenReturn(Optional.of(project));
        Mockito.when(container.getParentWithMarker(ArgumentMatchers.eq(ID))).thenReturn(Optional.of(srcFolder));
        final Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(LANGUAGE, Collections.singletonList("java"));
        Mockito.when(project.getAttributes()).thenReturn(attributes);
        action.update(updateActionEvent);
        Mockito.verify(presentation).setEnabled(ArgumentMatchers.eq(true));
    }

    @Test
    public void actionShouldPerformAction() throws Exception {
        action.actionPerformed(updateActionEvent);
        Mockito.verify(moveAction).actionPerformed(ArgumentMatchers.eq(updateActionEvent));
    }
}

