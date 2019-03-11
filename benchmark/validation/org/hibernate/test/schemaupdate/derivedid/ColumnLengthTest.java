/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.derivedid;


import TargetType.SCRIPT;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialect(H2Dialect.class)
public class ColumnLengthTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private File outputFile;

    private MetadataImplementor metadata;

    @Test
    public void testTheColumnsLenghtAreApplied() throws Exception {
        new SchemaExport().setOutputFile(outputFile.getAbsolutePath()).setDelimiter(";").setFormat(false).createOnly(EnumSet.of(SCRIPT), metadata);
        List<String> commands = Files.readAllLines(outputFile.toPath());
        Assert.assertTrue(checkCommandIsGenerated(commands, "create table DEPENDENT (name varchar(255) not null, FK1 varchar(32) not null, FK2 varchar(10) not null, primary key (FK1, FK2, name));"));
    }

    @Embeddable
    public class EmployeeId implements Serializable {
        @Column(name = "first_name", length = 32)
        String firstName;

        @Column(name = "last_name", length = 10)
        String lastName;
    }

    @Entity
    @Table(name = "EMLOYEE")
    public static class Employee {
        @EmbeddedId
        ColumnLengthTest.EmployeeId id;
    }

    @Embeddable
    public class DependentId implements Serializable {
        String name;

        ColumnLengthTest.EmployeeId empPK;
    }

    @Entity
    @Table(name = "DEPENDENT")
    public static class Dependent {
        @EmbeddedId
        ColumnLengthTest.DependentId id;

        @MapsId("empPK")
        @JoinColumns({ @JoinColumn(name = "FK1", referencedColumnName = "first_name"), @JoinColumn(name = "FK2", referencedColumnName = "last_name") })
        @ManyToOne
        ColumnLengthTest.Employee emp;
    }
}

