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


import Text.MODEL_MODULE_VALUE;
import Text.MODEL_NAME_VALUE;
import Text.VIEW_MODULE_VALUE;
import Text.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.strings.PasswordTest;
import com.twosigma.beakerx.widget.strings.TextTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class EasyFormTest {
    private KernelTest kernel;

    @Test
    public void onInitMethod() throws Exception {
        // given
        String textField1 = "tf1";
        String newValueForTf1 = "newValueForTf1";
        EasyForm easyForm = new EasyForm("EasyForm with text field");
        easyForm.addTextField(textField1);
        easyForm.addTextField("tf2").onInit(( value) -> easyForm.put(textField1, newValueForTf1));
        kernel.clearPublishedMessages();
        // when
        easyForm.display();
        // then
        verifyOnInit(kernel.getPublishedMessages().get(0), newValueForTf1);
    }

    @Test
    public void textFieldOnChangeMethod() throws Exception {
        // given
        final StringBuilder result = new StringBuilder();
        EasyForm easyForm = new EasyForm("EasyForm with text field");
        easyForm.addTextField("tf2").onChange(( value) -> result.append(value));
        easyForm.display();
        kernel.clearPublishedMessages();
        // when
        easyForm.put("tf2", "OK");
        // then
        assertThat(result.toString()).isEqualTo("OK");
    }

    @Test
    public void shouldCreateEasyFormWithRadioButton() throws Exception {
        // given
        String label = "RadioButto1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with RadioButto");
        easyForm.addRadioButtons(label, Arrays.asList("1", "2"));
        easyForm.display();
        // then
        verifyRadioButton(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithTextArea() throws Exception {
        // given
        String label = "ButtonLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with TextArea");
        easyForm.addTextArea(label);
        easyForm.display();
        // then
        verifyTextArea(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithButton() throws Exception {
        // given
        String label = "ButtonLabel";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with button");
        easyForm.addButton(label);
        easyForm.display();
        // then
        verifyButton(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithMultipleSelection() throws Exception {
        // given
        String label = "MultipleSelectionLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with MultipleSelection");
        easyForm.addList(label, Arrays.asList("1", "2", "3"));
        easyForm.display();
        // then
        Object multipleSelectionValue = easyForm.get(label);
        verifyMultipleSelection(kernel.getPublishedMessages(), ((List) (multipleSelectionValue)));
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithSelectMultipleSingle() throws Exception {
        // given
        String label = "MultipleSelectionLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with SelectMultipleSingle");
        easyForm.addList(label, Arrays.asList("1", "2", "3"), Boolean.FALSE);
        easyForm.display();
        // then
        Object multipleSelectionSingleValue = easyForm.get(label);
        verifySelectMultipleSingle(kernel.getPublishedMessages(), ((List) (multipleSelectionSingleValue)));
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithCombobox() throws Exception {
        // given
        String label = "ComboboxLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with Combobox");
        easyForm.addComboBox(label, Arrays.asList("1", "2"));
        easyForm.display();
        // then
        verifyCombobox(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithCheckbox() throws Exception {
        // given
        String label = "CheckboxLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with checkbox");
        easyForm.addCheckBox(label);
        easyForm.display();
        // then
        verifyCheckboxField(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithCheckboxGroup() throws Exception {
        // given
        String label = "label1";
        List<String> checkboxesLabels = Arrays.asList("1", "2", "3");
        // when
        EasyForm easyForm = new EasyForm("EasyForm with CheckboxGroup");
        easyForm.addCheckBoxes(label, checkboxesLabels);
        easyForm.display();
        // then
        verifyCheckboxGroup(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithTextField() throws Exception {
        // given
        String label = "text1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with text field");
        easyForm.addTextField(label, 10);
        easyForm.display();
        // then
        TextTest.verifyTextField(kernel.getPublishedMessages(), MODEL_NAME_VALUE, MODEL_MODULE_VALUE, VIEW_NAME_VALUE, VIEW_MODULE_VALUE);
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithPasswordField() throws Exception {
        // given
        String label = "pass1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with password field");
        easyForm.addPasswordField(label, 10);
        easyForm.display();
        // then
        PasswordTest.verifyPasswordField(kernel.getPublishedMessages(), Password.MODEL_NAME_VALUE, Password.MODEL_MODULE_VALUE, Password.VIEW_NAME_VALUE, Password.VIEW_MODULE_VALUE);
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }

    @Test
    public void shouldCreateEasyFormWithDatePicker() throws Exception {
        // given
        String label = "DatePickerLabel1";
        // when
        EasyForm easyForm = new EasyForm("EasyForm with DatePicker");
        easyForm.addDatePicker(label);
        easyForm.display();
        // then
        verifyDatePicker(kernel.getPublishedMessages());
        verifyEasyForm(kernel.getPublishedMessages(), easyForm.getCommFunctionalities());
        TestWidgetUtils.verifyDisplayMsg(kernel.getPublishedMessages());
    }
}

