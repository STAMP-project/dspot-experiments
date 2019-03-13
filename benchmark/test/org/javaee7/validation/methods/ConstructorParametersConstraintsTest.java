package org.javaee7.validation.methods;


import java.lang.reflect.Constructor;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
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
public class ConstructorParametersConstraintsTest {
    @Inject
    Validator validator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorViolationsWhenNullParameters() throws NoSuchMethodException, SecurityException {
        final MyParameter parameter = new MyParameter();
        ExecutableValidator methodValidator = validator.forExecutables();
        Constructor<MyBean2> constructor = MyBean2.class.getConstructor(parameter.getClass());
        Set<ConstraintViolation<MyBean2>> constraints = methodValidator.validateConstructorParameters(constructor, new Object[]{ parameter });
        ConstraintViolation<MyBean2> violation = constraints.iterator().next();
        Assert.assertThat(constraints.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(violation.getMessageTemplate(), CoreMatchers.equalTo("{javax.validation.constraints.NotNull.message}"));
        Assert.assertThat(violation.getPropertyPath().toString(), CoreMatchers.equalTo("MyBean2.arg0.value"));
    }

    @Test
    public void constructorViolationsWhenNotNullParameters() throws NoSuchMethodException, SecurityException {
        final MyParameter parameter = new MyParameter();
        parameter.setValue("foo");
        ExecutableValidator methodValidator = validator.forExecutables();
        Constructor<MyBean2> constructor = MyBean2.class.getConstructor(parameter.getClass());
        Set<ConstraintViolation<MyBean2>> constraints = methodValidator.validateConstructorParameters(constructor, new Object[]{ parameter });
        Assert.assertThat(constraints.isEmpty(), CoreMatchers.equalTo(true));
    }
}

