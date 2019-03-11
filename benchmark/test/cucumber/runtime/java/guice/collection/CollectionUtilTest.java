package cucumber.runtime.java.guice.collection;


import java.util.List;
import org.junit.Test;


public class CollectionUtilTest {
    private List<String> list;

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionIsThrownWhenListIsNull() {
        CollectionUtil.removeAllExceptFirstElement(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentExceptionIsThrownWhenListIsEmpty() {
        CollectionUtil.removeAllExceptFirstElement(list);
    }

    @Test
    public void testListIsNotModifiedWhenItContainsOneItem() {
        list.add("foo");
        CollectionUtil.removeAllExceptFirstElement(list);
        assertThatListContainsOneElement("foo");
    }

    @Test
    public void testSecondItemIsRemovedWhenListContainsTwoItems() {
        list.add("foo");
        list.add("bar");
        CollectionUtil.removeAllExceptFirstElement(list);
        assertThatListContainsOneElement("foo");
    }

    @Test
    public void testSecondAndThirdItemsAreRemovedWhenListContainsThreeItems() {
        list.add("foo");
        list.add("bar");
        list.add("baz");
        CollectionUtil.removeAllExceptFirstElement(list);
        assertThatListContainsOneElement("foo");
    }
}

