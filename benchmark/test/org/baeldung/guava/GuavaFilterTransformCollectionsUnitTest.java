package org.baeldung.guava;


import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GuavaFilterTransformCollectionsUnitTest {
    @Test
    public void whenFilterWithIterables_thenFiltered() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Iterable<String> result = Iterables.filter(names, Predicates.containsPattern("a"));
        Assert.assertThat(result, Matchers.containsInAnyOrder("Jane", "Adam"));
    }

    @Test
    public void whenFilterWithCollections2_thenFiltered() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<String> result = Collections2.filter(names, Predicates.containsPattern("a"));
        Assert.assertEquals(2, result.size());
        Assert.assertThat(result, Matchers.containsInAnyOrder("Jane", "Adam"));
        result.add("anna");
        Assert.assertEquals(5, names.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenFilteredCollection_whenAddingInvalidElement_thenException() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<String> result = Collections2.filter(names, Predicates.containsPattern("a"));
        result.add("elvis");
    }

    // 
    @Test
    public void whenFilterCollectionWithCustomPredicate_thenFiltered() {
        final Predicate<String> predicate = new Predicate<String>() {
            @Override
            public final boolean apply(final String input) {
                return (input.startsWith("A")) || (input.startsWith("J"));
            }
        };
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<String> result = Collections2.filter(names, predicate);
        Assert.assertEquals(3, result.size());
        Assert.assertThat(result, Matchers.containsInAnyOrder("John", "Jane", "Adam"));
    }

    // 
    @Test
    public void whenRemoveNullFromCollection_thenRemoved() {
        final List<String> names = Lists.newArrayList("John", null, "Jane", null, "Adam", "Tom");
        final Collection<String> result = Collections2.filter(names, Predicates.notNull());
        Assert.assertEquals(4, result.size());
        Assert.assertThat(result, Matchers.containsInAnyOrder("John", "Jane", "Adam", "Tom"));
    }

    // 
    @Test
    public void whenCheckingIfAllElementsMatchACondition_thenCorrect() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        boolean result = Iterables.all(names, Predicates.containsPattern("n|m"));
        Assert.assertTrue(result);
        result = Iterables.all(names, Predicates.containsPattern("a"));
        Assert.assertFalse(result);
    }

    // 
    @Test
    public void whenTransformingWithIterables_thenTransformed() {
        final Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public final Integer apply(final String input) {
                return input.length();
            }
        };
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Iterable<Integer> result = Iterables.transform(names, function);
        Assert.assertThat(result, Matchers.contains(4, 4, 4, 3));
    }

    // 
    @Test
    public void whenTransformWithCollections2_thenTransformed() {
        final Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public final Integer apply(final String input) {
                return input.length();
            }
        };
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<Integer> result = Collections2.transform(names, function);
        Assert.assertEquals(4, result.size());
        Assert.assertThat(result, Matchers.contains(4, 4, 4, 3));
        result.remove(3);
        Assert.assertEquals(3, names.size());
    }

    // 
    @Test
    public void whenCreatingAFunctionFromAPredicate_thenCorrect() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<Boolean> result = Collections2.transform(names, Functions.forPredicate(Predicates.containsPattern("m")));
        Assert.assertEquals(4, result.size());
        Assert.assertThat(result, Matchers.contains(false, false, true, true));
    }

    // 
    @Test
    public void whenTransformingUsingComposedFunction_thenTransformed() {
        final Function<String, Integer> f1 = new Function<String, Integer>() {
            @Override
            public final Integer apply(final String input) {
                return input.length();
            }
        };
        final Function<Integer, Boolean> f2 = new Function<Integer, Boolean>() {
            @Override
            public final Boolean apply(final Integer input) {
                return (input % 2) == 0;
            }
        };
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<Boolean> result = Collections2.transform(names, Functions.compose(f2, f1));
        Assert.assertEquals(4, result.size());
        Assert.assertThat(result, Matchers.contains(true, true, true, false));
    }

    // 
    @Test
    public void whenFilteringAndTransformingCollection_thenCorrect() {
        final Predicate<String> predicate = new Predicate<String>() {
            @Override
            public final boolean apply(final String input) {
                return (input.startsWith("A")) || (input.startsWith("T"));
            }
        };
        final Function<String, Integer> func = new Function<String, Integer>() {
            @Override
            public final Integer apply(final String input) {
                return input.length();
            }
        };
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<Integer> result = FluentIterable.from(names).filter(predicate).transform(func).toList();
        Assert.assertEquals(2, result.size());
        Assert.assertThat(result, Matchers.containsInAnyOrder(4, 3));
    }

    // 
    @Test
    public void whenFilterUsingMultiplePredicates_thenFiltered() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
        final Collection<String> result = Collections2.filter(names, Predicates.or(Predicates.containsPattern("J"), Predicates.not(Predicates.containsPattern("a"))));
        Assert.assertEquals(3, result.size());
        Assert.assertThat(result, Matchers.containsInAnyOrder("John", "Jane", "Tom"));
    }
}

