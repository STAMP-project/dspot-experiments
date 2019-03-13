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


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.xychart.Plot;
import com.twosigma.beakerx.widget.BeakerxWidget;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TabbedOutputContainerLayoutManagerTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendChildModel() throws Exception {
        // given
        TabbedOutputContainerLayoutManager layout = new TabbedOutputContainerLayoutManager();
        OutputContainer outputContainer = new OutputContainer();
        outputContainer.setLayoutManager(layout);
        outputContainer.addItem(new Plot(), "1990/01");
        // when
        outputContainer.display();
        // then
        Map model = TestWidgetUtils.getValueForProperty(plotUpdateMsg(), BeakerxWidget.MODEL, Map.class);
        Assert.assertTrue("Child model should be sent.", (model != null));
    }
}

