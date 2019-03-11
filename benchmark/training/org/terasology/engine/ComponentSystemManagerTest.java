package org.terasology.engine;


import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.console.Console;
import org.terasology.logic.console.commandSystem.MethodCommand;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.console.commandSystem.annotations.Sender;


public class ComponentSystemManagerTest {
    private ComponentSystemManager systemUnderTest;

    private Console console;

    @Test
    public void testRegisterUpdateSubscriberAddsSubscriber() {
        UpdateSubscriberSystem system = Mockito.mock(UpdateSubscriberSystem.class);
        systemUnderTest.register(system);
        Assert.assertThat(Iterables.size(systemUnderTest.iterateUpdateSubscribers()), CoreMatchers.is(1));
    }

    @Test
    public void testShutdownRemovesUpdateSubscribers() {
        UpdateSubscriberSystem system = Mockito.mock(UpdateSubscriberSystem.class);
        systemUnderTest.register(system);
        systemUnderTest.shutdown();
        Assert.assertThat(Iterables.size(systemUnderTest.iterateUpdateSubscribers()), CoreMatchers.is(0));
    }

    @Test
    public void testRegisterRenderSystemAddsRenderSubscriber() {
        RenderSystem system = Mockito.mock(RenderSystem.class);
        systemUnderTest.register(system);
        Assert.assertThat(Iterables.size(systemUnderTest.iterateRenderSubscribers()), CoreMatchers.is(1));
    }

    @Test
    public void testShutdownRemovesRenderSubscribers() {
        // see https://github.com/MovingBlocks/Terasology/issues/3087#issuecomment-326409756
        RenderSystem system = Mockito.mock(RenderSystem.class);
        systemUnderTest.register(system);
        systemUnderTest.shutdown();
        Assert.assertThat(Iterables.size(systemUnderTest.iterateRenderSubscribers()), CoreMatchers.is(0));
    }

    @Test
    public void shouldRegisterCommand() {
        systemUnderTest.register(new ComponentSystemManagerTest.SystemWithValidCommand());
        systemUnderTest.initialise();
        ArgumentCaptor<MethodCommand> methodCommandArgumentCaptor = ArgumentCaptor.forClass(MethodCommand.class);
        Mockito.verify(console).registerCommand(methodCommandArgumentCaptor.capture());
        MethodCommand command = methodCommandArgumentCaptor.getValue();
        Assert.assertThat(command.getName().toString(), CoreMatchers.is("validCommandName"));
    }

    @Test
    public void shouldRegisterCommandWithoutSenderAnnotation() {
        // see https://github.com/MovingBlocks/Terasology/issues/2679
        systemUnderTest.register(new ComponentSystemManagerTest.SystemWithCommandMissingSenderAnnotation());
        systemUnderTest.initialise();
        ArgumentCaptor<MethodCommand> methodCommandArgumentCaptor = ArgumentCaptor.forClass(MethodCommand.class);
        Mockito.verify(console).registerCommand(methodCommandArgumentCaptor.capture());
        MethodCommand command = methodCommandArgumentCaptor.getValue();
        Assert.assertThat(command.getName().toString(), CoreMatchers.is("commandWithoutSenderAnnotation"));
    }

    @Test
    public void shouldLogErrorWhenRegisterCommandWithoutSenderAnnotation() {
        // see https://github.com/MovingBlocks/Terasology/issues/2679
        Appender<ILoggingEvent> appender = ComponentSystemManagerTest.mockAppender();
        ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME))).addAppender(appender);
        systemUnderTest.register(new ComponentSystemManagerTest.SystemWithCommandMissingSenderAnnotation());
        systemUnderTest.initialise();
        ArgumentCaptor<LoggingEvent> loggingEventArgumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
        Mockito.verify(appender).doAppend(loggingEventArgumentCaptor.capture());
        List<String> allErrorLogMessages = loggingEventArgumentCaptor.getAllValues().stream().filter(( e) -> e.getLevel().isGreaterOrEqual(Level.ERROR)).map(LoggingEvent::getFormattedMessage).collect(Collectors.toList());
        String expectedMessage = "Command commandWithoutSenderAnnotation provided by " + ("SystemWithCommandMissingSenderAnnotation contains a EntityRef without @Sender annotation, " + "may cause a NullPointerException");
        Assert.assertThat(allErrorLogMessages, CoreMatchers.hasItem(expectedMessage));
    }

    private static class SystemWithValidCommand extends BaseComponentSystem {
        @Command
        public String validCommandName(@CommandParam("parameter")
        String value, @Sender
        EntityRef sender) {
            return value;
        }
    }

    private static class SystemWithCommandMissingSenderAnnotation extends BaseComponentSystem {
        @Command
        public String commandWithoutSenderAnnotation(@CommandParam("parameter")
        String value, EntityRef sender) {
            return value;
        }
    }
}

