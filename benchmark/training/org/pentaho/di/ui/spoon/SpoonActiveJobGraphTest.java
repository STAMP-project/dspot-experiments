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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.xul.swt.tab.TabItem;


public class SpoonActiveJobGraphTest {
    private Spoon spoon;

    @Test
    public void returnNullActiveJobGraphIfJobTabNotExists() {
        JobGraph actualJobGraph = spoon.getActiveJobGraph();
        Assert.assertNull(actualJobGraph);
    }

    @Test
    public void returnActiveJobGraphIfJobTabExists() {
        TabMapEntry tabMapEntry = Mockito.mock(TabMapEntry.class);
        JobGraph jobGraph = Mockito.mock(JobGraph.class);
        Mockito.when(tabMapEntry.getObject()).thenReturn(jobGraph);
        Mockito.when(spoon.delegates.tabs.getTab(Mockito.any(TabItem.class))).thenReturn(tabMapEntry);
        JobGraph actualJobGraph = spoon.getActiveJobGraph();
        Assert.assertNotNull(actualJobGraph);
    }
}

