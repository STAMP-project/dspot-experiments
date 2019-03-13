package com.pushtorefresh.storio3.sqlite.design;


import android.database.Cursor;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.List;
import org.junit.Test;


public class GetOperationDesignTest extends OperationDesignTest {
    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOSQLite().get().cursor().withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).prepare().executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<User> users = storIOSQLite().get().listOfObjects(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getCursorFlowable() {
        Flowable<Cursor> flowableCursor = storIOSQLite().get().cursor().withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getListOfObjectsFlowable() {
        Flowable<List<User>> flowableUsers = storIOSQLite().get().listOfObjects(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getCursorWithRawQueryBlocking() {
        Cursor cursor = storIOSQLite().get().cursor().withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).prepare().executeAsBlocking();
    }

    @Test
    public void getCursorWithRawQueryFlowable() {
        Flowable<Cursor> cursorFlowable = storIOSQLite().get().cursor().withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getListOfObjectsWithRawQueryBlocking() {
        List<User> users = storIOSQLite().get().listOfObjects(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getListOfObjectsWithRawQueryFlowable() {
        Flowable<List<User>> usersFlowable = storIOSQLite().get().listOfObjects(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getObjectBlocking() {
        User user = storIOSQLite().get().object(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getObjectBlockingWithRawQueryBlocking() {
        User user = storIOSQLite().get().object(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().executeAsBlocking();
    }

    @Test
    public void getObjectFlowable() {
        Flowable<Optional<User>> userFlowable = storIOSQLite().get().object(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getObjectWithRawQueryFlowable() {
        Flowable<Optional<User>> userFlowable = storIOSQLite().get().object(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxFlowable(BackpressureStrategy.LATEST);
    }

    @Test
    public void getListOfObjectsSingle() {
        Single<List<User>> singleUsers = storIOSQLite().get().listOfObjects(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getListOfObjectsWithRawQuerySingle() {
        Single<List<User>> singleUsers = storIOSQLite().get().listOfObjects(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getObjectSingle() {
        Single<Optional<User>> singleUser = storIOSQLite().get().object(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getObjectWithRawQuerySingle() {
        Single<Optional<User>> singleUsers = storIOSQLite().get().object(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxSingle();
    }

    @Test
    public void getCursorSingle() {
        Single<Cursor> singleCursor = storIOSQLite().get().cursor().withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).prepare().asRxSingle();
    }

    @Test
    public void getObjectMaybe() {
        Maybe<User> maybeUser = storIOSQLite().get().object(User.class).withQuery(Query.builder().table("users").where("email = ?").whereArgs("artem.zinnatullin@gmail.com").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxMaybe();
    }

    @Test
    public void getObjectWithRawQueryMaybe() {
        Maybe<User> maybeUser = storIOSQLite().get().object(User.class).withQuery(RawQuery.builder().query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?").args("arg1", "arg2").build()).withGetResolver(UserTableMeta.GET_RESOLVER).prepare().asRxMaybe();
    }
}

