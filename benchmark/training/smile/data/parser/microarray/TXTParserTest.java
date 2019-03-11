/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.data.parser.microarray;


import Attribute.Type.NUMERIC;
import org.junit.Assert;
import org.junit.Test;
import smile.data.Attribute;
import smile.data.AttributeDataset;


/**
 *
 *
 * @author Haifeng Li
 */
public class TXTParserTest {
    public TXTParserTest() {
    }

    /**
     * Test of parse method, of class TXTParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        TXTParser parser = new TXTParser();
        try {
            AttributeDataset data = parser.parse("PCL", smile.data.parser.IOUtils.getTestDataFile("microarray/Dunham2002.txt"));
            double[][] x = data.toArray(new double[data.size()][]);
            String[] id = data.toArray(new String[data.size()]);
            for (Attribute attribute : data.attributes()) {
                Assert.assertEquals(NUMERIC, attribute.getType());
                System.out.println(attribute.getName());
            }
            Assert.assertEquals(6694, data.size());
            Assert.assertEquals(16, data.attributes().length);
            Assert.assertEquals("YKR005C", id[0]);
            Assert.assertEquals((-0.43), x[0][0], 1.0E-7);
            Assert.assertEquals((-0.47), x[0][1], 1.0E-7);
            Assert.assertEquals((-0.39), x[0][2], 1.0E-7);
            Assert.assertEquals("YKR004C", id[6693]);
            Assert.assertEquals(0.03, x[6693][13], 1.0E-7);
            Assert.assertEquals((-0.53), x[6693][14], 1.0E-7);
            Assert.assertEquals(0.3, x[6693][15], 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

