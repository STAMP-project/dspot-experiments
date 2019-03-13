package com.pushtorefresh.storio3.sqlite.operations.internal;


import BackpressureStrategy.LATEST;
import android.support.annotation.NonNull;
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.integration.BaseTest;
import com.pushtorefresh.storio3.sqlite.integration.User;
import com.pushtorefresh.storio3.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio3.sqlite.operations.get.PreparedGetObject;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.pushtorefresh.storio3.sqlite.integration.UserTableMeta.COLUMN_ID;
import static com.pushtorefresh.storio3.sqlite.integration.UserTableMeta.TABLE;


@RunWith(RobolectricTestRunner.class)
public class RxJavaUtilsTest extends BaseTest {
    @NonNull
    private final String tableName = "some_table";

    @NonNull
    private final String tagName = "some_tag";

    @NonNull
    private final Query query = Query.builder().table(tableName).observesTags(tagName).build();

    @NonNull
    private final RawQuery rawQuery = RawQuery.builder().query("some query").observesTables(tableName).observesTags(tagName).build();

    @Test
    public void constructorShouldBePrivate() {
        PrivateConstructorChecker.forClass(RxJavaUtils.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please.").check();
    }

    @Test
    public void extractTablesThrowsIfBothQueriesAreNull() {
        try {
            RxJavaUtils.extractTables(null, null);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Please specify query").hasNoCause();
        }
    }

    @Test
    public void extractTablesReturnsQueryTablesIfRawQueryNull() {
        Set<String> tables = RxJavaUtils.extractTables(query, null);
        assertThat(tables).containsExactly(tableName);
    }

    @Test
    public void extractTablesReturnsRawQueryTablesIfQueryNull() {
        Set<String> tables = RxJavaUtils.extractTables(null, rawQuery);
        assertThat(tables).containsExactly(tableName);
    }

    @Test
    public void extractTablesReturnsQueryTablesIfQueryNull() {
        Set<String> tables = RxJavaUtils.extractTables(null, rawQuery);
        assertThat(tables).containsExactly(tableName);
    }

    @Test
    public void extractTagsThrowsIfBothQueriesAreNull() {
        try {
            RxJavaUtils.extractTags(null, null);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Please specify query").hasNoCause();
        }
    }

    @Test
    public void extractTagsReturnsQueryTablesIfRawQueryNull() {
        Set<String> tags = RxJavaUtils.extractTags(query, null);
        assertThat(tags).containsExactly(tagName);
    }

    @Test
    public void extractTagsReturnsRawQueryTablesIfQueryNull() {
        Set<String> tags = RxJavaUtils.extractTags(null, rawQuery);
        assertThat(tags).containsExactly(tagName);
    }

    @Test
    public void extractTagsReturnsQueryTablesIfQueryNull() {
        Set<String> tags = RxJavaUtils.extractTags(null, rawQuery);
        assertThat(tags).containsExactly(tagName);
    }

    @Test
    public void createGetFlowableCompletedAfterInitialEmissionIfNoTablesAndTags() {
        RawQuery queryWithoutTablesAnTags = RawQuery.builder().query(("select * from " + (TABLE))).build();
        PreparedGetListOfObjects<User> preparedGet = storIOSQLite.get().listOfObjects(User.class).withQuery(queryWithoutTablesAnTags).prepare();
        TestSubscriber<List<User>> subscriber = new TestSubscriber<List<User>>();
        RxJavaUtils.createGetFlowable(storIOSQLite, preparedGet, null, queryWithoutTablesAnTags, LATEST).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertValues(Collections.EMPTY_LIST);
        subscriber.assertComplete();
    }

    @Test
    public void createGetFlowableOptionalCompletedAfterInitialEmissionIfNoTablesAndTags() {
        RawQuery queryWithoutTablesAnTags = RawQuery.builder().query((((("select * from " + (TABLE)) + " where ") + (COLUMN_ID)) + "=?")).args(1).build();
        PreparedGetObject<User> preparedGet = storIOSQLite.get().object(User.class).withQuery(queryWithoutTablesAnTags).prepare();
        TestSubscriber<Optional<User>> subscriber = new TestSubscriber<Optional<User>>();
        RxJavaUtils.createGetFlowableOptional(storIOSQLite, preparedGet, null, queryWithoutTablesAnTags, LATEST).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertValues(Optional.<User>empty());
        subscriber.assertComplete();
    }
}

