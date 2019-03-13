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
package org.graylog2.streams;


import StreamRuleType.ALWAYS_MATCH;
import StreamRuleType.EXACT;
import StreamRuleType.GREATER;
import StreamRuleType.PRESENCE;
import StreamRuleType.REGEX;
import StreamRuleType.SMALLER;
import org.graylog2.streams.matchers.AlwaysMatcher;
import org.graylog2.streams.matchers.ExactMatcher;
import org.graylog2.streams.matchers.FieldPresenceMatcher;
import org.graylog2.streams.matchers.GreaterMatcher;
import org.graylog2.streams.matchers.RegexMatcher;
import org.graylog2.streams.matchers.SmallerMatcher;
import org.junit.Test;


public class StreamRuleMatcherFactoryTest {
    @Test
    public void buildReturnsCorrectStreamRuleMatcher() throws Exception {
        assertThat(StreamRuleMatcherFactory.build(EXACT)).isInstanceOf(ExactMatcher.class);
        assertThat(StreamRuleMatcherFactory.build(REGEX)).isInstanceOf(RegexMatcher.class);
        assertThat(StreamRuleMatcherFactory.build(GREATER)).isInstanceOf(GreaterMatcher.class);
        assertThat(StreamRuleMatcherFactory.build(SMALLER)).isInstanceOf(SmallerMatcher.class);
        assertThat(StreamRuleMatcherFactory.build(PRESENCE)).isInstanceOf(FieldPresenceMatcher.class);
        assertThat(StreamRuleMatcherFactory.build(ALWAYS_MATCH)).isInstanceOf(AlwaysMatcher.class);
    }
}

