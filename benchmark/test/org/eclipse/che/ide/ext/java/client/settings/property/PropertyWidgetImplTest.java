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
package org.eclipse.che.ide.ext.java.client.settings.property;


import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorWarningsOptions;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget.ActionDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PropertyWidgetImplTest {
    private static final String SOME_TEXT = "someText";

    @Mock
    private PropertyNameManager nameManager;

    @Mock
    private ActionDelegate delegate;

    @Mock
    private ChangeEvent event;

    private PropertyWidgetImpl widget;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(nameManager).getName(ErrorWarningsOptions.DEAD_CODE);
        Mockito.verify(widget.title).setText(PropertyWidgetImplTest.SOME_TEXT);
        Mockito.verify(widget.property).addItem(PropertyWidgetImpl.IGNORE);
        Mockito.verify(widget.property).addItem(PropertyWidgetImpl.WARNING);
        Mockito.verify(widget.property).addItem(PropertyWidgetImpl.ERROR);
    }

    @Test
    public void propertyValueShouldBeSelected() {
        Mockito.when(widget.property.getItemCount()).thenReturn(3);
        Mockito.when(widget.property.getValue(1)).thenReturn(PropertyWidgetImpl.WARNING);
        widget.selectPropertyValue(PropertyWidgetImpl.WARNING);
        Mockito.verify(widget.property).setItemSelected(1, true);
    }

    @Test
    public void onPropertyShouldBeChanged() {
        Mockito.when(widget.property.getSelectedIndex()).thenReturn(1);
        Mockito.when(widget.property.getValue(1)).thenReturn(PropertyWidgetImplTest.SOME_TEXT);
        widget.onPropertyChanged(event);
        Mockito.verify(delegate).onPropertyChanged();
    }

    @Test
    public void optionIdShouldBeReturned() {
        TestCase.assertEquals(widget.getOptionId(), ErrorWarningsOptions.DEAD_CODE);
    }
}

