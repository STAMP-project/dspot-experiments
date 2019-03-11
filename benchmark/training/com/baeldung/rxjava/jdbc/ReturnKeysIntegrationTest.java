package com.baeldung.rxjava.jdbc;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.junit.Test;
import rx.Observable;

import static Connector.connectionProvider;


public class ReturnKeysIntegrationTest {
    private Observable<Boolean> begin = null;

    private Observable<Integer> createStatement = null;

    private ConnectionProvider connectionProvider = connectionProvider;

    private Database db = Database.from(connectionProvider);

    @Test
    public void whenInsertAndReturnGeneratedKey_thenCorrect() {
        Integer key = db.update("INSERT INTO EMPLOYEE_SAMPLE(name) VALUES('John')").dependsOn(createStatement).returnGeneratedKeys().getAs(Integer.class).count().toBlocking().single();
        assertThat(key).isEqualTo(1);
    }
}

