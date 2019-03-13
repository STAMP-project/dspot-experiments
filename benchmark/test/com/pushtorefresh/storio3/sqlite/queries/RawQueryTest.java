package com.pushtorefresh.storio3.sqlite.queries;


import com.google.common.collect.HashMultiset;
import com.pushtorefresh.storio3.test.ToStringChecker;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RawQueryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotAllowNullQuery() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Query is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        RawQuery.builder().query(null);
    }

    @Test
    public void shouldNotAllowEmptyQuery() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Query is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        RawQuery.builder().query("");
    }

    @Test
    public void argsShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder().query("lalala I know SQL").build();
        assertThat(rawQuery.args()).isNotNull();
        assertThat(rawQuery.args()).isEmpty();
    }

    @Test
    public void observesTablesShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder().query("lalala I know SQL").build();
        assertThat(rawQuery.observesTables()).isNotNull();
        assertThat(rawQuery.observesTables()).isEmpty();
    }

    @Test
    public void observesTagsShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder().query("lalala I know SQL").build();
        assertThat(rawQuery.observesTags()).isNotNull();
        assertThat(rawQuery.observesTags()).isEmpty();
    }

    @Test
    public void affectsTablesShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder().query("lalala I know SQL").build();
        assertThat(rawQuery.affectsTables()).isNotNull();
        assertThat(rawQuery.affectsTables()).isEmpty();
    }

    @Test
    public void affectsTagsShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder().query("lalala I know SQL").build();
        assertThat(rawQuery.affectsTags()).isNotNull();
        assertThat(rawQuery.affectsTags()).isEmpty();
    }

    @Test
    public void affectsTablesShouldRewriteCollectionWithVarargOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTables(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTables("second_call_vararg").build();
        assertThat(rawQuery.affectsTables()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void affectsTablesShouldRewriteVarargWithCollectionOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTables("first_call_vararg").affectsTables(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.affectsTables()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void affectsTablesShouldRewriteOnSecondCallVararg() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTables("first_call_vararg").affectsTables("second_call_vararg").build();
        assertThat(rawQuery.affectsTables()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void affectsTablesShouldRewriteOnSecondCallCollection() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTables(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTables(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.affectsTables()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void affectsTagsCollectionShouldRewrite() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void affectsTagsVarargShouldRewrite() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTags("first_call_vararg").affectsTags("second_call_vararg").build();
        assertThat(rawQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void affectsTagsCollectionAllowsNull() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(null).build();
        assertThat(rawQuery.affectsTags()).isEmpty();
    }

    @Test
    public void observesTablesShouldRewriteCollectionWithVarargOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTables(new HashSet<String>(Collections.singletonList("first_call_collection"))).observesTables("second_call_vararg").build();
        assertThat(rawQuery.observesTables()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void observesTablesShouldRewriteVarargWithCollectionOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTables("first_call_vararg").observesTables(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.observesTables()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void observesTablesShouldRewriteOnSecondCallVararg() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTables("first_call_vararg").observesTables("second_call_vararg").build();
        assertThat(rawQuery.observesTables()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void observesTablesShouldRewriteOnSecondCallCollection() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTables(new HashSet<String>(Collections.singletonList("first_call_collection"))).observesTables(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.observesTables()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void observesTagsCollectionShouldRewrite() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).observesTags(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(rawQuery.observesTags()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void observesTagsVarargShouldRewrite() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTags("first_call_vararg").observesTags("second_call_vararg").build();
        assertThat(rawQuery.observesTags()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void observesTagsCollectionAllowsNull() {
        RawQuery rawQuery = RawQuery.builder().query("test_query").observesTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).observesTags(null).build();
        assertThat(rawQuery.affectsTags()).isEmpty();
    }

    @Test
    public void completeBuilderShouldNotAllowNullQuery() {
        try {
            // noinspection ConstantConditions
            RawQuery.builder().query("test_query").query(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Query is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyQuery() {
        try {
            RawQuery.builder().query("test_query").query("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Query is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateTable() {
        RawQuery rawQuery = RawQuery.builder().query("old_query").query("new_query").build();
        assertThat(rawQuery.query()).isEqualTo("new_query");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String query = "test_query";
        final Object[] args = new Object[]{ "arg1", "arg2", "arg3" };
        final String[] observesTables = new String[]{ "table_to_observe_1", "table_to_observe_2" };
        final List<String> observesTags = Arrays.asList("tag_to_observe_1", "tag_to_observe_2");
        final String[] affectsTables = new String[]{ "table_to_affect_1", "table_to_affect_2" };
        final List<String> affectsTags = Arrays.asList("tag_to_affect_1", "tag_to_affect_2");
        final RawQuery firstQuery = RawQuery.builder().query(query).args(args).observesTables(observesTables).observesTags(observesTags).affectsTables(affectsTables).affectsTags(affectsTags).build();
        final RawQuery secondQuery = firstQuery.toBuilder().build();
        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] args = new String[]{ "arg1", "arg2", "arg3" };
        final RawQuery rawQuery = RawQuery.builder().query("test_query").args(args).build();
        assertThat(rawQuery.args()).isEqualTo(Arrays.asList(args));
    }

    @Test
    public void buildWithNormalValues() {
        final String query = "test_query";
        final Object[] args = new Object[]{ "arg1", "arg2", "arg3" };
        final String[] observesTables = new String[]{ "table_to_observe_1", "table_to_observe_2" };
        final List<String> observesTags = Arrays.asList("tag_to_observe_1", "tag_to_observe_2");
        final String[] affectsTables = new String[]{ "table_to_affect_1", "table_to_affect_2" };
        final List<String> affectsTags = Arrays.asList("tag_to_affect_1", "tag_to_affect_2");
        final RawQuery rawQuery = RawQuery.builder().query(query).args(args).observesTables(observesTables).observesTags(observesTags).affectsTables(affectsTables).affectsTags(affectsTags).build();
        assertThat(rawQuery.query()).isEqualTo(query);
        assertThat(rawQuery.args()).isEqualTo(Arrays.asList(args));
        assertThat(HashMultiset.create(rawQuery.observesTables())).isEqualTo(HashMultiset.create(Arrays.asList(observesTables)));
        assertThat(HashMultiset.create(rawQuery.observesTags())).isEqualTo(HashMultiset.create(observesTags));
        assertThat(HashMultiset.create(rawQuery.affectsTables())).isEqualTo(HashMultiset.create(Arrays.asList(affectsTables)));
        assertThat(HashMultiset.create(rawQuery.affectsTags())).isEqualTo(HashMultiset.create(affectsTags));
    }

    @Test
    public void shouldNotAllowNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        RawQuery.builder().query("some query").affectsTags(((String) (null))).build();
    }

    @Test
    public void shouldNotAllowEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        RawQuery.builder().query("some query").affectsTags("").build();
    }

    @Test
    public void shouldNotAllowNullObservedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("observesTag must not be null or empty, observesTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        RawQuery.builder().query("some query").observesTags(((String) (null))).build();
    }

    @Test
    public void shouldNotAllowEmptyObservedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("observesTag must not be null or empty, observesTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        RawQuery.builder().query("some query").observesTags("").build();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(RawQuery.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(RawQuery.class).check();
    }
}

