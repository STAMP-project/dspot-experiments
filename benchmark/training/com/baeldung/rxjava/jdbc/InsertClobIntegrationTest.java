package com.baeldung.rxjava.jdbc;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;

import static Connector.connectionProvider;


public class InsertClobIntegrationTest {
    private ConnectionProvider connectionProvider = connectionProvider;

    private Database db = Database.from(connectionProvider);

    private String expectedDocument = null;

    private String actualDocument = null;

    private Observable<Integer> create;

    private Observable<Integer> insert = null;

    @Test
    public void whenSelectCLOB_thenCorrect() throws IOException {
        db.select("select document from SERVERLOG_TABLE where id = 1").dependsOn(create).dependsOn(insert).getAs(String.class).toList().toBlocking().single();
        Assert.assertEquals(expectedDocument, actualDocument);
    }
}

