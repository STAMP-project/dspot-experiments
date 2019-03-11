package org.jabref.logic.layout.format;


import org.jabref.logic.layout.LayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuthorOrgSciTest {
    @Test
    public void testOrgSci() {
        LayoutFormatter f = new AuthorOrgSci();
        Assertions.assertEquals("Flynn, J., S. Gartska", f.format("John Flynn and Sabine Gartska"));
        Assertions.assertEquals("Garvin, D. A.", f.format("David A. Garvin"));
        Assertions.assertEquals("Makridakis, S., S. C. Wheelwright, V. E. McGee", f.format("Sa Makridakis and Sa Ca Wheelwright and Va Ea McGee"));
    }

    @Test
    public void testOrgSciPlusAbbreviation() {
        LayoutFormatter f = new CompositeFormat(new AuthorOrgSci(), new NoSpaceBetweenAbbreviations());
        Assertions.assertEquals("Flynn, J., S. Gartska", f.format("John Flynn and Sabine Gartska"));
        Assertions.assertEquals("Garvin, D.A.", f.format("David A. Garvin"));
        Assertions.assertEquals("Makridakis, S., S.C. Wheelwright, V.E. McGee", f.format("Sa Makridakis and Sa Ca Wheelwright and Va Ea McGee"));
    }
}

