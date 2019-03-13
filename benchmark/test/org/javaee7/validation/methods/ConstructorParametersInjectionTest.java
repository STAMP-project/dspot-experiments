package org.javaee7.validation.methods;


import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class ConstructorParametersInjectionTest {
    @Inject
    MyBean2 bean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorViolationsWhenNullParameters() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("javax.validation.constraints.NotNull");
        thrown.expectMessage("MyBean2.arg0.value");
        bean.getValue();
    }
}

