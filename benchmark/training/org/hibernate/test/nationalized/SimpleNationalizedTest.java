/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.nationalized;


import StringType.INSTANCE;
import java.sql.NClob;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.Type;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SimpleNationalizedTest extends BaseUnitTestCase {
    @SuppressWarnings({ "UnusedDeclaration", "SpellCheckingInspection" })
    @Entity(name = "NationalizedEntity")
    public static class NationalizedEntity {
        @Id
        private Integer id;

        @Nationalized
        private String nvarcharAtt;

        @Lob
        @Nationalized
        private String materializedNclobAtt;

        @Lob
        @Nationalized
        private NClob nclobAtt;

        @Nationalized
        private Character ncharacterAtt;

        @Nationalized
        private Character[] ncharArrAtt;

        @Type(type = "ntext")
        private String nlongvarcharcharAtt;
    }

    @Test
    public void simpleNationalizedTest() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataSources ms = new MetadataSources(ssr);
            ms.addAnnotatedClass(SimpleNationalizedTest.NationalizedEntity.class);
            final Metadata metadata = ms.buildMetadata();
            PersistentClass pc = metadata.getEntityBinding(SimpleNationalizedTest.NationalizedEntity.class.getName());
            Assert.assertNotNull(pc);
            Property prop = pc.getProperty("nvarcharAtt");
            if ((metadata.getDatabase().getDialect()) instanceof PostgreSQL81Dialect) {
                // See issue HHH-10693
                Assert.assertSame(INSTANCE, prop.getType());
            } else {
                Assert.assertSame(StringNVarcharType.INSTANCE, prop.getType());
            }
            prop = pc.getProperty("materializedNclobAtt");
            if ((metadata.getDatabase().getDialect()) instanceof PostgreSQL81Dialect) {
                // See issue HHH-10693
                Assert.assertSame(MaterializedClobType.INSTANCE, prop.getType());
            } else {
                Assert.assertSame(MaterializedNClobType.INSTANCE, prop.getType());
            }
            prop = pc.getProperty("nclobAtt");
            Assert.assertSame(NClobType.INSTANCE, prop.getType());
            prop = pc.getProperty("nlongvarcharcharAtt");
            Assert.assertSame(NTextType.INSTANCE, prop.getType());
            prop = pc.getProperty("ncharArrAtt");
            if ((metadata.getDatabase().getDialect()) instanceof PostgreSQL81Dialect) {
                // See issue HHH-10693
                Assert.assertSame(CharacterArrayType.INSTANCE, prop.getType());
            } else {
                Assert.assertSame(StringNVarcharType.INSTANCE, prop.getType());
            }
            prop = pc.getProperty("ncharacterAtt");
            if ((metadata.getDatabase().getDialect()) instanceof PostgreSQL81Dialect) {
                // See issue HHH-10693
                Assert.assertSame(CharacterType.INSTANCE, prop.getType());
            } else {
                Assert.assertSame(CharacterNCharType.INSTANCE, prop.getType());
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

