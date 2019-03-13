/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor.INSTANCE;
import org.junit.Test;


public class LongListTypeContributorTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11409")
    public void testParameterRegisterredCollection() {
        LongListTypeContributorTest.LongList longList = new LongListTypeContributorTest.LongList(5L, 11L, 6123L, (-61235L), 24L);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.type.SpecialItem item = new org.hibernate.test.type.SpecialItem("LongList", longList);
            em.persist(item);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.type.SpecialItem item = ((org.hibernate.test.type.SpecialItem) (em.createNativeQuery("SELECT * FROM special_table WHERE longList = ?", .class).setParameter(1, longList).getSingleResult()));
            assertEquals("LongList", item.getName());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.test.type.SpecialItem item = ((org.hibernate.test.type.SpecialItem) (em.createNativeQuery("SELECT * FROM special_table WHERE longList = :longList", .class).setParameter("longList", longList).getSingleResult()));
            assertEquals("LongList", item.getName());
        });
    }

    @Entity(name = "SpecialItem")
    @Table(name = "special_table")
    public static class SpecialItem implements Serializable {
        @Id
        @Column(length = 30)
        private String name;

        @Column(columnDefinition = "VARCHAR(255)")
        private LongListTypeContributorTest.LongList longList;

        public SpecialItem() {
        }

        public SpecialItem(String name, LongListTypeContributorTest.LongList longList) {
            this.name = name;
            this.longList = longList;
        }

        public LongListTypeContributorTest.LongList getLongList() {
            return longList;
        }

        public void setLongList(LongListTypeContributorTest.LongList longList) {
            this.longList = longList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class LongList extends ArrayList<Long> {
        public LongList() {
            super();
        }

        public LongList(int initialCapacity) {
            super(initialCapacity);
        }

        public LongList(Long... longs) {
            super(longs.length);
            for (Long l : longs) {
                this.add(l);
            }
        }
    }

    public static class StringifiedCollectionTypeContributor implements TypeContributor {
        @Override
        public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
            // JavaTypeDescriptorRegistry.INSTANCE.addDescriptor( StringifiedCollectionJavaTypeDescriptor.INSTANCE );
            typeContributions.contributeType(LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionType.INSTANCE);
        }

        private static class StringifiedCollectionType extends AbstractSingleColumnStandardBasicType<LongListTypeContributorTest.LongList> {
            private final String[] regKeys;

            private final String name;

            public static final LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionType INSTANCE = new LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionType();

            public StringifiedCollectionType() {
                super(org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor.INSTANCE, LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionJavaTypeDescriptor.INSTANCE);
                regKeys = new String[]{ LongListTypeContributorTest.LongList.class.getName() };
                name = "StringifiedCollection";
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String[] getRegistrationKeys() {
                return regKeys.clone();
            }

            @Override
            protected boolean registerUnderJavaType() {
                return true;
            }
        }

        private static class StringifiedCollectionJavaTypeDescriptor extends AbstractTypeDescriptor<LongListTypeContributorTest.LongList> {
            public static LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionJavaTypeDescriptor INSTANCE = new LongListTypeContributorTest.StringifiedCollectionTypeContributor.StringifiedCollectionJavaTypeDescriptor();

            public StringifiedCollectionJavaTypeDescriptor() {
                super(LongListTypeContributorTest.LongList.class);
            }

            @Override
            public String toString(LongListTypeContributorTest.LongList value) {
                if (value == null) {
                    return "null";
                }
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                String glue = "";
                for (Long v : value) {
                    sb.append(glue).append(v);
                    glue = ",";
                }
                sb.append(']');
                return sb.toString();
            }

            @Override
            public LongListTypeContributorTest.LongList fromString(String string) {
                if ((string == null) || ("null".equals(string))) {
                    return null;
                }
                if ((string.length()) <= 2) {
                    return new LongListTypeContributorTest.LongList();
                }
                String[] parts = string.substring(1, ((string.length()) - 1)).split(",");
                LongListTypeContributorTest.LongList results = new LongListTypeContributorTest.LongList(parts.length);
                for (String part : parts) {
                    results.add(Long.valueOf(part));
                }
                return results;
            }

            @Override
            public <X> X unwrap(LongListTypeContributorTest.LongList value, Class<X> type, WrapperOptions options) {
                if (value == null) {
                    return null;
                }
                if (String.class.isAssignableFrom(type)) {
                    return ((X) (this.toString(value)));
                }
                throw unknownUnwrap(type);
            }

            @Override
            public <X> LongListTypeContributorTest.LongList wrap(X value, WrapperOptions options) {
                if (value == null) {
                    return null;
                }
                Class type = value.getClass();
                if (String.class.isAssignableFrom(type)) {
                    String s = ((String) (value));
                    return this.fromString(s);
                }
                throw unknownWrap(type);
            }
        }
    }
}

