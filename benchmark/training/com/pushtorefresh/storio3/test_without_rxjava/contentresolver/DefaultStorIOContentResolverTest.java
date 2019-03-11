package com.pushtorefresh.storio3.test_without_rxjava.contentresolver;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import com.pushtorefresh.storio3.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultStorIOContentResolverTest {
    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().get().cursor().withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().get().listOfObjects(Object.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetObject() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().get().object(Object.class).withQuery(Query.builder().uri(Mockito.mock(Uri.class)).build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().put().object(new Object()).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutCollectionOfObjects() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().put().objects(Mockito.mock(Collection.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValues() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesIterable() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().put().contentValues(Mockito.mock(Iterable.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteByQuery() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().delete().byQuery(DeleteQuery.builder().uri(Mockito.mock(Uri.class)).build()).withDeleteResolver(Mockito.mock(DeleteResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteObject() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().delete().object(new Object()).withDeleteResolver(Mockito.mock(DeleteResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateDeleteCollectionOfObjects() {
        DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build().delete().objects(Mockito.mock(Collection.class)).withDeleteResolver(Mockito.mock(DeleteResolver.class)).prepare();
    }
}

