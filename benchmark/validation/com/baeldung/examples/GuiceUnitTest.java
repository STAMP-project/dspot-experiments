package com.baeldung.examples;


import com.baeldung.examples.common.BookService;
import com.baeldung.examples.guice.FooProcessor;
import com.baeldung.examples.guice.GuicePersonService;
import com.baeldung.examples.guice.GuiceUserService;
import com.baeldung.examples.guice.Person;
import com.baeldung.examples.guice.modules.GuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;


public class GuiceUnitTest {
    @Test
    public void givenAccountServiceInjectedInGuiceUserService_WhenGetAccountServiceInvoked_ThenReturnValueIsNotNull() {
        Injector injector = Guice.createInjector(new GuiceModule());
        GuiceUserService guiceUserService = injector.getInstance(GuiceUserService.class);
        Assert.assertNotNull(guiceUserService.getAccountService());
    }

    @Test
    public void givenBookServiceIsRegisteredInModule_WhenBookServiceIsInjected_ThenReturnValueIsNotNull() {
        Injector injector = Guice.createInjector(new GuiceModule());
        BookService bookService = injector.getInstance(BookService.class);
        Assert.assertNotNull(bookService);
    }

    @Test
    public void givenMultipleBindingsForPerson_WhenPersonIsInjected_ThenTestFailsByProvisionException() {
        Injector injector = Guice.createInjector(new GuiceModule());
        Person person = injector.getInstance(Person.class);
        Assert.assertNotNull(person);
    }

    @Test
    public void givenFooInjectedToFooProcessorAsOptionalDependency_WhenFooProcessorIsRetrievedFromContext_ThenCreationExceptionIsNotThrown() {
        Injector injector = Guice.createInjector(new GuiceModule());
        FooProcessor fooProcessor = injector.getInstance(FooProcessor.class);
        Assert.assertNotNull(fooProcessor);
    }

    @Test
    public void givenGuicePersonServiceConstructorAnnotatedByInject_WhenGuicePersonServiceIsInjected_ThenInstanceWillBeCreatedFromTheConstructor() {
        Injector injector = Guice.createInjector(new GuiceModule());
        GuicePersonService personService = injector.getInstance(GuicePersonService.class);
        Assert.assertNotNull(personService);
    }

    @Test
    public void givenPersonDaoInjectedToGuicePersonServiceBySetterInjection_WhenGuicePersonServiceIsInjected_ThenPersonDaoInitializedByTheSetter() {
        Injector injector = Guice.createInjector(new GuiceModule());
        GuicePersonService personService = injector.getInstance(GuicePersonService.class);
        Assert.assertNotNull(personService);
        Assert.assertNotNull(personService.getPersonDao());
    }
}

