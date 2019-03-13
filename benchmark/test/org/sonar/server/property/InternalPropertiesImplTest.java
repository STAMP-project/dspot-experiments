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
package org.sonar.server.property;


import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.property.InternalPropertiesDao;


public class InternalPropertiesImplTest {
    private static final String EMPTY_STRING = "";

    public static final String SOME_VALUE = "a value";

    public static final String SOME_KEY = "some key";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private DbSession dbSession = Mockito.mock(DbSession.class);

    private InternalPropertiesDao internalPropertiesDao = Mockito.mock(InternalPropertiesDao.class);

    private InternalPropertiesImpl underTest = new InternalPropertiesImpl(dbClient);

    @Test
    public void reads_throws_IAE_if_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.read(null);
    }

    @Test
    public void reads_throws_IAE_if_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.read(InternalPropertiesImplTest.EMPTY_STRING);
    }

    @Test
    public void reads_returns_optional_from_DAO() {
        Optional<String> value = Optional.of("bablabla");
        Mockito.when(internalPropertiesDao.selectByKey(dbSession, InternalPropertiesImplTest.SOME_KEY)).thenReturn(value);
        assertThat(underTest.read(InternalPropertiesImplTest.SOME_KEY)).isSameAs(value);
    }

    @Test
    public void write_throws_IAE_if_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.write(null, InternalPropertiesImplTest.SOME_VALUE);
    }

    @Test
    public void writes_throws_IAE_if_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.write(InternalPropertiesImplTest.EMPTY_STRING, InternalPropertiesImplTest.SOME_VALUE);
    }

    @Test
    public void write_calls_dao_saveAsEmpty_when_value_is_null() {
        underTest.write(InternalPropertiesImplTest.SOME_KEY, null);
        Mockito.verify(internalPropertiesDao).saveAsEmpty(dbSession, InternalPropertiesImplTest.SOME_KEY);
        Mockito.verify(dbSession).commit();
    }

    @Test
    public void write_calls_dao_saveAsEmpty_when_value_is_empty() {
        underTest.write(InternalPropertiesImplTest.SOME_KEY, InternalPropertiesImplTest.EMPTY_STRING);
        Mockito.verify(internalPropertiesDao).saveAsEmpty(dbSession, InternalPropertiesImplTest.SOME_KEY);
        Mockito.verify(dbSession).commit();
    }

    @Test
    public void write_calls_dao_save_when_value_is_neither_null_nor_empty() {
        underTest.write(InternalPropertiesImplTest.SOME_KEY, InternalPropertiesImplTest.SOME_VALUE);
        Mockito.verify(internalPropertiesDao).save(dbSession, InternalPropertiesImplTest.SOME_KEY, InternalPropertiesImplTest.SOME_VALUE);
        Mockito.verify(dbSession).commit();
    }
}

