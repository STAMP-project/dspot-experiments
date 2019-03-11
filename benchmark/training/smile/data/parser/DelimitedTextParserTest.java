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
package smile.data.parser;


import Attribute.Type.NOMINAL;
import Attribute.Type.NUMERIC;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;


/**
 *
 *
 * @author Haifeng Li
 */
public class DelimitedTextParserTest {
    public DelimitedTextParserTest() {
    }

    /**
     * Test of parse method, of class DelimitedTextParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        try {
            DelimitedTextParser parser = new DelimitedTextParser();
            parser.setResponseIndex(new NominalAttribute("class"), 0);
            AttributeDataset usps = parser.parse("USPS Train", smile.data.parser.IOUtils.getTestDataFile("usps/zip.train"));
            double[][] x = usps.toArray(new double[usps.size()][]);
            int[] y = usps.toArray(new int[usps.size()]);
            Assert.assertEquals(NOMINAL, usps.responseAttribute().getType());
            for (Attribute attribute : usps.attributes()) {
                Assert.assertEquals(NUMERIC, attribute.getType());
            }
            Assert.assertEquals(7291, usps.size());
            Assert.assertEquals(256, usps.attributes().length);
            Assert.assertEquals("6", usps.responseAttribute().toString(y[0]));
            Assert.assertEquals("5", usps.responseAttribute().toString(y[1]));
            Assert.assertEquals("4", usps.responseAttribute().toString(y[2]));
            Assert.assertEquals((-1.0), x[0][6], 1.0E-7);
            Assert.assertEquals((-0.631), x[0][7], 1.0E-7);
            Assert.assertEquals(0.862, x[0][8], 1.0E-7);
            Assert.assertEquals("1", usps.responseAttribute().toString(y[7290]));
            Assert.assertEquals((-1.0), x[7290][4], 1.0E-7);
            Assert.assertEquals((-0.108), x[7290][5], 1.0E-7);
            Assert.assertEquals(1.0, x[7290][6], 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
            Assert.fail();
        }
    }

    /**
     * Test of parse method, of class DelimitedTextParser, with some ignored columns.
     */
    @Test
    public void testParseWithIgnoredColumns() throws Exception {
        System.out.println("parse");
        try {
            List<Integer> ignoredColumns = new ArrayList<>();
            ignoredColumns.add(6);
            ignoredColumns.add(8);
            DelimitedTextParser parser = new DelimitedTextParser();
            parser.setResponseIndex(new NominalAttribute("class"), 0);
            parser.setIgnoredColumns(ignoredColumns);
            AttributeDataset usps = parser.parse("USPS Train", smile.data.parser.IOUtils.getTestDataFile("usps/zip.train"));
            double[][] x = usps.toArray(new double[usps.size()][]);
            int[] y = usps.toArray(new int[usps.size()]);
            Assert.assertEquals(NOMINAL, usps.responseAttribute().getType());
            for (Attribute attribute : usps.attributes()) {
                Assert.assertEquals(NUMERIC, attribute.getType());
            }
            Assert.assertEquals(7291, usps.size());
            Assert.assertEquals((256 - (ignoredColumns.size())), usps.attributes().length);
            Assert.assertEquals("6", usps.responseAttribute().toString(y[0]));
            Assert.assertEquals("5", usps.responseAttribute().toString(y[1]));
            Assert.assertEquals("4", usps.responseAttribute().toString(y[2]));
            Assert.assertEquals(0.862, x[0][6], 1.0E-7);
            Assert.assertEquals((-0.167), x[0][7], 1.0E-7);
            Assert.assertEquals((-1.0), x[0][8], 1.0E-7);
            Assert.assertEquals("1", usps.responseAttribute().toString(y[7290]));
            Assert.assertEquals((-1.0), x[7290][4], 1.0E-7);
            Assert.assertEquals(1.0, x[7290][5], 1.0E-7);
            Assert.assertEquals((-0.867), x[7290][6], 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
            Assert.fail();
        }
    }
}

