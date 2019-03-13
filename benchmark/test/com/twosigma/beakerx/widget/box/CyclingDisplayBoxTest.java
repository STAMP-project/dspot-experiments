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
package com.twosigma.beakerx.widget.box;


import CyclingDisplayBox.MODEL_NAME_VALUE;
import CyclingDisplayBox.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.IntSlider;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.Text;
import com.twosigma.beakerx.widget.Widget;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class CyclingDisplayBoxTest {
    private KernelTest kernel;

    @Test
    public void createWithParam_shouldSendCommOpenMessage() throws Exception {
        // given
        List<Widget> children = Arrays.asList(new IntSlider(), new Text());
        kernel.clearPublishedMessages();
        // when
        new com.twosigma.beakerx.widget.CyclingDisplayBox(children);
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }
}

