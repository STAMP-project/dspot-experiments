/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.jvm.object;


import HBox.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.widget.IntText;
import java.util.Arrays;
import org.junit.Test;


public class GridOutputContainerLayoutManagerTest {
    private KernelTest kernel;

    @Test
    public void createLayoutManagerWithoutParam_hasTwoColumns() throws Exception {
        // when
        GridOutputContainerLayoutManager manager = new GridOutputContainerLayoutManager();
        // then
        assertThat(manager.getColumns()).isEqualTo(2);
    }

    @Test
    public void createLayoutManagerWithParam_hasCountColumnsEqualsThatParam() throws Exception {
        int columns = 5;
        // when
        GridOutputContainerLayoutManager manager = new GridOutputContainerLayoutManager(columns);
        // then
        assertThat(manager.getColumns()).isEqualTo(columns);
    }

    @Test
    public void dispaly_publishMessagesWithGridAndHBox() throws Exception {
        // given
        GridOutputContainerLayoutManager manager = new GridOutputContainerLayoutManager(2);
        OutputContainer container = new OutputContainer(Arrays.asList(new IntText(), new IntText()));
        container.setLayoutManager(manager);
        // when
        manager.display(container);
        // then
        verifyView(kernel.getPublishedMessages(), VIEW_NAME_VALUE);
        verifyView(kernel.getPublishedMessages(), GridView.VIEW_NAME_VALUE);
    }

    @Test
    public void dispaly_activateChildren() throws Exception {
        // given
        GridOutputContainerLayoutManager manager = new GridOutputContainerLayoutManager(2);
        OutputContainer container = new OutputContainer(Arrays.asList(new Plot()));
        container.setLayoutManager(manager);
        // when
        manager.display(container);
        // then
        verifyChildren(kernel);
    }
}

