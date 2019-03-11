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
package org.eclipse.che.ide.ui.button;


import ButtonResources.Css;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.ui.button.ConsoleButton.ActionDelegate;
import org.eclipse.che.ide.ui.tooltip.TooltipWidget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConsoleButtonImplTest {
    private static final String SOME_MESSAGE = "some message";

    @Mock
    private ButtonResources resources;

    @Mock
    private TooltipWidget tooltip;

    @Mock
    private Css runnerCss;

    private ConsoleButtonImpl widget;

    @Test
    public void constructorActionsShouldBeValidated() throws Exception {
        Mockito.verify(tooltip).setDescription(ConsoleButtonImplTest.SOME_MESSAGE);
        Mockito.verify(runnerCss).activeConsoleButton();
        Mockito.verify(runnerCss).whiteColor();
    }

    @Test
    public void checkStatusShouldBeChangedToCheckedStyle() throws Exception {
        Mockito.reset(runnerCss);
        widget.setCheckedStatus(true);
        Mockito.verify(runnerCss).activeConsoleButton();
        Mockito.verify(runnerCss).whiteColor();
    }

    @Test
    public void checkStatusShouldBeChangedToUncheckedStyle() throws Exception {
        Mockito.reset(runnerCss);
        widget.setCheckedStatus(false);
        Mockito.verify(runnerCss).activeConsoleButton();
        Mockito.verify(runnerCss).whiteColor();
    }

    @Test
    public void clickActionShouldBeDelegated() throws Exception {
        ActionDelegate delegate = Mockito.mock(ActionDelegate.class);
        widget.setDelegate(delegate);
        widget.onClick(Mockito.mock(ClickEvent.class));
        Mockito.verify(delegate).onButtonClicked();
    }

    @Test
    public void tooltipShouldBeHidden() throws Exception {
        widget.onMouseOut(Mockito.mock(MouseOutEvent.class));
        Mockito.verify(tooltip).hide();
    }

    @Test
    public void tooltipShouldBeShown() throws Exception {
        widget.onMouseOver(Mockito.mock(MouseOverEvent.class));
        Mockito.verify(tooltip).setPopupPosition(0, ConsoleButtonImpl.TOP_TOOLTIP_SHIFT);
        Mockito.verify(tooltip).show();
    }
}

