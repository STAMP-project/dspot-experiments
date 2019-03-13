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
package org.graylog2.plugin.streams;


import Stream.MatchingType;
import Stream.MatchingType.AND;
import Stream.MatchingType.DEFAULT;
import Stream.MatchingType.OR;
import org.junit.Assert;
import org.junit.Test;


public class MatchingTypeTest {
    @Test
    public void testValueOfOrDefault() throws Exception {
        Assert.assertEquals(AND, MatchingType.valueOfOrDefault("AND"));
        Assert.assertEquals(OR, MatchingType.valueOfOrDefault("OR"));
        Assert.assertEquals(DEFAULT, MatchingType.valueOfOrDefault(null));
        Assert.assertEquals(DEFAULT, MatchingType.valueOfOrDefault(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfOrDefaultThrowsExceptionForUnknownEnumName() {
        MatchingType.valueOfOrDefault("FOO");
    }
}

