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
package org.graylog2.inputs.extractors;


import Extractor.ConditionType;
import Extractor.CursorStrategy;
import org.graylog2.ConfigurationException;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.junit.Assert;
import org.junit.Test;


public class RegexExtractorTest extends AbstractExtractorTest {
    @Test
    public void testBasicExtraction() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.COPY, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNotNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001", msg.getField("somefield"));
        Assert.assertEquals("9001", msg.getField("our_result"));
    }

    @Test
    public void testBasicExtractionWithCutStrategy() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNotNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:", msg.getField("somefield"));
        Assert.assertEquals("9001", msg.getField("our_result"));
    }

    @Test
    public void testBasicExtractionWithCutStrategyCanOverwriteSameField() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "message", "message", RegexExtractorTest.config("The (.+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertEquals("short message", msg.getField("message"));
    }

    @Test
    public void testBasicExtractionDoesNotFailOnNonMatch() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.COPY, "somefield", "our_result", RegexExtractorTest.config("nothing:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001", msg.getField("somefield"));
    }

    @Test
    public void testBasicExtractionDoesNotFailOnNonMatchWithCutStrategy() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "our_result", RegexExtractorTest.config("nothing:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001", msg.getField("somefield"));
    }

    @Test
    public void testExtractsFirstMatcherGroupWhenProvidedWithSeveral() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001 lolwut");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.COPY, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+).*(lolwut)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNotNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001 lolwut", msg.getField("somefield"));
        Assert.assertEquals("9001", msg.getField("our_result"));
    }

    @Test
    public void testDoesNotFailOnNonExistentSourceField() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "LOLIDONTEXIST", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
    }

    @Test
    public void testDoesNotFailOnSourceFieldThatIsNotOfTypeString() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", 9001);
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
    }

    @Test
    public void testBasicExtractionWithCutStrategyDoesNotLeaveEmptyFields() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "our_result", RegexExtractorTest.config("(.*)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        Assert.assertNotNull(msg.getField("our_result"));
        Assert.assertEquals("fullyCutByExtractor", msg.getField("somefield"));
    }

    @Test(expected = ConfigurationException.class)
    public void testDoesNotInitializeOnNullConfigMap() throws Exception {
        new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "somefield", null, "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
    }

    @Test(expected = ConfigurationException.class)
    public void testDoesNotInitializeOnNullRegexValue() throws Exception {
        new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "somefield", RegexExtractorTest.config(null), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
    }

    @Test(expected = ConfigurationException.class)
    public void testDoesNotInitializeOnEmptyRegexValue() throws Exception {
        new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "somefield", "somefield", RegexExtractorTest.config(""), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
    }

    @Test
    public void testDoesNotRunWhenRegexConditionFails() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.COPY, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.REGEX, "^XXX");
        x.runExtractor(msg);
        Assert.assertNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001", msg.getField("somefield"));
    }

    @Test
    public void testDoesNotRunWhenStringConditionFails() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        msg.addField("somefield", "<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001");
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.COPY, "somefield", "our_result", RegexExtractorTest.config("id:(\\d+)"), "foo", AbstractExtractorTest.noConverters(), ConditionType.STRING, "FOOBAR");
        x.runExtractor(msg);
        Assert.assertNull(msg.getField("our_result"));
        Assert.assertEquals("<10> 07 Aug 2013 somesubsystem: this is my message for username9001 id:9001", msg.getField("somefield"));
    }

    @Test
    public void testDoesNotCutFromStandardFields() throws Exception {
        Message msg = new Message("The short message", "TestUnit", Tools.nowUTC());
        RegexExtractor x = new RegexExtractor(metricRegistry, "foo", "foo", 0, CursorStrategy.CUT, "message", "our_result", RegexExtractorTest.config("^(The).+"), "foo", AbstractExtractorTest.noConverters(), ConditionType.NONE, null);
        x.runExtractor(msg);
        // Would be cut to "short message" if cutting from standard field was allowed.
        Assert.assertEquals("The short message", msg.getField("message"));
    }
}

