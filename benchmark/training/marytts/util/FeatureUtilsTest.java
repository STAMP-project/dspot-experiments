/**
 * Copyright 2011 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.util;


import java.io.IOException;
import marytts.features.FeatureDefinition;
import marytts.features.FeatureVector;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author marc
 */
public class FeatureUtilsTest {
    String targetfeatures;

    @Test
    public void testReadFeatureDefinition() throws IOException {
        FeatureDefinition def = FeatureUtils.readFeatureDefinition(targetfeatures);
        Assert.assertNotNull(def);
    }

    @Test
    public void testReadFeatureVectors() throws IOException {
        FeatureDefinition def = FeatureUtils.readFeatureDefinition(targetfeatures);
        FeatureVector[] featureVectors = FeatureUtils.readFeatureVectors(targetfeatures);
        Assert.assertNotNull(featureVectors);
        Assert.assertEquals(9, featureVectors.length);
        Assert.assertEquals("h", def.getFeatureValueAsString("phone", featureVectors[0]));
    }
}

