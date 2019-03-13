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
package com.twosigma.beakerx.widget.bools;


import Valid.INVALID;
import Valid.MODEL_NAME_VALUE;
import Valid.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.Valid;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ValidTest {
    private KernelTest kernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Valid();
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void readoutShouldBeSetToInvalid() throws Exception {
        // given
        // when
        Valid valid = new Valid();
        // then
        Assertions.assertThat(valid.getReadOut()).isEqualTo(INVALID);
    }

    @Test
    public void setReadOutFlag_hasThatReadOutFlag() throws Exception {
        String expected = "hello";
        // given
        Valid valid = new Valid();
        kernel.clearPublishedMessages();
        // when
        valid.setReadOut(expected);
        // then
        Assertions.assertThat(valid.getReadOut()).isEqualTo(expected);
    }
}

