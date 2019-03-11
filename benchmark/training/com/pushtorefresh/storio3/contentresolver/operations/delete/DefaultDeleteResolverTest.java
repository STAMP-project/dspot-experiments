package com.pushtorefresh.storio3.contentresolver.operations.delete;


import StorIOContentResolver.LowLevel;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultDeleteResolverTest {
    @Test
    public void performDelete() {
        final StorIOContentResolver storIOContentResolver = Mockito.mock(StorIOContentResolver.class);
        final StorIOContentResolver.LowLevel lowLevel = Mockito.mock(LowLevel.class);
        Mockito.when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);
        final int expectedNumberOfRowsDeleted = 1;
        Mockito.when(lowLevel.delete(ArgumentMatchers.any(DeleteQuery.class))).thenReturn(expectedNumberOfRowsDeleted);
        final Uri expectedUri = Mockito.mock(Uri.class);
        final DeleteQuery expectedDeleteQuery = DeleteQuery.builder().uri(expectedUri).where("test where clause").whereArgs("test").build();
        final TestItem testItem = TestItem.newInstance();
        final DefaultDeleteResolver<TestItem> defaultDeleteResolver = new DefaultDeleteResolver<TestItem>() {
            @NonNull
            @Override
            protected DeleteQuery mapToDeleteQuery(@NonNull
            TestItem object) {
                assertThat(object).isSameAs(testItem);
                return expectedDeleteQuery;
            }
        };
        // Performing Delete Operation
        final DeleteResult deleteResult = defaultDeleteResolver.performDelete(storIOContentResolver, testItem);
        // checks that required delete was performed
        Mockito.verify(lowLevel, Mockito.times(1)).delete(expectedDeleteQuery);
        // only one delete should be performed
        Mockito.verify(lowLevel, Mockito.times(1)).delete(ArgumentMatchers.any(DeleteQuery.class));
        // delete result checks
        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(expectedNumberOfRowsDeleted);
        assertThat(deleteResult.affectedUris()).hasSize(1);
        assertThat(deleteResult.affectedUris()).contains(expectedUri);
    }
}

