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
public class RESParserTest {
    public RESParserTest() {
    }

    /**
     * Test of parse method, of class RESParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        RESParser parser = new RESParser();
        try {
            AttributeDataset data = parser.parse("RES", smile.data.parser.IOUtils.getTestDataFile("microarray/all_aml_test.res"));
            double[][] x = data.toArray(new double[data.size()][]);
            String[] id = data.toArray(new String[data.size()]);
            for (Attribute attribute : data.attributes()) {
                Assert.assertEquals(NUMERIC, attribute.getType());
                System.out.println((((attribute.getName()) + "\t") + (attribute.getDescription())));
            }
            Assert.assertEquals(7129, data.size());
            Assert.assertEquals(35, data.attributes().length);
            Assert.assertEquals("AFFX-BioB-5_at", id[0]);
            Assert.assertEquals((-214), x[0][0], 1.0E-7);
            Assert.assertEquals((-342), x[0][1], 1.0E-7);
            Assert.assertEquals((-87), x[0][2], 1.0E-7);
            Assert.assertEquals("Z78285_f_at", id[7128]);
            Assert.assertEquals(16, x[7128][32], 1.0E-7);
            Assert.assertEquals((-73), x[7128][33], 1.0E-7);
            Assert.assertEquals((-60), x[7128][34], 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

