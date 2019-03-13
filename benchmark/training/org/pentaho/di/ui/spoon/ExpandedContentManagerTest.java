/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon;


import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.ui.spoon.trans.TransGraph;


public class ExpandedContentManagerTest {
    @Test
    public void testIsBrowserVisibleTransGraph() {
        TransGraph transGraphMock = Mockito.mock(TransGraph.class);
        Control control1 = Mockito.mock(Control.class);
        Control control2 = Mockito.mock(Control.class);
        Browser browser = Mockito.mock(Browser.class);
        Control[] children = new Control[]{ control1, control2, browser };
        Mockito.when(transGraphMock.getChildren()).thenReturn(children);
        Boolean result = ExpandedContentManager.isVisible(transGraphMock);
        Assert.assertFalse(result);
        children = new Control[]{ browser, control1, control2 };
        Mockito.when(transGraphMock.getChildren()).thenReturn(children);
        result = ExpandedContentManager.isVisible(transGraphMock);
        Assert.assertTrue(result);
    }

    @Test
    public void testShowTransformationBrowserh() {
        TransGraph transGraphMock = Mockito.mock(TransGraph.class);
        Control control1 = Mockito.mock(Control.class);
        Control control2 = Mockito.mock(Control.class);
        Browser browser = Mockito.mock(Browser.class);
        SashForm sash = Mockito.mock(SashForm.class);
        Mockito.when(sash.getWeights()).thenReturn(new int[]{ 277, 722 });
        Composite comp1 = Mockito.mock(Composite.class);
        Composite comp2 = Mockito.mock(Composite.class);
        Composite comp3 = Mockito.mock(Composite.class);
        Composite comp4 = Mockito.mock(Composite.class);
        Mockito.when(browser.getParent()).thenReturn(comp1);
        Mockito.when(comp1.getParent()).thenReturn(comp2);
        Mockito.when(comp2.getParent()).thenReturn(comp3);
        Mockito.when(comp3.getParent()).thenReturn(sash);
        Mockito.when(comp4.getParent()).thenReturn(sash);
        Control[] children = new Control[]{ control1, control2, browser };
        Mockito.when(transGraphMock.getChildren()).thenReturn(children);
        ExpandedContentManager.createExpandedContent(transGraphMock, "");
        Mockito.verify(browser).setUrl("");
    }

    @Test
    public void testHideExpandedContentManager() throws Exception {
        TransGraph transGraph = Mockito.mock(TransGraph.class);
        Browser browser = Mockito.mock(Browser.class);
        SashForm sashForm = Mockito.mock(SashForm.class);
        Composite parent = setupExpandedContentMocks(transGraph, browser, sashForm);
        ExpandedContentManager.hideExpandedContent(transGraph);
        Mockito.verify(browser).moveBelow(null);
        Mockito.verify(parent).layout(true, true);
        Mockito.verify(parent).redraw();
        Mockito.verify(sashForm).setWeights(new int[]{ 3, 2, 1 });
    }

    @Test
    public void testCloseExpandedContentManager() throws Exception {
        TransGraph transGraph = Mockito.mock(TransGraph.class);
        Browser browser = Mockito.mock(Browser.class);
        SashForm sashForm = Mockito.mock(SashForm.class);
        setupExpandedContentMocks(transGraph, browser, sashForm);
        ExpandedContentManager.closeExpandedContent(transGraph);
        Mockito.verify(browser).close();
        Mockito.verify(sashForm).setWeights(new int[]{ 3, 2, 1 });
    }
}

