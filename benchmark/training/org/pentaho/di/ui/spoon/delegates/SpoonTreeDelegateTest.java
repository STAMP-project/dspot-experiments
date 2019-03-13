/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.spoon.delegates;


import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.TreeSelection;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConstUI.class, PluginRegistry.class })
public class SpoonTreeDelegateTest {
    private Spoon spoon = Mockito.mock(Spoon.class);

    @Test
    public void getTreeObjects_getStepByName() {
        SpoonTreeDelegate std = Mockito.spy(new SpoonTreeDelegate(spoon));
        Tree selection = Mockito.mock(Tree.class);
        Tree core = Mockito.mock(Tree.class);
        TreeItem item = Mockito.mock(TreeItem.class);
        PluginInterface step = Mockito.mock(PluginInterface.class);
        PluginRegistry registry = Mockito.mock(PluginRegistry.class);
        TreeItem[] items = new TreeItem[]{ item };
        Mockito.when(ConstUI.getTreeStrings(item)).thenReturn(new String[]{ "Output", "Delete" });
        Mockito.when(PluginRegistry.getInstance()).thenReturn(registry);
        Mockito.doReturn(items).when(core).getSelection();
        Mockito.doReturn(null).when(item).getData(ArgumentMatchers.anyString());
        Mockito.doReturn(step).when(registry).findPluginWithName(StepPluginType.class, "Delete");
        spoon.showJob = false;
        spoon.showTrans = true;
        TreeSelection[] ts = std.getTreeObjects(core, selection, core);
        Assert.assertEquals(1, ts.length);
        Assert.assertEquals(step, ts[0].getSelection());
    }

    @Test
    public void getTreeObjects_getStepById() {
        SpoonTreeDelegate std = Mockito.spy(new SpoonTreeDelegate(spoon));
        Tree selection = Mockito.mock(Tree.class);
        Tree core = Mockito.mock(Tree.class);
        TreeItem item = Mockito.mock(TreeItem.class);
        PluginInterface step = Mockito.mock(PluginInterface.class);
        PluginRegistry registry = Mockito.mock(PluginRegistry.class);
        TreeItem[] items = new TreeItem[]{ item };
        Mockito.when(ConstUI.getTreeStrings(item)).thenReturn(new String[]{ "Output", "Avro Output" });
        Mockito.when(PluginRegistry.getInstance()).thenReturn(registry);
        Mockito.doReturn(items).when(core).getSelection();
        Mockito.doReturn("AvroOutputPlugin").when(item).getData(ArgumentMatchers.anyString());
        Mockito.doReturn(step).when(registry).findPluginWithId(StepPluginType.class, "AvroOutputPlugin");
        spoon.showJob = false;
        spoon.showTrans = true;
        TreeSelection[] ts = std.getTreeObjects(core, selection, core);
        Assert.assertEquals(1, ts.length);
        Assert.assertEquals(step, ts[0].getSelection());
    }
}

