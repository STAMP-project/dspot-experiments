/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.server;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class UserLimitsTest {
    @Test
    public void testDefaultLimits() throws Exception {
        HTTPServerConfig config = new HTTPServerConfig();
        UserLimits defLimits = UserLimits.getDefaultLimits(config);
        Assert.assertThat(defLimits.getMaxTextLength(), Is.is(Integer.MAX_VALUE));
        Assert.assertThat(defLimits.getMaxCheckTimeMillis(), Is.is((-1L)));
        Assert.assertNull(defLimits.getPremiumUid());
    }

    @Test
    public void testLimitsFromToken1() throws Exception {
        HTTPServerConfig config = new HTTPServerConfig();
        config.setSecretTokenKey("foobar");
        // See TextCheckerText.makeToken():
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vZm9vYmFyIiwiaWF0IjoxNTA3MjM5ODgxLCJtYXhUZXh0TGVuZ3RoIjozMH0.nqmxKqIBoL7k1OLbfMm9lVqR5XvPIV0hERzWvM-otq8";
        UserLimits limits = UserLimits.getLimitsFromToken(config, token);
        Assert.assertThat(limits.getMaxTextLength(), Is.is(30));
        Assert.assertThat(limits.getMaxCheckTimeMillis(), Is.is((-1L)));
        Assert.assertNull(limits.getPremiumUid());
    }

    @Test
    public void testLimitsFromToken2() throws Exception {
        HTTPServerConfig config = new HTTPServerConfig();
        config.setSecretTokenKey("foobar");
        // See TextCheckerText.makeToken():
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEyMzQsInByZW1pdW0iOnRydWUsImlzcyI6Imh0dHA6Ly9mb29iYXIiLCJpYXQiOjE1MDcyNDAyMzYsIm1heFRleHRMZW5ndGgiOjUwfQ.MCEuhvTiuci2d35NhTqV3mRZ0zaCKMHWU2k-tipviMY";
        UserLimits limits = UserLimits.getLimitsFromToken(config, token);
        Assert.assertThat(limits.getMaxTextLength(), Is.is(50));
        Assert.assertThat(limits.getMaxCheckTimeMillis(), Is.is((-1L)));
        Assert.assertThat(limits.getPremiumUid(), Is.is(1234L));
    }
}

