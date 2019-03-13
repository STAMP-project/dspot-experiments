/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational.history;


import io.debezium.relational.Tables;
import io.debezium.relational.ddl.LegacyDdlParser;
import io.debezium.util.Testing;
import java.util.Map;
import org.junit.Test;

import static io.debezium.util.Testing.Print.isEnabled;


/**
 *
 *
 * @author Randall Hauch
 */
public abstract class AbstractDatabaseHistoryTest {
    protected DatabaseHistory history;

    protected Map<String, Object> source1;

    protected Map<String, Object> source2;

    protected Tables tables;

    protected Tables t0;

    protected Tables t1;

    protected Tables t2;

    protected Tables t3;

    protected Tables t4;

    protected Tables all;

    protected LegacyDdlParser parser;

    @Test
    public void shouldRecordChangesAndRecoverToVariousPoints() {
        record(1, 0, "CREATE TABLE foo ( first VARCHAR(22) NOT NULL );", all, t3, t2, t1, t0);
        record(23, 1, "CREATE TABLE\\nperson ( name VARCHAR(22) NOT NULL );", all, t3, t2, t1);
        record(30, 2, "CREATE TABLE address\\n( street VARCHAR(22) NOT NULL );", all, t3, t2);
        record(32, 3, "ALTER TABLE address ADD city VARCHAR(22) NOT NULL;", all, t3);
        // Testing.Print.enable();
        if (isEnabled()) {
            Testing.print(("t0 = " + (t0)));
            Testing.print(("t1 = " + (t1)));
            Testing.print(("t2 = " + (t2)));
            Testing.print(("t3 = " + (t3)));
        }
        assertThat(recover(1, 0)).isEqualTo(t0);
        assertThat(recover(1, 3)).isEqualTo(t0);
        assertThat(recover(10, 1)).isEqualTo(t0);
        assertThat(recover(22, 999999)).isEqualTo(t0);
        assertThat(recover(23, 0)).isEqualTo(t0);
        assertThat(recover(23, 1)).isEqualTo(t1);
        assertThat(recover(23, 2)).isEqualTo(t1);
        assertThat(recover(23, 3)).isEqualTo(t1);
        assertThat(recover(29, 999)).isEqualTo(t1);
        assertThat(recover(30, 1)).isEqualTo(t1);
        assertThat(recover(30, 2)).isEqualTo(t2);
        assertThat(recover(30, 3)).isEqualTo(t2);
        assertThat(recover(32, 2)).isEqualTo(t2);
        assertThat(recover(32, 3)).isEqualTo(t3);
        assertThat(recover(32, 4)).isEqualTo(t3);
        assertThat(recover(33, 0)).isEqualTo(t3);
        assertThat(recover(33, 0)).isEqualTo(all);
        assertThat(recover(1033, 4)).isEqualTo(t3);
        assertThat(recover(1033, 4)).isEqualTo(t3);
    }
}

