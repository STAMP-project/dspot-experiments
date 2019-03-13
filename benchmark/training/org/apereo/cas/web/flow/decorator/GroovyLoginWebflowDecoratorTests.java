package org.apereo.cas.web.flow.decorator;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link GroovyLoginWebflowDecoratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@Tag("Groovy")
public class GroovyLoginWebflowDecoratorTests {
    @Test
    public void verifyOperation() {
        val groovy = new GroovyLoginWebflowDecorator(new ClassPathResource("GroovyLoginWebflowDecorator.groovy"));
        val requestContext = new MockRequestContext();
        groovy.decorate(requestContext, Mockito.mock(ApplicationContext.class));
        Assertions.assertTrue(requestContext.getFlowScope().contains("decoration"));
    }
}

