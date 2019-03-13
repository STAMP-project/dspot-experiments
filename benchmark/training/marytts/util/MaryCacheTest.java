/**
 * Copyright 2009 DFKI GmbH.
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


import java.io.File;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author marc
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaryCacheTest {
    @ClassRule
    public static TemporaryFolder tmp = new TemporaryFolder();

    private static MaryCache c;

    private static File maryCacheFile;

    private static String inputtype = "TEXT";

    private static String outputtype = "RAWMARYXML";

    private static String locale = "de";

    private static String voice = "de1";

    private static String inputtext = "Welcome to the world of speech synthesis";

    private static String targetValue = "<rawmaryxml/>";

    private static byte[] targetAudio = new byte[12345];

    private static String inputtext2 = "Some other input text";

    private static String targetValue2 = "Two\nlines";

    @Test
    public void lookupText() throws Exception {
        String lookupValue = MaryCacheTest.c.lookupText(MaryCacheTest.inputtype, MaryCacheTest.outputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext);
        Assert.assertEquals(MaryCacheTest.targetValue, lookupValue);
    }

    @Test
    public void lookupText2() throws Exception {
        String lookupValue = MaryCacheTest.c.lookupText(MaryCacheTest.inputtype, MaryCacheTest.outputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext2);
        Assert.assertEquals(MaryCacheTest.targetValue2, lookupValue);
    }

    @Test
    public void lookupAudio() throws Exception {
        byte[] lookupAudio = MaryCacheTest.c.lookupAudio(MaryCacheTest.inputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext);
        Assert.assertNotNull(lookupAudio);
        Assert.assertArrayEquals(MaryCacheTest.targetAudio, lookupAudio);
    }

    @Test
    public void canInsertAgain() throws Exception {
        int numExceptions = 0;
        try {
            MaryCacheTest.c.insertText(MaryCacheTest.inputtype, MaryCacheTest.outputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext, MaryCacheTest.targetValue);
        } catch (SQLException e) {
            numExceptions++;
        }
        try {
            MaryCacheTest.c.insertAudio(MaryCacheTest.inputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext, MaryCacheTest.targetAudio);
        } catch (SQLException e) {
            numExceptions++;
        }
        Assert.assertEquals(0, numExceptions);
    }

    @Test
    public void isPersistent() throws Exception {
        MaryCacheTest.c.shutdown();
        MaryCacheTest.c = new MaryCache(MaryCacheTest.maryCacheFile, false);
        lookupText();
        lookupAudio();
    }

    @Test
    public void zzz_isClearable() throws Exception {
        MaryCacheTest.c.shutdown();
        MaryCacheTest.c = new MaryCache(MaryCacheTest.maryCacheFile, true);
        String lookupValue = MaryCacheTest.c.lookupText(MaryCacheTest.inputtype, MaryCacheTest.outputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext);
        Assert.assertNull(lookupValue);
        byte[] lookupAudio = MaryCacheTest.c.lookupAudio(MaryCacheTest.inputtype, MaryCacheTest.locale, MaryCacheTest.voice, MaryCacheTest.inputtext);
        Assert.assertNull(lookupAudio);
    }
}

