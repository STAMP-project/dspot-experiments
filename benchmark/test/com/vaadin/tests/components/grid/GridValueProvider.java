package com.vaadin.tests.components.grid;


import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class GridValueProvider {
    @Test
    public void getExplicitValueProvider() {
        Grid<Person> grid = new Grid();
        Column<Person, String> col = grid.addColumn(( person) -> ((person.getFirstName()) + " ") + (person.getLastName()));
        Person person = new Person("first", "last", "email", 123, Sex.UNKNOWN, null);
        Assert.assertEquals("first last", col.getValueProvider().apply(person));
    }

    @Test
    public void getBeanColumnValueProvider() {
        Grid<Person> grid = new Grid(Person.class);
        Column<Person, String> col = ((Column<Person, String>) (grid.getColumn("email")));
        Person person = new Person("first", "last", "eeemaaail", 123, Sex.UNKNOWN, null);
        Assert.assertEquals("eeemaaail", col.getValueProvider().apply(person));
    }

    @Test
    public void reuseValueProviderForFilter() {
        Grid<Person> grid = new Grid(Person.class);
        Column<Person, String> col = ((Column<Person, String>) (grid.getColumn("email")));
        Person lowerCasePerson = new Person("first", "last", "email", 123, Sex.UNKNOWN, null);
        Person upperCasePerson = new Person("FIRST", "LAST", "EMAIL", 123, Sex.UNKNOWN, null);
        ListDataProvider<Person> persons = DataProvider.ofItems(lowerCasePerson, upperCasePerson);
        persons.addFilter(col.getValueProvider(), ( value) -> value.toUpperCase(Locale.ROOT).equals(value));
        List<Person> queryPersons = persons.fetch(new com.vaadin.data.provider.Query()).collect(Collectors.toList());
        Assert.assertEquals(1, queryPersons.size());
        Assert.assertSame(upperCasePerson, queryPersons.get(0));
    }
}

