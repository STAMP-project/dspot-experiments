package org.jabref.logic.journals;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class AbbreviationsTest {
    private JournalAbbreviationPreferences prefs;

    private JournalAbbreviationLoader abbreviations;

    @Test
    public void getNextAbbreviationAbbreviatesIEEEJournalTitle() {
        Mockito.when(prefs.useIEEEAbbreviations()).thenReturn(true);
        Assertions.assertEquals("#IEEE_J_PROC#", abbreviations.getRepository(prefs).getNextAbbreviation("Proceedings of the IEEE").get());
    }

    @Test
    public void getNextAbbreviationExpandsIEEEAbbreviation() {
        Mockito.when(prefs.useIEEEAbbreviations()).thenReturn(true);
        Assertions.assertEquals("Proceedings of the IEEE", abbreviations.getRepository(prefs).getNextAbbreviation("#IEEE_J_PROC#").get());
    }

    @Test
    public void getNextAbbreviationAbbreviatesJournalTitle() {
        Assertions.assertEquals("Proc. IEEE", abbreviations.getRepository(prefs).getNextAbbreviation("Proceedings of the IEEE").get());
    }

    @Test
    public void getNextAbbreviationRemovesPoint() {
        Assertions.assertEquals("Proc IEEE", abbreviations.getRepository(prefs).getNextAbbreviation("Proc. IEEE").get());
    }

    @Test
    public void getNextAbbreviationExpandsAbbreviation() {
        Assertions.assertEquals("Proceedings of the IEEE", abbreviations.getRepository(prefs).getNextAbbreviation("Proc IEEE").get());
    }
}

