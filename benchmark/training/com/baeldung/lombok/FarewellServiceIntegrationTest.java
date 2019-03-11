package com.baeldung.lombok;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FarewellServiceIntegrationTest {
    private static final String TRANSLATED = "TRANSLATED";

    @Test
    public void sayByeWithTranslatedMessage() {
        Translator translator = Mockito.mock(Translator.class);
        Mockito.when(translator.translate("bye")).thenReturn(FarewellServiceIntegrationTest.TRANSLATED);
        FarewellService farewellService = new FarewellService(translator);
        Assert.assertEquals(FarewellServiceIntegrationTest.TRANSLATED, farewellService.farewell());
    }
}

