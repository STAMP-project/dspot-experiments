/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.patterns;


import Match.CaseConversion.ALLLOWER;
import Match.CaseConversion.ALLUPPER;
import Match.CaseConversion.NONE;
import Match.CaseConversion.PRESERVE;
import Match.CaseConversion.STARTLOWER;
import Match.CaseConversion.STARTUPPER;
import Match.IncludeRange.ALL;
import Match.IncludeRange.FOLLOWING;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.synthesis.Synthesizer;
import org.languagetool.tagging.Tagger;


/**
 *
 *
 * @author Ionu? P?duraru
 */
// TODO add tests for using Match.IncludeRange with {@link Match#staticLemma}
public class MatchTest {
    private static final String TEST_DATA = "# some test data\n" + (((("inflectedform11\tlemma1\tPOS1\n" + "inflectedform121\tlemma1\tPOS2\n") + "inflectedform122\tlemma1\tPOS2\n") + "inflectedform123\tlemma1\tPOS3\n") + "inflectedform2\tlemma2\tPOS1\n");

    private JLanguageTool languageTool;

    private Synthesizer synthesizer;

    private Tagger tagger;

    // -- test methods
    // -- CASE CONVERSION
    @Test
    public void testStartUpper() throws Exception {
        Match match = getMatch("POS1", "POS2", STARTUPPER);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11", "POS1", "Lemma1"));
        Assert.assertEquals("[Inflectedform121, Inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testStartLower() throws Exception {
        Match match = getMatch("POS1", "POS2", STARTLOWER);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("InflectedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testAllUpper() throws Exception {
        Match match = getMatch("POS1", "POS2", ALLUPPER);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("InflectedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[INFLECTEDFORM121, INFLECTEDFORM122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testAllLower() throws Exception {
        Match match = getMatch("POS1", "POS2", ALLLOWER);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("InflectedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveStartUpper() throws Exception {
        Match match = getMatch("POS1", "POS2", PRESERVE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("InflectedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[Inflectedform121, Inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testStaticLemmaPreserveStartLower() throws Exception {
        Match match = getMatch("POS2", "POS1", PRESERVE);
        match.setLemmaString("lemma2");
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform121", "POS2", "Lemma1"));
        Assert.assertEquals("[inflectedform2]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testStaticLemmaPreserveStartUpper() throws Exception {
        Match match = getMatch("POS2", "POS1", PRESERVE);
        match.setLemmaString("lemma2");
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("InflectedForm121", "POS2", "Lemma1"));
        Assert.assertEquals("[Inflectedform2]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testStaticLemmaPreserveAllUpper() throws Exception {
        Match match = getMatch("POS2", "POS1", PRESERVE);
        match.setLemmaString("lemma2");
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("INFLECTEDFORM121", "POS2", "Lemma1"));
        Assert.assertEquals("[INFLECTEDFORM2]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testStaticLemmaPreserveMixed() throws Exception {
        Match match = getMatch("POS2", "POS1", PRESERVE);
        match.setLemmaString("lemma2");
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("infleCtedForm121", "POS2", "Lemma1"));
        Assert.assertEquals("[inflectedform2]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveStartLower() throws Exception {
        Match match = getMatch("POS1", "POS2", PRESERVE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveAllUpper() throws Exception {
        Match match = getMatch("POS1", "POS2", PRESERVE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("INFLECTEDFORM11", "POS1", "Lemma1"));
        Assert.assertEquals("[INFLECTEDFORM121, INFLECTEDFORM122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveMixed() throws Exception {
        Match match = getMatch("POS1", "POS2", PRESERVE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflecTedForm11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveNoneUpper() throws Exception {
        Match match = getMatch("POS1", "POS2", NONE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("INFLECTEDFORM11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveNoneLower() throws Exception {
        Match match = getMatch("POS1", "POS2", NONE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPreserveNoneMixed() throws Exception {
        Match match = getMatch("POS1", "POS2", NONE);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inFLectedFOrm11", "POS1", "Lemma1"));
        Assert.assertEquals("[inflectedform121, inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    // -- INCLUDE RANGE
    @Test
    public void testSimpleIncludeFollowing() throws Exception {
        Match match = getMatch(null, null, FOLLOWING);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11 inflectedform2 inflectedform122 inflectedform122"), 1, 3);
        Assert.assertEquals("[inflectedform2 inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPOSIncludeFollowing() throws Exception {
        // POS is ignored when using IncludeRange.Following
        Match match = getMatch("POS2", "POS33", FOLLOWING);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11 inflectedform2 inflectedform122 inflectedform122"), 1, 3);
        Assert.assertEquals("[inflectedform2 inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testIncludeAll() throws Exception {
        Match match = getMatch(null, null, ALL);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11 inflectedform2 inflectedform122 inflectedform122"), 1, 3);
        Assert.assertEquals("[inflectedform11 inflectedform2 inflectedform122]", Arrays.toString(state.toFinalString(null)));
    }

    @Test
    public void testPOSIncludeAll() throws Exception {
        Match match = getMatch("POS1", "POS3", ALL);
        MatchState state = match.createState(synthesizer, getAnalyzedTokenReadings("inflectedform11 inflectedform2 inflectedform122 inflectedform122"), 1, 3);
        Assert.assertEquals("[inflectedform123 inflectedform2 inflectedform122]", Arrays.toString(state.toFinalString(null)));
        // Note that in this case the first token has the requested POS (POS3 replaces POS1)
    }
}

