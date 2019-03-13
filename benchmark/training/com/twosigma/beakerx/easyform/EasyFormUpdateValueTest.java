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
package com.twosigma.beakerx.easyform;


import com.twosigma.beakerx.KernelTest;
import java.util.Arrays;
import org.junit.Test;


public class EasyFormUpdateValueTest {
    private KernelTest kernel;

    @Test
    public void shouldUpdateTextField() throws Exception {
        // given
        String label = "text1";
        EasyForm easyForm = new EasyForm("EasyForm with text field");
        easyForm.addTextField(label);
        // when
        easyForm.getWidget(label).doUpdateValueWithCallback("new Value");
        // then
        assertThat(getValue()).isEqualTo("new Value");
    }

    @Test
    public void shouldUpdatePasswordField() throws Exception {
        // given
        String label = "pass1";
        EasyForm easyForm = new EasyForm("EasyForm with password field");
        easyForm.addPasswordField(label);
        // when
        easyForm.getWidget(label).doUpdateValueWithCallback("new Value");
        // then
        assertThat(getValue()).isEqualTo("new Value");
    }

    @Test
    public void shouldUpdateRadioButton() throws Exception {
        // given
        String label = "RadioButto1";
        EasyForm easyForm = new EasyForm("EasyForm with RadioButto");
        easyForm.addRadioButtons(label, Arrays.asList("1", "2"));
        // when
        easyForm.getWidget(label).doUpdateValueWithCallback("new Value");
        // then
        assertThat(getValue()).isEqualTo("new Value");
    }

    @Test
    public void shouldUpdateTextArea() throws Exception {
        // given
        String label = "ButtonLabel1";
        final StringBuilder result = new StringBuilder();
        EasyForm easyForm = new EasyForm("EasyForm with TextArea");
        easyForm.addTextArea(label).onChange(( value) -> result.append(value));
        // when
        easyForm.getWidget(label).doUpdateValueWithCallback("new TextArea");
        // then
        assertThat(result.toString()).isEqualTo("new TextArea");
    }

    @Test
    public void shouldUpdateButton() throws Exception {
        // given
        String label = "ButtonLabel1";
        final StringBuilder result = new StringBuilder();
        EasyForm easyForm = new EasyForm("EasyForm with button");
        easyForm.addButton(label).onChange(( value) -> result.append("new Button"));
        // when
        easyForm.getWidget(label).doUpdateValueWithCallback("new Button");
        // then
        assertThat(result.toString()).isEqualTo("new Button");
    }
}

