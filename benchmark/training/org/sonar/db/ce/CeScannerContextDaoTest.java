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
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.CloseableIterator;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class CeScannerContextDaoTest {
    private static final String TABLE_NAME = "ce_scanner_context";

    private static final String SOME_UUID = "some UUID";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system = Mockito.mock(System2.class);

    private DbSession dbSession = dbTester.getSession();

    private CeScannerContextDao underTest = new CeScannerContextDao(system);

    @Test
    public void selectScannerContext_returns_empty_on_empty_table() {
        assertThat(underTest.selectScannerContext(dbSession, CeScannerContextDaoTest.SOME_UUID)).isEmpty();
    }

    @Test
    public void selectScannerContext_returns_empty_when_no_row_exist_for_taskUuid() {
        String data = "some data";
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, CeScannerContextDaoTest.scannerContextInputStreamOf(data));
        dbSession.commit();
        assertThat(underTest.selectScannerContext(dbSession, "OTHER_uuid")).isEmpty();
        assertThat(underTest.selectScannerContext(dbSession, CeScannerContextDaoTest.SOME_UUID)).contains(data);
    }

    @Test
    public void insert_fails_with_IAE_if_data_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Scanner context can not be empty");
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, CloseableIterator.emptyCloseableIterator());
    }

    @Test
    public void insert_fails_with_IAE_if_data_is_fully_read() {
        CloseableIterator<String> iterator = CeScannerContextDaoTest.scannerContextInputStreamOf("aa");
        iterator.next();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Scanner context can not be empty");
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, iterator);
    }

    @Test
    public void insert_fails_if_row_already_exists_for_taskUuid() {
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, CeScannerContextDaoTest.scannerContextInputStreamOf("bla"));
        dbSession.commit();
        assertThat(dbTester.countRowsOfTable(dbSession, CeScannerContextDaoTest.TABLE_NAME)).isEqualTo(1);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Fail to insert scanner context for task " + (CeScannerContextDaoTest.SOME_UUID)));
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, CeScannerContextDaoTest.scannerContextInputStreamOf("blo"));
    }

    @Test
    public void insert_and_select_line_reader() {
        String scannerContext = ((("line 1" + (System.lineSeparator())) + "line 2") + (System.lineSeparator())) + "line 3";
        underTest.insert(dbSession, CeScannerContextDaoTest.SOME_UUID, CeScannerContextDaoTest.scannerContextInputStreamOf(scannerContext));
        dbSession.commit();
        assertThat(underTest.selectScannerContext(dbSession, CeScannerContextDaoTest.SOME_UUID)).contains(scannerContext);
    }

    @Test
    public void deleteByUuids_does_not_fail_on_empty_table() {
        underTest.deleteByUuids(dbSession, Collections.singleton("some uuid"));
    }

    @Test
    public void deleteByUuids_deletes_specified_existing_uuids() {
        insertScannerContext(CeScannerContextDaoTest.SOME_UUID);
        String data2 = insertScannerContext("UUID_2");
        insertScannerContext("UUID_3");
        underTest.deleteByUuids(dbSession, ImmutableSet.of(CeScannerContextDaoTest.SOME_UUID, "UUID_3", "UUID_4"));
        assertThat(underTest.selectScannerContext(dbSession, CeScannerContextDaoTest.SOME_UUID)).isEmpty();
        assertThat(underTest.selectScannerContext(dbSession, "UUID_2")).contains(data2);
        assertThat(underTest.selectScannerContext(dbSession, "UUID_3")).isEmpty();
    }

    @Test
    public void selectOlderThan() {
        insertWithCreationDate("TASK_1", 1450000000000L);
        insertWithCreationDate("TASK_2", 1460000000000L);
        insertWithCreationDate("TASK_3", 1470000000000L);
        assertThat(underTest.selectOlderThan(dbSession, 1465000000000L)).containsOnly("TASK_1", "TASK_2");
        assertThat(underTest.selectOlderThan(dbSession, 1450000000000L)).isEmpty();
    }
}

