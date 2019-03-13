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
package com.twosigma.beakerx.widget;


import Comm.BUFFER_PATHS;
import Image.FORMAT;
import Image.HEIGHT;
import Image.MODEL_NAME_VALUE;
import Image.VIEW_NAME_VALUE;
import Image.WIDTH;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ImageTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Image();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        Image widget = image();
        // when
        widget.setValue("picture".getBytes());
        // then
        Message message = groovyKernel.getPublishedMessages().get(0);
        Map data = TestWidgetUtils.getData(message);
        List<List<String>> o = ((List) (data.get(BUFFER_PATHS)));
        Assertions.assertThat(o.get(0).get(0)).isEqualTo("value");
    }

    @Test
    public void shouldSendCommMsgWhenFormatChange() throws Exception {
        // given
        Image widget = image();
        // when
        widget.setFormat("jpg");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, FORMAT, "jpg");
    }

    @Test
    public void shouldSendCommMsgWhenHeightChange() throws Exception {
        // given
        Image widget = image();
        // when
        widget.setHeight("123");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, HEIGHT, "123");
    }

    @Test
    public void shouldSendCommMsgWhenWidthChange() throws Exception {
        // given
        Image widget = image();
        // when
        widget.setWidth("321");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, WIDTH, "321");
    }

    @Test
    public void setWidth_hasThatWidth() throws Exception {
        String expected = "123";
        // given
        Image widget = image();
        // when
        widget.setWidth(expected);
        // then
        Assertions.assertThat(widget.getWidth()).isEqualTo(expected);
    }

    @Test
    public void setHeight_hasThatHeight() throws Exception {
        String expected = "123";
        // given
        Image widget = image();
        // when
        widget.setHeight(expected);
        // then
        Assertions.assertThat(widget.getHeight()).isEqualTo(expected);
    }

    @Test
    public void setFormat_hasThatFormat() throws Exception {
        String expected = "test";
        // given
        Image widget = image();
        // when
        widget.setFormat(expected);
        // then
        Assertions.assertThat(widget.getFormat()).isEqualTo(expected);
    }
}

