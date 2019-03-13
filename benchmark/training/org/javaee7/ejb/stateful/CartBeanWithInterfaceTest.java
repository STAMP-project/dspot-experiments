package org.javaee7.ejb.stateful;


import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.javaee7.ejb.stateful.remote.Cart;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author rafos
 */
@RunWith(Arquillian.class)
public class CartBeanWithInterfaceTest {
    @EJB
    private Cart sut;

    /**
     * Test of addItem method, of class CartBean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldAddOneItem() throws Exception {
        // given
        // when
        sut.addItem("apple");
        // then
        MatcherAssert.assertThat(sut.getItems(), CoreMatchers.hasItem("apple"));
    }

    /**
     * Test of addItem method, of class CartBean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldAddManyItems() throws Exception {
        // given
        final List<String> items = Arrays.asList("apple", "banana", "mango", "kiwi", "passion fruit");
        // when
        for (final String item : items) {
            sut.addItem(item);
        }
        // then
        MatcherAssert.assertThat(sut.getItems(), CoreMatchers.is(items));
    }

    /**
     * Test of removeItem method, of class CartBean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldRemoveOneItem() throws Exception {
        // given
        final List<String> items = Arrays.asList("apple", "banana", "mango", "kiwi", "passion fruit");
        for (final String item : items) {
            sut.addItem(item);
        }
        // when
        sut.removeItem("banana");
        // then
        MatcherAssert.assertThat(sut.getItems(), CoreMatchers.not(CoreMatchers.hasItem("banana")));
    }

    /**
     * Test of getItems method, of class CartBean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void shouldBeEmpty() throws Exception {
        // given
        // when
        final List<String> actual = sut.getItems();
        // then
        MatcherAssert.assertThat(actual.isEmpty(), CoreMatchers.is(true));
    }
}

