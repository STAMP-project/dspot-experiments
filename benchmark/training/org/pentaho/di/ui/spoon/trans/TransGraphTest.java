/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon.trans;


import TransGraph.TRANS_GRAPH_ENTRY_AGAIN;
import TransGraph.TRANS_GRAPH_ENTRY_SNIFF;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.events.MouseEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.containers.XulMenu;
import org.pentaho.ui.xul.dom.Document;


public class TransGraphTest {
    private static final boolean TRUE_RESULT = true;

    @Test
    public void testMouseUpHopGetsSelected() {
        MouseEvent event = Mockito.mock(MouseEvent.class);
        int x = 0;
        int y = 0;
        TransGraph transGraph = Mockito.mock(TransGraph.class);
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        StepErrorMeta errorMeta = new StepErrorMeta(null, null);
        TransHopMeta selectedHop = new TransHopMeta();
        selectedHop.setErrorHop(true);
        selectedHop.setEnabled(TransGraphTest.TRUE_RESULT);
        selectedHop.setFromStep(stepMeta);
        Mockito.when(stepMeta.getStepErrorMeta()).thenReturn(errorMeta);
        Mockito.when(transGraph.findHop(x, y)).thenReturn(selectedHop);
        Mockito.when(transGraph.screen2real(ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(Integer.class))).thenReturn(new Point(x, y));
        Mockito.doCallRealMethod().when(transGraph).mouseUp(event);
        transGraph.mouseUp(event);
        Assert.assertTrue(errorMeta.isEnabled());
    }

    @Test
    public void testEnableHopGetsSelected() {
        TransGraph transGraph = Mockito.mock(TransGraph.class);
        Mockito.doCallRealMethod().when(transGraph).setTransMeta(ArgumentMatchers.any(TransMeta.class));
        Mockito.doCallRealMethod().when(transGraph).setSpoon(ArgumentMatchers.any(Spoon.class));
        transGraph.setTransMeta(new TransMeta());
        transGraph.setSpoon(Mockito.mock(Spoon.class));
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        StepErrorMeta errorMeta = new StepErrorMeta(null, null);
        TransHopMeta selectedHop = new TransHopMeta();
        selectedHop.setErrorHop(true);
        selectedHop.setEnabled(false);
        selectedHop.setFromStep(stepMeta);
        Mockito.when(stepMeta.getStepErrorMeta()).thenReturn(errorMeta);
        StepMeta toStep = new StepMeta();
        toStep.setName("toStep");
        selectedHop.setToStep(toStep);
        Mockito.when(transGraph.getCurrentHop()).thenReturn(selectedHop);
        Mockito.doCallRealMethod().when(transGraph).enableHop();
        transGraph.enableHop();
        Assert.assertTrue(errorMeta.isEnabled());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitializeXulMenu() throws KettleException {
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        TransGraph transGraph = Mockito.mock(TransGraph.class);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Document document = Mockito.mock(Document.class);
        XulMenuitem xulItem = Mockito.mock(XulMenuitem.class);
        XulMenu xulMenu = Mockito.mock(XulMenu.class);
        StepErrorMeta stepErrorMeta = Mockito.mock(StepErrorMeta.class);
        Spoon spoon = Mockito.mock(Spoon.class);
        List<StepMeta> selection = Arrays.asList(new StepMeta(), stepMeta, new StepMeta());
        Mockito.doCallRealMethod().when(transGraph).setTransMeta(ArgumentMatchers.any(TransMeta.class));
        Mockito.doCallRealMethod().when(transGraph).setSpoon(ArgumentMatchers.any(Spoon.class));
        transGraph.setTransMeta(transMeta);
        transGraph.setSpoon(spoon);
        Mockito.when(stepMeta.getStepErrorMeta()).thenReturn(stepErrorMeta);
        Mockito.when(stepMeta.isDrawn()).thenReturn(true);
        Mockito.when(document.getElementById(ArgumentMatchers.any(String.class))).thenReturn(xulItem);
        Mockito.when(document.getElementById(TRANS_GRAPH_ENTRY_AGAIN)).thenReturn(xulMenu);
        Mockito.when(document.getElementById(TRANS_GRAPH_ENTRY_SNIFF)).thenReturn(xulMenu);
        Mockito.doCallRealMethod().when(transGraph).initializeXulMenu(ArgumentMatchers.any(Document.class), ArgumentMatchers.any(List.class), ArgumentMatchers.any(StepMeta.class));
        transGraph.initializeXulMenu(document, selection, stepMeta);
        Mockito.verify(transMeta).isAnySelectedStepUsedInTransHops();
    }
}

