/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.org.hibernate.Query;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.usertype.UserType;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TypedValueParametersTest extends BaseEntityManagerFunctionalTestCase {
    private int docId;

    @Test
    public void testNative() {
        test(new TypedValueParametersTest.Binder() {
            public void bind(Query q) {
                org.hibernate.Query hibernateQuery = q.unwrap(Query.class);
                hibernateQuery.setParameter("tags", Arrays.asList("important", "business"), new org.hibernate.type.CustomType(TypedValueParametersTest.TagUserType.INSTANCE));
            }
        });
    }

    @Test
    public void testJpa() {
        test(new TypedValueParametersTest.Binder() {
            public void bind(Query q) {
                q.setParameter("tags", new TypedParameterValue(new org.hibernate.type.CustomType(TypedValueParametersTest.TagUserType.INSTANCE), Arrays.asList("important", "business")));
            }
        });
    }

    private interface Binder {
        void bind(Query q);
    }

    @Entity(name = "Document")
    @Table(name = "Document")
    @TypeDef(name = "tagList", typeClass = TypedValueParametersTest.TagUserType.class)
    public static class Document {
        @Id
        private int id;

        @Type(type = "tagList")
        @Column(name = "tags")
        private List<String> tags = new ArrayList<String>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    public static class TagUserType implements UserType {
        public static final UserType INSTANCE = new TypedValueParametersTest.TagUserType();

        private final int SQLTYPE = Types.VARCHAR;

        @Override
        public void nullSafeSet(PreparedStatement statement, Object value, int index, SharedSessionContractImplementor session) throws SQLException, HibernateException {
            if (value == null) {
                statement.setNull(index, SQLTYPE);
            } else {
                @SuppressWarnings("unchecked")
                List<String> list = ((List<String>) (value));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < (list.size()); i++) {
                    if (i != 0) {
                        sb.append('|');
                    }
                    sb.append(list.get(i));
                }
                statement.setString(index, sb.toString());
            }
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException, HibernateException {
            String string = rs.getString(names[0]);
            if (rs.wasNull()) {
                return null;
            }
            List<String> list = new ArrayList<String>();
            int lastIndex = 0;
            int index;
            while ((index = string.indexOf('|', lastIndex)) != (-1)) {
                list.add(string.substring(lastIndex, index));
                lastIndex = index + 1;
            } 
            if (lastIndex != (string.length())) {
                list.add(string.substring(lastIndex));
            }
            return list;
        }

        public int[] sqlTypes() {
            return new int[]{ SQLTYPE };
        }

        public Class returnedClass() {
            return List.class;
        }

        @Override
        public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
            return cached;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object deepCopy(final Object o) throws HibernateException {
            return o == null ? null : new ArrayList<String>(((List<String>) (o)));
        }

        @Override
        public Serializable disassemble(final Object o) throws HibernateException {
            return ((Serializable) (o));
        }

        @Override
        public boolean equals(final Object x, final Object y) throws HibernateException {
            return x == null ? y == null : x.equals(y);
        }

        @Override
        public int hashCode(final Object o) throws HibernateException {
            return o == null ? 0 : o.hashCode();
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
            return original;
        }
    }
}

