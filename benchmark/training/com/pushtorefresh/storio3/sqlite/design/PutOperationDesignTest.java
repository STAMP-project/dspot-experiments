package com.pushtorefresh.storio3.sqlite.design;


import BackpressureStrategy.MISSING;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;


public class PutOperationDesignTest extends OperationDesignTest {
    private static final PutResolver<ContentValues> CONTENT_VALUES_PUT_RESOLVER = new com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull
        ContentValues object) {
            return InsertQuery.builder().table("some_table").build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull
        ContentValues contentValues) {
            return // it's just a sample, no need to specify params
            UpdateQuery.builder().table("some_table").build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull
        ContentValues contentValues) {
            return contentValues;// easy

        }
    };

    @Test
    public void putObjectBlocking() {
        User user = newUser();
        PutResult putResult = storIOSQLite().put().object(user).withPutResolver(UserTableMeta.PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putObjectFlowable() {
        User user = newUser();
        Flowable<PutResult> flowablePutResult = storIOSQLite().put().object(user).withPutResolver(UserTableMeta.PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putContentValuesBlocking() {
        PutResult putResult = storIOSQLite().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putContentValuesFlowable() {
        Flowable<PutResult> putResult = storIOSQLite().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putContentValuesIterableBlocking() {
        Iterable<ContentValues> contentValuesIterable = Arrays.asList(Mockito.mock(ContentValues.class));
        PutResults<ContentValues> putResults = storIOSQLite().put().contentValues(contentValuesIterable).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableFlowable() {
        Iterable<ContentValues> contentValuesIterable = Arrays.asList(Mockito.mock(ContentValues.class));
        Flowable<PutResults<ContentValues>> putResults = storIOSQLite().put().contentValues(contentValuesIterable).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putContentValuesArrayBlocking() {
        ContentValues[] contentValuesArray = new ContentValues[]{ Mockito.mock(ContentValues.class) };
        PutResults<ContentValues> putResults = storIOSQLite().put().contentValues(contentValuesArray).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void putContentValuesArrayFlowable() {
        ContentValues[] contentValuesArray = new ContentValues[]{ Mockito.mock(ContentValues.class) };
        Flowable<PutResults<ContentValues>> putResults = storIOSQLite().put().contentValues(contentValuesArray).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxFlowable(MISSING);
    }

    @Test
    public void putObjectSingle() {
        User user = newUser();
        Single<PutResult> singlePutResult = storIOSQLite().put().object(user).withPutResolver(UserTableMeta.PUT_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void putContentValuesSingle() {
        Single<PutResult> putResult = storIOSQLite().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void putContentValuesIterableSingle() {
        Iterable<ContentValues> contentValuesIterable = Arrays.asList(Mockito.mock(ContentValues.class));
        Single<PutResults<ContentValues>> putResults = storIOSQLite().put().contentValues(contentValuesIterable).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void putObjectCompletable() {
        User user = newUser();
        Completable completablePut = storIOSQLite().put().object(user).withPutResolver(UserTableMeta.PUT_RESOLVER).prepare().asRxCompletable();
    }

    @Test
    public void putContentValuesCompletable() {
        Completable completablePut = storIOSQLite().put().contentValues(Mockito.mock(ContentValues.class)).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxCompletable();
    }

    @Test
    public void putContentValuesIterableCompletable() {
        Iterable<ContentValues> contentValuesIterable = Arrays.asList(Mockito.mock(ContentValues.class));
        Completable completablePut = storIOSQLite().put().contentValues(contentValuesIterable).withPutResolver(PutOperationDesignTest.CONTENT_VALUES_PUT_RESOLVER).prepare().asRxCompletable();
    }
}

