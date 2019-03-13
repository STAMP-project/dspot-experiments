package com.github.jknack.handlebars;


import java.io.IOException;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;


public class TypeSafeTemplateTest extends AbstractTest {
    public static class User {
        private String name;

        public User(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static interface UserTemplate extends TypeSafeTemplate<TypeSafeTemplateTest.User> {
        public TypeSafeTemplateTest.UserTemplate setAge(int age);

        public TypeSafeTemplateTest.UserTemplate setRole(String role);

        public void set();

        public void set(int v);
    }

    @Test
    public void genericTypeSafe() throws IOException {
        TypeSafeTemplateTest.User user = new TypeSafeTemplateTest.User("edgar");
        TypeSafeTemplate<TypeSafeTemplateTest.User> userTemplate = compile("Hello {{name}}!").as();
        Assert.assertEquals("Hello edgar!", userTemplate.apply(user));
    }

    @Test
    public void customTypeSafe() throws IOException {
        TypeSafeTemplateTest.User user = new TypeSafeTemplateTest.User("Edgar");
        TypeSafeTemplateTest.UserTemplate userTemplate = compile("{{name}} is {{age}} years old!").as(TypeSafeTemplateTest.UserTemplate.class).setAge(32);
        Assert.assertEquals("Edgar is 32 years old!", apply(user));
    }

    @Test
    public void customTypeSafe2() throws IOException {
        TypeSafeTemplateTest.User user = new TypeSafeTemplateTest.User("Edgar");
        TypeSafeTemplateTest.UserTemplate userTemplate = compile("{{role}}").as(TypeSafeTemplateTest.UserTemplate.class).setRole("Software Architect");
        Assert.assertEquals("Software Architect", apply(user));
    }

    @Test
    public void testToString() throws IOException {
        TypeSafeTemplateTest.UserTemplate userTemplate = compile("Hello {{name}}").as(TypeSafeTemplateTest.UserTemplate.class);
        Assert.assertThat(userTemplate.toString(), StringContains.containsString("UserTemplate"));
    }

    @Test
    public void testHashCode() throws IOException {
        Template template = compile("Hello {{name}}");
        TypeSafeTemplateTest.UserTemplate userTemplate = template.as(TypeSafeTemplateTest.UserTemplate.class);
        Assert.assertEquals(userTemplate.hashCode(), userTemplate.hashCode());
        Assert.assertNotEquals(userTemplate.hashCode(), template.as(TypeSafeTemplateTest.UserTemplate.class));
    }

    @Test
    public void testEquals() throws IOException {
        Template template = compile("Hello {{name}}");
        TypeSafeTemplateTest.UserTemplate userTemplate = template.as(TypeSafeTemplateTest.UserTemplate.class);
        Assert.assertEquals(userTemplate, userTemplate);
        Assert.assertNotEquals(userTemplate, template.as(TypeSafeTemplateTest.UserTemplate.class));
        Assert.assertFalse(userTemplate.equals(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noHandlerMethod() throws IOException {
        TypeSafeTemplateTest.UserTemplate userTemplate = compile("{{role}}").as(TypeSafeTemplateTest.UserTemplate.class);
        userTemplate.set(6);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noHandlerMethod2() throws IOException {
        TypeSafeTemplateTest.UserTemplate userTemplate = compile("{{role}}").as(TypeSafeTemplateTest.UserTemplate.class);
        userTemplate.set();
    }
}

