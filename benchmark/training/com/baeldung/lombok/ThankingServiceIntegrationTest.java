package com.baeldung.lombok;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ThankingServiceIntegrationTest {
    private static final String TRANSLATED = "TRANSLATED";

    @Test
    public void thankWithTranslatedMessage() {
        Translator translator = Mockito.mock(Translator.class);
        Mockito.when(translator.translate("thank you")).thenReturn(ThankingServiceIntegrationTest.TRANSLATED);
        ThankingService thankingService = new ThankingService(translator);
        Assert.assertEquals(ThankingServiceIntegrationTest.TRANSLATED, thankingService.thank());
    }
}

