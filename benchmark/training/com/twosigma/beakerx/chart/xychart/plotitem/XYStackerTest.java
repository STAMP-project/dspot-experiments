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
package com.twosigma.beakerx.chart.xychart.plotitem;


import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class XYStackerTest {
    BasedXYGraphics area1;

    BasedXYGraphics area2;

    BasedXYGraphics area3;

    @Test
    public void callStackWithMaxSizeAreasIsThree_returnAllAreasWithSizeIsThree() {
        // when
        List<BasedXYGraphics> list = XYStacker.stack(Arrays.asList(area1, area2, area3));
        // then
        Assertions.assertThat(list.get(0).getX().size()).isEqualTo(3);
        Assertions.assertThat(list.get(0).getY().size()).isEqualTo(3);
        Assertions.assertThat(list.get(1).getX().size()).isEqualTo(3);
        Assertions.assertThat(list.get(1).getY().size()).isEqualTo(3);
        Assertions.assertThat(list.get(2).getX().size()).isEqualTo(3);
        Assertions.assertThat(list.get(2).getY().size()).isEqualTo(3);
    }

    @Test
    public void callStackWithOneAndThreeElementsArea_returnFirstAreaWithTheSameYs() {
        // when
        List<BasedXYGraphics> list = XYStacker.stack(Arrays.asList(area1, area3));
        List<Number> firstAreaYs = list.get(0).getY();
        // then
        Assertions.assertThat(firstAreaYs.get(0)).isEqualTo(firstAreaYs.get(1)).isEqualTo(firstAreaYs.get(2));
    }

    @Test
    public void callStackWithAreas_returnFirstAreaYsEqualsSecondAreaBases() {
        // when
        List<BasedXYGraphics> list = XYStacker.stack(Arrays.asList(area3, area1));
        List<Number> firstAreaYs = list.get(0).getY();
        List<Number> secondAreaBases = list.get(1).getBases();
        // then
        Assertions.assertThat(firstAreaYs.get(0)).isEqualTo(secondAreaBases.get(0));
        Assertions.assertThat(firstAreaYs.get(1)).isEqualTo(secondAreaBases.get(1));
        Assertions.assertThat(firstAreaYs.get(2)).isEqualTo(secondAreaBases.get(2));
    }

    @Test
    public void callStackWithOneArea_returnThatArea() {
        // when
        List<BasedXYGraphics> list = XYStacker.stack(Arrays.asList(area1));
        // then
        Assertions.assertThat(list.get(0)).isEqualTo(area1);
    }
}

