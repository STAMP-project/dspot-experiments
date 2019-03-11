package io.requery.test;


import io.requery.proxy.CollectionChanges;
import io.requery.test.model.Person;
import io.requery.test.model.Phone;
import io.requery.util.ObservableList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by mluchi on 23/05/2017.
 */
/**
 * Tests for issue https://github.com/requery/requery/issues/568
 */
public class CollectionChangesTest {
    private Person person;

    private Phone phone1;

    private Phone phone2;

    private ObservableList observableList;

    private CollectionChanges collectionChanges;

    @Test
    public void testAddingElementThatWasPreviouslyRemoved() {
        Phone phone = new Phone();
        observableList.remove(phone1);
        Assert.assertTrue(collectionChanges.removedElements().contains(phone1));
        observableList.add(phone1);
        Assert.assertTrue(collectionChanges.addedElements().isEmpty());
        Assert.assertTrue(collectionChanges.removedElements().isEmpty());
    }

    @Test
    public void testRemovingElementThatWasPreviouslyAdded() {
        Phone phone = new Phone();
        observableList.add(phone);
        Assert.assertTrue(collectionChanges.addedElements().contains(phone));
        observableList.remove(phone);
        Assert.assertTrue(collectionChanges.addedElements().isEmpty());
        Assert.assertTrue(collectionChanges.removedElements().isEmpty());
    }
}

