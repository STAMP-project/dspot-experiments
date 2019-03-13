package org.baeldung.customannotation;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CustomAnnotationConfiguration.class })
public class DataAccessAnnotationIntegrationTest {
    @DataAccess(entity = Person.class)
    private GenericDAO<Person> personGenericDAO;

    @DataAccess(entity = Account.class)
    private GenericDAO<Account> accountGenericDAO;

    @DataAccess(entity = Person.class)
    private GenericDAO<Person> anotherPersonGenericDAO;

    @Test
    public void whenGenericDAOInitialized_thenNotNull() {
        Assert.assertThat(personGenericDAO, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(accountGenericDAO, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void whenGenericDAOInjected_thenItIsSingleton() {
        Assert.assertThat(personGenericDAO, CoreMatchers.not(CoreMatchers.sameInstance(accountGenericDAO)));
        Assert.assertThat(personGenericDAO, CoreMatchers.not(CoreMatchers.equalTo(accountGenericDAO)));
        Assert.assertThat(personGenericDAO, CoreMatchers.sameInstance(anotherPersonGenericDAO));
    }

    @Test
    public void whenFindAll_thenMessagesIsCorrect() {
        personGenericDAO.findAll();
        Assert.assertThat(personGenericDAO.getMessage(), CoreMatchers.is("Would create findAll query from Person"));
        accountGenericDAO.findAll();
        Assert.assertThat(accountGenericDAO.getMessage(), CoreMatchers.is("Would create findAll query from Account"));
    }

    @Test
    public void whenPersist_thenMakeSureThatMessagesIsCorrect() {
        personGenericDAO.persist(new Person());
        Assert.assertThat(personGenericDAO.getMessage(), CoreMatchers.is("Would create persist query from Person"));
        accountGenericDAO.persist(new Account());
        Assert.assertThat(accountGenericDAO.getMessage(), CoreMatchers.is("Would create persist query from Account"));
    }
}

