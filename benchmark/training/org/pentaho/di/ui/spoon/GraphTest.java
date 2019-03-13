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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.trans.TransGraph;


public class GraphTest {
    @Test
    public void testRightClickStepSelection() {
        TransGraph graph = Mockito.mock(TransGraph.class);
        StepMeta meta1 = Mockito.mock(StepMeta.class);
        StepMeta meta2 = Mockito.mock(StepMeta.class);
        StepMeta meta3 = Mockito.mock(StepMeta.class);
        wireSelected(meta1, meta2, meta3);
        List<StepMeta> selected = new ArrayList<>(2);
        meta2.setSelected(true);
        meta3.setSelected(true);
        selected.add(meta2);
        selected.add(meta3);
        Mockito.doCallRealMethod().when(graph).doRightClickSelection(meta1, selected);
        graph.doRightClickSelection(meta1, selected);
        Assert.assertTrue(meta1.isSelected());
        Assert.assertEquals(meta1, selected.get(0));
        Assert.assertEquals(1, selected.size());
        Assert.assertFalse(((meta2.isSelected()) || (meta3.isSelected())));
    }

    @Test
    public void testRightClickAlreadySelected() {
        TransGraph graph = Mockito.mock(TransGraph.class);
        StepMeta meta1 = Mockito.mock(StepMeta.class);
        StepMeta meta2 = Mockito.mock(StepMeta.class);
        wireSelected(meta1, meta2);
        List<StepMeta> selected = new ArrayList<>(2);
        meta1.setSelected(true);
        meta2.setSelected(true);
        selected.add(meta1);
        selected.add(meta2);
        Mockito.doCallRealMethod().when(graph).doRightClickSelection(meta1, selected);
        graph.doRightClickSelection(meta1, selected);
        Assert.assertEquals(2, selected.size());
        Assert.assertTrue(selected.contains(meta1));
        Assert.assertTrue(selected.contains(meta2));
        Assert.assertTrue(((meta1.isSelected()) && (meta2.isSelected())));
    }

    @Test
    public void testRightClickNoSelection() {
        TransGraph graph = Mockito.mock(TransGraph.class);
        StepMeta meta1 = Mockito.mock(StepMeta.class);
        wireSelected(meta1);
        List<StepMeta> selected = new ArrayList<>();
        Mockito.doCallRealMethod().when(graph).doRightClickSelection(meta1, selected);
        graph.doRightClickSelection(meta1, selected);
        Assert.assertEquals(1, selected.size());
        Assert.assertTrue(selected.contains(meta1));
        Assert.assertTrue(meta1.isSelected());
    }

    @Test
    public void testDelJobNoSelections() {
        JobMeta jobMeta = Mockito.mock(JobMeta.class);
        Spoon spoon = Mockito.mock(Spoon.class);
        Mockito.when(jobMeta.getSelectedEntries()).thenReturn(Collections.<JobEntryCopy>emptyList());
        JobEntryCopy je = Mockito.mock(JobEntryCopy.class);
        JobGraph jobGraph = Mockito.mock(JobGraph.class);
        Mockito.doCallRealMethod().when(jobGraph).setJobMeta(ArgumentMatchers.any(JobMeta.class));
        Mockito.doCallRealMethod().when(jobGraph).setSpoon(ArgumentMatchers.any(Spoon.class));
        Mockito.doCallRealMethod().when(jobGraph).delSelected(ArgumentMatchers.any(JobEntryCopy.class));
        jobGraph.setJobMeta(jobMeta);
        jobGraph.setSpoon(spoon);
        jobGraph.delSelected(je);
        Mockito.verify(spoon).deleteJobEntryCopies(jobMeta, je);
    }

    @Test
    public void testDelSelectionsJob() {
        JobMeta jobMeta = Mockito.mock(JobMeta.class);
        Spoon spoon = Mockito.mock(Spoon.class);
        JobEntryCopy selected1 = Mockito.mock(JobEntryCopy.class);
        JobEntryCopy selected2 = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobMeta.getSelectedEntries()).thenReturn(Arrays.asList(selected1, selected2));
        JobGraph jobGraph = Mockito.mock(JobGraph.class);
        Mockito.doCallRealMethod().when(jobGraph).setJobMeta(ArgumentMatchers.any(JobMeta.class));
        Mockito.doCallRealMethod().when(jobGraph).setSpoon(ArgumentMatchers.any(Spoon.class));
        Mockito.doCallRealMethod().when(jobGraph).delSelected(ArgumentMatchers.any(JobEntryCopy.class));
        jobGraph.setJobMeta(jobMeta);
        jobGraph.setSpoon(spoon);
        jobGraph.delSelected(null);
        Mockito.verify(spoon).deleteJobEntryCopies(ArgumentMatchers.eq(jobMeta), AdditionalMatchers.aryEq(new JobEntryCopy[]{ selected1, selected2 }));
    }
}

