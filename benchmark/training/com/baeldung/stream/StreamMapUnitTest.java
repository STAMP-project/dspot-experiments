package com.baeldung.stream;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class StreamMapUnitTest {
    private Map<String, String> books;

    @Test
    public void whenOptionalVersionCalledForExistingTitle_thenReturnOptionalWithISBN() {
        Optional<String> optionalIsbn = books.entrySet().stream().filter(( e) -> "Effective Java".equals(e.getValue())).map(Map.Entry::getKey).findFirst();
        Assert.assertEquals("978-0134685991", optionalIsbn.get());
    }

    @Test
    public void whenOptionalVersionCalledForNonExistingTitle_thenReturnEmptyOptionalForISBN() {
        Optional<String> optionalIsbn = books.entrySet().stream().filter(( e) -> "Non Existent Title".equals(e.getValue())).map(Map.Entry::getKey).findFirst();
        Assert.assertEquals(false, optionalIsbn.isPresent());
    }

    @Test
    public void whenMultipleResultsVersionCalledForExistingTitle_aCollectionWithMultipleValuesIsReturned() {
        books.put("978-0321356680", "Effective Java: Second Edition");
        List<String> isbnCodes = books.entrySet().stream().filter(( e) -> e.getValue().startsWith("Effective Java")).map(Map.Entry::getKey).collect(Collectors.toList());
        Assert.assertTrue(isbnCodes.contains("978-0321356680"));
        Assert.assertTrue(isbnCodes.contains("978-0134685991"));
    }

    @Test
    public void whenMultipleResultsVersionCalledForNonExistingTitle_aCollectionWithNoValuesIsReturned() {
        List<String> isbnCodes = books.entrySet().stream().filter(( e) -> e.getValue().startsWith("Spring")).map(Map.Entry::getKey).collect(Collectors.toList());
        Assert.assertTrue(isbnCodes.isEmpty());
    }

    @Test
    public void whenKeysFollowingPatternReturnsAllValuesForThoseKeys() {
        List<String> titlesForKeyPattern = books.entrySet().stream().filter(( e) -> e.getKey().startsWith("978-0")).map(Map.Entry::getValue).collect(Collectors.toList());
        Assert.assertEquals(2, titlesForKeyPattern.size());
        Assert.assertTrue(titlesForKeyPattern.contains("Design patterns : elements of reusable object-oriented software"));
        Assert.assertTrue(titlesForKeyPattern.contains("Effective Java"));
    }
}

