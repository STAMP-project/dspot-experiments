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
package org.eclipse.che.ide.ui.tooltip;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TooltipWidgetImplTest {
    private static final String SOME_TEXT = "some text";

    @InjectMocks
    private TooltipWidgetImpl tooltip;

    @Test
    public void descriptionShouldBeSet() throws Exception {
        tooltip.setDescription(TooltipWidgetImplTest.SOME_TEXT);
        Mockito.verify(tooltip.description).setText(TooltipWidgetImplTest.SOME_TEXT);
    }
}

