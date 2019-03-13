/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.plugin;


import org.junit.Assert;
import org.junit.Test;


public class VersionTest {
    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("0.20.0", Version.from(0, 20, 0).toString());
        Assert.assertEquals("1.0.0", Version.from(1, 0, 0).toString());
        Assert.assertEquals("1.2.3", Version.from(1, 2, 3).toString());
        Assert.assertEquals("0.0.7", Version.from(0, 0, 7).toString());
        Assert.assertEquals("1.0.0-preview.1", Version.from(1, 0, 0, "preview.1").toString());
        Assert.assertEquals("1.0.0-preview.1+deadbeef", Version.from(1, 0, 0, "preview.1", "deadbeef").toString());
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(Version.from(0, 20, 0).equals(Version.from(0, 20, 0)));
        Assert.assertTrue(Version.from(0, 20, 0, "preview.1").equals(Version.from(0, 20, 0, "preview.1")));
        Assert.assertTrue(Version.from(1, 2, 3).equals(Version.from(1, 2, 3)));
        Version v = Version.from(0, 20, 0);
        Assert.assertEquals(Version.from(0, 20, 0), v);
        Assert.assertFalse(Version.from(0, 20, 0).equals(Version.from(0, 20, 1)));
        Assert.assertFalse(Version.from(0, 20, 0, "preview.1").equals(Version.from(0, 20, 0, "preview.2")));
        Assert.assertFalse(Version.from(0, 20, 0).equals(null));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGreaterMinor() throws Exception {
        Version v = Version.from(0, 20, 0);
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 0)));
        Assert.assertTrue(v.greaterMinor(Version.from(0, 18, 2)));
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 9001)));
        Assert.assertFalse(v.greaterMinor(Version.from(0, 20, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 0, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 0, 9001)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 20, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 1, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(3, 2, 1)));
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 0, "rc.1")));
        v = Version.from(1, 5, 0);
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 0)));
        Assert.assertTrue(v.greaterMinor(Version.from(1, 0, 0)));
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 9001)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 6, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(3, 0, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 5, 9001)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 20, 0)));
        Assert.assertFalse(v.greaterMinor(Version.from(1, 20, 5)));
        Assert.assertFalse(v.greaterMinor(Version.from(3, 2, 1)));
        Assert.assertTrue(v.greaterMinor(Version.from(0, 19, 0, "rc.1")));
    }

    @Test
    public void testSameOrHigher() throws Exception {
        Version v = Version.from(0, 20, 2);
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 0)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 18, 2)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 9001)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 20, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 0, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 0, 9001)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 20, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 1, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(3, 2, 1)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 0, "rc.1")));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 19, 0, "rc.1")));
        Assert.assertFalse(v.sameOrHigher(Version.from(0, 21, 0, "rc.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 20, 1, "rc.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 20, 0, "rc.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 20, 2, "rc.1")));
        Assert.assertFalse(v.sameOrHigher(Version.from(0, 20, 3, "rc.1")));
        v = Version.from(1, 5, 0);
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 0)));
        Assert.assertTrue(v.sameOrHigher(Version.from(1, 0, 0)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 9001)));
        Assert.assertTrue(v.sameOrHigher(Version.from(1, 5, 0)));
        Assert.assertTrue(v.sameOrHigher(Version.from(1, 4, 9)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 6, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(3, 0, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 5, 9001)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 20, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(1, 20, 5)));
        Assert.assertFalse(v.sameOrHigher(Version.from(3, 2, 1)));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 19, 0, "rc.1")));
        Assert.assertFalse(v.sameOrHigher(Version.from(2, 19, 0, "rc.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(0, 0, 0)));
        Assert.assertFalse(v.sameOrHigher(Version.from(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)));
        // See https://github.com/Graylog2/graylog2-server/issues/2462
        v = Version.from(2, 1, 0, "beta.2");
        Assert.assertTrue(v.sameOrHigher(Version.from(2, 1, 0, "alpha.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(2, 1, 0, "beta.1")));
        Assert.assertTrue(v.sameOrHigher(Version.from(2, 1, 0, "beta.2")));
        Assert.assertTrue(v.sameOrHigher(Version.from(2, 1, 0)));// This needs to work!

        Assert.assertFalse(v.sameOrHigher(Version.from(2, 2, 0, "alpha.1")));
        Assert.assertFalse(v.sameOrHigher(Version.from(2, 2, 0)));
    }

    @Test
    public void testCompareTo() {
        Version v = Version.from(0, 20, 2);
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 0))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 18, 2))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 9001))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 2))) == 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 0))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 9001))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 20, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 1, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(3, 2, 1))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 0, "rc.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 19, 0, "rc.1"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 21, 0, "rc.1"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 1, "rc.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 0, "rc.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 2, "rc.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 20, 3, "rc.1"))) < 0));
        v = Version.from(1, 5, 0);
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 0))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 9001))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 5, 0))) == 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 4, 9))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 6, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(3, 0, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 5, 9001))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 20, 0))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 20, 5))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(3, 2, 1))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 19, 0, "rc.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(2, 19, 0, "rc.1"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(0, 0, 0))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE))) < 0));
        v = Version.from(1, 0, 0, "beta.2");
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "beta.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "beta.2"))) == 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "beta.3"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "alpha.1"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "alpha.3"))) > 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "rc.1"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0, "rc.3"))) < 0));
        Assert.assertTrue(((v.compareTo(Version.from(1, 0, 0))) < 0));
    }
}

