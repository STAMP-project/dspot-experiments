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
package com.twosigma.beakerx.chart.treemap.util;


import com.twosigma.beakerx.chart.Color;
import com.twosigma.beakerx.chart.treemap.TreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class GradientColorProviderTest {
    TreeMap treeMap;

    TreeMapNode node01;

    @Test
    public void createProviderWithTreeMapParam_getColorWithNodeReturnBeakerColorWithRGB() {
        // when
        GradientColorProvider gradientColorProvider = new GradientColorProvider(treeMap);
        // then
        Assertions.assertThat(gradientColorProvider.getColor(treeMap.getRoot()).getRGB()).isNotZero();
        Assertions.assertThat(gradientColorProvider.getColor(node01).getRGB()).isNotZero();
    }

    @Test
    public void createProviderWithTreeMapParam_getValueWithNodeReturnDoubleIsNotZero() {
        // when
        GradientColorProvider gradientColorProvider = new GradientColorProvider(treeMap);
        // then
        Assertions.assertThat(gradientColorProvider.getValue(treeMap.getRoot())).isNotZero();
        Assertions.assertThat(gradientColorProvider.getValue(node01)).isNotZero();
    }

    @Test
    public void createProviderWithTreeMapAndTwoColorsParams_getColorWithNodeReturnBeakerColorWithRGB() {
        // when
        GradientColorProvider gradientColorProvider = new GradientColorProvider(treeMap, Color.BLUE, Color.GREEN);
        // then
        Assertions.assertThat(gradientColorProvider.getColor(treeMap.getRoot()).getRGB()).isNotZero();
        Assertions.assertThat(gradientColorProvider.getColor(node01).getRGB()).isNotZero();
    }
}

