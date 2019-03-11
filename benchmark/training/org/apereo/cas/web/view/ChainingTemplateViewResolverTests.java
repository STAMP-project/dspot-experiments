package org.apereo.cas.web.view;


import java.util.LinkedHashMap;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;


/**
 * This is {@link ChainingTemplateViewResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ChainingTemplateViewResolverTests {
    @Test
    public void verifyAction() {
        val r = new ChainingTemplateViewResolver();
        val resolver = new StringTemplateResolver();
        resolver.setCheckExistence(true);
        r.addResolver(resolver);
        r.initialize();
        val res = r.resolveTemplate(Mockito.mock(IEngineConfiguration.class), "cas", "template", new LinkedHashMap());
        Assertions.assertNotNull(res);
    }
}

