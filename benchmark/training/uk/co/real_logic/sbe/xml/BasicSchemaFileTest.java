/**
 * Copyright 2013-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.xml;


import ParserOptions.DEFAULT;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;


public class BasicSchemaFileTest {
    @Test
    public void shouldHandleBasicFile() throws Exception {
        XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema.xml"), DEFAULT);
    }

    @Test
    public void shouldHandleConstantHeaderField() throws Exception {
        XmlSchemaParser.parse(TestUtil.getLocalResource("basic-schema-constant-header-field.xml"), DEFAULT);
    }

    @Test
    public void shouldHandleBasicFileWithGroup() throws Exception {
        XmlSchemaParser.parse(TestUtil.getLocalResource("basic-group-schema.xml"), DEFAULT);
    }

    @Test
    public void shouldHandleBasicFileWithVariableLengthData() throws Exception {
        XmlSchemaParser.parse(TestUtil.getLocalResource("basic-variable-length-schema.xml"), DEFAULT);
    }

    @Test
    public void shouldHandleBasicAllTypes() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("basic-types-schema.xml"), DEFAULT);
        final List<Field> fields = schema.getMessage(1).fields();
        Assert.assertThat(fields.get(0).name(), Is.is("header"));
        Assert.assertThat(fields.get(1).name(), Is.is("EDTField"));
        Assert.assertThat(fields.get(2).name(), Is.is("ENUMField"));
        Assert.assertThat(fields.get(3).name(), Is.is("SETField"));
        Assert.assertThat(fields.get(4).name(), Is.is("int64Field"));
    }
}

