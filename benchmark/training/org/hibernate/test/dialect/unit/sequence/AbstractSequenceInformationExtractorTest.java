/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.unit.sequence;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hibernate.dialect.Dialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public abstract class AbstractSequenceInformationExtractorTest {
    @Test
    public void testSequenceGenerationExtractor() {
        final Dialect dialect = getDialect();
        Assert.assertThat(dialect.getQuerySequencesString(), Is.is(expectedQuerySequencesString()));
        Assert.assertThat(dialect.getSequenceInformationExtractor(), IsInstanceOf.instanceOf(expectedSequenceInformationExtractor()));
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity")
    public static class MyEntity {
        @Id
        public Integer id;
    }
}

