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


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import smile.data.parser.IOUtils;


/**
 *
 *
 * @author Haifeng Li
 */
public class BagTest {
    public BagTest() {
    }

    /**
     * Test of the uniqueness of features in the class Bag
     */
    @Test
    public void testUniquenessOfFeatures() {
        System.out.println("unique features");
        String[] featuresForBirdStories = new String[]{ "crane", "sparrow", "hawk", "owl", "kiwi" };
        String[] featuresForBuildingStories = new String[]{ "truck", "concrete", "foundation", "steel", "crane" };
        String testMessage = "This story is about a crane and a sparrow";
        ArrayList<String> mergedFeatureLists = new ArrayList<>();
        mergedFeatureLists.addAll(Arrays.asList(featuresForBirdStories));
        mergedFeatureLists.addAll(Arrays.asList(featuresForBuildingStories));
        Bag<String> bag = new Bag(mergedFeatureLists.toArray(new String[(featuresForBirdStories.length) + (featuresForBuildingStories.length)]));
        double[] result = bag.feature(testMessage.split(" "));
        Assert.assertEquals(9, result.length);
    }

    /**
     * Test of feature method, of class Bag.
     */
    @Test
    public void testFeature() {
        System.out.println("feature");
        String[][] text = new String[2000][];
        try (BufferedReader input = IOUtils.getTestDataReader("text/movie.txt")) {
            for (int i = 0; i < (text.length); i++) {
                String[] words = input.readLine().trim().split("\\s+");
                text[i] = words;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        String[] feature = new String[]{ "outstanding", "wonderfully", "wasted", "lame", "awful", "poorly", "ridiculous", "waste", "worst", "bland", "unfunny", "stupid", "dull", "fantastic", "laughable", "mess", "pointless", "terrific", "memorable", "superb", "boring", "badly", "subtle", "terrible", "excellent", "perfectly", "masterpiece", "realistic", "flaws" };
        Bag<String> bag = new Bag(feature);
        double[][] x = new double[text.length][];
        for (int i = 0; i < (text.length); i++) {
            x[i] = bag.feature(text[i]);
            Assert.assertEquals(feature.length, x[i].length);
        }
        Assert.assertEquals(1.0, x[0][15], 1.0E-7);
    }
}

