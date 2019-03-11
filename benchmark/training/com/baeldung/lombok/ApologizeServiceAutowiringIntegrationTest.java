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
public class ApologizeServiceAutowiringIntegrationTest {
    private static final String TRANSLATED = "TRANSLATED";

    @Autowired
    private ApologizeService apologizeService;

    @Autowired
    private Translator translator;

    @Test
    public void apologizeWithTranslatedMessage() {
        Mockito.when(translator.translate("sorry")).thenReturn(ApologizeServiceAutowiringIntegrationTest.TRANSLATED);
        Assert.assertEquals(ApologizeServiceAutowiringIntegrationTest.TRANSLATED, apologizeService.apologize());
    }
}

