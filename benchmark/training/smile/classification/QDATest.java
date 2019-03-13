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
package smile.classification;


import org.junit.Assert;
import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.data.parser.IOUtils;
import smile.validation.LOOCV;


/**
 *
 *
 * @author Haifeng Li
 */
public class QDATest {
    public QDATest() {
    }

    /**
     * Test of learn method, of class QDA.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(4);
        try {
            AttributeDataset iris = arffParser.parse(IOUtils.getTestDataFile("weka/iris.arff"));
            double[][] x = iris.toArray(new double[iris.size()][]);
            int[] y = iris.toArray(new int[iris.size()]);
            int n = x.length;
            LOOCV loocv = new LOOCV(n);
            int error = 0;
            double[] posteriori = new double[3];
            for (int i = 0; i < n; i++) {
                double[][] trainx = Math.slice(x, loocv.train[i]);
                int[] trainy = Math.slice(y, loocv.train[i]);
                QDA qda = new QDA(trainx, trainy);
                if ((y[loocv.test[i]]) != (qda.predict(x[loocv.test[i]], posteriori)))
                    error++;

                // System.out.println(posteriori[0]+"\t"+posteriori[1]+"\t"+posteriori[2]);
            }
            System.out.println(("QDA error = " + error));
            Assert.assertEquals(4, error);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

