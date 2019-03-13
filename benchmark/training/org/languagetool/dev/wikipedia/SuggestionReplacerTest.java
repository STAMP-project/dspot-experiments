/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.dev.wikipedia;


import GermanSpellerRule.RULE_ID;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.English;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.AbstractPatternRule;


public class SuggestionReplacerTest {
    private final SwebleWikipediaTextFilter filter = new SwebleWikipediaTextFilter();

    private final GermanyGerman germanyGerman = new GermanyGerman();

    private final JLanguageTool langTool = getLanguageTool();

    private final JLanguageTool englishLangTool = getLanguageTool(new English());

    @Test
    public void testApplySuggestionToOriginalText() throws Exception {
        SwebleWikipediaTextFilter filter = new SwebleWikipediaTextFilter();
        applySuggestion(langTool, filter, "Die CD ROM.", "Die <s>CD-ROM.</s>");
        applySuggestion(langTool, filter, "Die [[verlinkte]] CD ROM.", "Die [[verlinkte]] <s>CD-ROM.</s>");
        applySuggestion(langTool, filter, "Die [[Link|verlinkte]] CD ROM.", "Die [[Link|verlinkte]] <s>CD-ROM.</s>");
        applySuggestion(langTool, filter, "Die [[CD ROM]].", "Die <s>[[CD-ROM]].</s>");
        applySuggestion(langTool, filter, "Der [[Abschied]].\n\n==\u00dcberschrift==\n\nEin Ab schied.", "Der [[Abschied]].\n\n==\u00dcberschrift==\n\nEin <s>Abschied.</s>");
        applySuggestion(langTool, filter, "Ein ?konomischer Gottesdienst.", "Ein <s>?kumenischer</s> Gottesdienst.");
        applySuggestion(langTool, filter, "Ein ?konomischer Gottesdienst mit ?konomischer Planung.", "Ein <s>?kumenischer</s> Gottesdienst mit ?konomischer Planung.");
        applySuggestion(langTool, filter, "\nEin \u00f6konomischer Gottesdienst.\n", "\nEin <s>\u00f6kumenischer</s> Gottesdienst.\n");
        applySuggestion(langTool, filter, "\n\nEin \u00f6konomischer Gottesdienst.\n", "\n\nEin <s>\u00f6kumenischer</s> Gottesdienst.\n");
    }

    @Test
    public void testNestedTemplates() throws Exception {
        String markup = "{{FNBox|\n" + (("  {{FNZ|1|1979 und 1984}}\n" + "  {{FNZ|2|[[Rundungsfehler]]}}\n") + "}}\n\nEin \u00f6konomischer Gottesdienst.\n");
        applySuggestion(langTool, filter, markup, markup.replace("?konomischer", "<s>?kumenischer</s>"));
    }

    @Test
    public void testReference1() throws Exception {
        String markup = "Hier <ref name=isfdb>\n" + "Retrieved 2012-07-31.</ref> steht,, das Haus.";
        applySuggestion(langTool, filter, markup, markup.replace("steht,, das Haus.", "<s>steht,</s> das Haus."));
    }

    @Test
    public void testReference2() throws Exception {
        String markup = "Hier <ref name=\"NPOVxxx\" /> steht,, das Haus.";
        applySuggestion(langTool, filter, markup, markup.replace("steht,, das Haus.", "<s>steht, das</s> Haus."));
    }

    @Test
    public void testErrorAtTextBeginning() throws Exception {
        String markup = "A hour ago\n";
        applySuggestion(englishLangTool, filter, markup, markup.replace("A", "<s>An</s>"));
    }

    @Test
    public void testErrorAtParagraphBeginning() throws Exception {
        String markup = "X\n\nA hour ago.\n";
        applySuggestion(englishLangTool, filter, markup, markup.replace("A", "<s>An</s>"));
    }

    @Test
    public void testKnownBug() throws Exception {
        String markup = "{{HdBG GKZ|9761000}}.";
        try {
            applySuggestion(langTool, filter, markup, markup);
        } catch (RuntimeException e) {
            // known problem - Sweble's location seems to be wrong?!
        }
    }

    @Test
    public void testComplexText() throws Exception {
        String markup = "{{Dieser Artikel|behandelt die freie Onlineenzyklop\u00e4die Wikipedia; zu dem gleichnamigen Asteroiden siehe [[(274301) Wikipedia]].}}\n" + (((((((((("\n" + "{{Infobox Website\n") + "| Name = \'\'\'Wikipedia\'\'\'\n") + "| Logo = [[Datei:Wikipedia-logo-v2-de.svg|180px|Das Wikipedia-Logo]]\n") + "| url = [//de.wikipedia.org/ de.wikipedia.org] (deutschsprachige Version)<br />\n") + "[//www.wikipedia.org/ www.wikipedia.org] (\u00dcbersicht aller Sprachen)\n") + "| Kommerziell = nein\n") + "| Beschreibung = [[Wiki]] einer freien kollektiv erstellten Online-Enzyklop\u00e4die\n") + "}}\n") + "\n") + "\'\'\'Wikipedia\'\'\' [{{IPA|\u02ccv\u026aki\u02c8pe\u02d0dia}}] (auch: \'\'die Wikipedia\'\') ist ein am [[15. Januar|15.&nbsp;Januar]] [[2001]] gegr\u00fcndetes Projekt. Und und so.\n");
        applySuggestion(langTool, filter, markup, markup.replace("Und und so.", "<s>Und so.</s>"));
    }

    @Test
    public void testCompleteText() throws Exception {
        InputStream stream = SuggestionReplacerTest.class.getResourceAsStream("/org/languagetool/dev/wikipedia/wikipedia.txt");
        String origMarkup = IOUtils.toString(stream, "utf-8");
        JLanguageTool langTool = new JLanguageTool(new GermanyGerman() {
            @Override
            protected synchronized List<AbstractPatternRule> getPatternRules() {
                return Collections.emptyList();
            }
        });
        langTool.disableRule(RULE_ID);
        langTool.disableRule("DE_AGREEMENT");
        langTool.disableRule("GERMAN_WORD_REPEAT_BEGINNING_RULE");
        langTool.disableRule("COMMA_PARENTHESIS_WHITESPACE");
        langTool.disableRule("DE_CASE");
        langTool.disableRule("ABKUERZUNG_LEERZEICHEN");
        langTool.disableRule("TYPOGRAFISCHE_ANFUEHRUNGSZEICHEN");
        langTool.disableRule("OLD_SPELLING");
        langTool.disableRule("DE_TOO_LONG_SENTENCE_40");
        langTool.disableRule("PUNCTUATION_PARAGRAPH_END");
        PlainTextMapping mapping = filter.filter(origMarkup);
        List<RuleMatch> matches = langTool.check(mapping.getPlainText());
        MatcherAssert.assertThat(("Expected 3 matches, got: " + matches), matches.size(), CoreMatchers.is(3));
        int oldPos = 0;
        for (RuleMatch match : matches) {
            SuggestionReplacer replacer = new SuggestionReplacer(mapping, origMarkup, new ErrorMarker("<s>", "</s>"));
            List<RuleMatchApplication> ruleMatchApplications = replacer.applySuggestionsToOriginalText(match);
            MatcherAssert.assertThat(ruleMatchApplications.size(), CoreMatchers.is(1));
            RuleMatchApplication ruleMatchApplication = ruleMatchApplications.get(0);
            MatcherAssert.assertThat(StringUtils.countMatches(ruleMatchApplication.getTextWithCorrection(), "absichtlicher absichtlicher"), CoreMatchers.is(2));
            int pos = ruleMatchApplication.getTextWithCorrection().indexOf("<s>absichtlicher</s> Fehler");
            if (pos == (-1)) {
                // markup area varies because our mapping is sometimes a bit off:
                pos = ruleMatchApplication.getTextWithCorrection().indexOf("<s>absichtlicher Fehler</s>");
            }
            Assert.assertTrue(("Found correction at: " + pos), (pos > oldPos));
            oldPos = pos;
        }
    }

    @Test
    public void testCompleteText2() throws Exception {
        InputStream stream = SuggestionReplacerTest.class.getResourceAsStream("/org/languagetool/dev/wikipedia/wikipedia2.txt");
        String origMarkup = IOUtils.toString(stream, "utf-8");
        JLanguageTool langTool = new JLanguageTool(germanyGerman);
        PlainTextMapping mapping = filter.filter(origMarkup);
        langTool.disableRule("PUNCTUATION_PARAGRAPH_END");// added to prevent crash; TODO: check if needed

        List<RuleMatch> matches = langTool.check(mapping.getPlainText());
        Assert.assertTrue(("Expected >= 30 matches, got: " + matches), ((matches.size()) >= 30));
        for (RuleMatch match : matches) {
            SuggestionReplacer replacer = new SuggestionReplacer(mapping, origMarkup, new ErrorMarker("<s>", "</s>"));
            List<RuleMatchApplication> ruleMatchApplications = replacer.applySuggestionsToOriginalText(match);
            if ((ruleMatchApplications.size()) == 0) {
                continue;
            }
            RuleMatchApplication ruleMatchApplication = ruleMatchApplications.get(0);
            MatcherAssert.assertThat(StringUtils.countMatches(ruleMatchApplication.getTextWithCorrection(), "<s>"), CoreMatchers.is(1));
        }
    }
}

