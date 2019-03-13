/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.editor.form.converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class FormJsonConverterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormJsonConverterTest.class);

    private static final String JSON_RESOURCE_1 = "org/flowable/editor/form/converter/form_1.json";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSimpleJsonForm() throws Exception {
        String testJsonResource = readJsonToString(FormJsonConverterTest.JSON_RESOURCE_1);
        SimpleFormModel formModel = new FormJsonConverter().convertToFormModel(testJsonResource);
        Assert.assertNotNull(formModel);
        Assert.assertNotNull(formModel.getFields());
        Assert.assertEquals(1, formModel.getFields().size());
        FormField formField = formModel.getFields().get(0);
        Assert.assertEquals("input1", formField.getId());
        Assert.assertEquals("Input1", formField.getName());
        Assert.assertEquals("text", formField.getType());
        Assert.assertFalse(formField.isRequired());
        Assert.assertEquals("empty", formField.getPlaceholder());
    }
}

