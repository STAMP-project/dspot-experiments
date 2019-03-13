package com.vaadin.data.provider.bov;


import com.vaadin.data.provider.DataProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;


/**
 * Vaadin 8 Example from Book of Vaadin
 *
 * @author Vaadin Ltd
 */
public class DataProviderBoVTest {
    private DataProviderBoVTest.PersonServiceImpl personService;

    public static class PersonServiceImpl implements PersonService {
        final Person[] persons;

        public PersonServiceImpl(Person... persons) {
            this.persons = persons;
        }

        @Override
        public List<Person> fetchPersons(int offset, int limit) {
            return Arrays.stream(persons).skip(offset).limit(limit).collect(Collectors.toList());
        }

        @Override
        public List<Person> fetchPersons(int offset, int limit, Collection<PersonService.PersonSort> personSorts) {
            Stream<Person> personStream = Arrays.stream(persons).skip(offset).limit(limit);
            if (personSorts != null) {
                for (PersonService.PersonSort personSort : personSorts) {
                    personStream = personStream.sorted(personSort);
                }
            }
            return personStream.collect(Collectors.toList());
        }

        @Override
        public int getPersonCount() {
            return persons.length;
        }

        @Override
        public PersonService.PersonSort createSort(String propertyName, boolean descending) {
            PersonService.PersonSort result;
            switch (propertyName) {
                case "name" :
                    result = ( person1, person2) -> String.CASE_INSENSITIVE_ORDER.compare(person1.getName(), person2.getName());
                    break;
                case "born" :
                    result = ( person1, person2) -> (person2.getBorn()) - (person1.getBorn());
                    break;
                default :
                    throw new IllegalArgumentException(("wrong field name " + propertyName));
            }
            if (descending) {
                return ( person1, person2) -> result.compare(person2, person1);
            } else {
                return result;
            }
        }
    }

    @Test
    public void testPersons() {
        DataProvider<Person, ?> dataProvider = createUnsortedDataProvider();
        // TODO test if the provider contains all defined Persons in
        // correct(unchanged) order
    }

    @Test
    public void testSortedPersons() {
        DataProvider<Person, ?> dataProvider = createSortedDataProvider();
        // TODO test if provider contains all defined Persons in correct order
        // TODO test Query.sortOrders correctness
    }
}

