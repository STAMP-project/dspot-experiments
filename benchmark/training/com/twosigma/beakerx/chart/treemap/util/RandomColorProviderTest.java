/**
 * Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
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
import java.util.Arrays;
import net.sf.jtreemap.swing.TreeMapNode;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class RandomColorProviderTest {
    TreeMapNode node01;

    TreeMapNode node02;

    @Test
    public void createProviderWithEmptyConstructor_getColorWithNodeReturnBeakerColorWithRGB() {
        // when
        RandomColorProvider randomColorProvider = new RandomColorProvider();
        // then
        Assertions.assertThat(randomColorProvider.getColor(node01).getRGB()).isNotZero();
        Assertions.assertThat(randomColorProvider.getColor(node02).getRGB()).isNotZero();
    }

    @Test
    public void createProviderWithEmptyConstructor_getValueWithNodeReturnDoubleIsNotZero() {
        // when
        RandomColorProvider randomColorProvider = new RandomColorProvider();
        // then
        Assertions.assertThat(randomColorProvider.getValue(node01)).isNotZero();
        Assertions.assertThat(randomColorProvider.getValue(node02)).isNotZero();
    }

    @Test
    public void createProviderWithColorArrayParam_getColorWithNodeReturnBeakerColorWithRGB() {
        // when
        RandomColorProvider randomColorProvider = new RandomColorProvider(new Color[]{ Color.BLUE, Color.GREEN });
        // then
        Assertions.assertThat(randomColorProvider.getColor(node01).getRGB()).isNotZero();
        Assertions.assertThat(randomColorProvider.getColor(node02).getRGB()).isNotZero();
    }

    @Test
    public void createProviderWithListParams_getColorWithNodeReturnBeakerColorWithRGB() {
        // when
        RandomColorProvider randomColorProvider = new RandomColorProvider(Arrays.asList(Arrays.asList(100, 150, 200), "#AABBCC", "RED"));
        // then
        Assertions.assertThat(randomColorProvider.getColor(node01).getRGB()).isNotZero();
        Assertions.assertThat(randomColorProvider.getColor(node02).getRGB()).isNotZero();
    }

    @Test
    public void setGroupByParent_hasGroupByParentIsTrue() {
        // given
        RandomColorProvider randomColorProvider = new RandomColorProvider();
        // when
        randomColorProvider.setGroupByParent(true);
        // then
        Assertions.assertThat(randomColorProvider.isGroupByParent()).isTrue();
    }
}

