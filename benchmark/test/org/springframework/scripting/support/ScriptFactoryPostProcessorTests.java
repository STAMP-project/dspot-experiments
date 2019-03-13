/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.scripting.support;


import BeanDefinition.SCOPE_PROTOTYPE;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scripting.Messenger;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.groovy.GroovyScriptFactory;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ScriptFactoryPostProcessorTests {
    private static final String MESSAGE_TEXT = "Bingo";

    private static final String MESSENGER_BEAN_NAME = "messenger";

    private static final String PROCESSOR_BEAN_NAME = "processor";

    private static final String CHANGED_SCRIPT = "package org.springframework.scripting.groovy\n" + ((((((((("import org.springframework.scripting.Messenger\n" + "class GroovyMessenger implements Messenger {\n") + "  private String message = \"Bingo\"\n") + "  public String getMessage() {\n") + // quote the returned message (this is the change)...
    "    return \"\'\"  + this.message + \"\'\"\n") + "  }\n") + "  public void setMessage(String message) {\n") + "    this.message = message\n") + "  }\n") + "}");

    private static final String EXPECTED_CHANGED_MESSAGE_TEXT = ("'" + (ScriptFactoryPostProcessorTests.MESSAGE_TEXT)) + "'";

    private static final int DEFAULT_SECONDS_TO_PAUSE = 1;

    private static final String DELEGATING_SCRIPT = "inline:package org.springframework.scripting;\n" + (((((((("class DelegatingMessenger implements Messenger {\n" + "  private Messenger wrappedMessenger;\n") + "  public String getMessage() {\n") + "    return this.wrappedMessenger.getMessage()\n") + "  }\n") + "  public void setMessenger(Messenger wrappedMessenger) {\n") + "    this.wrappedMessenger = wrappedMessenger\n") + "  }\n") + "}");

    @Test
    public void testDoesNothingWhenPostProcessingNonScriptFactoryTypeBeforeInstantiation() throws Exception {
        Assert.assertNull(new ScriptFactoryPostProcessor().postProcessBeforeInstantiation(getClass(), "a.bean"));
    }

    @Test
    public void testThrowsExceptionIfGivenNonAbstractBeanFactoryImplementation() throws Exception {
        try {
            new ScriptFactoryPostProcessor().setBeanFactory(Mockito.mock(BeanFactory.class));
            Assert.fail("Must have thrown exception by this point.");
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testChangeScriptWithRefreshableBeanFunctionality() throws Exception {
        BeanDefinition processorBeanDefinition = ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(true);
        BeanDefinition scriptedBeanDefinition = ScriptFactoryPostProcessorTests.createScriptedGroovyBean();
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.PROCESSOR_BEAN_NAME, processorBeanDefinition);
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, scriptedBeanDefinition);
        ctx.refresh();
        Messenger messenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        Assert.assertEquals(ScriptFactoryPostProcessorTests.MESSAGE_TEXT, messenger.getMessage());
        // cool; now let's change the script and check the refresh behaviour...
        ScriptFactoryPostProcessorTests.pauseToLetRefreshDelayKickIn(ScriptFactoryPostProcessorTests.DEFAULT_SECONDS_TO_PAUSE);
        StaticScriptSource source = ScriptFactoryPostProcessorTests.getScriptSource(ctx);
        source.setScript(ScriptFactoryPostProcessorTests.CHANGED_SCRIPT);
        Messenger refreshedMessenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        // the updated script surrounds the message in quotes before returning...
        Assert.assertEquals(ScriptFactoryPostProcessorTests.EXPECTED_CHANGED_MESSAGE_TEXT, refreshedMessenger.getMessage());
    }

    @Test
    public void testChangeScriptWithNoRefreshableBeanFunctionality() throws Exception {
        BeanDefinition processorBeanDefinition = ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(false);
        BeanDefinition scriptedBeanDefinition = ScriptFactoryPostProcessorTests.createScriptedGroovyBean();
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.PROCESSOR_BEAN_NAME, processorBeanDefinition);
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, scriptedBeanDefinition);
        ctx.refresh();
        Messenger messenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        Assert.assertEquals(ScriptFactoryPostProcessorTests.MESSAGE_TEXT, messenger.getMessage());
        // cool; now let's change the script and check the refresh behaviour...
        ScriptFactoryPostProcessorTests.pauseToLetRefreshDelayKickIn(ScriptFactoryPostProcessorTests.DEFAULT_SECONDS_TO_PAUSE);
        StaticScriptSource source = ScriptFactoryPostProcessorTests.getScriptSource(ctx);
        source.setScript(ScriptFactoryPostProcessorTests.CHANGED_SCRIPT);
        Messenger refreshedMessenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        Assert.assertEquals("Script seems to have been refreshed (must not be as no refreshCheckDelay set on ScriptFactoryPostProcessor)", ScriptFactoryPostProcessorTests.MESSAGE_TEXT, refreshedMessenger.getMessage());
    }

    @Test
    public void testRefreshedScriptReferencePropagatesToCollaborators() throws Exception {
        BeanDefinition processorBeanDefinition = ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(true);
        BeanDefinition scriptedBeanDefinition = ScriptFactoryPostProcessorTests.createScriptedGroovyBean();
        BeanDefinitionBuilder collaboratorBuilder = BeanDefinitionBuilder.rootBeanDefinition(ScriptFactoryPostProcessorTests.DefaultMessengerService.class);
        collaboratorBuilder.addPropertyReference(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME);
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.PROCESSOR_BEAN_NAME, processorBeanDefinition);
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, scriptedBeanDefinition);
        final String collaboratorBeanName = "collaborator";
        ctx.registerBeanDefinition(collaboratorBeanName, collaboratorBuilder.getBeanDefinition());
        ctx.refresh();
        Messenger messenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        Assert.assertEquals(ScriptFactoryPostProcessorTests.MESSAGE_TEXT, messenger.getMessage());
        // cool; now let's change the script and check the refresh behaviour...
        ScriptFactoryPostProcessorTests.pauseToLetRefreshDelayKickIn(ScriptFactoryPostProcessorTests.DEFAULT_SECONDS_TO_PAUSE);
        StaticScriptSource source = ScriptFactoryPostProcessorTests.getScriptSource(ctx);
        source.setScript(ScriptFactoryPostProcessorTests.CHANGED_SCRIPT);
        Messenger refreshedMessenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        // the updated script surrounds the message in quotes before returning...
        Assert.assertEquals(ScriptFactoryPostProcessorTests.EXPECTED_CHANGED_MESSAGE_TEXT, refreshedMessenger.getMessage());
        // ok, is this change reflected in the reference that the collaborator has?
        ScriptFactoryPostProcessorTests.DefaultMessengerService collaborator = ((ScriptFactoryPostProcessorTests.DefaultMessengerService) (ctx.getBean(collaboratorBeanName)));
        Assert.assertEquals(ScriptFactoryPostProcessorTests.EXPECTED_CHANGED_MESSAGE_TEXT, collaborator.getMessage());
    }

    @Test
    public void testReferencesAcrossAContainerHierarchy() throws Exception {
        GenericApplicationContext businessContext = new GenericApplicationContext();
        businessContext.registerBeanDefinition("messenger", BeanDefinitionBuilder.rootBeanDefinition(StubMessenger.class).getBeanDefinition());
        businessContext.refresh();
        BeanDefinitionBuilder scriptedBeanBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroovyScriptFactory.class);
        scriptedBeanBuilder.addConstructorArgValue(ScriptFactoryPostProcessorTests.DELEGATING_SCRIPT);
        scriptedBeanBuilder.addPropertyReference("messenger", "messenger");
        GenericApplicationContext presentationCtx = new GenericApplicationContext(businessContext);
        presentationCtx.registerBeanDefinition("needsMessenger", scriptedBeanBuilder.getBeanDefinition());
        presentationCtx.registerBeanDefinition("scriptProcessor", ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(true));
        presentationCtx.refresh();
    }

    @Test
    public void testScriptHavingAReferenceToAnotherBean() throws Exception {
        // just tests that the (singleton) script-backed bean is able to be instantiated with references to its collaborators
        new ClassPathXmlApplicationContext("org/springframework/scripting/support/groovyReferences.xml");
    }

    @Test
    public void testForRefreshedScriptHavingErrorPickedUpOnFirstCall() throws Exception {
        BeanDefinition processorBeanDefinition = ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(true);
        BeanDefinition scriptedBeanDefinition = ScriptFactoryPostProcessorTests.createScriptedGroovyBean();
        BeanDefinitionBuilder collaboratorBuilder = BeanDefinitionBuilder.rootBeanDefinition(ScriptFactoryPostProcessorTests.DefaultMessengerService.class);
        collaboratorBuilder.addPropertyReference(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME);
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.PROCESSOR_BEAN_NAME, processorBeanDefinition);
        ctx.registerBeanDefinition(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME, scriptedBeanDefinition);
        final String collaboratorBeanName = "collaborator";
        ctx.registerBeanDefinition(collaboratorBeanName, collaboratorBuilder.getBeanDefinition());
        ctx.refresh();
        Messenger messenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        Assert.assertEquals(ScriptFactoryPostProcessorTests.MESSAGE_TEXT, messenger.getMessage());
        // cool; now let's change the script and check the refresh behaviour...
        ScriptFactoryPostProcessorTests.pauseToLetRefreshDelayKickIn(ScriptFactoryPostProcessorTests.DEFAULT_SECONDS_TO_PAUSE);
        StaticScriptSource source = ScriptFactoryPostProcessorTests.getScriptSource(ctx);
        // needs The Sundays compiler; must NOT throw any exception here...
        source.setScript("I keep hoping you are the same as me, and I'll send you letters and come to your house for tea");
        Messenger refreshedMessenger = ((Messenger) (ctx.getBean(ScriptFactoryPostProcessorTests.MESSENGER_BEAN_NAME)));
        try {
            refreshedMessenger.getMessage();
            Assert.fail("Must have thrown an Exception (invalid script)");
        } catch (FatalBeanException expected) {
            Assert.assertTrue(expected.contains(ScriptCompilationException.class));
        }
    }

    @Test
    public void testPrototypeScriptedBean() throws Exception {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBeanDefinition("messenger", BeanDefinitionBuilder.rootBeanDefinition(StubMessenger.class).getBeanDefinition());
        BeanDefinitionBuilder scriptedBeanBuilder = BeanDefinitionBuilder.rootBeanDefinition(GroovyScriptFactory.class);
        scriptedBeanBuilder.setScope(SCOPE_PROTOTYPE);
        scriptedBeanBuilder.addConstructorArgValue(ScriptFactoryPostProcessorTests.DELEGATING_SCRIPT);
        scriptedBeanBuilder.addPropertyReference("messenger", "messenger");
        final String BEAN_WITH_DEPENDENCY_NAME = "needsMessenger";
        ctx.registerBeanDefinition(BEAN_WITH_DEPENDENCY_NAME, scriptedBeanBuilder.getBeanDefinition());
        ctx.registerBeanDefinition("scriptProcessor", ScriptFactoryPostProcessorTests.createScriptFactoryPostProcessor(true));
        ctx.refresh();
        Messenger messenger1 = ((Messenger) (ctx.getBean(BEAN_WITH_DEPENDENCY_NAME)));
        Messenger messenger2 = ((Messenger) (ctx.getBean(BEAN_WITH_DEPENDENCY_NAME)));
        Assert.assertNotSame(messenger1, messenger2);
    }

    public static class DefaultMessengerService {
        private Messenger messenger;

        public void setMessenger(Messenger messenger) {
            this.messenger = messenger;
        }

        public String getMessage() {
            return this.messenger.getMessage();
        }
    }
}

