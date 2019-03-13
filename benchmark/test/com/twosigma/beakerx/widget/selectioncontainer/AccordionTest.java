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
package com.twosigma.beakerx.widget.selectioncontainer;


import Accordion.MODEL_NAME_VALUE;
import Accordion.TITLES;
import Accordion.VIEW_NAME_VALUE;
import JupyterMessages.COMM_MSG;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.jupyter.SearchMessages;
import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.widget.Accordion;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.Widget;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class AccordionTest {
    private KernelTest kernel;

    private List<Widget> children;

    @Test
    public void createWithTwoParams_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new Accordion(children, Arrays.asList("t1", "t2"));
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void createWithParam_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new Accordion(children);
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setTitle_hasThatTitle() throws Exception {
        int index = 1;
        // given
        Accordion accordion = new Accordion(children);
        // when
        accordion.set_title(index, "first");
        // then
        Assertions.assertThat(accordion.get_title(index)).isEqualTo("first");
    }

    @Test
    public void setTitle_shouldSendCommMessage() throws Exception {
        int index = 1;
        // given
        Accordion accordion = new Accordion(children);
        // when
        accordion.set_title(index, "first");
        // then
        Message titlesMessage = SearchMessages.getListMessagesByType(kernel.getPublishedMessages(), COMM_MSG).get(0);
        Map titles = ((Map) (TestWidgetUtils.getValueForProperty(titlesMessage, TITLES, Object.class)));
        Assertions.assertThat(titles.get(index)).isEqualTo("first");
    }
}

