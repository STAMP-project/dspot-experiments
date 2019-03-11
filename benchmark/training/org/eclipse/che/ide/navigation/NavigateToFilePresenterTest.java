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
package org.eclipse.che.ide.navigation;


import Path.ROOT;
import com.google.common.base.Optional;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.api.core.jsonrpc.commons.RequestTransmitter;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link NavigateToFilePresenter}.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyi
 * @author Vlad Zhukovskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigateToFilePresenterTest {
    @Mock
    private NavigateToFileView view;

    @Mock
    private EventBus eventBus;

    @Mock
    private Container container;

    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Mock
    private Promise<Optional<File>> optFilePromise;

    @Mock
    private AppContext appContext;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private RequestTransmitter requestTransmitter;

    private NavigateToFilePresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();
        Mockito.verify(view).showPopup();
    }

    @Test
    public void testOnFileSelected() throws Exception {
        presenter.onFileSelected(ROOT);
        Mockito.verify(view).hidePopup();
        Mockito.verify(container).getFile(ArgumentMatchers.eq(ROOT));
    }
}

