/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.Gui.GraphWindows.Undo;


import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.config.ConfigManager;
import com.google.security.zynamics.binnavi.config.FileReadException;
import com.google.security.zynamics.binnavi.disassembly.MockView;
import com.google.security.zynamics.binnavi.yfileswrap.Gui.GraphWindows.Loader.CGraphBuilder;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.ZyGraph;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CSelectionHistoryTest {
    @Test
    public void testUndo() throws LoadCancelledException, FileReadException {
        ConfigManager.instance().read();
        final ZyGraph graph = CGraphBuilder.buildGraph(new MockView());
        final CSelectionHistory history = new CSelectionHistory(graph, 4);
        final CSelectionSnapshot snapshot1 = new CSelectionSnapshot(new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode>());
        final CSelectionSnapshot snapshot2 = new CSelectionSnapshot(new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode>());
        final CSelectionSnapshot snapshot3 = new CSelectionSnapshot(new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode>());
        final CSelectionSnapshot snapshot4 = new CSelectionSnapshot(new ArrayList<com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode>());
        history.addSnapshot(snapshot1);
        history.addSnapshot(snapshot2);
        history.addSnapshot(snapshot3);
        history.addSnapshot(snapshot4);
        // First Undo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot3, history.getUndoSnapshot(true));
        // Second Undo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot2, history.getUndoSnapshot(true));
        // Third Undo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot1, history.getUndoSnapshot(true));
        // Fourth Undo (Fail) + First Redo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot1, history.getUndoSnapshot(true));
        Assert.assertEquals(snapshot2, history.getUndoSnapshot(false));
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        // Second Redo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot3, history.getUndoSnapshot(false));
        // Third Redo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot4, history.getUndoSnapshot(false));
        // Fourth Redo (Fail) + Last Undo
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
        Assert.assertEquals(snapshot4, history.getUndoSnapshot(false));
        Assert.assertEquals(snapshot3, history.getUndoSnapshot(true));
        Assert.assertTrue(history.canUndo());
        Assert.assertTrue(history.canRedo());
    }
}

