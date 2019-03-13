package com.pushtorefresh.storio3.sqlite.integration;


import android.support.annotation.NonNull;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.test.AbstractEmissionChecker;
import com.pushtorefresh.storio3.test.ConcurrencyTesting;
import com.pushtorefresh.storio3.test.Repeat;
import com.pushtorefresh.storio3.test.RepeatRule;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RxQueryTest extends BaseTest {
    private class EmissionChecker extends AbstractEmissionChecker<List<User>> {
        EmissionChecker(@NonNull
        Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Disposable subscribe() {
            return storIOSQLite.get().listOfObjects(User.class).withQuery(UserTableMeta.QUERY_ALL).prepare().asRxFlowable(BackpressureStrategy.LATEST).subscribe(new io.reactivex.functions.Consumer<List<User>>() {
                @Override
                public void accept(@NonNull
                List<User> users) {
                    onNextObtained(users);
                }
            });
        }
    }

    @Rule
    public RepeatRule repeat = new RepeatRule();

    @Test
    public void insertEmission() {
        final List<User> initialUsers = putUsersBlocking(10);
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(((initialUsers.size()) + (usersForInsert.size())));
        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);
        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(allUsers);
        final RxQueryTest.EmissionChecker emissionChecker = new RxQueryTest.EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();
        // Should receive initial users
        awaitNextExpectedValue();
        putUsersBlocking(usersForInsert);
        // Should receive initial users + inserted users
        awaitNextExpectedValue();
        assertThatNoExpectedValuesLeft();
        disposable.dispose();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsersBlocking(10);
        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        final List<User> updatedList = new ArrayList<User>(users.size());
        int count = 1;
        for (User user : users) {
            updatedList.add(User.newInstance(user.id(), ("new_email" + (count++))));
        }
        expectedUsers.add(users);
        expectedUsers.add(updatedList);
        final RxQueryTest.EmissionChecker emissionChecker = new RxQueryTest.EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();
        // Should receive all users
        awaitNextExpectedValue();
        storIOSQLite.put().objects(updatedList).prepare().executeAsBlocking();
        // Should receive updated users
        awaitNextExpectedValue();
        assertThatNoExpectedValuesLeft();
        disposable.dispose();
    }

    @Test
    public void deleteEmission() {
        final List<User> usersThatShouldBeSaved = TestFactory.newUsers(10);
        final List<User> usersThatShouldBeDeleted = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>();
        allUsers.addAll(usersThatShouldBeSaved);
        allUsers.addAll(usersThatShouldBeDeleted);
        putUsersBlocking(allUsers);
        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(allUsers);
        expectedUsers.add(usersThatShouldBeSaved);
        final RxQueryTest.EmissionChecker emissionChecker = new RxQueryTest.EmissionChecker(expectedUsers);
        final Disposable disposable = emissionChecker.subscribe();
        // Should receive all users
        awaitNextExpectedValue();
        deleteUsersBlocking(usersThatShouldBeDeleted);
        // Should receive users that should be saved
        awaitNextExpectedValue();
        assertThatNoExpectedValuesLeft();
        disposable.dispose();
    }

    @Test
    @Repeat(times = 20)
    public void concurrentPutWithoutGlobalTransaction() throws InterruptedException {
        final int numberOfConcurrentPuts = ConcurrencyTesting.optimalTestThreadsCount();
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
        storIOSQLite.observeChangesInTable(TweetTableMeta.TABLE, BackpressureStrategy.LATEST).subscribe(testSubscriber);
        final CountDownLatch concurrentPutLatch = new CountDownLatch(1);
        final CountDownLatch allPutsDoneLatch = new CountDownLatch(numberOfConcurrentPuts);
        for (int i = 0; i < numberOfConcurrentPuts; i++) {
            final int iCopy = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        concurrentPutLatch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    storIOSQLite.put().object(Tweet.newInstance(null, 1L, ("Some text: " + iCopy))).prepare().executeAsBlocking();
                    allPutsDoneLatch.countDown();
                }
            }).start();
        }
        // Start concurrent Put operations.
        concurrentPutLatch.countDown();
        assertThat(allPutsDoneLatch.await(25, TimeUnit.SECONDS)).isTrue();
        testSubscriber.assertNoErrors();
        // Put operation creates short-term transaction which might result in merge of some notifications.
        // So we have two extreme cases:
        // - no merged notifications ? isEqualTo(numberOfParallelPuts)
        // - all notifications merged ? isEqualTo(1)
        // Obviously truth is somewhere between those (depends on CPU of machine that runs test).
        assertThat(testSubscriber.valueCount()).isLessThanOrEqualTo(numberOfConcurrentPuts).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void nestedTransaction() {
        storIOSQLite.lowLevel().beginTransaction();
        storIOSQLite.lowLevel().beginTransaction();
        storIOSQLite.lowLevel().setTransactionSuccessful();
        storIOSQLite.lowLevel().endTransaction();
        storIOSQLite.lowLevel().setTransactionSuccessful();
        storIOSQLite.lowLevel().endTransaction();
    }

    @Test
    public void queryOneExistedObjectFlowable() {
        final List<User> users = putUsersBlocking(3);
        final User expectedUser = users.get(0);
        final Flowable<Optional<User>> userFlowable = storIOSQLite.get().object(User.class).withQuery(Query.builder().table(UserTableMeta.TABLE).where(((UserTableMeta.COLUMN_EMAIL) + "=?")).whereArgs(expectedUser.email()).build()).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(1);
        TestSubscriber<Optional<User>> testSubscriber = new TestSubscriber<Optional<User>>();
        userFlowable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Optional.of(expectedUser));
    }

    @Test
    public void queryOneNonExistedObjectFlowable() {
        putUsersBlocking(3);
        final Flowable<Optional<User>> userFlowable = storIOSQLite.get().object(User.class).withQuery(Query.builder().table(UserTableMeta.TABLE).where(((UserTableMeta.COLUMN_EMAIL) + "=?")).whereArgs("some arg").build()).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(1);
        TestSubscriber<Optional<User>> testSubscriber = new TestSubscriber<Optional<User>>();
        userFlowable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Optional.<User>empty());
    }

    @Test
    public void queryOneExistedObjectTableUpdate() {
        User expectedUser = User.newInstance(null, "such@email.com");
        putUsersBlocking(3);
        final Flowable<Optional<User>> userFlowable = storIOSQLite.get().object(User.class).withQuery(Query.builder().table(UserTableMeta.TABLE).where(((UserTableMeta.COLUMN_EMAIL) + "=?")).whereArgs(expectedUser.email()).build()).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(2);
        TestSubscriber<Optional<User>> testSubscriber = new TestSubscriber<Optional<User>>();
        userFlowable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Optional.<User>empty());
        putUserBlocking(expectedUser);
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(Optional.<User>empty(), Optional.of(expectedUser));
    }

    @Test
    public void queryOneNonexistedObjectTableUpdate() {
        final Flowable<Optional<User>> userFlowable = storIOSQLite.get().object(User.class).withQuery(Query.builder().table(UserTableMeta.TABLE).where(((UserTableMeta.COLUMN_EMAIL) + "=?")).whereArgs("some arg").build()).prepare().asRxFlowable(BackpressureStrategy.LATEST).take(2);
        TestSubscriber<Optional<User>> testSubscriber = new TestSubscriber<Optional<User>>();
        userFlowable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Optional.<User>empty());
        putUserBlocking();
        testSubscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(Optional.<User>empty(), Optional.<User>empty());
    }

    @Test
    public void queryListOfObjectsAsSingle() {
        final List<User> users = putUsersBlocking(10);
        final Single<List<User>> usersSingle = storIOSQLite.get().listOfObjects(User.class).withQuery(UserTableMeta.QUERY_ALL).prepare().asRxSingle();
        TestObserver<List<User>> testObserver = new TestObserver<List<User>>();
        usersSingle.subscribe(testObserver);
        testObserver.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testObserver.assertNoErrors();
        testObserver.assertValue(users);
        testObserver.assertComplete();
    }

    @Test
    public void queryObjectAsSingle() {
        final List<User> users = putUsersBlocking(3);
        final Single<Optional<User>> usersSingle = storIOSQLite.get().object(User.class).withQuery(UserTableMeta.QUERY_ALL).prepare().asRxSingle();
        TestObserver<Optional<User>> testObserver = new TestObserver<Optional<User>>();
        usersSingle.subscribe(testObserver);
        testObserver.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testObserver.assertNoErrors();
        testObserver.assertValue(Optional.of(users.get(0)));
        testObserver.assertComplete();
    }

    @Test
    public void queryNumberOfResultsAsSingle() {
        final List<User> users = putUsersBlocking(3);
        final Single<Integer> usersSingle = storIOSQLite.get().numberOfResults().withQuery(UserTableMeta.QUERY_ALL).prepare().asRxSingle();
        TestObserver<Integer> testObserver = new TestObserver<Integer>();
        usersSingle.subscribe(testObserver);
        testObserver.awaitTerminalEvent(5, TimeUnit.SECONDS);
        testObserver.assertNoErrors();
        testObserver.assertValue(users.size());
        testObserver.assertComplete();
    }
}

