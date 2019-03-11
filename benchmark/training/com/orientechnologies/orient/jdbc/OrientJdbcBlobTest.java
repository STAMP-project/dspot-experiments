/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.jdbc;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;


public class OrientJdbcBlobTest extends OrientJdbcDbPerClassTemplateTest {
    private static final String TEST_WORKING_DIR = "./target/working/";

    @Test
    public void shouldStoreBinaryStream() throws Exception {
        OrientJdbcDbPerClassTemplateTest.conn.createStatement().executeQuery("CREATE CLASS Blobs");
        PreparedStatement statement = OrientJdbcDbPerClassTemplateTest.conn.prepareStatement("INSERT INTO Blobs (uuid,attachment) VALUES (?,?)");
        statement.setInt(1, 1);
        statement.setBinaryStream(2, ClassLoader.getSystemResourceAsStream("file.pdf"));
        int rowsInserted = statement.executeUpdate();
        assertThat(rowsInserted).isEqualTo(1);
        // verify the blob
        PreparedStatement stmt = OrientJdbcDbPerClassTemplateTest.conn.prepareStatement("SELECT FROM Blobs WHERE uuid = 1 ");
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next()).isTrue();
        rs.next();
        Blob blob = rs.getBlob("attachment");
        verifyBlobAgainstFile(blob);
    }

    @Test
    public void shouldLoadBlob() throws IOException, NoSuchAlgorithmException, SQLException {
        PreparedStatement stmt = OrientJdbcDbPerClassTemplateTest.conn.prepareStatement("SELECT FROM Article WHERE uuid = 1 ");
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next()).isTrue();
        rs.next();
        Blob blob = rs.getBlob("attachment");
        verifyBlobAgainstFile(blob);
    }

    @Test
    public void shouldLoadChuckedBlob() throws IOException, NoSuchAlgorithmException, SQLException {
        PreparedStatement stmt = OrientJdbcDbPerClassTemplateTest.conn.prepareStatement("SELECT FROM Article WHERE uuid = 2 ");
        ResultSet rs = stmt.executeQuery();
        assertThat(rs.next()).isTrue();
        rs.next();
        Blob blob = rs.getBlob("attachment");
        verifyBlobAgainstFile(blob);
    }
}

