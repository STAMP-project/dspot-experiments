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
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;


public class CompositeOffsetsTest {
    @Test
    public void shouldGenerateCorrectOffsetsForFieldsWithEmbeddedComposite() throws Exception {
        final MessageSchema schema = XmlSchemaParser.parse(TestUtil.getLocalResource("composite-offsets-schema.xml"), DEFAULT);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final List<Token> message2 = ir.getMessage(2);
        final int fieldOneIndex = 1;
        Assert.assertThat(message2.get(fieldOneIndex).offset(), Matchers.is(0));
        Assert.assertThat(message2.get((fieldOneIndex + 1)).encodedLength(), Matchers.is(4));
        final int fieldTwoIndex = 4;
        Assert.assertThat(message2.get(fieldTwoIndex).offset(), Matchers.is(8));
        Assert.assertThat(message2.get((fieldTwoIndex + 1)).encodedLength(), Matchers.is(16));
        Assert.assertThat(message2.get((fieldTwoIndex + 2)).offset(), Matchers.is(0));
        Assert.assertThat(message2.get((fieldTwoIndex + 2)).encodedLength(), Matchers.is(1));
        Assert.assertThat(message2.get((fieldTwoIndex + 3)).offset(), Matchers.is(8));
        Assert.assertThat(message2.get((fieldTwoIndex + 3)).encodedLength(), Matchers.is(8));
        final int fieldThreeIndex = 10;
        Assert.assertThat(message2.get(fieldThreeIndex).offset(), Matchers.is(24));
        Assert.assertThat(message2.get((fieldThreeIndex + 1)).encodedLength(), Matchers.is(8));
        Assert.assertThat(message2.get(0).encodedLength(), Matchers.is(32));
    }
}

