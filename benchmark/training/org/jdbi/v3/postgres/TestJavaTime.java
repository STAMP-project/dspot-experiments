/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.postgres;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.ClassRule;
import org.junit.Test;


public class TestJavaTime {
    @ClassRule
    public static JdbiRule db = PostgresDbRule.rule();

    Handle h;

    @Test
    public void localDate() {
        LocalDate d = LocalDate.now();
        h.execute("insert into stuff(d) values (?)", d);
        assertThat(h.createQuery("select d from stuff").mapTo(LocalDate.class).findOnly()).isEqualTo(d);
    }

    @Test
    public void localDateTime() {
        LocalDateTime d = LocalDateTime.now();
        h.execute("insert into stuff(ts) values (?)", d);
        assertThat(h.createQuery("select ts from stuff").mapTo(LocalDateTime.class).findOnly()).isCloseTo(d, getAllowableOffset());
    }

    @Test
    public void offsetDateTime() {
        OffsetDateTime dt = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        h.execute("insert into stuff(ts) values (?)", dt);
        assertThat(h.createQuery("select ts from stuff").mapTo(OffsetDateTime.class).findOnly()).isCloseTo(dt, getAllowableOffset());
    }

    @Test
    public void offsetDateTimeLosesOffset() {
        OffsetDateTime dt = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.ofHours((-7)));
        h.execute("insert into stuff(ts) values (?)", dt);
        assertThat(h.createQuery("select ts from stuff").mapTo(OffsetDateTime.class).findOnly()).isCloseTo(dt, getAllowableOffset());
    }

    @Test
    public void localTime() {
        h.execute("create table schedule (start time, stop time)");
        LocalTime start = LocalTime.of(8, 30, 0);
        LocalTime stop = LocalTime.of(10, 30, 0);
        h.execute("insert into schedule (start, stop) values (?,?)", start, stop);
        assertThat(h.createQuery("select start from schedule").mapTo(LocalTime.class).findOnly()).isEqualTo(start);
        assertThat(h.createQuery("select stop from schedule").mapTo(LocalTime.class).findOnly()).isEqualTo(stop);
    }

    @Test
    public void instant() {
        final Instant leap = Instant.ofEpochMilli((-14159025000L));
        h.execute("insert into stuff values(?)", leap);
        assertThat(h.createQuery("select ts from stuff").mapTo(Instant.class).findOnly()).isEqualTo(leap);
    }

    @Test
    public void zoneId() {
        final ZoneId zone = ZoneId.systemDefault();
        h.execute("insert into stuff(z) values (?)", zone);
        assertThat(h.createQuery("select z from stuff").mapTo(ZoneId.class).findOnly()).isEqualTo(zone);
    }
}

