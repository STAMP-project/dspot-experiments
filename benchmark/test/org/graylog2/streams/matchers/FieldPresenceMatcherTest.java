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


import StreamRuleType.PRESENCE;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.streams.StreamRule;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class FieldPresenceMatcherTest extends MatcherTest {
    @Test
    public void testBasicMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("message");
        rule.setType(PRESENCE);
        rule.setInverted(false);
        Message message = getSampleMessage();
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertTrue(result);
    }

    @Test
    public void testBasicNonMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("nonexistentField");
        rule.setType(PRESENCE);
        rule.setInverted(false);
        Message message = getSampleMessage();
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertFalse(result);
    }

    @Test
    public void testInvertedBasicMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("message");
        rule.setType(PRESENCE);
        rule.setInverted(true);
        Message message = getSampleMessage();
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertFalse(result);
    }

    @Test
    public void testInvertedBasicNonMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("nonexistentField");
        rule.setType(PRESENCE);
        rule.setInverted(true);
        Message message = getSampleMessage();
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertTrue(result);
    }

    @Test
    public void testNulledFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(false);
        Message message = getSampleMessage();
        message.addField(fieldName, null);
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertFalse(result);
    }

    @Test
    public void testInvertedNulledFieldMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(true);
        Message message = getSampleMessage();
        message.addField(fieldName, null);
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertTrue(result);
    }

    @Test
    public void testEmptyStringFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(false);
        Message message = getSampleMessage();
        message.addField(fieldName, "");
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertFalse(result);
    }

    @Test
    public void testInvertedEmptyStringFieldMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(true);
        Message message = getSampleMessage();
        message.addField(fieldName, "");
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertTrue(result);
    }

    @Test
    public void testRandomNumberFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        Integer randomNumber = 4;
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(false);
        Message message = getSampleMessage();
        message.addField(fieldName, randomNumber);
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertTrue(result);
    }

    @Test
    public void testInvertedRandomNumberFieldMatch() throws Exception {
        String fieldName = "sampleField";
        Integer randomNumber = 4;
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(PRESENCE);
        rule.setInverted(true);
        Message message = getSampleMessage();
        message.addField(fieldName, randomNumber);
        StreamRuleMatcher matcher = getMatcher(rule);
        Boolean result = matcher.match(message, rule);
        Assert.assertFalse(result);
    }
}

