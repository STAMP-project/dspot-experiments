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


public class CompositeElementsTest {
    @Test
    public void shouldParseSchemaSuccessfully() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("composite-elements-schema.xml"), DEFAULT);
        final List<Field> fields = schema.getMessage(1).fields();
        final Field composite = fields.get(0);
        Assert.assertThat(composite.name(), Is.is("structure"));
        final CompositeType compositeType = ((CompositeType) (composite.type()));
        Assert.assertThat(compositeType.name(), Is.is("outer"));
        final List<Type> elements = compositeType.getTypeList();
        final EnumType enumType = ((EnumType) (elements.get(0)));
        final EncodedDataType encodedDataType = ((EncodedDataType) (elements.get(1)));
        final SetType setType = ((SetType) (elements.get(2)));
        final CompositeType nestedCompositeType = ((CompositeType) (elements.get(3)));
        Assert.assertThat(enumType.name(), Is.is("enumOne"));
        Assert.assertThat(encodedDataType.name(), Is.is("zeroth"));
        Assert.assertThat(setType.name(), Is.is("setOne"));
        Assert.assertThat(nestedCompositeType.name(), Is.is("inner"));
        final List<Type> nestedElements = nestedCompositeType.getTypeList();
        final EncodedDataType first = ((EncodedDataType) (nestedElements.get(0)));
        final EncodedDataType second = ((EncodedDataType) (nestedElements.get(1)));
        Assert.assertThat(first.name(), Is.is("first"));
        Assert.assertThat(second.name(), Is.is("second"));
        Assert.assertThat(nestedCompositeType.encodedLength(), Is.is(16));
        Assert.assertThat(compositeType.encodedLength(), Is.is(22));
    }
}

