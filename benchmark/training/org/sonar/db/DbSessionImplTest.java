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
package org.sonar.db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.mockito.Mockito;


public class DbSessionImplTest {
    private SqlSession sqlSessionMock = Mockito.mock(SqlSession.class);

    private DbSessionImpl underTest = new DbSessionImpl(sqlSessionMock);

    @Test
    public void all_methods_to_wrapped_SqlSession() {
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        int randomInt = random.nextInt(200);
        String randomStatement = randomAlphabetic(10);
        Object randomParameter = new Object();
        Cursor<Object> mockCursor = Mockito.mock(Cursor.class);
        RowBounds rowBounds = new RowBounds();
        Object randomObject = new Object();
        List<Object> randomList = new ArrayList<>();
        Map<Object, Object> randomMap = new HashMap<>();
        String randomMapKey = randomAlphabetic(10);
        ResultHandler randomResultHandler = ( resultContext) -> {
            // don't care
        };
        List<BatchResult> randomBatchResults = new ArrayList<>();
        Configuration randomConfiguration = new Configuration();
        verifyDelegation(DbSessionImpl::commit, ( s) -> verify(s).commit());
        verifyDelegation(( t) -> t.commit(randomBoolean), ( s) -> Mockito.verify(s).commit(randomBoolean));
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectCursor(randomStatement)).thenReturn(mockCursor), ( dbSession) -> dbSession.selectCursor(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).selectCursor(randomStatement);
            return mockCursor;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectCursor(randomStatement, randomParameter)).thenReturn(mockCursor), ( dbSession) -> dbSession.selectCursor(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectCursor(randomStatement, randomParameter);
            return mockCursor;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectCursor(randomStatement, randomParameter, rowBounds)).thenReturn(mockCursor), ( dbSession) -> dbSession.selectCursor(randomStatement, randomParameter, rowBounds), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectCursor(randomStatement, randomParameter, rowBounds);
            return mockCursor;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectOne(randomStatement)).thenReturn(randomObject), ( dbSession) -> dbSession.selectOne(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).selectOne(randomStatement);
            return randomObject;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectOne(randomStatement, randomParameter)).thenReturn(randomObject), ( dbSession) -> dbSession.selectOne(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectOne(randomStatement, randomParameter);
            return randomObject;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectList(randomStatement)).thenReturn(randomList), ( dbSession) -> dbSession.selectList(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).selectList(randomStatement);
            return randomList;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectList(randomStatement, randomParameter)).thenReturn(randomList), ( dbSession) -> dbSession.selectList(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectList(randomStatement, randomParameter);
            return randomList;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectList(randomStatement, randomParameter, rowBounds)).thenReturn(randomList), ( dbSession) -> dbSession.selectList(randomStatement, randomParameter, rowBounds), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectList(randomStatement, randomParameter, rowBounds);
            return randomList;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectMap(randomStatement, randomMapKey)).thenReturn(randomMap), ( dbSession) -> dbSession.selectMap(randomStatement, randomMapKey), ( sqlSession) -> {
            Mockito.verify(sqlSession).selectMap(randomStatement, randomMapKey);
            return randomMap;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectMap(randomStatement, randomParameter, randomMapKey)).thenReturn(randomMap), ( dbSession) -> dbSession.selectMap(randomStatement, randomParameter, randomMapKey), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectMap(randomStatement, randomParameter, randomMapKey);
            return randomMap;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.selectMap(randomStatement, randomParameter, randomMapKey, rowBounds)).thenReturn(randomMap), ( dbSession) -> dbSession.selectMap(randomStatement, randomParameter, randomMapKey, rowBounds), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).selectMap(randomStatement, randomParameter, randomMapKey, rowBounds);
            return randomMap;
        });
        verifyDelegation(( dbSession) -> dbSession.select(randomStatement, randomResultHandler), ( sqlSession) -> Mockito.verify(sqlSessionMock).select(randomStatement, randomResultHandler));
        verifyDelegation(( dbSession) -> dbSession.select(randomStatement, randomParameter, randomResultHandler), ( sqlSession) -> Mockito.verify(sqlSession).select(randomStatement, randomParameter, randomResultHandler));
        verifyDelegation(( dbSession) -> dbSession.select(randomStatement, randomParameter, rowBounds, randomResultHandler), ( sqlSession) -> Mockito.verify(sqlSessionMock).select(randomStatement, randomParameter, rowBounds, randomResultHandler));
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.insert(randomStatement)).thenReturn(randomInt), ( dbSession) -> dbSession.insert(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).insert(randomStatement);
            return randomInt;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.insert(randomStatement, randomParameter)).thenReturn(randomInt), ( dbSession) -> dbSession.insert(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).insert(randomStatement, randomParameter);
            return randomInt;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.update(randomStatement)).thenReturn(randomInt), ( dbSession) -> dbSession.update(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).update(randomStatement);
            return randomInt;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.update(randomStatement, randomParameter)).thenReturn(randomInt), ( dbSession) -> dbSession.update(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).update(randomStatement, randomParameter);
            return randomInt;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.delete(randomStatement)).thenReturn(randomInt), ( dbSession) -> dbSession.delete(randomStatement), ( sqlSession) -> {
            Mockito.verify(sqlSession).delete(randomStatement);
            return randomInt;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.delete(randomStatement, randomParameter)).thenReturn(randomInt), ( dbSession) -> dbSession.delete(randomStatement, randomParameter), ( sqlSession) -> {
            Mockito.verify(sqlSessionMock).delete(randomStatement, randomParameter);
            return randomInt;
        });
        verifyDelegation(DbSessionImpl::rollback, ( s) -> verify(s).rollback());
        verifyDelegation(( t) -> t.rollback(randomBoolean), ( s) -> Mockito.verify(s).rollback(randomBoolean));
        verifyDelegation(( sqlSession) -> when(sqlSession.flushStatements()).thenReturn(randomBatchResults), DbSessionImpl::flushStatements, ( sqlSession) -> {
            verify(sqlSession).flushStatements();
            return randomBatchResults;
        });
        verifyDelegation(DbSessionImpl::close, ( s) -> verify(s).close());
        verifyDelegation(DbSessionImpl::clearCache, ( s) -> verify(s).clearCache());
        verifyDelegation(( sqlSession) -> when(sqlSession.getConfiguration()).thenReturn(randomConfiguration), DbSessionImpl::getConfiguration, ( sqlSession) -> {
            verify(sqlSession).getConfiguration();
            return randomConfiguration;
        });
        verifyDelegation(( sqlSession) -> Mockito.when(sqlSession.getMapper(DbSessionImplTest.class)).thenReturn(this), ( dbSession) -> dbSession.getMapper(DbSessionImplTest.class), ( sqlSession) -> {
            Mockito.verify(sqlSession).getMapper(DbSessionImplTest.class);
            return this;
        });
        verifyDelegation(DbSessionImpl::getConnection, ( s) -> verify(s).getConnection());
    }

    @Test
    public void getSqlSession_returns_wrapped_SqlSession_object() {
        assertThat(underTest.getSqlSession()).isSameAs(sqlSessionMock);
    }
}

