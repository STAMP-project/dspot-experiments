package com.pushtorefresh.storio3.sqlite.queries;


import com.pushtorefresh.storio3.test.ToStringChecker;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class InsertQueryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotAllowNullTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Table name is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        InsertQuery.builder().table(null);
    }

    @Test
    public void shouldNotAllowEmptyTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Table name is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        InsertQuery.builder().table("");
    }

    @Test
    public void nullColumnHackShouldBeNullByDefault() {
        InsertQuery insertQuery = InsertQuery.builder().table("test_table").build();
        assertThat(insertQuery.nullColumnHack()).isNull();
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            // noinspection ConstantConditions
            InsertQuery.builder().table("test_table").table(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Table name is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyTable() {
        try {
            InsertQuery.builder().table("test_table").table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Table name is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateTable() {
        InsertQuery query = InsertQuery.builder().table("old_table").table("new_table").build();
        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";
        final String tag = "test_tag";
        final InsertQuery firstQuery = InsertQuery.builder().table(table).nullColumnHack(nullColumnHack).affectsTags(tag).build();
        final InsertQuery secondQuery = firstQuery.toBuilder().build();
        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void affectsTagsCollectionShouldRewrite() {
        InsertQuery insertQuery = InsertQuery.builder().table("table").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(insertQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void affectsTagsVarargShouldRewrite() {
        InsertQuery insertQuery = InsertQuery.builder().table("table").affectsTags("first_call_vararg").affectsTags("second_call_vararg").build();
        assertThat(insertQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void affectsTagsCollectionAllowsNull() {
        InsertQuery insertQuery = InsertQuery.builder().table("table").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(null).build();
        assertThat(insertQuery.affectsTags()).isEmpty();
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";
        final Set<String> tags = Collections.singleton("tag");
        final InsertQuery insertQuery = InsertQuery.builder().table(table).nullColumnHack(nullColumnHack).affectsTags(tags).build();
        assertThat(insertQuery.table()).isEqualTo(table);
        assertThat(insertQuery.nullColumnHack()).isEqualTo(nullColumnHack);
        assertThat(insertQuery.affectsTags()).isEqualTo(tags);
    }

    @Test
    public void shouldNotAllowNullTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        InsertQuery.builder().table("table").affectsTags(((String) (null))).build();
    }

    @Test
    public void shouldNotAllowEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        InsertQuery.builder().table("table").affectsTags("").build();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(InsertQuery.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(InsertQuery.class).check();
    }
}

