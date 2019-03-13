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


import Attribute.Type.NOMINAL;
import Attribute.Type.NUMERIC;
import DateFeature.Type;
import org.junit.Assert;
import org.junit.Test;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.data.parser.IOUtils;


/**
 *
 *
 * @author Haifeng Li
 */
public class DateFeatureTest {
    public DateFeatureTest() {
    }

    /**
     * Test of attributes method, of class DateFeature.
     */
    @Test
    public void testAttributes() {
        System.out.println("attributes");
        try {
            ArffParser parser = new ArffParser();
            AttributeDataset data = parser.parse(IOUtils.getTestDataFile("weka/date.arff"));
            DateFeature[] features = new Type[]{ Type.YEAR, Type.MONTH, Type.DAY_OF_MONTH, Type.DAY_OF_WEEK, Type.HOURS, Type.MINUTES, Type.SECONDS };
            DateFeature df = new DateFeature(features);
            Attribute[] attributes = df.attributes();
            Assert.assertEquals(features.length, attributes.length);
            for (int i = 0; i < (attributes.length); i++) {
                System.out.println(attributes[i]);
                if ((i == 1) || (i == 3)) {
                    Assert.assertEquals(NOMINAL, attributes[i].getType());
                } else {
                    Assert.assertEquals(NUMERIC, attributes[i].getType());
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Test of feature method, of class DateFeature.
     */
    @Test
    public void testFeature() {
        System.out.println("feature");
        double[][] result = new double[][]{ new double[]{ 2001.0, 3.0, 3.0, 2.0, 12.0, 12.0, 12.0 }, new double[]{ 2001.0, 4.0, 3.0, 4.0, 12.0, 59.0, 55.0 } };
        try {
            ArffParser parser = new ArffParser();
            AttributeDataset data = parser.parse(IOUtils.getTestDataFile("weka/date.arff"));
            double[][] x = data.toArray(new double[data.size()][]);
            DateFeature[] features = new Type[]{ Type.YEAR, Type.MONTH, Type.DAY_OF_MONTH, Type.DAY_OF_WEEK, Type.HOURS, Type.MINUTES, Type.SECONDS };
            DateFeature df = new DateFeature(features);
            Attribute[] attributes = df.attributes();
            Assert.assertEquals(features.length, attributes.length);
            for (int i = 0; i < (x.length); i++) {
                double[] y = df.feature(toDate(x[i][0]));
                for (int j = 0; j < (y.length); j++) {
                    Assert.assertEquals(result[i][j], y[j], 1.0E-7);
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

