package com.pushtorefresh.storio3.sqlite.operations.put;


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


public class PutResultTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void createInsertResultWithSeveralAffectedTables() {
        final int insertedId = 10;
        final Set<String> affectedTables = new HashSet<String>(Arrays.asList("table1", "table2", "table3"));
        checkCreateInsertResult(insertedId, affectedTables, Collections.<String>emptySet(), PutResult.newInsertResult(insertedId, affectedTables));
    }

    @Test
    public void createInsertResultWithSeveralAffectedTags() {
        final int insertedId = 10;
        final Set<String> affectedTables = new HashSet<String>(Arrays.asList("table1", "table2", "table3"));
        final Set<String> affectedTags = new HashSet<String>(Arrays.asList("tag1", "tag2", "tag3"));
        checkCreateInsertResult(insertedId, affectedTables, affectedTags, PutResult.newInsertResult(insertedId, affectedTables, affectedTags));
    }

    @Test
    public void createInsertResultWithOneAffectedTables() {
        final int insertedId = 10;
        final String affectedTable = "table";
        checkCreateInsertResult(insertedId, Collections.singleton(affectedTable), Collections.<String>emptySet(), PutResult.newInsertResult(insertedId, affectedTable));
    }

    @Test
    public void createInsertResultWithOneAffectedTag() {
        final int insertedId = 10;
        final String affectedTable = "table";
        final String affectedTag = "tag";
        checkCreateInsertResult(insertedId, Collections.singleton(affectedTable), Collections.singleton(affectedTag), PutResult.newInsertResult(insertedId, affectedTable, affectedTag));
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("affectedTables must not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newInsertResult(1, ((Set<String>) (null)));
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTables() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("affectedTables must contain at least one element"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newInsertResult(1, new HashSet<String>());
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Please specify affected table"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newInsertResult(1, ((String) (null)));
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTable must not be null or empty, affectedTables = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newInsertResult(1, "");
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newInsertResult(1, "table", ((String) (null)));
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newInsertResult(1, "table", "");
    }

    @Test
    public void createUpdateResultWithSeveralAffectedTables() {
        final int numberOfRowsUpdated = 10;
        final Set<String> affectedTables = new HashSet<String>(Arrays.asList("table1", "table2", "table3"));
        checkCreateUpdateResult(numberOfRowsUpdated, affectedTables, Collections.<String>emptySet(), PutResult.newUpdateResult(numberOfRowsUpdated, affectedTables));
    }

    @Test
    public void createUpdateResultWithSeveralAffectedTags() {
        final int numberOfRowsUpdated = 10;
        final Set<String> affectedTables = new HashSet<String>(Arrays.asList("table1", "table2", "table3"));
        final Set<String> affectedTags = new HashSet<String>(Arrays.asList("tag1", "tag2", "tag3"));
        checkCreateUpdateResult(numberOfRowsUpdated, affectedTables, affectedTags, PutResult.newUpdateResult(numberOfRowsUpdated, affectedTables, affectedTags));
    }

    @Test
    public void createUpdateResultWithOneAffectedTable() {
        final int numberOfRowsUpdated = 10;
        final String affectedTable = "table";
        checkCreateUpdateResult(numberOfRowsUpdated, Collections.singleton(affectedTable), Collections.<String>emptySet(), PutResult.newUpdateResult(numberOfRowsUpdated, affectedTable));
    }

    @Test
    public void createUpdateResultWithOneAffectedTag() {
        final int numberOfRowsUpdated = 10;
        final String affectedTable = "table";
        final String affectedTag = "tag";
        checkCreateUpdateResult(numberOfRowsUpdated, Collections.singleton(affectedTable), Collections.singleton(affectedTag), PutResult.newUpdateResult(numberOfRowsUpdated, affectedTable, affectedTag));
    }

    @Test
    public void shouldAllowCreatingUpdateResultWith0RowsUpdated() {
        PutResult putResult = PutResult.newUpdateResult(0, "table");
        assertThat(putResult.wasUpdated()).isFalse();
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Number of rows updated must be >= 0, but was: -1"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newUpdateResult((-1), "table");
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("affectedTables must not be null"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newUpdateResult(1, ((Set<String>) (null)));
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTables() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("affectedTables must contain at least one element"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newUpdateResult(1, new HashSet<String>());
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.equalTo("Please specify affected table"));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newUpdateResult(1, ((String) (null)));
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTable must not be null or empty, affectedTables = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newUpdateResult(1, "");
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        PutResult.newUpdateResult(1, "tag", ((String) (null)));
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        PutResult.newUpdateResult(1, "table", "");
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier.forClass(PutResult.class).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker.forClass(PutResult.class).check();
    }
}

