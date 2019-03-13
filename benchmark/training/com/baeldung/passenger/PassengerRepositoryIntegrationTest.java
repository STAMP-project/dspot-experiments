package com.baeldung.passenger;


import ExampleMatcher.GenericPropertyMatchers;
import Sort.Direction.ASC;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;


@DataJpaTest
@RunWith(SpringRunner.class)
public class PassengerRepositoryIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PassengerRepository repository;

    @Test
    public void givenSeveralPassengersWhenOrderedBySeatNumberLimitedToThenThePassengerInTheFirstFilledSeatIsReturned() {
        Passenger expected = Passenger.from("Fred", "Bloggs", 22);
        List<Passenger> passengers = repository.findOrderedBySeatNumberLimitedTo(1);
        Assert.assertEquals(1, passengers.size());
        Passenger actual = passengers.get(0);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenSeveralPassengersWhenFindFirstByOrderBySeatNumberAscThenThePassengerInTheFirstFilledSeatIsReturned() {
        Passenger expected = Passenger.from("Fred", "Bloggs", 22);
        Passenger actual = repository.findFirstByOrderBySeatNumberAsc();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenSeveralPassengersWhenFindPageSortedByThenThePassengerInTheFirstFilledSeatIsReturned() {
        Passenger expected = Passenger.from("Fred", "Bloggs", 22);
        Page<Passenger> page = repository.findAll(PageRequest.of(0, 1, Sort.by(ASC, "seatNumber")));
        Assert.assertEquals(1, page.getContent().size());
        Passenger actual = page.getContent().get(0);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void givenPassengers_whenOrderedBySeatNumberAsc_thenCorrectOrder() {
        Passenger fred = Passenger.from("Fred", "Bloggs", 22);
        Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
        Passenger jill = Passenger.from("Jill", "Smith", 50);
        Passenger siya = Passenger.from("Siya", "Kolisi", 85);
        Passenger eve = Passenger.from("Eve", "Jackson", 95);
        List<Passenger> passengers = repository.findByOrderBySeatNumberAsc();
        MatcherAssert.assertThat(passengers, Matchers.contains(fred, ricki, jill, siya, eve));
    }

    @Test
    public void givenPassengers_whenFindAllWithSortBySeatNumberAsc_thenCorrectOrder() {
        Passenger fred = Passenger.from("Fred", "Bloggs", 22);
        Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
        Passenger jill = Passenger.from("Jill", "Smith", 50);
        Passenger siya = Passenger.from("Siya", "Kolisi", 85);
        Passenger eve = Passenger.from("Eve", "Jackson", 95);
        List<Passenger> passengers = repository.findAll(Sort.by(ASC, "seatNumber"));
        MatcherAssert.assertThat(passengers, Matchers.contains(fred, ricki, jill, siya, eve));
    }

    @Test
    public void givenPassengers_whenFindByExampleDefaultMatcher_thenExpectedReturned() {
        Example<Passenger> example = Example.of(Passenger.from("Fred", "Bloggs", null));
        Optional<Passenger> actual = repository.findOne(example);
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(Passenger.from("Fred", "Bloggs", 22), actual.get());
    }

    @Test
    public void givenPassengers_whenFindByExampleCaseInsensitiveMatcher_thenExpectedReturned() {
        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
        Example<Passenger> example = Example.of(Passenger.from("fred", "bloggs", null), caseInsensitiveExampleMatcher);
        Optional<Passenger> actual = repository.findOne(example);
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(Passenger.from("Fred", "Bloggs", 22), actual.get());
    }

    @Test
    public void givenPassengers_whenFindByExampleCustomMatcher_thenExpectedReturned() {
        Passenger jill = Passenger.from("Jill", "Smith", 50);
        Passenger eve = Passenger.from("Eve", "Jackson", 95);
        Passenger fred = Passenger.from("Fred", "Bloggs", 22);
        Passenger siya = Passenger.from("Siya", "Kolisi", 85);
        Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny().withMatcher("firstName", GenericPropertyMatchers.contains().ignoreCase()).withMatcher("lastName", GenericPropertyMatchers.contains().ignoreCase());
        Example<Passenger> example = Example.of(Passenger.from("e", "s", null), customExampleMatcher);
        List<Passenger> passengers = repository.findAll(example);
        MatcherAssert.assertThat(passengers, Matchers.contains(jill, eve, fred, siya));
        MatcherAssert.assertThat(passengers, IsNot.not(Matchers.contains(ricki)));
    }

    @Test
    public void givenPassengers_whenFindByIgnoringMatcher_thenExpectedReturned() {
        Passenger jill = Passenger.from("Jill", "Smith", 50);
        Passenger eve = Passenger.from("Eve", "Jackson", 95);
        Passenger fred = Passenger.from("Fred", "Bloggs", 22);
        Passenger siya = Passenger.from("Siya", "Kolisi", 85);
        Passenger ricki = Passenger.from("Ricki", "Bobbie", 36);
        ExampleMatcher ignoringExampleMatcher = ExampleMatcher.matchingAny().withMatcher("lastName", GenericPropertyMatchers.startsWith().ignoreCase()).withIgnorePaths("firstName", "seatNumber");
        Example<Passenger> example = Example.of(Passenger.from(null, "b", null), ignoringExampleMatcher);
        List<Passenger> passengers = repository.findAll(example);
        MatcherAssert.assertThat(passengers, Matchers.contains(fred, ricki));
        MatcherAssert.assertThat(passengers, IsNot.not(Matchers.contains(jill)));
        MatcherAssert.assertThat(passengers, IsNot.not(Matchers.contains(eve)));
        MatcherAssert.assertThat(passengers, IsNot.not(Matchers.contains(siya)));
    }
}

