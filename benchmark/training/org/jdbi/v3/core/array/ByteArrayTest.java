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
package org.jdbi.v3.core.array;


import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.rule.DatabaseRule;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ByteArrayTest {
    @Rule
    public DatabaseRule db = new H2DatabaseRule();

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private PreparedStatement stmt;

    @Test
    public void byteArrayIsTypedAsVarbinary() throws SQLException {
        Argument nullByteArrayArg = db.getJdbi().withHandle(( h) -> h.getConfig(.class).findFor(.class, new byte[]{ 1 })).get();
        nullByteArrayArg.apply(0, stmt, null);
        Mockito.verify(stmt, Mockito.never()).setArray(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Array.class));
        Mockito.verify(stmt).setBytes(ArgumentMatchers.anyInt(), ArgumentMatchers.any(byte[].class));
    }

    @Test
    public void nullByteArrayIsTypedAsVarbinary() throws SQLException {
        Argument nullByteArrayArg = db.getJdbi().withHandle(( h) -> h.getConfig(.class).findFor(.class, null)).get();
        nullByteArrayArg.apply(0, stmt, null);
        Mockito.verify(stmt, Mockito.never()).setNull(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Types.ARRAY));
        Mockito.verify(stmt).setNull(ArgumentMatchers.anyInt(), ArgumentMatchers.eq(Types.VARBINARY));
    }
}

