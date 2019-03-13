package com.vaadin.v7.data.util;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class BeanItemContainerSortTest {
    public class Person {
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        private int age;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public class Parent extends BeanItemContainerSortTest.Person {
        private Set<BeanItemContainerSortTest.Person> children = new HashSet<BeanItemContainerSortTest.Person>();

        public void setChildren(Set<BeanItemContainerSortTest.Person> children) {
            this.children = children;
        }

        public Set<BeanItemContainerSortTest.Person> getChildren() {
            return children;
        }
    }

    String[] names = new String[]{ "Antti", "Ville", "Sirkka", "Jaakko", "Pekka", "John" };

    int[] ages = new int[]{ 10, 20, 50, 12, 64, 67 };

    String[] sortedByAge = new String[]{ names[0], names[3], names[1], names[2], names[4], names[5] };

    @Test
    public void testSort() {
        testSort(true);
    }

    @Test
    public void testReverseSort() {
        testSort(false);
    }

    @Test
    public void primitiveSorting() {
        BeanItemContainer<BeanItemContainerSortTest.Person> container = getContainer();
        container.sort(new Object[]{ "age" }, new boolean[]{ true });
        int i = 0;
        for (String string : sortedByAge) {
            BeanItemContainerSortTest.Person idByIndex = container.getIdByIndex((i++));
            Assert.assertTrue(container.containsId(idByIndex));
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void customSorting() {
        BeanItemContainer<BeanItemContainerSortTest.Person> container = getContainer();
        // custom sorter using the reverse order
        container.setItemSorter(new DefaultItemSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                return -(super.compare(o1, o2));
            }
        });
        container.sort(new Object[]{ "age" }, new boolean[]{ true });
        int i = (container.size()) - 1;
        for (String string : sortedByAge) {
            BeanItemContainerSortTest.Person idByIndex = container.getIdByIndex((i--));
            Assert.assertTrue(container.containsId(idByIndex));
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void testGetSortableProperties() {
        BeanItemContainer<BeanItemContainerSortTest.Person> container = getContainer();
        Collection<?> sortablePropertyIds = container.getSortableContainerPropertyIds();
        Assert.assertEquals(2, sortablePropertyIds.size());
        Assert.assertTrue(sortablePropertyIds.contains("name"));
        Assert.assertTrue(sortablePropertyIds.contains("age"));
    }

    @Test
    public void testGetNonSortableProperties() {
        BeanItemContainer<BeanItemContainerSortTest.Parent> container = getParentContainer();
        Assert.assertEquals(3, container.getContainerPropertyIds().size());
        Collection<?> sortablePropertyIds = container.getSortableContainerPropertyIds();
        Assert.assertEquals(2, sortablePropertyIds.size());
        Assert.assertTrue(sortablePropertyIds.contains("name"));
        Assert.assertTrue(sortablePropertyIds.contains("age"));
    }
}

