package com.pushtorefresh.storio3.sqlite.integration;


import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InterceptorTest {
    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private StorIOSQLite storIOSQLite;

    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private Interceptor interceptor1;

    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private Interceptor interceptor2;

    // @Before
    @SuppressWarnings("NullableProblems")
    @NonNull
    private AtomicInteger callCount;

    @Test
    public void deleteByQuery() {
        storIOSQLite.delete().byQuery(DeleteQuery.builder().table(TweetTableMeta.TABLE).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteCollectionOfObjects() {
        storIOSQLite.delete().objects(Collections.singleton(createTweet())).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void deleteObject() {
        storIOSQLite.delete().object(createTweet()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void execSql() {
        storIOSQLite.executeSQL().withQuery(RawQuery.builder().query(("select * from " + (TweetTableMeta.TABLE))).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithRawQuery() {
        storIOSQLite.get().cursor().withQuery(RawQuery.builder().query(("select * from " + (TweetTableMeta.TABLE))).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getCursorWithQuery() {
        storIOSQLite.get().cursor().withQuery(Query.builder().table(TweetTableMeta.TABLE).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithRawQuery() {
        storIOSQLite.get().listOfObjects(Tweet.class).withQuery(RawQuery.builder().query(("select * from " + (TweetTableMeta.TABLE))).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getListOfObjectsWithQuery() {
        storIOSQLite.get().listOfObjects(Tweet.class).withQuery(Query.builder().table(TweetTableMeta.TABLE).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithRawQuery() {
        storIOSQLite.get().numberOfResults().withQuery(RawQuery.builder().query(("select * from " + (TweetTableMeta.TABLE))).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getNumberOfResultsWithQuery() {
        storIOSQLite.get().numberOfResults().withQuery(Query.builder().table(TweetTableMeta.TABLE).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithRawQuery() {
        storIOSQLite.get().object(Tweet.class).withQuery(RawQuery.builder().query(("select * from " + (TweetTableMeta.TABLE))).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void getObjectWithQuery() {
        storIOSQLite.get().object(Tweet.class).withQuery(Query.builder().table(TweetTableMeta.TABLE).build()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putCollection() {
        storIOSQLite.put().objects(Collections.singleton(createTweet())).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValues() {
        storIOSQLite.put().contentValues(createContentValues()).withPutResolver(createCVPutResolver()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putContentValuesIterable() {
        storIOSQLite.put().contentValues(createContentValues(), createContentValues()).withPutResolver(createCVPutResolver()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }

    @Test
    public void putObject() {
        storIOSQLite.put().object(createTweet()).prepare().executeAsBlocking();
        checkInterceptorsCalls();
    }
}

