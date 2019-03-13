package io.dropwizard.jdbi3;


import java.util.UUID;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.TemplateEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class NamePrependingTemplateEngineTest {
    private static final String TEMPLATE = UUID.randomUUID().toString();

    private static final String ORIGINAL_RENDERED = UUID.randomUUID().toString();

    public interface MyDao {
        String myDbCall();
    }

    private TemplateEngine original;

    private StatementContext ctx;

    private NamePrependingTemplateEngine sut;

    @Test
    public void testNoExtensionMethodShouldReturnOriginal() {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(null);
        final String result = sut.render(NamePrependingTemplateEngineTest.TEMPLATE, ctx);
        assertThat(result).isEqualTo(NamePrependingTemplateEngineTest.ORIGINAL_RENDERED);
    }

    @Test
    public void testPrependsCorrectName() throws NoSuchMethodException {
        final ExtensionMethod extensionMethod = new ExtensionMethod(NamePrependingTemplateEngineTest.MyDao.class, NamePrependingTemplateEngineTest.MyDao.class.getMethod("myDbCall"));
        Mockito.when(ctx.getExtensionMethod()).thenReturn(extensionMethod);
        final String result = sut.render(NamePrependingTemplateEngineTest.TEMPLATE, ctx);
        assertThat(result).isEqualTo(((((("/* " + (extensionMethod.getType().getSimpleName())) + ".") + (extensionMethod.getMethod().getName())) + " */ ") + (NamePrependingTemplateEngineTest.ORIGINAL_RENDERED)));
    }
}

