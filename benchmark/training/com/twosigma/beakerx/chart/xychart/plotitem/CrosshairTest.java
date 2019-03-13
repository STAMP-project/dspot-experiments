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


import Color.BLUE;
import Color.GREEN;
import java.awt.Color;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CrosshairTest {
    Crosshair crosshair;

    @Test
    public void createCrosshairByEmptyConstructor_hasStrokeTypeIsNull() {
        // when
        Crosshair crosshair = new Crosshair();
        // then
        Assertions.assertThat(crosshair.getStyle()).isNull();
    }

    @Test
    public void setColorWithAwtColor_crosshairHasBeakerColor() {
        // when
        crosshair.setColor(Color.GREEN);
        // then
        Assertions.assertThat(((crosshair.getColor()) instanceof com.twosigma.beakerx.chart.Color)).isTrue();
    }

    @Test
    public void clone_shouldCloneCrosshair() throws CloneNotSupportedException {
        // when
        crosshair.setColor(GREEN);
        Crosshair cloneCrosshair = ((Crosshair) (crosshair.clone()));
        crosshair.setColor(BLUE);
        // then
        Assertions.assertThat(cloneCrosshair.getColor()).isEqualTo(GREEN);
        Assertions.assertThat(crosshair.getColor()).isEqualTo(BLUE);
    }
}

