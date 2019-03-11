package com.baeldung.applicationcontext;


import java.util.List;
import java.util.Locale;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ClasspathXmlApplicationContextIntegrationTest {
    @Test
    public void testBasicUsage() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        Student student = ((Student) (context.getBean("student")));
        Assert.assertThat(student.getNo(), IsEqual.equalTo(15));
        Assert.assertThat(student.getName(), IsEqual.equalTo("Tom"));
        Student sameStudent = context.getBean("student", Student.class);// do not need cast class

        Assert.assertThat(sameStudent.getNo(), IsEqual.equalTo(15));
        Assert.assertThat(sameStudent.getName(), IsEqual.equalTo("Tom"));
    }

    @Test
    public void testRegisterShutdownHook() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        context.registerShutdownHook();
    }

    @Test
    public void testInternationalization() {
        MessageSource resources = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-internationalization.xml");
        String enHello = resources.getMessage("hello", null, "Default", Locale.ENGLISH);
        String enYou = resources.getMessage("you", null, Locale.ENGLISH);
        String enThanks = resources.getMessage("thanks", new Object[]{ enYou }, Locale.ENGLISH);
        Assert.assertThat(enHello, IsEqual.equalTo("hello"));
        Assert.assertThat(enThanks, IsEqual.equalTo("thank you"));
        String chHello = resources.getMessage("hello", null, "Default", Locale.SIMPLIFIED_CHINESE);
        String chYou = resources.getMessage("you", null, Locale.SIMPLIFIED_CHINESE);
        String chThanks = resources.getMessage("thanks", new Object[]{ chYou }, Locale.SIMPLIFIED_CHINESE);
        Assert.assertThat(chHello, IsEqual.equalTo("??"));
        Assert.assertThat(chThanks, IsEqual.equalTo("???"));
    }

    @Test
    public void testApplicationContextAware() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        Teacher teacher = context.getBean("teacher", Teacher.class);
        List<Course> courses = teacher.getCourses();
        Assert.assertThat(courses.size(), IsEqual.equalTo(1));
        Assert.assertThat(courses.get(0).getName(), IsEqual.equalTo("math"));
    }
}

