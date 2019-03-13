package com.vaadin.ui;


import org.junit.Test;


public class AbstractMultiSelectUsingIdTest {
    public TwinColSelect<AbstractMultiSelectUsingIdTest.ItemWithId> selectToTest;

    public static class ItemWithId {
        private int id;

        public ItemWithId() {
        }

        public ItemWithId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Test
    public void selectTwiceSelectsOnce() {
        selectToTest.select(new AbstractMultiSelectUsingIdTest.ItemWithId(1));
        assertSelectionOrder(1);
        selectToTest.select(new AbstractMultiSelectUsingIdTest.ItemWithId(1));
        assertSelectionOrder(1);
    }

    @Test
    public void deselectWorks() {
        selectToTest.select(new AbstractMultiSelectUsingIdTest.ItemWithId(1));
        selectToTest.deselect(new AbstractMultiSelectUsingIdTest.ItemWithId(1));
        assertSelectionOrder();
    }
}

