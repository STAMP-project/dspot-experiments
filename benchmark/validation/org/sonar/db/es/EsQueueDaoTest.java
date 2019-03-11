/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.es;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class EsQueueDaoTest {
    private static final int LIMIT = 10;

    private static TestSystem2 system2 = new TestSystem2().setNow(1000);

    @Rule
    public DbTester dbTester = DbTester.create(EsQueueDaoTest.system2);

    private DbSession dbSession = dbTester.getSession();

    private EsQueueDao underTest = dbTester.getDbClient().esQueueDao();

    @Test
    public void insert_data() {
        int nbOfInsert = 10 + (new Random().nextInt(20));
        List<EsQueueDto> esQueueDtos = new ArrayList<>();
        IntStream.rangeClosed(1, nbOfInsert).forEach(( i) -> esQueueDtos.add(EsQueueDto.create("foo", UuidFactoryFast.getInstance().create())));
        underTest.insert(dbSession, esQueueDtos);
        assertThat(dbTester.countSql(dbSession, "select count(*) from es_queue")).isEqualTo(nbOfInsert);
    }

    @Test
    public void delete_unknown_EsQueueDto_does_not_throw_exception() {
        int nbOfInsert = 10 + (new Random().nextInt(20));
        List<EsQueueDto> esQueueDtos = new ArrayList<>();
        IntStream.rangeClosed(1, nbOfInsert).forEach(( i) -> esQueueDtos.add(EsQueueDto.create("foo", UuidFactoryFast.getInstance().create())));
        underTest.insert(dbSession, esQueueDtos);
        underTest.delete(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        assertThat(dbTester.countSql(dbSession, "select count(*) from es_queue")).isEqualTo(nbOfInsert);
    }

    @Test
    public void delete_EsQueueDto_does_not_throw_exception() {
        int nbOfInsert = 10 + (new Random().nextInt(20));
        List<EsQueueDto> esQueueDtos = new ArrayList<>();
        IntStream.rangeClosed(1, nbOfInsert).forEach(( i) -> esQueueDtos.add(EsQueueDto.create("foo", UuidFactoryFast.getInstance().create())));
        underTest.insert(dbSession, esQueueDtos);
        assertThat(dbTester.countSql(dbSession, "select count(*) from es_queue")).isEqualTo(nbOfInsert);
        underTest.delete(dbSession, esQueueDtos);
        assertThat(dbTester.countSql(dbSession, "select count(*) from es_queue")).isEqualTo(0);
    }

    @Test
    public void selectForRecovery_must_return_limit_when_there_are_more_rows() {
        EsQueueDaoTest.system2.setNow(1000L);
        EsQueueDto i1 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        EsQueueDaoTest.system2.setNow(1001L);
        EsQueueDto i2 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        EsQueueDaoTest.system2.setNow(1002L);
        EsQueueDto i3 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        assertThat(underTest.selectForRecovery(dbSession, 2000, 1)).extracting(EsQueueDto::getUuid).containsExactly(i3.getUuid());
        assertThat(underTest.selectForRecovery(dbSession, 2000, 2)).extracting(EsQueueDto::getUuid).containsExactly(i3.getUuid(), i2.getUuid());
        assertThat(underTest.selectForRecovery(dbSession, 2000, 10)).extracting(EsQueueDto::getUuid).containsExactly(i3.getUuid(), i2.getUuid(), i1.getUuid());
    }

    @Test
    public void selectForRecovery_returns_ordered_rows_created_before_date() {
        EsQueueDaoTest.system2.setNow(1000L);
        EsQueueDto i1 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        EsQueueDaoTest.system2.setNow(1001L);
        EsQueueDto i2 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        EsQueueDaoTest.system2.setNow(1002L);
        EsQueueDto i3 = underTest.insert(dbSession, EsQueueDto.create("foo", UuidFactoryFast.getInstance().create()));
        assertThat(underTest.selectForRecovery(dbSession, 999, EsQueueDaoTest.LIMIT)).hasSize(0);
        assertThat(underTest.selectForRecovery(dbSession, 1000, EsQueueDaoTest.LIMIT)).extracting(EsQueueDto::getUuid).containsExactly(i1.getUuid());
        assertThat(underTest.selectForRecovery(dbSession, 1001, EsQueueDaoTest.LIMIT)).extracting(EsQueueDto::getUuid).containsExactly(i2.getUuid(), i1.getUuid());
        assertThat(underTest.selectForRecovery(dbSession, 2000, EsQueueDaoTest.LIMIT)).extracting(EsQueueDto::getUuid).containsExactly(i3.getUuid(), i2.getUuid(), i1.getUuid());
    }
}

