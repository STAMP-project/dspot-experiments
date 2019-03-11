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
package org.graylog2.streams.matchers;


import java.util.Collections;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.StreamRule;
import org.junit.Assert;
import org.junit.Test;


public class RegexMatcherTest extends MatcherTest {
    @Test
    public void testSuccessfulMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("^foo");
        Message msg = getSampleMessage();
        msg.addField("something", "foobar");
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testSuccessfulInvertedMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("^foo");
        rule.setInverted(true);
        Message msg = getSampleMessage();
        msg.addField("something", "zomg");
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testMissedMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("^foo");
        Message msg = getSampleMessage();
        msg.addField("something", "zomg");
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testMissedInvertedMatch() {
        StreamRule rule = getSampleRule();
        rule.setValue("^foo");
        rule.setInverted(true);
        Message msg = getSampleMessage();
        msg.addField("something", "foobar");
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testMissingFieldShouldNotMatch() throws Exception {
        final StreamRule rule = getSampleRule();
        rule.setField("nonexistingfield");
        rule.setValue("^foo");
        final Message msg = getSampleMessage();
        final StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedMissingFieldShouldMatch() throws Exception {
        final StreamRule rule = getSampleRule();
        rule.setField("nonexistingfield");
        rule.setValue("^foo");
        rule.setInverted(true);
        final Message msg = getSampleMessage();
        final StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testNullFieldShouldNotMatch() throws Exception {
        final String fieldName = "nullfield";
        final StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setValue("^foo");
        final Message msg = getSampleMessage();
        msg.addField(fieldName, null);
        final StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertFalse(matcher.match(msg, rule));
    }

    @Test
    public void testInvertedNullFieldShouldMatch() throws Exception {
        final String fieldName = "nullfield";
        final StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setValue("^foo");
        rule.setInverted(true);
        final Message msg = getSampleMessage();
        msg.addField(fieldName, null);
        final StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testSuccessfulComplexRegexMatch() {
        StreamRule rule = getSampleRule();
        rule.setField("some_field");
        rule.setValue("foo=^foo|bar\\d.+wat");
        Message msg = getSampleMessage();
        msg.addField("some_field", "bar1foowat");
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }

    @Test
    public void testSuccessfulMatchInArray() {
        StreamRule rule = getSampleRule();
        rule.setValue("foobar");
        Message msg = getSampleMessage();
        msg.addField("something", Collections.singleton("foobar"));
        StreamRuleMatcher matcher = getMatcher(rule);
        Assert.assertTrue(matcher.match(msg, rule));
    }
}

