/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.chart.serializer;


import org.junit.Test;


public class HeatMapReducerTest {
    public static final int ROWS_LIMIT = 100;

    public static final int COLUMN_LIMIT = 100;

    private HeatMapReducer sut;

    @Test
    public void shouldLimitDataInHeatMap() {
        // given
        Integer[][] items = createData(1000, 1000);
        // when
        Number[][] limitedItems = sut.limitHeatmap(items);
        // then
        assertThat(HeatMapReducer.totalPoints(limitedItems)).isEqualTo(((HeatMapReducerTest.ROWS_LIMIT) * (HeatMapReducerTest.COLUMN_LIMIT)));
    }
}

