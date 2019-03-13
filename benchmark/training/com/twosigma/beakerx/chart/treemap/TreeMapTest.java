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
package com.twosigma.beakerx.chart.treemap;


import Mode.DICE;
import ValueAccessor.VALUE;
import com.twosigma.beakerx.chart.ChartTest;
import com.twosigma.beakerx.chart.serializer.TreeMapSerializer;
import com.twosigma.beakerx.chart.treemap.util.IToolTipBuilder;
import java.util.LinkedHashMap;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.sf.jtreemap.swing.DefaultValue;
import net.sf.jtreemap.swing.TreeMapNode;
import org.junit.Test;


public class TreeMapTest extends ChartTest<TreeMap> {
    private TreeMap treeMap;

    @Test
    public void createTreeMapWithTreeMapNodeParam_hasColorProviderNotNullShowLegendIsFalseRootInNotNull() {
        // when
        TreeMap treeMap = new TreeMap(new TreeMapNode("label"));
        // then
        assertThat(treeMap.getColorProvider()).isNotNull();
        assertThat(treeMap.getShowLegend()).isFalse();
        assertThat(treeMap.getRoot()).isNotNull();
    }

    @Test
    public void createTreeMapByDefaultConstructor_hasModeAndStickyAndRoundAndRatioAreNulls() {
        // given
        treeMap = createWidget();
        // then
        assertThat(treeMap.getMode()).isNull();
        assertThat(treeMap.getRatio()).isNull();
        assertThat(treeMap.getSticky()).isNull();
        assertThat(treeMap.getRound()).isNull();
    }

    @Test
    public void shouldSendCommMsgWhenModeChange() throws Exception {
        // given
        treeMap = createWidget();
        // when
        treeMap.setMode(DICE);
        // then
        assertThat(treeMap.getMode()).isEqualTo(DICE);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TreeMapSerializer.MODE)).isEqualTo(DICE.getJsName());
    }

    @Test
    public void shouldSendCommMsgWhenStickyChange() throws Exception {
        // given
        treeMap = createWidget();
        // when
        treeMap.setSticky(true);
        // then
        assertThat(treeMap.getSticky()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TreeMapSerializer.STICKY)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenRatioChange() throws Exception {
        // given
        treeMap = createWidget();
        // when
        treeMap.setRatio(1.1);
        // then
        assertThat(treeMap.getRatio()).isEqualTo(1.1);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TreeMapSerializer.RATIO)).isEqualTo(1.1);
    }

    @Test
    public void shouldSendCommMsgWhenRoundChange() throws Exception {
        // given
        treeMap = createWidget();
        // when
        treeMap.setRound(true);
        // then
        assertThat(treeMap.getRound()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TreeMapSerializer.ROUND)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenValueAccessorChange() throws Exception {
        // given
        treeMap = createWidget();
        // when
        treeMap.setValueAccessor(VALUE);
        // then
        assertThat(treeMap.getValueAccessor()).isEqualTo(VALUE);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(TreeMapSerializer.VALUE_ACCESSOR)).isEqualTo(VALUE.toString());
    }

    @Test
    public void setToolTipBuilder_hasToolTipBuilder() {
        // given
        treeMap = createWidget();
        // when
        treeMap.setToolTipBuilder(new IToolTipBuilder() {
            @Override
            public String getToolTip(TreeMapNode node) {
                return "tooltip";
            }
        });
        // then
        assertThat(treeMap.getToolTipBuilder()).isNotNull();
        LinkedHashMap model = getModelUpdate();
        assertThat(model).isNotNull();
    }

    @Test
    public void problem_1501_nodes() {
        // given
        TreeMapNode rootNode = new TreeMapNode("0");
        IntStream.range(1, 1501).forEach(( it) -> {
            TreeMapNode nodeX = new TreeMapNode(("X" + it));
            nodeX.add(new TreeMapNode(("a" + it), it, new DefaultValue(it)));
            nodeX.add(new TreeMapNode(("b" + it), it, new DefaultValue(it)));
            rootNode.add(nodeX);
        });
        TreeMap treeMap = new TreeMap(rootNode);
        // when
        treeMap.display();
        // then
        assertThat(treeMap).isNotNull();
    }
}

