package com.baeldung.lombok;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TestConfig.class)
public class ThankingServiceAutowiringIntegrationTest {
    @Autowired
    private ThankingService thankingService;

    @Autowired
    private Translator translator;

    @Test
    public void thankWithTranslatedMessage() {
        String translated = "translated";
        Mockito.when(translator.translate("thank you")).thenReturn(translated);
        Assert.assertEquals(translated, thankingService.thank());
    }
}

