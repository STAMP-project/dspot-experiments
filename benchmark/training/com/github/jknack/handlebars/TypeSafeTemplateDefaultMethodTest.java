package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TypeSafeTemplateDefaultMethodTest {
    private static class User {
        private final String name;

        User(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    interface UserTemplate extends TypeSafeTemplate<TypeSafeTemplateDefaultMethodTest.User> {
        default String renderUpperCase(TypeSafeTemplateDefaultMethodTest.User user) throws IOException {
            return apply(user).toUpperCase();
        }
    }

    @Test
    public void testDefaultMethod() throws Exception {
        TypeSafeTemplateDefaultMethodTest.User user = new TypeSafeTemplateDefaultMethodTest.User("Smitty");
        TypeSafeTemplateDefaultMethodTest.UserTemplate template = new Handlebars().compileInline("Hello {{name}}").as(TypeSafeTemplateDefaultMethodTest.UserTemplate.class);
        String result = template.renderUpperCase(user);
        Assert.assertEquals("HELLO SMITTY", result);
    }
}

