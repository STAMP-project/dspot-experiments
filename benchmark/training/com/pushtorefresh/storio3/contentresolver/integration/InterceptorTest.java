package com.pushtorefresh.storio3.contentresolver.integration;


import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InterceptorTest extends IntegrationTest {
    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private AtomicInteger callCount;

    @Test
    public void deleteByQuery() {
        storIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TestItem.CONTENT_URI).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteCollectionOfObjects() {
        storIOContentResolver.delete().objects(Collections.singleton(createTestItem())).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteObject() {
        storIOContentResolver.delete().object(createTestItem()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithQuery() {
        storIOContentResolver.get().cursor().withQuery(Query.builder().uri(TestItem.CONTENT_URI).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithQuery() {
        storIOContentResolver.get().listOfObjects(TestItem.class).withQuery(Query.builder().uri(TestItem.CONTENT_URI).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithQuery() {
        storIOContentResolver.get().numberOfResults().withQuery(Query.builder().uri(TestItem.CONTENT_URI).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithQuery() {
        storIOContentResolver.get().object(TestItem.class).withQuery(Query.builder().uri(TestItem.CONTENT_URI).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putCollection() {
        storIOContentResolver.put().objects(Collections.singleton(createTestItem())).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValues() {
        storIOContentResolver.put().contentValues(createContentValues()).withPutResolver(createCVPutResolver()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValuesIterable() {
        storIOContentResolver.put().contentValues(Arrays.asList(createContentValues(), createContentValues())).withPutResolver(createCVPutResolver()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putObject() {
        storIOContentResolver.put().object(createTestItem()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }
}

