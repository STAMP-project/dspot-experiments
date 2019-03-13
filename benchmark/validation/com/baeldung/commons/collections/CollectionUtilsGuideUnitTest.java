package com.baeldung.commons.collections;


import com.baeldung.commons.collectionutil.Address;
import com.baeldung.commons.collectionutil.Customer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;


public class CollectionUtilsGuideUnitTest {
    Customer customer1 = new Customer(1, "Daniel", 123456L, "locality1", "city1", "1234");

    Customer customer4 = new Customer(4, "Bob", 456789L, "locality4", "city4", "4567");

    List<Customer> list1;

    List<Customer> list2;

    List<Customer> list3;

    List<Customer> linkedList1;

    @Test
    public void givenList_whenAddIgnoreNull_thenNoNullAdded() {
        CollectionUtils.addIgnoreNull(list1, null);
        Assert.assertFalse(list1.contains(null));
    }

    @Test
    public void givenTwoSortedLists_whenCollated_thenSorted() {
        List<Customer> sortedList = CollectionUtils.collate(list1, list2);
        Assert.assertEquals(6, sortedList.size());
        Assert.assertTrue(sortedList.get(0).getName().equals("Bob"));
        Assert.assertTrue(sortedList.get(2).getName().equals("Daniel"));
    }

    @Test
    public void givenListOfCustomers_whenTransformed_thenListOfAddress() {
        Collection<Address> addressCol = CollectionUtils.collect(list1, new org.apache.commons.collections4.Transformer<Customer, Address>() {
            public Address transform(Customer customer) {
                return new Address(customer.getLocality(), customer.getCity(), customer.getZip());
            }
        });
        List<Address> addressList = new ArrayList(addressCol);
        Assert.assertTrue(((addressList.size()) == 3));
        Assert.assertTrue(addressList.get(0).getLocality().equals("locality1"));
    }

    @Test
    public void givenCustomerList_whenFiltered_thenCorrectSize() {
        boolean isModified = CollectionUtils.filter(linkedList1, new org.apache.commons.collections4.Predicate<Customer>() {
            public boolean evaluate(Customer customer) {
                return Arrays.asList("Daniel", "Kyle").contains(customer.getName());
            }
        });
        // filterInverse does the opposite. It removes the element from the list if the Predicate returns true
        // select and selectRejected work the same way except that they do not remove elements from the given collection and return a new collection
        Assert.assertTrue((isModified && ((linkedList1.size()) == 2)));
    }

    @Test
    public void givenNonEmptyList_whenCheckedIsNotEmpty_thenTrue() {
        List<Customer> emptyList = new ArrayList<>();
        List<Customer> nullList = null;
        // Very handy at times where we want to check if a collection is not null and not empty too.
        // isNotEmpty does the opposite. Handy because using ! operator on isEmpty makes it missable while reading
        Assert.assertTrue(CollectionUtils.isNotEmpty(list1));
        Assert.assertTrue(CollectionUtils.isEmpty(nullList));
        Assert.assertTrue(CollectionUtils.isEmpty(emptyList));
    }

    @Test
    public void givenCustomerListAndASubcollection_whenChecked_thenTrue() {
        Assert.assertTrue(CollectionUtils.isSubCollection(list3, list1));
    }

    @Test
    public void givenTwoLists_whenIntersected_thenCheckSize() {
        Collection<Customer> intersection = CollectionUtils.intersection(list1, list3);
        Assert.assertTrue(((intersection.size()) == 2));
    }

    @Test
    public void givenTwoLists_whenSubtracted_thenCheckElementNotPresentInA() {
        Collection<Customer> result = CollectionUtils.subtract(list1, list3);
        Assert.assertFalse(result.contains(customer1));
    }

    @Test
    public void givenTwoLists_whenUnioned_thenCheckElementPresentInResult() {
        Collection<Customer> union = CollectionUtils.union(list1, list2);
        Assert.assertTrue(union.contains(customer1));
        Assert.assertTrue(union.contains(customer4));
    }
}

