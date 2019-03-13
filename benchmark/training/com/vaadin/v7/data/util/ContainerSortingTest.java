package com.vaadin.v7.data.util;


import com.vaadin.tests.util.TestUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class ContainerSortingTest {
    private static final String ITEM_DATA_MINUS2_NULL = "Data -2 null";

    private static final String ITEM_DATA_MINUS2 = "Data -2";

    private static final String ITEM_DATA_MINUS1 = "Data -1";

    private static final String ITEM_DATA_MINUS1_NULL = "Data -1 null";

    private static final String ITEM_ANOTHER_NULL = "Another null";

    private static final String ITEM_STRING_2 = "String 2";

    private static final String ITEM_STRING_NULL2 = "String null";

    private static final String ITEM_STRING_1 = "String 1";

    private static final String PROPERTY_INTEGER_NULL2 = "integer-null";

    private static final String PROPERTY_INTEGER_NOT_NULL = "integer-not-null";

    private static final String PROPERTY_STRING_NULL = "string-null";

    private static final String PROPERTY_STRING_ID = "string-not-null";

    @Test
    public void testEmptyFilteredIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();
        addProperties(ic);
        populate(ic);
        ic.addContainerFilter(ContainerSortingTest.PROPERTY_STRING_ID, "aasdfasdfasdf", true, false);
        ic.sort(new Object[]{ ContainerSortingTest.PROPERTY_STRING_ID }, new boolean[]{ true });
    }

    @Test
    public void testFilteredIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();
        addProperties(ic);
        populate(ic);
        ic.addContainerFilter(ContainerSortingTest.PROPERTY_STRING_ID, "a", true, false);
        ic.sort(new Object[]{ ContainerSortingTest.PROPERTY_STRING_ID }, new boolean[]{ true });
        verifyOrder(ic, new String[]{ ContainerSortingTest.ITEM_ANOTHER_NULL, ContainerSortingTest.ITEM_DATA_MINUS1, ContainerSortingTest.ITEM_DATA_MINUS1_NULL, ContainerSortingTest.ITEM_DATA_MINUS2, ContainerSortingTest.ITEM_DATA_MINUS2_NULL });
    }

    @Test
    public void testIndexedContainer() {
        IndexedContainer ic = new IndexedContainer();
        addProperties(ic);
        populate(ic);
        ic.sort(new Object[]{ ContainerSortingTest.PROPERTY_STRING_ID }, new boolean[]{ true });
        verifyOrder(ic, new String[]{ ContainerSortingTest.ITEM_ANOTHER_NULL, ContainerSortingTest.ITEM_DATA_MINUS1, ContainerSortingTest.ITEM_DATA_MINUS1_NULL, ContainerSortingTest.ITEM_DATA_MINUS2, ContainerSortingTest.ITEM_DATA_MINUS2_NULL, ContainerSortingTest.ITEM_STRING_1, ContainerSortingTest.ITEM_STRING_2, ContainerSortingTest.ITEM_STRING_NULL2 });
        ic.sort(new Object[]{ ContainerSortingTest.PROPERTY_INTEGER_NOT_NULL, ContainerSortingTest.PROPERTY_INTEGER_NULL2, ContainerSortingTest.PROPERTY_STRING_ID }, new boolean[]{ true, false, true });
        verifyOrder(ic, new String[]{ ContainerSortingTest.ITEM_DATA_MINUS2, ContainerSortingTest.ITEM_DATA_MINUS2_NULL, ContainerSortingTest.ITEM_DATA_MINUS1, ContainerSortingTest.ITEM_DATA_MINUS1_NULL, ContainerSortingTest.ITEM_ANOTHER_NULL, ContainerSortingTest.ITEM_STRING_NULL2, ContainerSortingTest.ITEM_STRING_1, ContainerSortingTest.ITEM_STRING_2 });
        ic.sort(new Object[]{ ContainerSortingTest.PROPERTY_INTEGER_NOT_NULL, ContainerSortingTest.PROPERTY_INTEGER_NULL2, ContainerSortingTest.PROPERTY_STRING_ID }, new boolean[]{ true, true, true });
        verifyOrder(ic, new String[]{ ContainerSortingTest.ITEM_DATA_MINUS2_NULL, ContainerSortingTest.ITEM_DATA_MINUS2, ContainerSortingTest.ITEM_DATA_MINUS1_NULL, ContainerSortingTest.ITEM_DATA_MINUS1, ContainerSortingTest.ITEM_ANOTHER_NULL, ContainerSortingTest.ITEM_STRING_NULL2, ContainerSortingTest.ITEM_STRING_1, ContainerSortingTest.ITEM_STRING_2 });
    }

    @Test
    public void testHierarchicalContainer() {
        HierarchicalContainer hc = new HierarchicalContainer();
        ContainerSortingTest.populateContainer(hc);
        hc.sort(new Object[]{ "name" }, new boolean[]{ true });
        verifyOrder(hc, new String[]{ "Audi", "C++", "Call of Duty", "Cars", "English", "Fallout", "Finnish", "Ford", "Games", "Java", "Might and Magic", "Natural languages", "PHP", "Programming languages", "Python", "Red Alert", "Swedish", "Toyota", "Volvo" });
        TestUtil.assertArrays(hc.rootItemIds().toArray(), new Integer[]{ ContainerSortingTest.nameToId.get("Cars"), ContainerSortingTest.nameToId.get("Games"), ContainerSortingTest.nameToId.get("Natural languages"), ContainerSortingTest.nameToId.get("Programming languages") });
        TestUtil.assertArrays(hc.getChildren(ContainerSortingTest.nameToId.get("Games")).toArray(), new Integer[]{ ContainerSortingTest.nameToId.get("Call of Duty"), ContainerSortingTest.nameToId.get("Fallout"), ContainerSortingTest.nameToId.get("Might and Magic"), ContainerSortingTest.nameToId.get("Red Alert") });
    }

    private static int index = 0;

    private static Map<String, Integer> nameToId = new HashMap<String, Integer>();

    private static Map<Integer, String> idToName = new HashMap<Integer, String>();

    public class MyObject implements Comparable<ContainerSortingTest.MyObject> {
        private String data;

        @Override
        public int compareTo(ContainerSortingTest.MyObject o) {
            if (o == null) {
                return 1;
            }
            if ((o.data) == null) {
                return (data) == null ? 0 : 1;
            } else
                if ((data) == null) {
                    return -1;
                } else {
                    return data.compareTo(o.data);
                }

        }
    }
}

