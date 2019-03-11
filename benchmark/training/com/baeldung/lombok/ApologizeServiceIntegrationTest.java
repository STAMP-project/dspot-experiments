package com.baeldung.lombok;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ApologizeServiceIntegrationTest {
    private static final String MESSAGE = "MESSAGE";

    private static final String TRANSLATED = "TRANSLATED";

    @Test
    public void apologizeWithCustomTranslatedMessage() {
        Translator translator = Mockito.mock(Translator.class);
        ApologizeService apologizeService = new ApologizeService(translator, ApologizeServiceIntegrationTest.MESSAGE);
        Mockito.when(translator.translate(ApologizeServiceIntegrationTest.MESSAGE)).thenReturn(ApologizeServiceIntegrationTest.TRANSLATED);
        Assert.assertEquals(ApologizeServiceIntegrationTest.TRANSLATED, apologizeService.apologize());
    }
}

