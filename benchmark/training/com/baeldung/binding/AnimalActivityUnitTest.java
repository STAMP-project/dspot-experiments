package com.baeldung.binding;


import Level.INFO;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * https://gist.github.com/bloodredsun/a041de13e57bf3c6c040
 */
@RunWith(MockitoJUnitRunner.class)
public class AnimalActivityUnitTest {
    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Test
    public void givenAnimalReference__whenRefersAnimalObject_shouldCallFunctionWithAnimalParam() {
        Animal animal = new Animal();
        AnimalActivity.sleep(animal);
        Mockito.verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        Assert.assertThat(loggingEvent.getLevel(), CoreMatchers.is(INFO));
        Assert.assertThat(loggingEvent.getFormattedMessage(), CoreMatchers.is("Animal is sleeping"));
    }

    @Test
    public void givenDogReference__whenRefersCatObject_shouldCallFunctionWithAnimalParam() {
        Cat cat = new Cat();
        AnimalActivity.sleep(cat);
        Mockito.verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        Assert.assertThat(loggingEvent.getLevel(), CoreMatchers.is(INFO));
        Assert.assertThat(loggingEvent.getFormattedMessage(), CoreMatchers.is("Cat is sleeping"));
    }

    @Test
    public void givenAnimaReference__whenRefersDogObject_shouldCallFunctionWithAnimalParam() {
        Animal cat = new Cat();
        AnimalActivity.sleep(cat);
        Mockito.verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        Assert.assertThat(loggingEvent.getLevel(), CoreMatchers.is(INFO));
        Assert.assertThat(loggingEvent.getFormattedMessage(), CoreMatchers.is("Animal is sleeping"));
    }
}

