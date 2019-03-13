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
public class GCTParserTest {
    public GCTParserTest() {
    }

    /**
     * Test of parse method, of class GCTParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        GCTParser parser = new GCTParser();
        try {
            AttributeDataset data = parser.parse("GCT", smile.data.parser.IOUtils.getTestDataFile("microarray/allaml.dataset.gct"));
            double[][] x = data.toArray(new double[data.size()][]);
            String[] id = data.toArray(new String[data.size()]);
            for (Attribute attribute : data.attributes()) {
                Assert.assertEquals(NUMERIC, attribute.getType());
                System.out.println(attribute.getName());
            }
            Assert.assertEquals(12564, data.size());
            Assert.assertEquals(48, data.attributes().length);
            Assert.assertEquals("AFFX-MurIL2_at", id[0]);
            Assert.assertEquals((-161.8), x[0][0], 1.0E-7);
            Assert.assertEquals((-231.0), x[0][1], 1.0E-7);
            Assert.assertEquals((-279.0), x[0][2], 1.0E-7);
            Assert.assertEquals("128_at", id[12563]);
            Assert.assertEquals(95.0, x[12563][45], 1.0E-7);
            Assert.assertEquals(108.0, x[12563][46], 1.0E-7);
            Assert.assertEquals(346.0, x[12563][47], 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

