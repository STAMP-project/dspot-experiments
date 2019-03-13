/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Markus Brenneis
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
package org.languagetool.rules.de;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;


/**
 *
 *
 * @author Markus Brenneis
 */
public class VerbAgreementRuleTest {
    private JLanguageTool lt;

    private VerbAgreementRule rule;

    @Test
    public void testPositions() throws IOException {
        RuleMatch[] match1 = rule.match(lt.analyzeText("Du erreichst ich unter 12345"));
        Assert.assertThat(match1.length, Is.is(1));
        Assert.assertThat(match1[0].getFromPos(), Is.is(3));
        Assert.assertThat(match1[0].getToPos(), Is.is(16));
        RuleMatch[] match2 = rule.match(lt.analyzeText("Hallo Karl. Du erreichst ich unter 12345"));
        Assert.assertThat(match2.length, Is.is(1));
        Assert.assertThat(match2[0].getFromPos(), Is.is((12 + 3)));
        Assert.assertThat(match2[0].getToPos(), Is.is((12 + 16)));
        RuleMatch[] match3 = rule.match(lt.analyzeText("Ihr k?nnt das Training abbrechen, weil es nichts bringen wird. Er geht los und sagt dabei: Werde ich machen."));
        Assert.assertThat(match3.length, Is.is(1));
        Assert.assertThat(match3[0].getFromPos(), Is.is(97));
        Assert.assertThat(match3[0].getToPos(), Is.is(107));
    }

    @Test
    public void testWrongVerb() throws IOException {
        // correct sentences:
        assertGood("Du bist in dem Moment angekommen, als ich gegangen bin.");
        assertGood("K?mmere du dich mal nicht darum!");
        assertGood("Ich wei?, was ich tun werde, falls etwas geschehen sollte.");
        assertGood("...die drei?ig Jahre j?nger als ich ist.");
        assertGood("Ein Mann wie ich braucht einen Hut.");
        assertGood("Egal, was er sagen wird, ich habe meine Entscheidung getroffen.");
        assertGood("Du Beharrst darauf, dein W?rterbuch h?tte recht, hast aber von den Feinheiten des Japanischen keine Ahnung!");
        assertGood("Bin gleich wieder da.");
        assertGood("Wobei ich ?u?erst vorsichtig bin.");
        assertGood("Es ist klar, dass ich ?u?erst vorsichtig mit den Informationen umgehe");
        assertGood("Es ist klar, dass ich ?u?erst vorsichtig bin.");
        assertGood("Wobei er ?u?erst selten dar?ber spricht.");
        assertGood("Wobei er ?u?erst selten ?ber seine erste Frau spricht.");
        assertGood("Das Wort ?schreibst? ist sch?n.");
        assertGood("Die Jagd nach bin Laden.");
        assertGood("Die Unterlagen solltet ihr gr?ndlich durcharbeiten.");
        assertGood("Er reagierte ?u?erst negativ.");
        assertGood("Max und ich sollten das machen.");
        assertGood("Osama bin Laden stammt aus Saudi-Arabien.");
        assertGood("Solltet ihr das machen?");
        assertGood("Ein Geschenk, das er einst von Aphrodite erhalten hatte.");
        assertGood("Wenn ich sterben sollte, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Wenn er sterben sollte, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Wenn sie sterben sollte, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Wenn es sterben sollte, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Wenn ihr sterben solltet, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Wenn wir sterben sollten, wer w?rde sich dann um die Katze k?mmern?");
        assertGood("Daf?r erhielten er sowie der Hofgoldschmied Theodor Heiden einen Preis.");
        assertGood("Probst wurde deshalb in den Medien gefeiert.");
        assertGood("/usr/bin/firefox");
        assertGood("Das sind Leute, die viel mehr als ich wissen.");
        assertGood("Das ist mir nicht klar, kannst ja mal beim Kunden nachfragen.");
        assertGood("So tes\u00adtest Du das mit dem soft hyphen.");
        assertGood("Viele Brunnen in Italiens Hauptstadt sind bereits abgeschaltet.");
        assertGood("?Werde ich tun!?");
        assertGood("Sie fragte: ?Muss ich aussagen??");
        assertGood("?K?nnen wir bitte das Thema wechseln, denn ich m?chte ungern dar?ber reden??");
        assertGood("Er sagt: ?Willst du behaupten, dass mein Sohn euch liebt??");
        // incorrect sentences:
        assertBad("Als Borcarbid wei?t es eine hohe H?rte auf.");
        assertBad("Das greift auf Vorl?uferinstitutionen bist auf die Zeit von 1234 zur?ck.");
        assertBad("Die Eisenbahn dienst ?berwiegend dem G?terverkehr.");
        assertBad("Die Unterlagen solltest ihr gr?ndlich durcharbeiten.");
        assertBad("Peter bin nett.");
        assertBad("Solltest ihr das machen?", "Subjekt und Pr?dikat (Solltest)");
        assertBad("Weiter befindest sich im Osten die Gemeinde Dorf.");
        assertBad("Ich geht jetzt nach Hause, weil ich schon zu sp?t bin.");
        assertBad("?Du muss gehen.?");
        assertBad("Du wei? es doch.");
        assertBad("Sie sagte zu mir: ?Du muss gehen.?");
    }

    @Test
    public void testWrongVerbSubject() throws IOException {
        // correct sentences:
        assertGood("Auch morgen lebe ich.");
        assertGood("Auch morgen leben wir noch.");
        assertGood("Auch morgen lebst du.");
        assertGood("Auch morgen lebt er.");
        assertGood("Auch wenn du leben m?chtest.");
        assertGood("auf der er sieben Jahre blieb.");
        assertGood("Das absolute Ich ist nicht mit dem individuellen Geist zu verwechseln.");
        assertGood("Das Ich ist keine Einbildung");
        assertGood("Das lyrische Ich ist verzweifelt.");
        assertGood("Den Park, von dem er ?u?erst genaue Karten zeichnete.");
        assertGood("Der auff?lligste Ring ist der erster Ring, obwohl er verglichen mit den anderen Ringen sehr schwach erscheint.");
        assertGood("Der Fehler, falls er bestehen sollte, ist schwerwiegend.");
        assertGood("Der Vorfall, bei dem er einen Teil seines Verm?gens verloren hat, ist lange vorbei.");
        assertGood("Diese L?sung wurde in der 64'er beschrieben, kam jedoch nie.");
        assertGood("Die Theorie, mit der ich arbeiten konnte.");
        // assertGood("Die Zeitschrift film-dienst.");
        assertGood("Du bist nett.");
        assertGood("Du kannst heute leider nicht kommen.");
        assertGood("Du lebst.");
        assertGood("Du w?nschst dir so viel.");
        assertGood("Er geht zu ihr.");
        assertGood("Er ist nett.");
        assertGood("Er kann heute leider nicht kommen.");
        assertGood("Er lebt.");
        assertGood("Er wisse nicht, ob er lachen oder weinen solle.");
        assertGood("Er und du leben.");
        assertGood("Er und ich leben.");
        assertGood("Falls er bestehen sollte, gehen sie weg.");
        // assertGood("Fest wie unsere Eichen halten allezeit wir stand, wenn St?rme brausen ?bers Land."); // TODO (remembers "brausen", forgets about "halten")
        assertGood("Heere, des Gottes der Schlachtreihen Israels, den du verh?hnt hast.");
        assertGood("Ich bin");
        assertGood("Ich bin Frankreich!");
        assertGood("Ich bin froh, dass ich arbeiten kann.");
        assertGood("Ich bin nett.");
        assertGood("?ich bin tot?");// TODO 1st token is "?ich" ?

        assertGood("Ich kann heute leider nicht kommen.");
        assertGood("Ich lebe.");
        assertGood("Lebst du?");
        assertGood("Morgen kommen du und ich.");
        assertGood("Morgen kommen er, den ich sehr mag, und ich.");
        assertGood("Morgen kommen er und ich.");
        assertGood("Morgen kommen ich und sie.");
        assertGood("Morgen kommen wir und sie.");
        assertGood("nachdem er erfahren hatte");
        assertGood("Nett bin ich.");
        assertGood("Nett bist du.");
        assertGood("Nett ist er.");
        assertGood("Nett sind wir.");
        assertGood("Niemand ahnte, dass er gewinnen k?nne.");
        assertGood("Sie lebt und wir leben.");
        assertGood("Sie und er leben.");
        assertGood("Sind ich und Peter nicht nette Kinder?");
        assertGood("Sodass ich sagen m?chte, dass unsere sch?nen Erinnerungen gut sind.");
        assertGood("Wann ich meinen letzten Film drehen werde, ist unbekannt.");
        assertGood("Was ich tun muss.");
        assertGood("Welche Aufgaben er dabei tats?chlich ?bernehmen k?nnte");
        assertGood("wie er beschaffen war");
        assertGood("Wir gelangen zu dir.");
        assertGood("Wir k?nnen heute leider nicht kommen.");
        assertGood("Wir leben noch.");
        assertGood("Wir sind nett.");
        assertGood("Wobei wir benutzt haben, dass der Satz gilt.");
        assertGood("W?nschst du dir mehr Zeit?");
        assertGood("Wyrjtjbst du?");// make sure that "UNKNOWN" is handled correctly

        assertGood("Wenn ich du w?re, w?rde ich das nicht machen.");
        assertGood("Er sagte: ?Darf ich bitten, mir zu folgen??");
        // TODO: assertBad("Er fragte irritiert: ?Darf ich fragen, die an dich gerichtet werden, beantworten??");
        // assertGood("Angenommen, du w?rst ich."); TODO
        assertGood("Ich denke, dass das Haus, in das er gehen will, heute Morgen gestrichen worden ist.");
        // incorrect sentences:
        assertBad("Auch morgen leben du.");
        assertBad("Du wei? noch, dass du das gestern gesagt hast.");
        assertBad("Auch morgen leben du");// do not segfault because "du" is the last token

        assertBad("Auch morgen leben er.");
        assertBad("Auch morgen leben ich.");
        assertBad("Auch morgen lebte wir noch.");
        assertBad("Du bin nett.", 2);// TODO 2 errors

        assertBad("Du k?nnen heute leider nicht kommen.");
        assertBad("Du k?nnen heute leider nicht kommen.", "Du kannst", "Du konntest", "Du k?nnest", "Du k?nntest", "Wir k?nnen", "Sie k?nnen");
        assertBad("Du leben.");
        assertBad("Du w?nscht dir so viel.");
        assertBad("Er bin nett.", 2);
        assertBad("Er gelangst zu ihr.", 2);
        assertBad("Er k?nnen heute leider nicht kommen.", "Subjekt (Er) und Pr?dikat (k?nnen)");
        assertBad("Er lebst.", 2);
        assertBad("Ich bist nett.", 2);
        // assertBad("Ich geht jetzt nach Hause und dort gehe ich sofort unter die Dusche."); TODO
        assertBad("Ich kannst heute leider nicht kommen.", 2);
        assertBad("Ich leben.");
        assertBad("Ich leben.", "Ich lebe", "Ich lebte", "Wir leben", "Sie leben");
        assertBad("Lebe du?");
        assertBad("Lebe du?", "Lebest du", "Lebst du", "Lebtest du", "Lebe ich", "Lebe er", "Lebe sie", "Lebe es");
        assertBad("Leben du?");
        assertBad("Nett bist ich nicht.", 2);
        assertBad("Nett bist ich nicht.", 2, "bin ich", "sei ich", "war ich", "w?re ich", "bist du");
        assertBad("Nett sind du.");
        assertBad("Nett sind er.");
        assertBad("Nett sind er.", "ist er", "sei er", "war er", "w?re er", "sind wir", "sind sie");
        assertBad("Nett warst wir.", 2);
        assertBad("Wir bin nett.", 2);
        assertBad("Wir gelangst zu ihr.", 2);
        assertBad("Wir k?nnt heute leider nicht kommen.");
        assertBad("W?nscht du dir mehr Zeit?", "Subjekt (du) und Pr?dikat (W?nscht)");
        assertBad("Wir lebst noch.", 2);
        assertBad("Wir lebst noch.", 2, "Wir leben", "Wir lebten", "Du lebst");
    }
}

