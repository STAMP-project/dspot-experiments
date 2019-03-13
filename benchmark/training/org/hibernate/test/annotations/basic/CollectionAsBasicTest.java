/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.basic;


import VarcharTypeDescriptor.INSTANCE;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CollectionAsBasicTest extends BaseUnitTestCase {
    @Test
    public void testCollectionAsBasic() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            Metadata metadata = addAnnotatedClass(CollectionAsBasicTest.Post.class).getMetadataBuilder().applyBasicType(new CollectionAsBasicTest.DelimitedStringsType()).build();
            PersistentClass postBinding = metadata.getEntityBinding(CollectionAsBasicTest.Post.class.getName());
            Property tagsAttribute = postBinding.getProperty("tags");
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @Table(name = "post")
    public static class Post {
        @Id
        public Integer id;

        @Basic
        @Type(type = "delimited_strings")
        Set<String> tags;
    }

    public static class DelimitedStringsType extends AbstractSingleColumnStandardBasicType<Set> {
        public DelimitedStringsType() {
            super(INSTANCE, new CollectionAsBasicTest.DelimitedStringsJavaTypeDescriptor());
        }

        @Override
        public String getName() {
            return "delimited_strings";
        }
    }

    public static class DelimitedStringsJavaTypeDescriptor extends AbstractTypeDescriptor<Set> {
        public DelimitedStringsJavaTypeDescriptor() {
            super(Set.class, new org.hibernate.type.descriptor.java.MutableMutabilityPlan<Set>() {
                @Override
                protected Set deepCopyNotNull(Set value) {
                    Set<String> copy = new HashSet<String>();
                    copy.addAll(value);
                    return copy;
                }
            });
        }

        @Override
        public String toString(Set value) {
            return null;
        }

        @Override
        public Set fromString(String string) {
            return null;
        }

        @Override
        public <X> X unwrap(Set value, Class<X> type, WrapperOptions options) {
            return null;
        }

        @Override
        public <X> Set wrap(X value, WrapperOptions options) {
            return null;
        }
    }
}

