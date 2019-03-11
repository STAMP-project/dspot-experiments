package org.mockserver.collections;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;


/**
 *
 *
 * @author jamesdbloom
 */
public class ContainIgnoreCaseTest {
    @Test
    public void shouldFindEntryInSet() {
        // then - at start
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "one"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "One"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "ONE"), Is.is(true));
        // then - in middle
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "two"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "Two"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "TWO"), Is.is(true));
        // then - at end
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "three"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "Three"), Is.is(true));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "THREE"), Is.is(true));
    }

    @Test
    public void shouldNotFindEntryInSet() {
        // then
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "four"), Is.is(false));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "Four"), Is.is(false));
        Assert.assertThat(ContainIgnoreCase.containsIgnoreCase(Sets.newSet("one", "two", "three"), "FOUR"), Is.is(false));
    }
}

