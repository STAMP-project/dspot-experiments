package org.baeldung.customannotation;


import java.lang.reflect.Type;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CustomAnnotationConfiguration.class })
public class DataAccessFieldCallbackIntegrationTest {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Autowired
    private BeanWithGenericDAO beanWithGenericDAO;

    @Rule
    public ExpectedException ex = ExpectedException.none();

    @Test
    public void whenObjectCreated_thenObjectCreationIsSuccessful() {
        final DataAccessFieldCallback dataAccessFieldCallback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
        Assert.assertThat(dataAccessFieldCallback, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void whenMethodGenericTypeIsValidCalled_thenReturnCorrectValue() throws NoSuchFieldException, SecurityException {
        final DataAccessFieldCallback callback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
        final Type fieldType = BeanWithGenericDAO.class.getDeclaredField("personGenericDAO").getGenericType();
        final boolean result = callback.genericTypeIsValid(Person.class, fieldType);
        Assert.assertThat(result, CoreMatchers.is(true));
    }

    @Test
    public void whenMethodGetBeanInstanceCalled_thenReturnCorrectInstance() {
        final DataAccessFieldCallback callback = new DataAccessFieldCallback(configurableListableBeanFactory, beanWithGenericDAO);
        final Object result = callback.getBeanInstance("personGenericDAO", GenericDAO.class, Person.class);
        Assert.assertThat((result instanceof GenericDAO), CoreMatchers.is(true));
    }
}

