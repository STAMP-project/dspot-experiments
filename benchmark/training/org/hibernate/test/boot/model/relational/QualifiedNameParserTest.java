/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.model.relational;


import QualifiedNameParser.NameParts;
import org.hamcrest.CoreMatchers;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10174")
public class QualifiedNameParserTest {
    private static final Identifier DEFAULT_SCHEMA = Identifier.toIdentifier("schema");

    private static final Identifier DEFAULT_CATALOG = Identifier.toIdentifier("catalog");

    private static final QualifiedNameParser PARSER = new QualifiedNameParser();

    @Test
    public void testStringSplittingWithSchema() {
        QualifiedNameParser.NameParts nameParts = QualifiedNameParserTest.PARSER.parse("schema.MyEntity", null, QualifiedNameParserTest.DEFAULT_SCHEMA);
        Assert.assertThat(nameParts.getCatalogName(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(nameParts.getSchemaName().getText(), CoreMatchers.is(QualifiedNameParserTest.DEFAULT_SCHEMA.getText()));
        Assert.assertThat(nameParts.getObjectName().getText(), CoreMatchers.is("MyEntity"));
    }

    @Test
    public void testStringSplittingWithCatalogAndSchema() {
        QualifiedNameParser.NameParts nameParts = QualifiedNameParserTest.PARSER.parse("schema.catalog.MyEntity", QualifiedNameParserTest.DEFAULT_CATALOG, QualifiedNameParserTest.DEFAULT_SCHEMA);
        Assert.assertThat(nameParts.getCatalogName().getText(), CoreMatchers.is(QualifiedNameParserTest.DEFAULT_CATALOG.getText()));
        Assert.assertThat(nameParts.getSchemaName().getText(), CoreMatchers.is(QualifiedNameParserTest.DEFAULT_SCHEMA.getText()));
        Assert.assertThat(nameParts.getObjectName().getText(), CoreMatchers.is("MyEntity"));
    }

    @Test
    public void testStringSplittingWithoutCatalogAndSchema() {
        QualifiedNameParser.NameParts nameParts = QualifiedNameParserTest.PARSER.parse("MyEntity", null, null);
        Assert.assertThat(nameParts.getCatalogName(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(nameParts.getSchemaName(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(nameParts.getObjectName().getText(), CoreMatchers.is("MyEntity"));
    }
}

