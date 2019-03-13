package com.pushtorefresh.storio3.sqlite.operations.delete;


import StorIOSQLite.LowLevel;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import java.util.Collections;
import java.util.Set;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultDeleteResolverTest {
    @Test
    public void performDelete() {
        final StorIOSQLite storIOSQLite = Mockito.mock(StorIOSQLite.class);
        final StorIOSQLite.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        final String testTable = "test_table";
        final Set<String> testTags = Collections.singleton("test_tag");
        final DeleteQuery deleteQuery = DeleteQuery.builder().table(testTable).affectsTags(testTags).build();
        Mockito.when(storIOSQLite.lowLevel()).thenReturn(lowLevel);
        Mockito.when(lowLevel.delete(deleteQuery)).thenReturn(1);
        final DefaultDeleteResolverTest.TestItem testItem = new DefaultDeleteResolverTest.TestItem();
        final DefaultDeleteResolver<DefaultDeleteResolverTest.TestItem> defaultDeleteResolver = new DefaultDeleteResolver<DefaultDeleteResolverTest.TestItem>() {
            @NonNull
            @Override
            public DeleteQuery mapToDeleteQuery(@NonNull
            DefaultDeleteResolverTest.TestItem testItem) {
                return deleteQuery;
            }
        };
        final DeleteResult deleteResult = defaultDeleteResolver.performDelete(storIOSQLite, testItem);
        Mockito.verify(lowLevel).delete(ArgumentMatchers.any(DeleteQuery.class));
        Mockito.verify(lowLevel).delete(deleteQuery);
        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);
        assertThat(deleteResult.affectedTables()).isEqualTo(Collections.singleton(testTable));
        assertThat(deleteResult.affectedTags()).isEqualTo(testTags);
    }

    private static class TestItem {}
}

