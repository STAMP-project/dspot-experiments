package org.javaee7.validation.methods;


import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
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
public class MethodParametersConstraintsTest {
    @Inject
    MyBean bean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void methodSizeTooLong() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("javax.validation.constraints.Size");
        thrown.expectMessage("org.javaee7.validation.methods.MyBean.sayHello");
        bean.sayHello("Duke");
    }

    @Test
    public void methodSizeOk() {
        bean.sayHello("Duk");
    }

    @Test
    public void showDateFromPast() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("javax.validation.constraints.Future");
        thrown.expectMessage("org.javaee7.validation.methods.MyBean.showDate");
        bean.showDate(false);
    }

    @Test
    public void showDateFromFuture() {
        bean.showDate(true);
    }

    @Test
    public void multipleParametersWithEmptyList() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("javax.validation.constraints.Size");
        thrown.expectMessage("showList.arg0");
        bean.showList(new ArrayList<String>(), "foo");
    }

    @Test
    public void multipleParametersNullSecondParameter() {
        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("javax.validation.constraints.NotNull");
        thrown.expectMessage("showList.arg1");
        List<String> list = new ArrayList<>();
        list.add("bar");
        bean.showList(list, null);
    }

    @Test
    public void multipleParametersWithCorrectValues() {
        List<String> list = new ArrayList<>();
        list.add("bar");
        list.add("woof");
        String string = bean.showList(list, "foo");
        Assert.assertThat(string, CoreMatchers.is(CoreMatchers.equalTo("foobar foowoof ")));
    }
}

