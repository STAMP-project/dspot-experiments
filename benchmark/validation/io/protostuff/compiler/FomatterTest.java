/**
 * ========================================================================
 */
/**
 * Copyright 2012 David Yu
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff.compiler;


import Formatter.BUILTIN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Formatter}.
 *
 * @author David Yu
 * @author Kostiantyn Shchepanovskyi
 */
public class FomatterTest {
    @Test
    public void testCC() {
        final Formatter f = BUILTIN.CC;
        verify(f, "someFoo", "some_foo");
        verify(f, "someFoo", "SomeFoo");
        // verify that it does not change anything
        verify(f, "someFoo", "someFoo");
    }

    @Test
    public void testCCU() {
        final Formatter f = BUILTIN.CCU;
        verify(f, "someFoo_", "some_foo");
        verify(f, "someFoo_", "SomeFoo");
        verify(f, "someFoo_", "someFoo");
    }

    @Test
    public void testUC() {
        final Formatter f = BUILTIN.UC;
        verify(f, "some_foo", "someFoo");
        verify(f, "some_foo", "SomeFoo");
        // verify that it does not change anything
        verify(f, "some_foo", "some_foo");
    }

    @Test
    public void testUCU() {
        final Formatter f = BUILTIN.UCU;
        verify(f, "some_foo_", "someFoo");
        verify(f, "some_foo_", "SomeFoo");
        verify(f, "some_foo_", "some_foo");
    }

    @Test
    public void testUUC() {
        final Formatter f = BUILTIN.UUC;
        verify(f, "SOME_FOO", "someFoo");
        verify(f, "SOME_FOO", "SomeFoo");
        verify(f, "SOME_FOO", "some_foo");
        // verify that it does not change anything
        verify(f, "SOME_FOO", "SOME_FOO");
    }

    @Test
    public void testPC() {
        final Formatter f = BUILTIN.PC;
        verify(f, "SomeFoo", "someFoo");
        verify(f, "SomeFoo", "some_foo");
        // verify that it does not change anything
        verify(f, "SomeFoo", "SomeFoo");
    }

    @Test
    public void testPCS() {
        final Formatter f = BUILTIN.PCS;
        verify(f, "Some Foo", "someFoo");
        verify(f, "Some Foo", "some_foo");
        verify(f, "Some Foo", "SomeFoo");
        // verify that it does not change anything
        verify(f, "Some Foo", "Some Foo");
    }

    @Test
    public void testPluralize() {
        Assert.assertEquals("octopi", BUILTIN.PLURAL.format("octopus"));
        Assert.assertEquals("vertices", BUILTIN.PLURAL.format("vertex"));
        Assert.assertEquals("oxen", BUILTIN.PLURAL.format("ox"));
        Assert.assertEquals("books", BUILTIN.PLURAL.format("book"));
        Assert.assertEquals("people", BUILTIN.PLURAL.format("Person"));
        Assert.assertEquals("children", BUILTIN.PLURAL.format("Child"));
        Assert.assertEquals("Addresses", BUILTIN.PLURAL.format("Address"));
        Assert.assertEquals("money", BUILTIN.PLURAL.format("money"));
        Assert.assertEquals("libraries", BUILTIN.PLURAL.format("library"));
    }

    @Test
    public void testSingularize() {
        Assert.assertEquals("prognosis", BUILTIN.SINGULAR.format("prognoses"));
        Assert.assertEquals("Analysis", BUILTIN.SINGULAR.format("Analyses"));
        Assert.assertEquals("book", BUILTIN.SINGULAR.format("books"));
        Assert.assertEquals("person", BUILTIN.SINGULAR.format("people"));
        Assert.assertEquals("money", BUILTIN.SINGULAR.format("money"));
        Assert.assertEquals("action", BUILTIN.SINGULAR.format("actions"));
        Assert.assertEquals("availableBettingLimit", BUILTIN.SINGULAR.format("availableBettingLimits"));
        Assert.assertEquals("availableExtendDuration", BUILTIN.SINGULAR.format("availableExtendDurations"));
        Assert.assertEquals("betStat", BUILTIN.SINGULAR.format("betStats"));
        Assert.assertEquals("bet", BUILTIN.SINGULAR.format("bets"));
        Assert.assertEquals("brokenGame", BUILTIN.SINGULAR.format("brokenGames"));
        Assert.assertEquals("capability", BUILTIN.SINGULAR.format("capabilities"));
        Assert.assertEquals("card", BUILTIN.SINGULAR.format("cards"));
        Assert.assertEquals("casinoClientProperty", BUILTIN.SINGULAR.format("casinoClientProperties"));
        Assert.assertEquals("clientProperty", BUILTIN.SINGULAR.format("clientProperties"));
        Assert.assertEquals("countRange", BUILTIN.SINGULAR.format("countRanges"));
        Assert.assertEquals("deleted", BUILTIN.SINGULAR.format("deleted"));
        Assert.assertEquals("delta", BUILTIN.SINGULAR.format("delta"));
        Assert.assertEquals("gameType", BUILTIN.SINGULAR.format("gameTypes"));
        Assert.assertEquals("history", BUILTIN.SINGULAR.format("histories"));
        Assert.assertEquals("history", BUILTIN.SINGULAR.format("history"));
        Assert.assertEquals("info", BUILTIN.SINGULAR.format("infos"));
        Assert.assertEquals("limit", BUILTIN.SINGULAR.format("limits"));
        Assert.assertEquals("maskUrl", BUILTIN.SINGULAR.format("maskUrls"));
        Assert.assertEquals("metadata", BUILTIN.SINGULAR.format("metadata"));
        Assert.assertEquals("offlineGame", BUILTIN.SINGULAR.format("offlineGames"));
        Assert.assertEquals("playerInfo", BUILTIN.SINGULAR.format("playerInfo"));
        Assert.assertEquals("playerInfo", BUILTIN.SINGULAR.format("playerInfos"));
        Assert.assertEquals("position", BUILTIN.SINGULAR.format("positions"));
        Assert.assertEquals("providerItem", BUILTIN.SINGULAR.format("providerItems"));
        Assert.assertEquals("resolvedSideBet", BUILTIN.SINGULAR.format("resolvedSideBets"));
        Assert.assertEquals("resultCount", BUILTIN.SINGULAR.format("resultCounts"));
        Assert.assertEquals("result", BUILTIN.SINGULAR.format("results"));
        Assert.assertEquals("serviceUrl", BUILTIN.SINGULAR.format("serviceUrls"));
        Assert.assertEquals("setting", BUILTIN.SINGULAR.format("settings"));
        Assert.assertEquals("statistic", BUILTIN.SINGULAR.format("statistic"));
        Assert.assertEquals("stream", BUILTIN.SINGULAR.format("streams"));
        Assert.assertEquals("suggestedBet", BUILTIN.SINGULAR.format("suggestedBets"));
        Assert.assertEquals("supportedMessage", BUILTIN.SINGULAR.format("supportedMessages"));
        Assert.assertEquals("tableHistory", BUILTIN.SINGULAR.format("tableHistory"));
        Assert.assertEquals("tableStat", BUILTIN.SINGULAR.format("tableStats"));
        Assert.assertEquals("table", BUILTIN.SINGULAR.format("tables"));
        Assert.assertEquals("tag", BUILTIN.SINGULAR.format("tags"));
        Assert.assertEquals("updated", BUILTIN.SINGULAR.format("updated"));
        Assert.assertEquals("urlTypeList", BUILTIN.SINGULAR.format("urlTypeList"));
        Assert.assertEquals("urlsList", BUILTIN.SINGULAR.format("urlsList"));
        Assert.assertEquals("winnerListRange", BUILTIN.SINGULAR.format("winnerListRanges"));
        Assert.assertEquals("winner", BUILTIN.SINGULAR.format("winners"));
    }

    @Test
    public void testTrim() throws Exception {
        final Formatter f = BUILTIN.TRIM;
        Assert.assertEquals("Some Foo", f.format("\n\n   Some Foo\n"));
    }

    @Test
    public void testCutL() throws Exception {
        final Formatter f = BUILTIN.CUT_L;
        Assert.assertEquals("oo", f.format("foo"));
        Assert.assertEquals("", f.format(""));
    }

    @Test
    public void testCutR() throws Exception {
        final Formatter f = BUILTIN.CUT_R;
        Assert.assertEquals("fo", f.format("foo"));
        Assert.assertEquals("", f.format(""));
    }
}

