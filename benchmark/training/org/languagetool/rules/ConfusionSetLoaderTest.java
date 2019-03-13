/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


@SuppressWarnings("QuestionableName")
public class ConfusionSetLoaderTest {
    @Test
    public void testLoadWithStrictLimits() throws IOException {
        try (InputStream inputStream = JLanguageTool.getDataBroker().getFromResourceDirAsStream("/yy/confusion_sets.txt")) {
            ConfusionSetLoader loader = new ConfusionSetLoader();
            Map<String, List<ConfusionSet>> map = loader.loadConfusionSet(inputStream);
            MatcherAssert.assertThat(map.size(), CoreMatchers.is(10));
            MatcherAssert.assertThat(map.get("there").size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(map.get("there").get(0).getFactor(), CoreMatchers.is(10L));
            MatcherAssert.assertThat(map.get("their").size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(map.get("their").get(0).getFactor(), CoreMatchers.is(10L));
            MatcherAssert.assertThat(map.get("foo").size(), CoreMatchers.is(2));
            MatcherAssert.assertThat(map.get("foo").get(0).getFactor(), CoreMatchers.is(5L));
            MatcherAssert.assertThat(map.get("foo").get(1).getFactor(), CoreMatchers.is(8L));
            MatcherAssert.assertThat(map.get("goo").size(), CoreMatchers.is(2));
            MatcherAssert.assertThat(map.get("goo").get(0).getFactor(), CoreMatchers.is(11L));
            MatcherAssert.assertThat(map.get("goo").get(1).getFactor(), CoreMatchers.is(12L));
            MatcherAssert.assertThat(map.get("lol").size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(map.get("something").size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(map.get("bar").size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(map.get("bar").get(0).getFactor(), CoreMatchers.is(5L));
            Set<ConfusionString> there = map.get("there").get(0).getSet();
            Assert.assertTrue(getAsString(there).contains("there - example 1"));
            Assert.assertTrue(getAsString(there).contains("their - example 2"));
            Set<ConfusionString> their = map.get("their").get(0).getSet();
            Assert.assertTrue(getAsString(their).contains("there - example 1"));
            Assert.assertTrue(getAsString(their).contains("their - example 2"));
            Assert.assertFalse(getAsString(their).contains("comment"));
            Set<ConfusionString> foo = map.get("foo").get(0).getSet();
            Assert.assertTrue(getAsString(foo).contains("foo"));
            Set<ConfusionString> bar = map.get("foo").get(0).getSet();
            Assert.assertTrue(getAsString(bar).contains("bar"));
            Set<ConfusionString> baz = map.get("foo").get(1).getSet();
            Assert.assertTrue(getAsString(baz).contains("baz"));
        }
    }
}

