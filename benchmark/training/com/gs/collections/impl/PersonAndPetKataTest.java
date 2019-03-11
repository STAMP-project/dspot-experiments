/**
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl;


import Lists.mutable;
import Multimaps.mutable.bag;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.bag.primitive.ImmutableIntBag;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.bag.MutableBagMultimap;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.IntSet;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import com.gs.collections.impl.utility.StringIterate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class PersonAndPetKataTest {
    private MutableList<PersonAndPetKataTest.Person> people;

    @Test
    public void doAnyPeopleHaveCats() {
        boolean resultEager = this.people.anySatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertTrue(resultEager);
        boolean resultEagerMR = this.people.anySatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertTrue(resultEagerMR);
        boolean resultLazy = this.people.asLazy().anySatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertTrue(resultLazy);
        boolean resultLazyMR = this.people.asLazy().anySatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertTrue(resultLazyMR);
    }

    @Test
    public void doAnyPeopleHaveCatsUsingStreams() {
        boolean result = this.people.stream().anyMatch(( person) -> person.hasPet(PetType.CAT));
        Assert.assertTrue(result);
    }

    @Test
    public void doAllPeopleHaveCats() {
        boolean resultEager = this.people.allSatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultEager);
        boolean resultEagerMR = this.people.allSatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertFalse(resultEagerMR);
        boolean resultLazy = this.people.allSatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultLazy);
        boolean resultLazyMR = this.people.allSatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertFalse(resultLazyMR);
    }

    @Test
    public void doAllPeopleHaveCatsUsingStreams() {
        boolean resultStream = this.people.stream().allMatch(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultStream);
    }

    @Test
    public void doNoPeopleHaveCats() {
        boolean resultEager = this.people.noneSatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultEager);
        boolean resultEagerMR = this.people.noneSatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertFalse(resultEagerMR);
        boolean resultLazy = this.people.asLazy().noneSatisfy(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultLazy);
        boolean resultLazyMR = this.people.asLazy().noneSatisfyWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertFalse(resultLazyMR);
    }

    @Test
    public void doNoPeopleHaveCatsUsingStreams() {
        boolean resultStream = this.people.stream().noneMatch(( person) -> person.hasPet(PetType.CAT));
        Assert.assertFalse(resultStream);
    }

    @Test
    public void howManyPeopleHaveCats() {
        int countEager = this.people.count(( person) -> person.hasPet(PetType.CAT));
        Assert.assertEquals(2, countEager);
        int countEagerMR = this.people.countWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertEquals(2, countEagerMR);
        int countLazy = this.people.asLazy().count(( person) -> person.hasPet(PetType.CAT));
        Assert.assertEquals(2, countLazy);
        int countLazyMR = this.people.asLazy().countWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Assert.assertEquals(2, countLazyMR);
    }

    @Test
    public void howManyPeopleHaveCatsUsingStreams() {
        long countStream = this.people.stream().filter(( person) -> person.hasPet(PetType.CAT)).count();
        Assert.assertEquals(2, countStream);
    }

    @Test
    public void getPeopleWithCats() {
        MutableList<PersonAndPetKataTest.Person> peopleWithCatsEager = this.people.select(( person) -> person.hasPet(PetType.CAT));
        Verify.assertSize(2, peopleWithCatsEager);
        MutableList<PersonAndPetKataTest.Person> peopleWithCatsEagerMR = this.people.selectWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Verify.assertSize(2, peopleWithCatsEagerMR);
        MutableList<PersonAndPetKataTest.Person> peopleWithCatsLazy = this.people.asLazy().select(( person) -> person.hasPet(PetType.CAT)).toList();
        Verify.assertSize(2, peopleWithCatsLazy);
        MutableList<PersonAndPetKataTest.Person> peopleWithCatsLazyMR = this.people.asLazy().selectWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT).toList();
        Verify.assertSize(2, peopleWithCatsLazyMR);
    }

    @Test
    public void getPeopleWithCatsUsingStreams() {
        List<PersonAndPetKataTest.Person> peopleWithCatsStream = this.people.stream().filter(( person) -> person.hasPet(PetType.CAT)).collect(Collectors.toList());
        Verify.assertSize(2, peopleWithCatsStream);
    }

    @Test
    public void getPeopleWhoDontHaveCats() {
        MutableList<PersonAndPetKataTest.Person> peopleWithNoCatsEager = this.people.reject(( person) -> person.hasPet(PetType.CAT));
        Verify.assertSize(5, peopleWithNoCatsEager);
        MutableList<PersonAndPetKataTest.Person> peopleWithNoCatsEagerMR = this.people.rejectWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Verify.assertSize(5, peopleWithNoCatsEagerMR);
        MutableList<PersonAndPetKataTest.Person> peopleWithNoCatsLazy = this.people.reject(( person) -> person.hasPet(PetType.CAT));
        Verify.assertSize(5, peopleWithNoCatsLazy);
        MutableList<PersonAndPetKataTest.Person> peopleWithNoCatsLazyMR = this.people.rejectWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Verify.assertSize(5, peopleWithNoCatsLazyMR);
    }

    @Test
    public void getPeopleWhoDontHaveCatsUsingStreams() {
        List<PersonAndPetKataTest.Person> peopleWithNoCatsStream = this.people.stream().filter(( person) -> !(person.hasPet(PetType.CAT))).collect(Collectors.toList());
        Verify.assertSize(5, peopleWithNoCatsStream);
    }

    @Test
    public void partitionPeopleByCatOwnersAndNonCatOwners() {
        PartitionMutableList<PersonAndPetKataTest.Person> catsAndNoCatsEager = this.people.partition(( person) -> person.hasPet(PetType.CAT));
        Verify.assertSize(2, catsAndNoCatsEager.getSelected());
        Verify.assertSize(5, catsAndNoCatsEager.getRejected());
        PartitionMutableList<PersonAndPetKataTest.Person> catsAndNoCatsEagerMR = this.people.partitionWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Verify.assertSize(2, catsAndNoCatsEagerMR.getSelected());
        Verify.assertSize(5, catsAndNoCatsEagerMR.getRejected());
        PartitionIterable<PersonAndPetKataTest.Person> catsAndNoCatsLazy = this.people.asLazy().partition(( person) -> person.hasPet(PetType.CAT));
        Verify.assertSize(2, catsAndNoCatsLazy.getSelected());
        Verify.assertSize(5, catsAndNoCatsLazy.getRejected());
        PartitionIterable<PersonAndPetKataTest.Person> catsAndNoCatsLazyMR = this.people.asLazy().partitionWith(PersonAndPetKataTest.Person::hasPet, PersonAndPetKataTest.PetType.CAT);
        Verify.assertSize(2, catsAndNoCatsLazyMR.getSelected());
        Verify.assertSize(5, catsAndNoCatsLazyMR.getRejected());
    }

    @Test
    public void partitionPeopleByCatOwnersAndNonCatOwnersUsingStreams() {
        Map<Boolean, List<PersonAndPetKataTest.Person>> catsAndNoCatsStream = this.people.stream().collect(Collectors.partitioningBy(( person) -> person.hasPet(PetType.CAT)));
        Verify.assertSize(2, catsAndNoCatsStream.get(true));
        Verify.assertSize(5, catsAndNoCatsStream.get(false));
    }

    @Test
    public void findPersonNamedMarySmith() {
        PersonAndPetKataTest.Person resultEager = this.people.detect(( person) -> person.named("Mary Smith"));
        Assert.assertEquals("Mary", resultEager.getFirstName());
        Assert.assertEquals("Smith", resultEager.getLastName());
        PersonAndPetKataTest.Person resultEagerMR = this.people.detectWith(PersonAndPetKataTest.Person::named, "Mary Smith");
        Assert.assertEquals("Mary", resultEagerMR.getFirstName());
        Assert.assertEquals("Smith", resultEagerMR.getLastName());
        PersonAndPetKataTest.Person resultLazy = this.people.asLazy().detect(( person) -> person.named("Mary Smith"));
        Assert.assertEquals("Mary", resultLazy.getFirstName());
        Assert.assertEquals("Smith", resultLazy.getLastName());
        PersonAndPetKataTest.Person resultLazyMR = this.people.asLazy().detectWith(PersonAndPetKataTest.Person::named, "Mary Smith");
        Assert.assertEquals("Mary", resultLazyMR.getFirstName());
        Assert.assertEquals("Smith", resultLazyMR.getLastName());
    }

    @Test
    public void findPersonNamedMarySmithUsingStreams() {
        PersonAndPetKataTest.Person resultStream = this.people.stream().filter(( person) -> person.named("Mary Smith")).findFirst().get();
        Assert.assertEquals("Mary", resultStream.getFirstName());
        Assert.assertEquals("Smith", resultStream.getLastName());
    }

    @Test
    public void getTheNamesOfBobSmithPets() {
        PersonAndPetKataTest.Person personEager = this.people.detectWith(PersonAndPetKataTest.Person::named, "Bob Smith");
        MutableList<String> names = personEager.getPets().collect(PersonAndPetKataTest.Pet::getName);
        Assert.assertEquals(mutable.with("Dolly", "Spot"), names);
        Assert.assertEquals("Dolly & Spot", names.makeString(" & "));
    }

    @Test
    public void getTheNamesOfBobSmithPetsUsingStreams() {
        PersonAndPetKataTest.Person personStream = this.people.stream().filter(( each) -> each.named("Bob Smith")).findFirst().get();
        List<String> names = personStream.getPets().stream().map(PersonAndPetKataTest.Pet::getName).collect(Collectors.toList());
        Assert.assertEquals(mutable.with("Dolly", "Spot"), names);
        Assert.assertEquals("Dolly & Spot", names.stream().collect(Collectors.joining(" & ")));
    }

    @Test
    public void getAllPetTypes() {
        MutableSet<PersonAndPetKataTest.PetType> allPetTypesEager = this.people.flatCollect(PersonAndPetKataTest.Person::getPetTypes).toSet();
        Assert.assertEquals(UnifiedSet.newSetWith(PersonAndPetKataTest.PetType.values()), allPetTypesEager);
        MutableSet<PersonAndPetKataTest.PetType> allPetTypesEagerTarget = this.people.flatCollect(PersonAndPetKataTest.Person::getPetTypes, Sets.mutable.empty());
        Assert.assertEquals(UnifiedSet.newSetWith(PersonAndPetKataTest.PetType.values()), allPetTypesEagerTarget);
        MutableSet<PersonAndPetKataTest.PetType> allPetTypesLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPetTypes).toSet();
        Assert.assertEquals(UnifiedSet.newSetWith(PersonAndPetKataTest.PetType.values()), allPetTypesLazy);
        MutableSet<PersonAndPetKataTest.PetType> allPetTypesLazyTarget = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPetTypes, Sets.mutable.empty());
        Assert.assertEquals(UnifiedSet.newSetWith(PersonAndPetKataTest.PetType.values()), allPetTypesLazyTarget);
    }

    @Test
    public void getAllPetTypesUsingStreams() {
        Set<PersonAndPetKataTest.PetType> allPetTypesStream = this.people.stream().flatMap(( person) -> person.getPetTypes().stream()).collect(Collectors.toSet());
        Assert.assertEquals(new HashSet<>(Arrays.asList(PersonAndPetKataTest.PetType.values())), allPetTypesStream);
    }

    @Test
    public void groupPeopleByLastName() {
        MutableListMultimap<String, PersonAndPetKataTest.Person> byLastNameEager = this.people.groupBy(PersonAndPetKataTest.Person::getLastName);
        Verify.assertIterableSize(3, byLastNameEager.get("Smith"));
        MutableBagMultimap<String, PersonAndPetKataTest.Person> byLastNameEagerTargetBag = this.people.groupBy(PersonAndPetKataTest.Person::getLastName, bag.empty());
        Verify.assertIterableSize(3, byLastNameEagerTargetBag.get("Smith"));
        Multimap<String, PersonAndPetKataTest.Person> byLastNameLazy = this.people.asLazy().groupBy(PersonAndPetKataTest.Person::getLastName);
        Verify.assertIterableSize(3, byLastNameLazy.get("Smith"));
        MutableBagMultimap<String, PersonAndPetKataTest.Person> byLastNameLazyTargetBag = this.people.asLazy().groupBy(PersonAndPetKataTest.Person::getLastName, bag.empty());
        Verify.assertIterableSize(3, byLastNameLazyTargetBag.get("Smith"));
    }

    @Test
    public void groupPeopleByLastNameUsingStreams() {
        Map<String, List<PersonAndPetKataTest.Person>> byLastNameStream = this.people.stream().collect(Collectors.groupingBy(PersonAndPetKataTest.Person::getLastName));
        Verify.assertIterableSize(3, byLastNameStream.get("Smith"));
        Map<String, MutableBag<PersonAndPetKataTest.Person>> byLastNameStreamTargetBag = this.people.stream().collect(Collectors.groupingBy(PersonAndPetKataTest.Person::getLastName, Collectors.toCollection(Bags.mutable::empty)));
        Verify.assertIterableSize(3, byLastNameStreamTargetBag.get("Smith"));
    }

    @Test
    public void groupPeopleByTheirPets() {
        Multimap<PersonAndPetKataTest.PetType, PersonAndPetKataTest.Person> peopleByPetsEager = this.people.groupByEach(PersonAndPetKataTest.Person::getPetTypes);
        RichIterable<PersonAndPetKataTest.Person> catPeople = peopleByPetsEager.get(PersonAndPetKataTest.PetType.CAT);
        Assert.assertEquals("Mary, Bob", catPeople.collect(PersonAndPetKataTest.Person::getFirstName).makeString());
        RichIterable<PersonAndPetKataTest.Person> dogPeople = peopleByPetsEager.get(PersonAndPetKataTest.PetType.DOG);
        Assert.assertEquals("Bob, Ted", dogPeople.collect(PersonAndPetKataTest.Person::getFirstName).makeString());
    }

    @Test
    public void groupPeopleByTheirPetsUsingStreams() {
        Map<PersonAndPetKataTest.PetType, List<PersonAndPetKataTest.Person>> peopleByPetsStream = new HashMap<>();
        this.people.stream().forEach(( person) -> person.getPetTypes().stream().forEach(( petType) -> peopleByPetsStream.computeIfAbsent(petType, ( e) -> new ArrayList<>()).add(person)));
        List<PersonAndPetKataTest.Person> catPeople = peopleByPetsStream.get(PersonAndPetKataTest.PetType.CAT);
        Assert.assertEquals("Mary, Bob", catPeople.stream().map(PersonAndPetKataTest.Person::getFirstName).collect(Collectors.joining(", ")));
        List<PersonAndPetKataTest.Person> dogPeople = peopleByPetsStream.get(PersonAndPetKataTest.PetType.DOG);
        Assert.assertEquals("Bob, Ted", dogPeople.stream().map(PersonAndPetKataTest.Person::getFirstName).collect(Collectors.joining(", ")));
    }

    @Test
    public void getTotalNumberOfPets() {
        long numberOfPetsEager = this.people.sumOfInt(PersonAndPetKataTest.Person::getNumberOfPets);
        Assert.assertEquals(9, numberOfPetsEager);
        long numberOfPetsLazy = this.people.asLazy().sumOfInt(PersonAndPetKataTest.Person::getNumberOfPets);
        Assert.assertEquals(9, numberOfPetsLazy);
    }

    @Test
    public void getTotalNumberOfPetsUsingStreams() {
        int numberOfPetsStream = this.people.stream().mapToInt(PersonAndPetKataTest.Person::getNumberOfPets).sum();
        Assert.assertEquals(9, numberOfPetsStream);
    }

    @Test
    public void testStrings() {
        Assert.assertEquals("HELLO", "h1e2l3l4o".chars().filter(Character::isLetter).map(Character::toUpperCase).collect(StringBuilder::new, StringBuilder::appendCodePoint, null).toString());
        Assert.assertEquals("HELLO", "h1e2l3l4o".codePoints().filter(Character::isLetter).map(Character::toUpperCase).collect(StringBuilder::new, StringBuilder::appendCodePoint, null).toString());
        Assert.assertEquals("HELLO", StringIterate.asCharAdapter("h1e2l3l4o").select(Character::isLetter).collectChar(Character::toUpperCase).toString());
        Assert.assertEquals("HELLO", StringIterate.asCodePointAdapter("h1e2l3l4o").select(Character::isLetter).collectInt(Character::toUpperCase).toString());
    }

    @Test
    public void getAgeStatisticsOfPets() {
        IntList agesLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPets).collectInt(PersonAndPetKataTest.Pet::getAge).toList();
        IntSet uniqueAges = agesLazy.toSet();
        IntSummaryStatistics stats = new IntSummaryStatistics();
        agesLazy.forEach(stats::accept);
        Assert.assertEquals(IntHashSet.newSetWith(1, 2, 3, 4), uniqueAges);
        Assert.assertEquals(stats.getMin(), agesLazy.min());
        Assert.assertEquals(stats.getMax(), agesLazy.max());
        Assert.assertEquals(stats.getSum(), agesLazy.sum());
        Assert.assertEquals(stats.getAverage(), agesLazy.average(), 0.0);
        Assert.assertEquals(stats.getCount(), agesLazy.size());
        Assert.assertTrue(agesLazy.allSatisfy(IntPredicates.greaterThan(0)));
        Assert.assertTrue(agesLazy.allSatisfy(( i) -> i > 0));
        Assert.assertFalse(agesLazy.anySatisfy(( i) -> i == 0));
        Assert.assertTrue(agesLazy.noneSatisfy(( i) -> i < 0));
        Assert.assertEquals(2.0, agesLazy.median(), 0.0);
    }

    @Test
    public void getAgeStatisticsOfPetsUsingStreams() {
        List<Integer> agesStream = this.people.stream().flatMap(( person) -> person.getPets().stream()).map(PersonAndPetKataTest.Pet::getAge).collect(Collectors.toList());
        Set<Integer> uniqueAges = new HashSet<>(agesStream);
        IntSummaryStatistics stats = agesStream.stream().collect(Collectors.summarizingInt(( i) -> i));
        Assert.assertEquals(Sets.mutable.with(1, 2, 3, 4), uniqueAges);
        Assert.assertEquals(stats.getMin(), agesStream.stream().mapToInt(( i) -> i).min().getAsInt());
        Assert.assertEquals(stats.getMax(), agesStream.stream().mapToInt(( i) -> i).max().getAsInt());
        Assert.assertEquals(stats.getSum(), agesStream.stream().mapToInt(( i) -> i).sum());
        Assert.assertEquals(stats.getAverage(), agesStream.stream().mapToInt(( i) -> i).average().getAsDouble(), 0.0);
        Assert.assertEquals(stats.getCount(), agesStream.size());
        Assert.assertTrue(agesStream.stream().allMatch(( i) -> i > 0));
        Assert.assertFalse(agesStream.stream().anyMatch(( i) -> i == 0));
        Assert.assertTrue(agesStream.stream().noneMatch(( i) -> i < 0));
    }

    @Test
    public void getCountsByPetType() {
        ImmutableBag<PersonAndPetKataTest.PetType> countsLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPets).collect(PersonAndPetKataTest.Pet::getType).toBag().toImmutable();
        Assert.assertEquals(2, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.CAT));
        Assert.assertEquals(2, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.DOG));
        Assert.assertEquals(2, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.HAMSTER));
        Assert.assertEquals(1, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.SNAKE));
        Assert.assertEquals(1, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.TURTLE));
        Assert.assertEquals(1, countsLazy.occurrencesOf(PersonAndPetKataTest.PetType.BIRD));
    }

    @Test
    public void getCountsByPetTypeUsingStreams() {
        Map<PersonAndPetKataTest.PetType, Long> countsStream = Collections.unmodifiableMap(this.people.stream().flatMap(( person) -> person.getPets().stream()).collect(Collectors.groupingBy(PersonAndPetKataTest.Pet::getType, Collectors.counting())));
        Assert.assertEquals(Long.valueOf(2L), countsStream.get(PersonAndPetKataTest.PetType.CAT));
        Assert.assertEquals(Long.valueOf(2L), countsStream.get(PersonAndPetKataTest.PetType.DOG));
        Assert.assertEquals(Long.valueOf(2L), countsStream.get(PersonAndPetKataTest.PetType.HAMSTER));
        Assert.assertEquals(Long.valueOf(1L), countsStream.get(PersonAndPetKataTest.PetType.SNAKE));
        Assert.assertEquals(Long.valueOf(1L), countsStream.get(PersonAndPetKataTest.PetType.TURTLE));
        Assert.assertEquals(Long.valueOf(1L), countsStream.get(PersonAndPetKataTest.PetType.BIRD));
    }

    @Test
    public void getTop3Pets() {
        MutableList<ObjectIntPair<PersonAndPetKataTest.PetType>> favoritesLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPets).collect(PersonAndPetKataTest.Pet::getType).toBag().topOccurrences(3);
        Verify.assertSize(3, favoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.CAT, 2), favoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.DOG, 2), favoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.HAMSTER, 2), favoritesLazy);
    }

    @Test
    public void getTop3PetsUsingStreams() {
        List<Map.Entry<PersonAndPetKataTest.PetType, Long>> favoritesStream = this.people.stream().flatMap(( p) -> p.getPets().stream()).collect(Collectors.groupingBy(PersonAndPetKataTest.Pet::getType, Collectors.counting())).entrySet().stream().sorted(Comparator.comparingLong(( e) -> -(e.getValue()))).limit(3).collect(Collectors.toList());
        Verify.assertSize(3, favoritesStream);
        Verify.assertContains(new AbstractMap.SimpleEntry(PersonAndPetKataTest.PetType.CAT, Long.valueOf(2)), favoritesStream);
        Verify.assertContains(new AbstractMap.SimpleEntry(PersonAndPetKataTest.PetType.DOG, Long.valueOf(2)), favoritesStream);
        Verify.assertContains(new AbstractMap.SimpleEntry(PersonAndPetKataTest.PetType.HAMSTER, Long.valueOf(2)), favoritesStream);
    }

    @Test
    public void getBottom3Pets() {
        MutableList<ObjectIntPair<PersonAndPetKataTest.PetType>> leastFavoritesLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPets).collect(PersonAndPetKataTest.Pet::getType).toBag().bottomOccurrences(3);
        Verify.assertSize(3, leastFavoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.SNAKE, 1), leastFavoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.TURTLE, 1), leastFavoritesLazy);
        Verify.assertContains(PrimitiveTuples.pair(PersonAndPetKataTest.PetType.BIRD, 1), leastFavoritesLazy);
    }

    @Test
    public void getCountsByPetAge() {
        ImmutableIntBag countsLazy = this.people.asLazy().flatCollect(PersonAndPetKataTest.Person::getPets).collectInt(PersonAndPetKataTest.Pet::getAge).toBag().toImmutable();
        Assert.assertEquals(4, countsLazy.occurrencesOf(1));
        Assert.assertEquals(3, countsLazy.occurrencesOf(2));
        Assert.assertEquals(1, countsLazy.occurrencesOf(3));
        Assert.assertEquals(1, countsLazy.occurrencesOf(4));
        Assert.assertEquals(0, countsLazy.occurrencesOf(5));
    }

    @Test
    public void getCountsByPetAgeUsingStreams() {
        Map<Integer, Long> countsStream = Collections.unmodifiableMap(this.people.stream().flatMap(( person) -> person.getPets().stream()).collect(Collectors.groupingBy(PersonAndPetKataTest.Pet::getAge, Collectors.counting())));
        Assert.assertEquals(Long.valueOf(4), countsStream.get(1));
        Assert.assertEquals(Long.valueOf(3), countsStream.get(2));
        Assert.assertEquals(Long.valueOf(1), countsStream.get(3));
        Assert.assertEquals(Long.valueOf(1), countsStream.get(4));
        Assert.assertNull(countsStream.get(5));
    }

    public static final class Person {
        private final String firstName;

        private final String lastName;

        private final MutableList<PersonAndPetKataTest.Pet> pets = FastList.newList();

        private Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return this.firstName;
        }

        public String getLastName() {
            return this.lastName;
        }

        public boolean named(String name) {
            return name.equals((((this.firstName) + ' ') + (this.lastName)));
        }

        public boolean hasPet(PersonAndPetKataTest.PetType petType) {
            return this.pets.anySatisfyWith(Predicates2.attributeEqual(PersonAndPetKataTest.Pet::getType), petType);
        }

        public MutableList<PersonAndPetKataTest.Pet> getPets() {
            return this.pets;
        }

        public MutableBag<PersonAndPetKataTest.PetType> getPetTypes() {
            return this.pets.collect(PersonAndPetKataTest.Pet::getType, HashBag.newBag());
        }

        public PersonAndPetKataTest.Person addPet(PersonAndPetKataTest.PetType petType, String name, int age) {
            this.pets.add(new PersonAndPetKataTest.Pet(petType, name, age));
            return this;
        }

        public int getNumberOfPets() {
            return this.pets.size();
        }
    }

    public static class Pet {
        private final PersonAndPetKataTest.PetType type;

        private final String name;

        private final int age;

        public Pet(PersonAndPetKataTest.PetType type, String name, int age) {
            this.type = type;
            this.name = name;
            this.age = age;
        }

        public PersonAndPetKataTest.PetType getType() {
            return this.type;
        }

        public String getName() {
            return this.name;
        }

        public int getAge() {
            return this.age;
        }
    }

    public enum PetType {

        CAT,
        DOG,
        HAMSTER,
        TURTLE,
        BIRD,
        SNAKE;}
}

