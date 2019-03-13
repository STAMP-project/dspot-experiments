package com.vaadin.tests.server.component.combobox;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.ComboBox;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for ComboBox data providers and filtering.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxFilteringTest {
    private static final String[] PERSON_NAMES = new String[]{ "Enrique Iglesias", "Henry Dunant", "Erwin Engelbrecht" };

    private ComboBox<Person> comboBox;

    @Test
    public void setItems_array_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonArray());
        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_array_setItemCaptionAfterItems() {
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonArray());
        // It shouldn't matter if this is done before or after setItems
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_collection_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonCollection());
        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_collection_setItemCaptionAfterItems() {
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setItems(getPersonCollection());
        // It shouldn't matter if this is done before or after setItems
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setItems_array_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setItems(String::startsWith, getPersonArray());
        checkFiltering("En", "en", 3, 1);
    }

    @Test
    public void setItems_collection_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setItems(String::startsWith, getPersonCollection());
        checkFiltering("En", "en", 3, 1);
    }

    @Test
    public void setListDataProvider_defaultFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "en" into the search field finds "Enrique Iglesias"
        // and "Henry Dunant", but not "Erwin Engelbrecht"
        comboBox.setDataProvider(DataProvider.ofCollection(getPersonCollection()));
        checkFiltering("en", "ennen", 3, 2);
    }

    @Test
    public void setListDataProvider_customFiltering() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Result: typing "En" into the search field finds "Enrique Iglesias"
        // but not "Henry Dunant" or "Erwin Engelbrecht"
        comboBox.setDataProvider(String::startsWith, DataProvider.ofCollection(getPersonCollection()));
        checkFiltering("En", "en", 3, 1);
    }

    @Test
    public void customDataProvider_filterByLastName() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Filters by last name, regardless of the item caption generator
        ListDataProvider<Person> ldp = DataProvider.ofItems(getPersonArray());
        comboBox.setDataProvider(ldp.withConvertedFilter(( text) -> ( person) -> person.getLastName().contains(text)));
        checkFiltering("u", "ab", 3, 1);
    }

    @Test
    public void customDataProvider_filterByLastNameWithAccessRestriction() {
        comboBox.setItemCaptionGenerator(Person::getFirstName);
        // Filters by last name, regardless of the item caption generator
        ListDataProvider<Person> ldp = DataProvider.ofItems(getPersonArray());
        ldp.setFilter(( person) -> person.getFirstName().contains("nr"));
        // Same as above, but only showing a subset of the persons
        comboBox.setDataProvider(ldp.withConvertedFilter(( text) -> ( person) -> person.getLastName().contains(text)));
        checkFiltering("t", "Engel", 2, 1);
    }

    @Test
    public void filterEmptyComboBox() {
        // Testing that filtering doesn't cause problems in the edge case where
        // neither setDataProvider nor setItems has been called
        checkFiltering("foo", "bar", 0, 0);
    }

    @Test
    public void setListDataProvider_notWrapped() {
        ListDataProvider<Person> provider = new ListDataProvider(Collections.emptyList());
        comboBox.setDataProvider(provider);
        Assert.assertSame(provider, comboBox.getDataProvider());
    }

    @Test
    public void setItems_hasListDataProvider() {
        comboBox.setItems();
        Assert.assertEquals(ListDataProvider.class, comboBox.getDataProvider().getClass());
    }
}

