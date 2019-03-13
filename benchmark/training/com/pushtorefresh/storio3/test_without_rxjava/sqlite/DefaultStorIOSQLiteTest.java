package com.pushtorefresh.storio3.test_without_rxjava.sqlite;


import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultStorIOSQLiteTest {
    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetCursor() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().get().cursor().withQuery(Query.builder().table("test_table").build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiateGetListOfObjects() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().get().listOfObjects(Object.class).withQuery(Query.builder().table("test_table").build()).withGetResolver(Mockito.mock(GetResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutObject() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().put().object(Mockito.mock(Object.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutCollectionOfObjects() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().put().objects(Mockito.mock(Collection.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValues() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesIterable() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().put().contentValues(Mockito.mock(Iterable.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void instantiatePutContentValuesVarArgs() {
        DefaultStorIOSQLite.builder().sqliteOpenHelper(Mockito.mock(SQLiteOpenHelper.class)).build().put().contentValues(Mockito.mock(ContentValues.class), Mockito.mock(ContentValues.class)).withPutResolver(Mockito.mock(PutResolver.class)).prepare();
    }
}

