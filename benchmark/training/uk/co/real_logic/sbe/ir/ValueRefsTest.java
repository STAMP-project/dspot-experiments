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
package uk.co.real_logic.sbe.ir;


import ParserOptions.DEFAULT;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class ValueRefsTest {
    @Test
    public void shouldGenerateValueRefToEnum() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("value-ref-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        Assert.assertThat(ir.getMessage(1).get(1).encodedLength(), Is.is(8));
        Assert.assertThat(ir.getMessage(2).get(1).encodedLength(), Is.is(0));
        Assert.assertThat(ir.getMessage(3).get(1).encodedLength(), Is.is(0));
        Assert.assertThat(ir.getMessage(4).get(1).encodedLength(), Is.is(0));
        Assert.assertThat(ir.getMessage(5).get(1).encodedLength(), Is.is(0));
    }
}

