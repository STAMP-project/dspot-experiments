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


import org.junit.Assert;
import org.junit.Test;
import smile.math.matrix.SparseMatrix;


/**
 *
 *
 * @author Haifeng Li
 */
public class SparseMatrixParserTest {
    public SparseMatrixParserTest() {
    }

    /**
     * Test of parse method, of class SparseMatrixParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        try {
            SparseMatrixParser parser = new SparseMatrixParser();
            SparseMatrix data = parser.parse(smile.data.parser.IOUtils.getTestDataFile("matrix/08blocks.txt"));
            Assert.assertEquals(592, data.size());
            Assert.assertEquals(300, data.nrows());
            Assert.assertEquals(300, data.ncols());
            Assert.assertEquals(94.0, data.get(36, 0), 1.0E-7);
            Assert.assertEquals(1.0, data.get(0, 1), 1.0E-7);
            Assert.assertEquals(33.0, data.get(36, 1), 1.0E-7);
            Assert.assertEquals(95.0, data.get(299, 299), 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of parse method, of class SparseMatrixParser.
     */
    @Test
    public void testParseExchange() throws Exception {
        System.out.println("HB exchange format");
        try {
            SparseMatrixParser parser = new SparseMatrixParser();
            SparseMatrix data = parser.parse(smile.data.parser.IOUtils.getTestDataFile("matrix/5by5_rua.hb"));
            Assert.assertEquals(13, data.size());
            Assert.assertEquals(5, data.nrows());
            Assert.assertEquals(5, data.ncols());
            Assert.assertEquals(11.0, data.get(0, 0), 1.0E-7);
            Assert.assertEquals(31.0, data.get(2, 0), 1.0E-7);
            Assert.assertEquals(51.0, data.get(4, 0), 1.0E-7);
            Assert.assertEquals(55.0, data.get(4, 4), 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

