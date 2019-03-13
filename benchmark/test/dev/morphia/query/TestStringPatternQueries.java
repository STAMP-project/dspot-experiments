package dev.morphia.query;


import dev.morphia.TestBase;
import dev.morphia.TestMapping;
import dev.morphia.annotations.Entity;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestStringPatternQueries extends TestBase {
    @Test
    public void testContains() throws Exception {
        getDs().save(Arrays.asList(new TestStringPatternQueries.E("xBA"), new TestStringPatternQueries.E("xa"), new TestStringPatternQueries.E("xAb"), new TestStringPatternQueries.E("xab"), new TestStringPatternQueries.E("xcB"), new TestStringPatternQueries.E("aba")));
        Assert.assertEquals(3, getDs().find(TestStringPatternQueries.E.class).field("name").contains("b").count());
        Assert.assertEquals(5, getDs().find(TestStringPatternQueries.E.class).field("name").containsIgnoreCase("b").count());
    }

    @Test
    public void testEndsWith() throws Exception {
        getDs().save(Arrays.asList(new TestStringPatternQueries.E("bxA"), new TestStringPatternQueries.E("xba"), new TestStringPatternQueries.E("xAb"), new TestStringPatternQueries.E("xab"), new TestStringPatternQueries.E("xcB"), new TestStringPatternQueries.E("aba")));
        Assert.assertEquals(2, getDs().find(TestStringPatternQueries.E.class).field("name").endsWith("b").count());
        Assert.assertEquals(3, getDs().find(TestStringPatternQueries.E.class).field("name").endsWithIgnoreCase("b").count());
    }

    @Test
    public void testStartsWith() throws Exception {
        getDs().save(Arrays.asList(new TestStringPatternQueries.E("A"), new TestStringPatternQueries.E("a"), new TestStringPatternQueries.E("Ab"), new TestStringPatternQueries.E("ab"), new TestStringPatternQueries.E("c")));
        Assert.assertEquals(2, getDs().find(TestStringPatternQueries.E.class).field("name").startsWith("a").count());
        Assert.assertEquals(4, getDs().find(TestStringPatternQueries.E.class).field("name").startsWithIgnoreCase("a").count());
        Assert.assertEquals(4, getDs().find(TestStringPatternQueries.E.class).field("name").startsWithIgnoreCase("A").count());
    }

    @Entity
    static class E extends TestMapping.BaseEntity {
        private final String name;

        E(final String name) {
            this.name = name;
        }

        protected E() {
            name = null;
        }
    }
}

