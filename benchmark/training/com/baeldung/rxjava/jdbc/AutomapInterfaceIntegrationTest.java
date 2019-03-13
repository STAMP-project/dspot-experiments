package com.baeldung.rxjava.jdbc;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import java.util.List;
import org.junit.Test;
import rx.Observable;

import static Connector.connectionProvider;


public class AutomapInterfaceIntegrationTest {
    private ConnectionProvider connectionProvider = connectionProvider;

    private Database db = Database.from(connectionProvider);

    private Observable<Integer> truncate = null;

    private Observable<Integer> insert1;

    private Observable<Integer> insert2 = null;

    @Test
    public void whenSelectFromTableAndAutomap_thenCorrect() {
        List<Employee> employees = db.select("select id, name from EMPLOYEE").dependsOn(insert2).autoMap(Employee.class).toList().toBlocking().single();
        assertThat(employees.get(0).id()).isEqualTo(1);
        assertThat(employees.get(0).name()).isEqualTo("Alan");
        assertThat(employees.get(1).id()).isEqualTo(2);
        assertThat(employees.get(1).name()).isEqualTo("Sarah");
    }
}

