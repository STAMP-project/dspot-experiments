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
package smile.feature;


import Attribute.Type.NUMERIC;
import org.junit.Assert;
import org.junit.Test;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.data.parser.IOUtils;


/**
 *
 *
 * @author Haifeng
 */
public class OneHotEncoderTest {
    public OneHotEncoderTest() {
    }

    /**
     * Test of attributes method, of class OneHotEncoder.
     */
    @SuppressWarnings("unused")
    @Test
    public void testAttributes() {
        System.out.println("attributes");
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        try {
            AttributeDataset weather = arffParser.parse(IOUtils.getTestDataFile("weka/weather.nominal.arff"));
            double[][] x = weather.toArray(new double[weather.size()][]);
            OneHotEncoder n2b = new OneHotEncoder(weather.attributes());
            Attribute[] attributes = n2b.attributes();
            Assert.assertEquals(10, attributes.length);
            for (int i = 0; i < (attributes.length); i++) {
                System.out.println(attributes[i]);
                Assert.assertEquals(NUMERIC, attributes[i].getType());
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of f method, of class OneHotEncoder.
     */
    @Test
    public void testFeature() {
        System.out.println("feature");
        double[][] result = new double[][]{ new double[]{ 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, new double[]{ 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, new double[]{ 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, new double[]{ 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0 }, new double[]{ 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0 }, new double[]{ 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0 }, new double[]{ 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0 }, new double[]{ 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0 }, new double[]{ 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0 }, new double[]{ 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0 } };
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        try {
            AttributeDataset weather = arffParser.parse(IOUtils.getTestDataFile("weka/weather.nominal.arff"));
            double[][] x = weather.toArray(new double[weather.size()][]);
            OneHotEncoder n2b = new OneHotEncoder(weather.attributes());
            for (int i = 0; i < (x.length); i++) {
                double[] y = n2b.feature(x[i]);
                for (int j = 0; j < (y.length); j++) {
                    Assert.assertEquals(result[i][j], y[j], 1.0E-7);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

