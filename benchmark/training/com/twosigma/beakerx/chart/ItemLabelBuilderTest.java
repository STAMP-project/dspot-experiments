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
package com.twosigma.beakerx.chart;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.categoryplot.CategoryPlot;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryGraphics;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ItemLabelBuilderTest {
    private CategoryGraphics categoryGraphics;

    private CategoryPlot categoryPlot;

    private static KernelTest kernel;

    @Test
    public void setItemLabelBuilderWithOneParam_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value) {
                return "value=" + value;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 1;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("value=1");
    }

    @Test
    public void setItemLabelBuilderWithTwoParams_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value, Object base) {
                return "base=" + base;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 2;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("base=0.0");
    }

    @Test
    public void setItemLabelBuilderWithThreeParams_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value, Object base, Object series) {
                return "series=" + series;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 3;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("series=test series name");
    }

    @Test
    public void setItemLabelBuilderWithFourParams_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value, Object base, Object series, Object category) {
                return "category=" + category;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 4;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("category=test category name");
    }

    @Test
    public void setItemLabelBuilderWithFiveParams_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value, Object base, Object series, Object category, Object row) {
                return "row=" + row;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 5;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("row=0");
    }

    @Test
    public void setItemLabelBuilderWithSixParams_hasItemLabels() {
        // given
        categoryGraphics.setItemLabel(new ItemLabelBuilder() {
            @Override
            public Object call(Object value, Object base, Object series, Object category, Object row, Object column) {
                return "column=" + column;
            }

            @Override
            public int getMaximumNumberOfParameters() {
                return 6;
            }
        });
        // when
        categoryGraphics.createItemLabels(categoryPlot);
        // then
        Assertions.assertThat(categoryGraphics.getItemLabels()[0][0]).isEqualTo("column=0");
    }

    @Test
    public void callDefaultMethods_returnNull() {
        // when
        ItemLabelBuilder itemLabelBuilder = new ItemLabelBuilder() {
            @Override
            public int getMaximumNumberOfParameters() {
                return 0;
            }
        };
        // then
        Assertions.assertThat(itemLabelBuilder.call("v")).isNull();
        Assertions.assertThat(itemLabelBuilder.call("v", "b")).isNull();
        Assertions.assertThat(itemLabelBuilder.call("v", "b", "s")).isNull();
        Assertions.assertThat(itemLabelBuilder.call("v", "b", "s", "c")).isNull();
        Assertions.assertThat(itemLabelBuilder.call("v", "b", "s", "c", "r")).isNull();
        Assertions.assertThat(itemLabelBuilder.call("v", "b", "s", "c", "r", "l")).isNull();
    }
}

