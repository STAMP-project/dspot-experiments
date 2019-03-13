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
package com.twosigma.beakerx.widget.integers;


import Play.INTERVAL;
import Play.MODEL_NAME_VALUE;
import Play.VALUE;
import Play.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.Play;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class PlayTest {
    private KernelTest groovyKernel;

    @Test
    public void createByEmptyConstructor_sendCommOpenMessage() throws Exception {
        // given
        // when
        new Play();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setValue_sendCommMessage() throws Exception {
        // given
        Play play = Play();
        // when
        play.setValue(11);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, 11);
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        int expected = 50;
        // given
        Play play = Play();
        // when
        play.setOrientation(expected);
        // then
        Assertions.assertThat(play.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void setOrientation_sendCommMessage() throws Exception {
        int expected = 50;
        // given
        Play play = Play();
        // when
        play.setOrientation(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, INTERVAL, expected);
    }
}

