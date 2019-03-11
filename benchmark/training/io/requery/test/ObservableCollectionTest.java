package io.requery.test;


import io.requery.proxy.CollectionChanges;
import io.requery.test.model.Phone;
import io.requery.util.ObservableCollection;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by mluchi on 25/05/2017.
 */
@RunWith(Parameterized.class)
public class ObservableCollectionTest<T extends Collection<Phone> & ObservableCollection<Phone>> {
    private T observableCollection;

    private Phone phone1;

    private Phone phone2;

    private CollectionChanges collectionChanges;

    public ObservableCollectionTest(T observableCollection) {
        this.observableCollection = observableCollection;
    }

    /**
     * Tests for issue https://github.com/requery/requery/issues/569
     */
    @Test
    public void testClear() {
        // Add an element to the collection, then clear the collection
        Phone phone3 = new Phone();
        phone3.setPhoneNumber("3");
        observableCollection.add(phone3);
        observableCollection.clear();
        // Assert that the collection changes do not contain the phone3 item (add+remove=nothing) and contains the removals of phone1 and phone2
        Assert.assertTrue(collectionChanges.addedElements().isEmpty());
        Assert.assertTrue(((collectionChanges.removedElements().size()) == 2));
        Assert.assertTrue(collectionChanges.removedElements().contains(phone1));
        Assert.assertTrue(collectionChanges.removedElements().contains(phone2));
        TestCase.assertFalse(collectionChanges.removedElements().contains(phone3));
    }

    /**
     * Tests for issue https://github.com/requery/requery/issues/569
     */
    @Test
    public void testRemoveUsingIterator() {
        // Remove all items using iterator
        Iterator iterator = observableCollection.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        } 
        // Assert that collection changes contains the removed items
        Assert.assertTrue(collectionChanges.addedElements().isEmpty());
        Assert.assertTrue(((collectionChanges.removedElements().size()) == 2));
        Assert.assertTrue(collectionChanges.removedElements().contains(phone1));
        Assert.assertTrue(collectionChanges.removedElements().contains(phone2));
    }
}

