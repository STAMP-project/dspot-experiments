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
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Markus Brenneis
 */
public class GermanWrongWordInContextRuleTest {
    private JLanguageTool langTool;

    private GermanWrongWordInContextRule rule;

    @Test
    public void testRule() throws IOException {
        // Laiche/Leiche
        assertBad("Eine Laiche ist ein toter K?rper.");
        assertGood("Eine Leiche ist ein toter K?rper.");
        assertGood("Die Leichen der Verstorbenen wurden ins Wasser geworfen.");
        // Lid/Lied
        assertGood("Ihre Lider sind entz?ndet.");
        assertGood("Er hat entz?ndete Lider.");
        assertGood("Wir singen gemeinsam Lieder.");
        assertGood("Lieder singen wir.");
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Lider singen wir."))[0].getFromPos());
        Assert.assertEquals(11, rule.match(langTool.getAnalyzedSentence("Ihre Lieder sind entz?ndet."))[0].getToPos());
        Assert.assertEquals("Lider", rule.match(langTool.getAnalyzedSentence("Er hat entz?ndete Lieder."))[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Lieder", rule.match(langTool.getAnalyzedSentence("Wir singen gemeinsam Lider."))[0].getSuggestedReplacements().get(0));
        // malen/mahlen
        assertGood("Ich soll Bilder einer M?hle malen.");
        assertGood("Ich male ein Bild einer M?hle.");
        assertGood("Das Bild zeigt eine mahlende M?hle.");
        assertGood("Eine mahlende M?hle zeigt das Bild.");
        assertGood("Wenn du mal etwas Mehl brauchst, kannst du zu mir kommen.");
        assertBad("Weizen ausmalen.");
        assertBad("Ich mahle das Bild aus.");
        assertBad("Eine M?hle wird zum Malen verwendet.");
        assertBad("Das gemalene Korn aus der M?hle ist gut.");
        assertBad("Zum Malen verwendet man eine M?hle.");
        assertBad("Du musst das Bild ausmahlen.");
        assertBad("Wir haben das im Kunstunterricht gemahlt.");
        assertBad("Er hat ein sch?nes Selbstportr?t gemahlt.");
        Assert.assertEquals("gemahlen", rule.match(langTool.getAnalyzedSentence("Das Korn wird in den M?hlen gemalen."))[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("malten", rule.match(langTool.getAnalyzedSentence("Wir mahlten im Kunstunterricht."))[0].getSuggestedReplacements().get(0));
        // Mine/Miene
        assertGood("Er verzieht keine Miene.");
        assertGood("Er verzieht keine Miene.");
        assertGood("Die Explosion der Mine.");
        assertGood("Die Mine ist explodiert.");
        assertGood("Er versucht, keine Miene zu verziehen.");
        assertGood("Sie sollen weiter Minen eingesetzt haben.");
        assertGood("Er verzieht sich nach Bekanntgabe der Mineral?lsteuerverordnung.");
        assertBad("Er verzieht keine Mine.");
        assertBad("Mit unbewegter Mine.");
        assertBad("Er setzt eine kalte Mine auf.");
        assertBad("Er sagt, die unterirdische Miene sei zusammengest?rzt.");
        assertBad("Die Miene ist eingest?rzt.");
        assertBad("Die Sprengung mit Mienen ist toll.");
        assertBad("Der Bleistift hat eine Miene.");
        assertBad("Die Mienen sind gestern Abend explodiert.");
        assertBad("Die Miene des Kugelschreibers ist leer.");
        Assert.assertEquals("Minen", rule.match(langTool.getAnalyzedSentence("Er hat das mit den Mienen weggesprengt."))[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Miene", rule.match(langTool.getAnalyzedSentence("Er versucht, keine Mine zu verziehen."))[0].getSuggestedReplacements().get(0));
        // Saite/Seite
        assertGood("Die Seiten des Buches sind beschrieben.");
        assertGood("Dieses Buch ?ber die Gitarre hat nur sechs Seiten.");
        assertGood("Diese Gitarre hat sechs Saiten.");
        assertGood("Die UNO muss andere Saiten aufziehen.");
        assertGood("Eine Gitarre hat Saiten, aber keine Seiten.");
        assertGood("Die Saiten des Violoncellos sind kurz.");
        assertGood("Dieses Buch ?ber die Gitarre hat nur sechs Seiten.");
        assertGood("Eine Seite und eine scharfe Suppe.");
        assertBad("Die Saiten des Buches sind beschrieben.");
        assertBad("Die Seiten des Klaviers werden angeschlagen.");
        assertBad("Die Seiten der Kurzhalsgeige sind gerissen.");
        assertBad("Die Seiten des Kontrabasses sind gerissen.");
        assertBad("Bei der UNO m?ssen andere Seiten aufgezogen werden.");
        assertBad("Die Seiten des Violoncellos sind kurz.");
        Assert.assertEquals("Saite", rule.match(langTool.getAnalyzedSentence("Die E-Gitarre hat eine sechste Seite."))[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("Seiten", rule.match(langTool.getAnalyzedSentence("Dieses Buch hat sechs Saiten."))[0].getSuggestedReplacements().get(0));
    }
}

