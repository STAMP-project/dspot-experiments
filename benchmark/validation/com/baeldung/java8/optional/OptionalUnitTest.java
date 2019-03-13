package com.baeldung.java8.optional;


import com.baeldung.optional.Modem;
import com.baeldung.optional.Person;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OptionalUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(OptionalUnitTest.class);

    // creating Optional
    @Test
    public void whenCreatesEmptyOptional_thenCorrect() {
        Optional<String> empty = Optional.empty();
        Assert.assertFalse(empty.isPresent());
    }

    @Test
    public void givenNonNull_whenCreatesNonNullable_thenCorrect() {
        String name = "baeldung";
        Optional.of(name);
    }

    @Test(expected = NullPointerException.class)
    public void givenNull_whenThrowsErrorOnCreate_thenCorrect() {
        String name = null;
        Optional<String> opt = Optional.of(name);
    }

    @Test
    public void givenNonNull_whenCreatesOptional_thenCorrect() {
        String name = "baeldung";
        Optional<String> opt = Optional.of(name);
        Assert.assertEquals("Optional[baeldung]", opt.toString());
    }

    @Test
    public void givenNonNull_whenCreatesNullable_thenCorrect() {
        String name = "baeldung";
        Optional<String> opt = Optional.ofNullable(name);
        Assert.assertEquals("Optional[baeldung]", opt.toString());
    }

    @Test
    public void givenNull_whenCreatesNullable_thenCorrect() {
        String name = null;
        Optional<String> opt = Optional.ofNullable(name);
        Assert.assertEquals("Optional.empty", opt.toString());
    }

    // Checking Value With isPresent()
    @Test
    public void givenOptional_whenIsPresentWorks_thenCorrect() {
        Optional<String> opt = Optional.of("Baeldung");
        Assert.assertTrue(opt.isPresent());
        opt = Optional.ofNullable(null);
        Assert.assertFalse(opt.isPresent());
    }

    // Condition Action With ifPresent()
    @Test
    public void givenOptional_whenIfPresentWorks_thenCorrect() {
        Optional<String> opt = Optional.of("baeldung");
        opt.ifPresent(( name) -> OptionalUnitTest.LOG.debug("{}", name.length()));
    }

    // returning Value With get()
    @Test
    public void givenOptional_whenGetsValue_thenCorrect() {
        Optional<String> opt = Optional.of("baeldung");
        String name = opt.get();
        Assert.assertEquals("baeldung", name);
    }

    @Test(expected = NoSuchElementException.class)
    public void givenOptionalWithNull_whenGetThrowsException_thenCorrect() {
        Optional<String> opt = Optional.ofNullable(null);
        String name = opt.get();
    }

    // Conditional Return With filter()
    @Test
    public void whenOptionalFilterWorks_thenCorrect() {
        Integer year = 2016;
        Optional<Integer> yearOptional = Optional.of(year);
        boolean is2016 = yearOptional.filter(( y) -> y == 2016).isPresent();
        Assert.assertTrue(is2016);
        boolean is2017 = yearOptional.filter(( y) -> y == 2017).isPresent();
        Assert.assertFalse(is2017);
    }

    @Test
    public void whenFiltersWithoutOptional_thenCorrect() {
        Assert.assertTrue(priceIsInRange1(new Modem(10.0)));
        Assert.assertFalse(priceIsInRange1(new Modem(9.9)));
        Assert.assertFalse(priceIsInRange1(new Modem(null)));
        Assert.assertFalse(priceIsInRange1(new Modem(15.5)));
        Assert.assertFalse(priceIsInRange1(null));
    }

    @Test
    public void whenFiltersWithOptional_thenCorrect() {
        Assert.assertTrue(priceIsInRange2(new Modem(10.0)));
        Assert.assertFalse(priceIsInRange2(new Modem(9.9)));
        Assert.assertFalse(priceIsInRange2(new Modem(null)));
        Assert.assertFalse(priceIsInRange2(new Modem(15.5)));
        Assert.assertFalse(priceIsInRange1(null));
    }

    // Transforming Value With map()
    @Test
    public void givenOptional_whenMapWorks_thenCorrect() {
        List<String> companyNames = Arrays.asList("paypal", "oracle", "", "microsoft", "", "apple");
        Optional<List<String>> listOptional = Optional.of(companyNames);
        int size = listOptional.map(List::size).orElse(0);
        Assert.assertEquals(6, size);
    }

    @Test
    public void givenOptional_whenMapWorks_thenCorrect2() {
        String name = "baeldung";
        Optional<String> nameOptional = Optional.of(name);
        int len = nameOptional.map(String::length).orElse(0);
        Assert.assertEquals(8, len);
    }

    @Test
    public void givenOptional_whenMapWorksWithFilter_thenCorrect() {
        String password = " password ";
        Optional<String> passOpt = Optional.of(password);
        boolean correctPassword = passOpt.filter(( pass) -> pass.equals("password")).isPresent();
        Assert.assertFalse(correctPassword);
        correctPassword = passOpt.map(String::trim).filter(( pass) -> pass.equals("password")).isPresent();
        Assert.assertTrue(correctPassword);
    }

    // Transforming Value With flatMap()
    @Test
    public void givenOptional_whenFlatMapWorks_thenCorrect2() {
        Person person = new Person("john", 26);
        Optional<Person> personOptional = Optional.of(person);
        Optional<Optional<String>> nameOptionalWrapper = personOptional.map(Person::getName);
        Optional<String> nameOptional = nameOptionalWrapper.orElseThrow(IllegalArgumentException::new);
        String name1 = nameOptional.orElseThrow(IllegalArgumentException::new);
        Assert.assertEquals("john", name1);
        String name = personOptional.flatMap(Person::getName).orElseThrow(IllegalArgumentException::new);
        Assert.assertEquals("john", name);
    }

    @Test
    public void givenOptional_whenFlatMapWorksWithFilter_thenCorrect() {
        Person person = new Person("john", 26);
        person.setPassword("password");
        Optional<Person> personOptional = Optional.of(person);
        String password = personOptional.flatMap(Person::getPassword).filter(( cleanPass) -> cleanPass.equals("password")).orElseThrow(IllegalArgumentException::new);
        Assert.assertEquals("password", password);
    }

    // Default Value With orElse
    @Test
    public void whenOrElseWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElse("john");
        Assert.assertEquals("john", name);
    }

    // Default Value With orElseGet
    @Test
    public void whenOrElseGetWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElseGet(() -> "john");
        Assert.assertEquals("john", name);
    }

    @Test
    public void whenOrElseGetAndOrElseOverlap_thenCorrect() {
        String text = null;
        OptionalUnitTest.LOG.debug("Using orElseGet:");
        String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefault);
        Assert.assertEquals("Default Value", defaultText);
        OptionalUnitTest.LOG.debug("Using orElse:");
        defaultText = Optional.ofNullable(text).orElse(getMyDefault());
        Assert.assertEquals("Default Value", defaultText);
    }

    @Test
    public void whenOrElseGetAndOrElseDiffer_thenCorrect() {
        String text = "Text present";
        OptionalUnitTest.LOG.debug("Using orElseGet:");
        String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefault);
        Assert.assertEquals("Text present", defaultText);
        OptionalUnitTest.LOG.debug("Using orElse:");
        defaultText = Optional.ofNullable(text).orElse(getMyDefault());
        Assert.assertEquals("Text present", defaultText);
    }

    // Exceptions With orElseThrow
    @Test(expected = IllegalArgumentException.class)
    public void whenOrElseThrowWorks_thenCorrect() {
        String nullName = null;
        String name = Optional.ofNullable(nullName).orElseThrow(IllegalArgumentException::new);
    }
}

