package com.baeldung.stream;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StreamOperateAndRemoveUnitTest {
    private List<StreamOperateAndRemoveUnitTest.Item> itemList;

    @Test
    public void givenAListOf10Items_whenFilteredForQualifiedItems_thenFilteredListContains5Items() {
        final List<StreamOperateAndRemoveUnitTest.Item> filteredList = itemList.stream().filter(( item) -> item.isQualified()).collect(Collectors.toList());
        Assert.assertEquals(5, filteredList.size());
    }

    @Test
    public void givenAListOf10Items_whenOperateAndRemoveQualifiedItemsUsingRemoveIf_thenListContains5Items() {
        final Predicate<StreamOperateAndRemoveUnitTest.Item> isQualified = ( item) -> item.isQualified();
        itemList.stream().filter(isQualified).forEach(( item) -> item.operate());
        itemList.removeIf(isQualified);
        Assert.assertEquals(5, itemList.size());
    }

    @Test
    public void givenAListOf10Items_whenOperateAndRemoveQualifiedItemsUsingRemoveAll_thenListContains5Items() {
        final List<StreamOperateAndRemoveUnitTest.Item> operatedList = new ArrayList<>();
        itemList.stream().filter(( item) -> item.isQualified()).forEach(( item) -> {
            item.operate();
            operatedList.add(item);
        });
        itemList.removeAll(operatedList);
        Assert.assertEquals(5, itemList.size());
    }

    class Item {
        private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

        private final int value;

        public Item(final int value) {
            this.value = value;
        }

        public boolean isQualified() {
            return ((value) % 2) == 0;
        }

        public void operate() {
            logger.info(("Even Number: " + (this.value)));
        }
    }
}

