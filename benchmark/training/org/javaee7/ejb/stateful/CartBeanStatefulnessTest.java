package org.javaee7.ejb.stateful;


import javax.ejb.EJB;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class CartBeanStatefulnessTest {
    private final String item_to_add = "apple";

    @EJB
    private CartBean bean1;

    @EJB
    private CartBean bean2;

    /**
     * JSR 318: Enterprise JavaBeans, Version 3.1
     * 3.4.7.1 Session Object Identity / Stateful Session Beans
     *
     * A stateful session object has a unique identity that is assigned by
     * the container at the time the object is created. A client of the stateful
     * session bean business interface can determine if two business interface
     * or no-interface view references refer to the same session object
     * by use of the equals method
     */
    @Test
    @InSequence(1)
    public void should_not_be_identical_beans() {
        MatcherAssert.assertThat("Expect different instances", bean1, CoreMatchers.is(CoreMatchers.not(bean2)));
    }

    @Test
    @InSequence(2)
    public void should_add_items_to_first_cart() {
        // when
        bean1.addItem(item_to_add);
        // then
        MatcherAssert.assertThat(bean1.getItems(), CoreMatchers.hasItem(item_to_add));
    }

    @Test
    @InSequence(3)
    public void should_not_contain_any_items_in_second_cart() {
        MatcherAssert.assertThat(bean2.getItems().isEmpty(), CoreMatchers.is(true));
    }
}

