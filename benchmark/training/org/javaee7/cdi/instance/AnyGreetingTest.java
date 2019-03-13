package org.javaee7.cdi.instance;


import javax.enterprise.inject.Any;
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
public class AnyGreetingTest {
    /**
     * Built-in qualifier @Any is assumed on each bean regardless other qualifiers specified.
     */
    @Inject
    @Any
    private Instance<Greeting> instance;

    /**
     * Both bean instances of Greeting interface should be available.<br/>
     *
     * When dependent scoped bean is retrieved via an instance then explicit destroy action should be taken.
     * This is a known memory leak in CDI 1.0 fixed in CDI 1.1 see the link bellow for details.
     *
     * @see <a href="https://issues.jboss.org/browse/CDI-139">CDI-139</a>
     */
    @Test
    public void test() throws Exception {
        Assert.assertFalse(instance.isUnsatisfied());
        Assert.assertTrue(instance.isAmbiguous());
        // use Instance<T>#select()
        Instance<Greeting> businessInstance = instance.select(new javax.enterprise.util.AnnotationLiteral<Business>() {});
        Greeting businessBean = businessInstance.get();
        Assert.assertThat(businessBean, CoreMatchers.instanceOf(FormalGreeting.class));
        businessInstance.destroy(businessBean);
        Instance<Greeting> defaultInstance = instance.select(new javax.enterprise.util.AnnotationLiteral<Default>() {});
        Greeting defaultBean = defaultInstance.get();
        Assert.assertThat(defaultBean, CoreMatchers.instanceOf(SimpleGreeting.class));
        defaultInstance.destroy(defaultBean);
    }
}

