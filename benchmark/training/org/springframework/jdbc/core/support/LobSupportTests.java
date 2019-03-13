/**
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.core.support;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.LobRetrievalFailureException;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;


/**
 *
 *
 * @author Alef Arendsen
 */
public class LobSupportTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreatingPreparedStatementCallback() throws SQLException {
        LobHandler handler = Mockito.mock(LobHandler.class);
        LobCreator creator = Mockito.mock(LobCreator.class);
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        BDDMockito.given(handler.getLobCreator()).willReturn(creator);
        BDDMockito.given(ps.executeUpdate()).willReturn(3);
        class SetValuesCalled {
            boolean b = false;
        }
        final SetValuesCalled svc = new SetValuesCalled();
        AbstractLobCreatingPreparedStatementCallback psc = new AbstractLobCreatingPreparedStatementCallback(handler) {
            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                svc.b = true;
            }
        };
        Assert.assertEquals(Integer.valueOf(3), psc.doInPreparedStatement(ps));
        Assert.assertTrue(svc.b);
        Mockito.verify(creator).close();
        Mockito.verify(handler).getLobCreator();
        Mockito.verify(ps).executeUpdate();
    }

    @Test
    public void testAbstractLobStreamingResultSetExtractorNoRows() throws SQLException {
        ResultSet rset = Mockito.mock(ResultSet.class);
        AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
        thrown.expect(IncorrectResultSizeDataAccessException.class);
        try {
            lobRse.extractData(rset);
        } finally {
            Mockito.verify(rset).next();
        }
    }

    @Test
    public void testAbstractLobStreamingResultSetExtractorOneRow() throws SQLException {
        ResultSet rset = Mockito.mock(ResultSet.class);
        BDDMockito.given(rset.next()).willReturn(true, false);
        AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
        lobRse.extractData(rset);
        Mockito.verify(rset).clearWarnings();
    }

    @Test
    public void testAbstractLobStreamingResultSetExtractorMultipleRows() throws SQLException {
        ResultSet rset = Mockito.mock(ResultSet.class);
        BDDMockito.given(rset.next()).willReturn(true, true, false);
        AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(false);
        thrown.expect(IncorrectResultSizeDataAccessException.class);
        try {
            lobRse.extractData(rset);
        } finally {
            Mockito.verify(rset).clearWarnings();
        }
    }

    @Test
    public void testAbstractLobStreamingResultSetExtractorCorrectException() throws SQLException {
        ResultSet rset = Mockito.mock(ResultSet.class);
        BDDMockito.given(rset.next()).willReturn(true);
        AbstractLobStreamingResultSetExtractor<Void> lobRse = getResultSetExtractor(true);
        thrown.expect(LobRetrievalFailureException.class);
        lobRse.extractData(rset);
    }
}

