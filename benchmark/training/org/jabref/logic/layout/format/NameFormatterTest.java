package org.jabref.logic.layout.format;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NameFormatterTest {
    @Test
    public void testFormatStringStringBibtexEntry() {
        NameFormatter l = new NameFormatter();
        Assertions.assertEquals("Doe", l.format("Joe Doe", "1@*@{ll}"));
        Assertions.assertEquals("moremoremoremore", l.format("Joe Doe and Mary Jane and Bruce Bar and Arthur Kay", "1@*@{ll}@@2@1..1@{ff}{ll}@2..2@ and {ff}{last}@@*@*@more"));
        Assertions.assertEquals("Doe", l.format("Joe Doe", "1@*@{ll}@@2@1..1@{ff}{ll}@2..2@ and {ff}{last}@@*@*@more"));
        Assertions.assertEquals("JoeDoe and MaryJ", l.format("Joe Doe and Mary Jane", "1@*@{ll}@@2@1..1@{ff}{ll}@2..2@ and {ff}{l}@@*@*@more"));
        Assertions.assertEquals("Doe, Joe and Jane, M. and Kamp, J.~A.", l.format("Joe Doe and Mary Jane and John Arthur van Kamp", "1@*@{ll}, {ff}@@*@1@{ll}, {ff}@2..-1@ and {ll}, {f}."));
        Assertions.assertEquals("Doe Joe and Jane, M. and Kamp, J.~A.", l.format("Joe Doe and Mary Jane and John Arthur van Kamp", "1@*@{ll}, {ff}@@*@1@{ll} {ff}@2..-1@ and {ll}, {f}."));
    }

    @Test
    public void testFormat() {
        NameFormatter a = new NameFormatter();
        // Empty case
        Assertions.assertEquals("", a.format(""));
        String formatString = "1@1@{vv }{ll}{ ff}@@2@1@{vv }{ll}{ ff}@2@ and {vv }{ll}{, ff}@@*@1@{vv }{ll}{ ff}@2..-2@, {vv }{ll}{, ff}@-1@ and {vv }{ll}{, ff}";
        // Single Names
        Assertions.assertEquals("Vandekamp Mary~Ann", a.format("Mary Ann Vandekamp", formatString));
        // Two names
        Assertions.assertEquals("von Neumann John and Black~Brown, Peter", a.format("John von Neumann and Black Brown, Peter", formatString));
        // Three names
        Assertions.assertEquals("von Neumann John, Smith, John and Black~Brown, Peter", a.format("von Neumann, John and Smith, John and Black Brown, Peter", formatString));
        Assertions.assertEquals("von Neumann John, Smith, John and Black~Brown, Peter", a.format("John von Neumann and John Smith and Black Brown, Peter", formatString));
        // Four names
        Assertions.assertEquals("von Neumann John, Smith, John, Vandekamp, Mary~Ann and Black~Brown, Peter", a.format("von Neumann, John and Smith, John and Vandekamp, Mary Ann and Black Brown, Peter", formatString));
    }
}

