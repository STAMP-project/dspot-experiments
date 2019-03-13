package com.pushtorefresh.storio3.sqlite.design;


import com.pushtorefresh.storio3.sqlite.queries.RawQuery;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.junit.Test;


public class ExecuteSQLOperationDesignTest extends OperationDesignTest {
    @Test
    public void execSqlBlocking() {
        Object nothing = storIOSQLite().executeSQL().withQuery(RawQuery.builder().query("ALTER TABLE users ...").build()).prepare().executeAsBlocking();
    }

    @Test
    public void execSqlAsRxFlowable() {
        Flowable<Object> flowable = storIOSQLite().executeSQL().withQuery(RawQuery.builder().query("DROP TABLE users").build()).prepare().asRxFlowable(BackpressureStrategy.MISSING);
    }
}

