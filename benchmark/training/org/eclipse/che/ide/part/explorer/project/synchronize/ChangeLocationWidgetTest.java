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
package org.eclipse.che.ide.part.explorer.project.synchronize;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ChangeLocationWidgetTest {
    @Mock
    private TextBox textBox;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private Label label;

    @InjectMocks
    private ChangeLocationWidget widget;

    @Test
    public void widgetShouldBeInitialized() {
        Mockito.verify(locale).locationIncorrect();
        Mockito.verify(textBox).setWidth("420px");
    }

    @Test
    public void textShouldBeReturned() {
        widget.getText();
        Mockito.verify(textBox).getText();
    }
}

