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
package org.eclipse.che.ide.ui.dialogs.input;


import com.google.gwt.event.dom.client.ClickEvent;
import org.eclipse.che.ide.ui.UILocalizationConstant;
import org.eclipse.che.ide.ui.dialogs.BaseTest;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link InputDialogFooter} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class InputDialogFooterTest extends BaseTest {
    @Mock
    private UILocalizationConstant uiLocalizationConstant;

    @Mock
    private InputDialogView.ActionDelegate actionDelegate;

    @InjectMocks
    private InputDialogFooter footer;

    @Test
    public void shouldCallAcceptedOnOkClicked() throws Exception {
        footer.handleOkClick(Mockito.mock(ClickEvent.class));
        Mockito.verify(actionDelegate).accepted();
    }

    @Test
    public void shouldCallCancelledOnCancelClicked() throws Exception {
        footer.handleCancelClick(Mockito.mock(ClickEvent.class));
        Mockito.verify(actionDelegate).cancelled();
    }
}

