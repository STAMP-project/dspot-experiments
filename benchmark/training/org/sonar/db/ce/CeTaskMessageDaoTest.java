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
package org.sonar.db.ce;


import System2.INSTANCE;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbTester;


public class CeTaskMessageDaoTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CeTaskMessageDao underTest = new CeTaskMessageDao();

    @Test
    public void insert() {
        underTest.insert(dbTester.getSession(), new CeTaskMessageDto().setUuid("uuid_1").setTaskUuid("task_uuid_1").setMessage("message_1").setCreatedAt(1222333L));
        dbTester.getSession().commit();
        assertThat(dbTester.select("select uuid as \"UUID\", task_uuid as \"TASK_UUID\", message as \"MESSAGE\", created_at as \"CREATED_AT\" from ce_task_message")).hasSize(1).extracting(( t) -> t.get("UUID"), ( t) -> t.get("TASK_UUID"), ( t) -> t.get("MESSAGE"), ( t) -> t.get("CREATED_AT")).containsOnly(Tuple.tuple("uuid_1", "task_uuid_1", "message_1", 1222333L));
    }

    @Test
    public void selectByTask_returns_empty_on_empty_table() {
        String taskUuid = randomAlphabetic(10);
        List<CeTaskMessageDto> dto = underTest.selectByTask(dbTester.getSession(), taskUuid);
        assertThat(dto).isEmpty();
    }

    @Test
    public void selectByTask_returns_message_of_task_ordered_by_CREATED_AT_asc() {
        String task1 = "task1";
        String task2 = "task2";
        CeTaskMessageDto[] messages = new CeTaskMessageDto[]{ insertMessage(task1, 0, 1222333L), insertMessage(task2, 1, 2222333L), insertMessage(task2, 2, 1111333L), insertMessage(task1, 3, 1222111L), insertMessage(task1, 4, 222111L), insertMessage(task1, 5, 3222111L) };
        assertThat(underTest.selectByTask(dbTester.getSession(), task1)).extracting(CeTaskMessageDto::getUuid).containsExactly(messages[4].getUuid(), messages[3].getUuid(), messages[0].getUuid(), messages[5].getUuid());
        assertThat(underTest.selectByTask(dbTester.getSession(), task2)).extracting(CeTaskMessageDto::getUuid).containsExactly(messages[2].getUuid(), messages[1].getUuid());
        assertThat(underTest.selectByTask(dbTester.getSession(), randomAlphabetic(5))).isEmpty();
    }
}

