package com.github.jknack.handlebars.springmvc;


import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Options.class, MessageSourceHelper.class, Context.class })
public class MessageSourceHelperTest {
    @Test(expected = NullPointerException.class)
    public void nullMessageSource() {
        new MessageSourceHelper(null);
    }

    @Test
    public void messageSource() throws IOException {
        String message = "Hola";
        String code = "sayHi";
        String defaultMessage = null;
        // Options
        Object[] params = new Object[]{  };
        @SuppressWarnings("unchecked")
        Map<String, Object> hash = createMock(Map.class);
        expect(hash.get("default")).andReturn(defaultMessage);
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(params).setHash(hash).build();
        MessageSource messageSource = createMock(MessageSource.class);
        expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage), isA(Locale.class))).andReturn(message);
        replay(messageSource, hash);
        Object result = new MessageSourceHelper(messageSource).apply(code, options);
        Assert.assertEquals(message, result);
        verify(messageSource, hash);
    }

    @Test
    public void messageSourceWithParams() throws IOException {
        String message = "Hola";
        String code = "sayHi";
        String defaultMessage = null;
        // Options
        Object[] params = new Object[]{ 1, 2, 3 };
        @SuppressWarnings("unchecked")
        Map<String, Object> hash = createMock(Map.class);
        expect(hash.get("default")).andReturn(defaultMessage);
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(params).setHash(hash).build();
        MessageSource messageSource = createMock(MessageSource.class);
        expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage), isA(Locale.class))).andReturn(message);
        replay(messageSource, hash);
        Object result = new MessageSourceHelper(messageSource).apply(code, options);
        Assert.assertEquals(message, result);
        verify(messageSource, hash);
    }

    @Test
    public void messageSourceWithDefaulMessage() throws IOException {
        String message = "Hola";
        String code = "sayHi";
        String defaultMessage = "Aca viene el 3";
        // Options
        Object[] params = new Object[]{ 1, 2, 3 };
        @SuppressWarnings("unchecked")
        Map<String, Object> hash = createMock(Map.class);
        expect(hash.get("default")).andReturn(defaultMessage);
        Handlebars hbs = createMock(Handlebars.class);
        Context ctx = createMock(Context.class);
        Template fn = createMock(Template.class);
        Options options = setParams(params).setHash(hash).build();
        MessageSource messageSource = createMock(MessageSource.class);
        expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage), isA(Locale.class))).andReturn(message);
        replay(messageSource, hash);
        Object result = new MessageSourceHelper(messageSource).apply(code, options);
        Assert.assertEquals(message, result);
        verify(messageSource, hash);
    }
}

