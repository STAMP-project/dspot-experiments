/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.ui;


import ChartTimeline.TimelineEntry;
import LengthUnit.Percent;
import LengthUnit.Px;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.ui.api.Style;
import org.deeplearning4j.ui.components.chart.style.StyleChart;
import org.deeplearning4j.ui.components.decorator.style.StyleAccordion;
import org.deeplearning4j.ui.components.table.style.StyleTable;
import org.deeplearning4j.ui.components.text.ComponentText;
import org.deeplearning4j.ui.components.text.style.StyleText;
import org.junit.Test;


/**
 * Created by Alex on 9/04/2016.
 */
public class TestComponentSerialization {
    @Test
    public void testSerialization() throws Exception {
        // Common style for all of the charts
        StyleChart s = new StyleChart.Builder().width(640, Px).height(480, Px).margin(Px, 100, 40, 40, 20).strokeWidth(2).pointSize(4).seriesColors(Color.GREEN, Color.MAGENTA).titleStyle(new StyleText.Builder().font("courier").fontSize(16).underline(true).color(Color.GRAY).build()).build();
        TestComponentSerialization.assertSerializable(s);
        // Line chart with vertical grid
        Component c1 = // Vertical grid lines, no horizontal grid
        build();
        TestComponentSerialization.assertSerializable(c1);
        // Scatter chart
        Component c2 = build();
        TestComponentSerialization.assertSerializable(c2);
        // Histogram with variable sized bins
        Component c3 = build();
        TestComponentSerialization.assertSerializable(c3);
        // Stacked area chart
        Component c4 = build();
        TestComponentSerialization.assertSerializable(c4);
        // Table
        StyleTable ts = new StyleTable.Builder().backgroundColor(Color.LIGHT_GRAY).headerColor(Color.ORANGE).borderWidth(1).columnWidths(Percent, 20, 40, 40).width(500, Px).height(200, Px).build();
        TestComponentSerialization.assertSerializable(ts);
        Component c5 = build();
        TestComponentSerialization.assertSerializable(c5);
        // Accordion decorator, with the same chart
        StyleAccordion ac = new StyleAccordion.Builder().height(480, Px).width(640, Px).build();
        TestComponentSerialization.assertSerializable(ac);
        Component c6 = build();
        TestComponentSerialization.assertSerializable(c6);
        // Text with styling
        Component c7 = build();
        TestComponentSerialization.assertSerializable(c7);
        // Div, with a chart inside
        Style divStyle = new org.deeplearning4j.ui.components.component.style.StyleDiv.Builder().width(30, Percent).height(200, Px).backgroundColor(Color.GREEN).floatValue(StyleDiv.FloatValue.right).build();
        TestComponentSerialization.assertSerializable(divStyle);
        Component c8 = new org.deeplearning4j.ui.components.component.ComponentDiv(divStyle, c7, new ComponentText("(Also: it's float right, 30% width, 200 px high )", null));
        TestComponentSerialization.assertSerializable(c8);
        // Timeline chart:
        List<ChartTimeline.TimelineEntry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entries.add(new ChartTimeline.TimelineEntry(String.valueOf(i), (10 * i), ((10 * i) + 5)));
        }
        Component c9 = build();
        TestComponentSerialization.assertSerializable(c9);
    }
}

