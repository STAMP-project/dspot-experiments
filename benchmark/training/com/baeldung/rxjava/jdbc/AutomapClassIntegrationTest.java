package com.baeldung.rxjava.jdbc;


import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import java.util.List;
import org.junit.Test;
import rx.Observable;

import static Connector.connectionProvider;


public class AutomapClassIntegrationTest {
    private ConnectionProvider connectionProvider = connectionProvider;

    private Database db = Database.from(connectionProvider);

    private Observable<Integer> create = null;

    private Observable<Integer> insert1;

    private Observable<Integer> insert2 = null;

    @Test
    public void whenSelectManagersAndAutomap_thenCorrect() {
        List<Manager> managers = db.select("select id, name from MANAGER").dependsOn(create).dependsOn(insert1).dependsOn(insert2).autoMap(Manager.class).toList().toBlocking().single();
        assertThat(managers.get(0).getId()).isEqualTo(1);
        assertThat(managers.get(0).getName()).isEqualTo("Alan");
        assertThat(managers.get(1).getId()).isEqualTo(2);
        assertThat(managers.get(1).getName()).isEqualTo("Sarah");
    }
}

