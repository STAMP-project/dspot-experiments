package com.pushtorefresh.storio3.sqlite.queries;


import com.pushtorefresh.storio3.test.ToStringChecker;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DeleteQueryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotAllowNullTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Table name is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        DeleteQuery.builder().table(null);
    }

    @Test
    public void shouldNotAllowEmptyTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Table name is null or empty"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        DeleteQuery.builder().table("");
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder().table("test_table").build();
        assertThat(deleteQuery.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder().table("test_table").build();
        assertThat(deleteQuery.whereArgs()).isNotNull();
        assertThat(deleteQuery.whereArgs()).isEmpty();
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            // noinspection ConstantConditions
            DeleteQuery.builder().table("test_table").table(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("Table name is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyTable() {
        try {
            DeleteQuery.builder().table("test_table").table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Table name is null or empty").hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateTable() {
        DeleteQuery query = DeleteQuery.builder().table("old_table").table("new_table").build();
        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = new Object[]{ "arg1", "arg2", "arg3" };
        final String tag = "test_tag";
        final DeleteQuery firstQuery = DeleteQuery.builder().table(table).where(where).whereArgs(whereArgs).affectsTags(tag).build();
        final DeleteQuery secondQuery = firstQuery.toBuilder().build();
        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        try {
            // Without WHERE clause!
            DeleteQuery.builder().table("test_table").whereArgs("someArg").build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("You can not use whereArgs without where clause");
        }
    }

    @Test
    public void shouldAllowNullWhereArgsWithoutWhereClause() {
        // noinspection NullArgumentToVariableArgMethod
        DeleteQuery.builder().table("test_table").whereArgs(((Object[]) (null))).build();
        // We don't expect any exceptions here
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = new String[]{ "arg1", "arg2", "arg3" };
        final DeleteQuery deleteQuery = DeleteQuery.builder().table("test_table").where("test_where").whereArgs(whereArgs).build();
        assertThat(deleteQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
    }

    @Test
    public void affectsTagsCollectionShouldRewrite() {
        DeleteQuery deleteQuery = DeleteQuery.builder().table("table").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(new HashSet<String>(Collections.singletonList("second_call_collection"))).build();
        assertThat(deleteQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_collection"));
    }

    @Test
    public void affectsTagsVarargShouldRewrite() {
        DeleteQuery deleteQuery = DeleteQuery.builder().table("table").affectsTags("first_call_vararg").affectsTags("second_call_vararg").build();
        assertThat(deleteQuery.affectsTags()).isEqualTo(Collections.singleton("second_call_vararg"));
    }

    @Test
    public void affectsTagsCollectionAllowsNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder().table("table").affectsTags(new HashSet<String>(Collections.singletonList("first_call_collection"))).affectsTags(null).build();
        assertThat(deleteQuery.affectsTags()).isEmpty();
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = new Object[]{ "arg1", "arg2", "arg3" };
        final Set<String> affectsTags = Collections.singleton("tag1");
        final DeleteQuery deleteQuery = DeleteQuery.builder().table(table).where(where).whereArgs(whereArgs).affectsTags(affectsTags).build();
        assertThat(deleteQuery.table()).isEqualTo(table);
        assertThat(deleteQuery.where()).isEqualTo(where);
        assertThat(deleteQuery.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
        assertThat(deleteQuery.affectsTags()).isEqualTo(affectsTags);
    }

    @Test
    public void shouldNotAllowNullTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        DeleteQuery.builder().table("table").affectsTags(((String) (null))).build();
    }

    @Test
    public void shouldNotAllowEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectsTag must not be null or empty, affectsTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        DeleteQuery.builder().table("table").affectsTags("").build();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(DeleteQuery.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(DeleteQuery.class).check();
    }
}

