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


import InputDialogView.ActionDelegate;
import org.eclipse.che.ide.ui.UILocalizationConstant;
import org.eclipse.che.ide.ui.dialogs.BaseTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link InputDialogViewImpl} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class InputDialogViewTest extends BaseTest {
    @Mock
    private ActionDelegate actionDelegate;

    @Mock
    private UILocalizationConstant uiLocalizationConstant;

    @Mock
    private InputDialogFooter footer;

    private InputDialogViewImpl view;

    @Test
    public void shouldSetDelegateOnFooter() throws Exception {
        view.setDelegate(actionDelegate);
        Mockito.verify(footer).setDelegate(ArgumentMatchers.eq(actionDelegate));
    }
}

