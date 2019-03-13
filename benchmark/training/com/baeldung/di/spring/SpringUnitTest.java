package com.baeldung.di.spring;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SpringMainConfig.class })
public class SpringUnitTest {
    @Autowired
    ApplicationContext context;

    @Test
    public void givenAccountServiceAutowiredToUserService_WhenGetAccountServiceInvoked_ThenReturnValueIsNotNull() {
        UserService userService = context.getBean(UserService.class);
        Assert.assertNotNull(userService.getAccountService());
    }

    @Test
    public void givenBookServiceIsRegisteredAsBeanInContext_WhenBookServiceIsRetrievedFromContext_ThenReturnValueIsNotNull() {
        BookService bookService = context.getBean(BookService.class);
        Assert.assertNotNull(bookService);
    }

    @Test
    public void givenBookServiceIsRegisteredAsBeanInContextByOverridingAudioBookService_WhenAudioBookServiceIsRetrievedFromContext_ThenNoSuchBeanDefinitionExceptionIsThrown() {
        BookService bookService = context.getBean(BookService.class);
        Assert.assertNotNull(bookService);
        AudioBookService audioBookService = context.getBean(AudioBookService.class);
        Assert.assertNotNull(audioBookService);
    }

    @Test
    public void givenAuthorServiceAutowiredToBookServiceAsOptionalDependency_WhenBookServiceIsRetrievedFromContext_ThenNoSuchBeanDefinitionExceptionIsNotThrown() {
        BookService bookService = context.getBean(BookService.class);
        Assert.assertNotNull(bookService);
    }

    @Test
    public void givenSpringPersonServiceConstructorAnnotatedByAutowired_WhenSpringPersonServiceIsRetrievedFromContext_ThenInstanceWillBeCreatedFromTheConstructor() {
        SpringPersonService personService = context.getBean(SpringPersonService.class);
        Assert.assertNotNull(personService);
    }

    @Test
    public void givenPersonDaoAutowiredToSpringPersonServiceBySetterInjection_WhenSpringPersonServiceRetrievedFromContext_ThenPersonDaoInitializedByTheSetter() {
        SpringPersonService personService = context.getBean(SpringPersonService.class);
        Assert.assertNotNull(personService);
        Assert.assertNotNull(personService.getPersonDao());
    }
}

