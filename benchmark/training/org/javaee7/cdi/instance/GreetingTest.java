package org.javaee7.cdi.instance;


import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    /**
     * Container will assume built-in @Default qualifier here as well as for beans that don't declare a qualifier.
     */
    @Inject
    private Instance<Greeting> instance;

    /**
     * Only instance of SimpleGreeting class should be available.<br/>
     *
     * When dependent scoped bean is retrieved via an instance then explicit destroy action should be taken.
     * This is a known memory leak in CDI 1.0 fixed in CDI 1.1 see the link bellow for details.
     *
     * @see <a href="https://issues.jboss.org/browse/CDI-139">CDI-139</a>
     */
    @Test
    public void test() throws Exception {
        Assert.assertFalse(instance.isUnsatisfied());
        Assert.assertFalse(instance.isAmbiguous());
        // use Instance<T>#get()
        Greeting bean = instance.get();
        Assert.assertThat(bean, CoreMatchers.instanceOf(SimpleGreeting.class));
        instance.destroy(bean);
        // use Instance<T>#select()
        Instance<Greeting> anotherInstance = instance.select(new javax.enterprise.util.AnnotationLiteral<Default>() {});
        Greeting anotherBean = anotherInstance.get();
        Assert.assertThat(anotherBean, CoreMatchers.instanceOf(SimpleGreeting.class));
        anotherInstance.destroy(anotherBean);
    }
}

