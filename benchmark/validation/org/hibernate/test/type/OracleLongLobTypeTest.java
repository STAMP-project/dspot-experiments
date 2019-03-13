/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.sql.Blob;
import java.sql.Clob;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BlobType;
import org.hibernate.type.CharArrayType;
import org.hibernate.type.ClobType;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.type.PrimitiveCharacterArrayClobType;
import org.junit.Test;


/**
 * A test asserting LONG/LONGRAW versus CLOB/BLOB resolution for various Oracle Dialects
 *
 * @author Steve Ebersole
 */
public class OracleLongLobTypeTest extends BaseUnitTestCase {
    @Test
    public void testOracle8() {
        check(Oracle8iDialect.class, OracleLongLobTypeTest.Primitives.class, BinaryType.class, CharArrayType.class);
        check(Oracle8iDialect.class, OracleLongLobTypeTest.LobPrimitives.class, MaterializedBlobType.class, PrimitiveCharacterArrayClobType.class);
        check(Oracle8iDialect.class, OracleLongLobTypeTest.LobLocators.class, BlobType.class, ClobType.class);
    }

    @Test
    public void testOracle9() {
        check(Oracle9iDialect.class, OracleLongLobTypeTest.Primitives.class, BinaryType.class, CharArrayType.class);
        check(Oracle9iDialect.class, OracleLongLobTypeTest.LobPrimitives.class, MaterializedBlobType.class, PrimitiveCharacterArrayClobType.class);
        check(Oracle9iDialect.class, OracleLongLobTypeTest.LobLocators.class, BlobType.class, ClobType.class);
    }

    @Test
    public void testOracle10() {
        check(Oracle10gDialect.class, OracleLongLobTypeTest.Primitives.class, BinaryType.class, CharArrayType.class);
        check(Oracle10gDialect.class, OracleLongLobTypeTest.LobPrimitives.class, MaterializedBlobType.class, PrimitiveCharacterArrayClobType.class);
        check(Oracle10gDialect.class, OracleLongLobTypeTest.LobLocators.class, BlobType.class, ClobType.class);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10345")
    public void testOracle12() {
        check(Oracle12cDialect.class, OracleLongLobTypeTest.Primitives.class, MaterializedBlobType.class, CharArrayType.class);
        check(Oracle12cDialect.class, OracleLongLobTypeTest.LobPrimitives.class, MaterializedBlobType.class, PrimitiveCharacterArrayClobType.class);
        check(Oracle12cDialect.class, OracleLongLobTypeTest.LobLocators.class, BlobType.class, ClobType.class);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10345")
    public void testOracle12PreferLongRaw() {
        check(Oracle12cDialect.class, OracleLongLobTypeTest.Primitives.class, BinaryType.class, CharArrayType.class, true);
        check(Oracle12cDialect.class, OracleLongLobTypeTest.LobPrimitives.class, MaterializedBlobType.class, PrimitiveCharacterArrayClobType.class, true);
        check(Oracle12cDialect.class, OracleLongLobTypeTest.LobLocators.class, BlobType.class, ClobType.class, true);
    }

    @Entity
    public static class Primitives {
        @Id
        public Integer id;

        public byte[] binaryData;

        public char[] characterData;
    }

    @Entity
    public static class LobPrimitives {
        @Id
        public Integer id;

        @Lob
        public byte[] binaryData;

        @Lob
        public char[] characterData;
    }

    @Entity
    public static class LobLocators {
        @Id
        public Integer id;

        @Lob
        public Blob binaryData;

        @Lob
        public Clob characterData;
    }
}

