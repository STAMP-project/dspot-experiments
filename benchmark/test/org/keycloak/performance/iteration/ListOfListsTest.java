package org.keycloak.performance.iteration;


import java.util.List;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.performance.util.Loggable;


/**
 *
 *
 * @author tkyjovsk
 */
public class ListOfListsTest implements Loggable {
    int[] sizes;

    List<List<String>> lists;

    List<String> items;

    @Test
    public void testSize() {
        lists.forEach(( l) -> logger().debug(l));
        ListOfLists<String> lol = new ListOfLists(lists);
        Assert.assertEquals(items.size(), lol.size());
        for (int i = 0; i < (items.size()); i++) {
            Assert.assertEquals(items.get(i), lol.get(i));
        }
    }
}

