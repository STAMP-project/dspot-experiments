package com.vaadin.data;


import com.vaadin.ui.Grid;
import org.junit.Test;


public class NestedPropertyNameTest {
    @Test
    public void nestedProperty_sameNameCanBeAdded() {
        Grid<NestedPropertyNameTest.Person> grid = new Grid(NestedPropertyNameTest.Person.class);
        grid.addColumn("street.name");
    }

    private class Person {
        String name;

        NestedPropertyNameTest.Street street;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public NestedPropertyNameTest.Street getStreet() {
            return street;
        }

        public void setStreet(NestedPropertyNameTest.Street street) {
            this.street = street;
        }
    }

    private class Street {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

