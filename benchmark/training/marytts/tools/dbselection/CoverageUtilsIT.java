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
package marytts.tools.dbselection;


import java.util.Locale;
import marytts.exceptions.SynthesisException;
import org.junit.Test;


/**
 *
 *
 * @author marc
 */
public class CoverageUtilsIT {
    String text = "Hello world";

    Locale locale = Locale.US;

    String[] phones = new String[]{ "h", "@", "l", "@U", "w", "r=", "l", "d", "_" };

    String[] next_phones = new String[]{ "@", "l", "@U", "w", "r=", "l", "d", "_", "_" };

    String[] selection_prosodies = new String[]{ "0", "0", "pre-nuclear", "pre-nuclear", "nuclear", "nuclear", "nuclear", "nuclear", "0" };

    @Test
    public void canComputePhoneFeature() throws Exception {
        // setup
        String featureNames = "phone";
        // exercise
        byte[] data = CoverageUtils.sentenceToFeatures(text, locale, featureNames, false);
        // verify
        assertFeaturesMatchTarget(data, featureNames);
    }

    @Test(expected = SynthesisException.class)
    public void willRejectUnknownFeature() throws Exception {
        // setup
        String featureNames = "unknown";
        // exercise
        CoverageUtils.sentenceToFeatures(text, locale, featureNames, false);
    }

    @Test
    public void canComputeDiphoneFeatures() throws Exception {
        // setup
        String featureNames = "phone next_phone";
        // exercise
        byte[] data = CoverageUtils.sentenceToFeatures(text, locale, featureNames, false);
        // verify
        assertFeaturesMatchTarget(data, featureNames);
    }

    @Test
    public void canComputeDiphoneProsodyFeatures() throws Exception {
        // setup
        String featureNames = "phone next_phone selection_prosody";
        // exercise
        byte[] data = CoverageUtils.sentenceToFeatures(text, locale, featureNames, false);
        // verify
        assertFeaturesMatchTarget(data, featureNames);
    }
}

