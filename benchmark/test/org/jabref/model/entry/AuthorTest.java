package org.jabref.model.entry;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorTest {
    @Test
    public void addDotIfAbbreviationAddDot() {
        Assertions.assertEquals("O.", Author.addDotIfAbbreviation("O"));
        Assertions.assertEquals("A. O.", Author.addDotIfAbbreviation("AO"));
        Assertions.assertEquals("A. O.", Author.addDotIfAbbreviation("AO."));
        Assertions.assertEquals("A. O.", Author.addDotIfAbbreviation("A.O."));
        Assertions.assertEquals("A.-O.", Author.addDotIfAbbreviation("A-O"));
    }

    @Test
    public void addDotIfAbbreviationDoNotAddDot() {
        Assertions.assertEquals("O.", Author.addDotIfAbbreviation("O."));
        Assertions.assertEquals("A. O.", Author.addDotIfAbbreviation("A. O."));
        Assertions.assertEquals("A.-O.", Author.addDotIfAbbreviation("A.-O."));
        Assertions.assertEquals("O. Moore", Author.addDotIfAbbreviation("O. Moore"));
        Assertions.assertEquals("A. O. Moore", Author.addDotIfAbbreviation("A. O. Moore"));
        Assertions.assertEquals("O. von Moore", Author.addDotIfAbbreviation("O. von Moore"));
        Assertions.assertEquals("A.-O. Moore", Author.addDotIfAbbreviation("A.-O. Moore"));
        Assertions.assertEquals("Moore, O.", Author.addDotIfAbbreviation("Moore, O."));
        Assertions.assertEquals("Moore, O., Jr.", Author.addDotIfAbbreviation("Moore, O., Jr."));
        Assertions.assertEquals("Moore, A. O.", Author.addDotIfAbbreviation("Moore, A. O."));
        Assertions.assertEquals("Moore, A.-O.", Author.addDotIfAbbreviation("Moore, A.-O."));
        Assertions.assertEquals("MEmre", Author.addDotIfAbbreviation("MEmre"));
        Assertions.assertEquals("{\\\'{E}}douard", Author.addDotIfAbbreviation("{\\\'{E}}douard"));
        Assertions.assertEquals("J{\\\"o}rg", Author.addDotIfAbbreviation("J{\\\"o}rg"));
        Assertions.assertEquals("Moore, O. and O. Moore", Author.addDotIfAbbreviation("Moore, O. and O. Moore"));
        Assertions.assertEquals("Moore, O. and O. Moore and Moore, O. O.", Author.addDotIfAbbreviation("Moore, O. and O. Moore and Moore, O. O."));
    }
}

