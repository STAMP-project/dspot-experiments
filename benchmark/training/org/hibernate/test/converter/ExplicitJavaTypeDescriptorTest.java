/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Cacheable;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.hibernate.annotations.Immutable;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ExplicitJavaTypeDescriptorTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11098")
    public void testIt() {
        // set up test data
        Session session = openSession();
        getTypeConfiguration().getJavaTypeDescriptorRegistry().addDescriptor(new JavaTypeDescriptorRegistry.FallbackJavaTypeDescriptor(ExplicitJavaTypeDescriptorTest.MutableState2.class) {
            @Override
            public MutabilityPlan getMutabilityPlan() {
                return ImmutableMutabilityPlan.INSTANCE;
            }
        });
        session.beginTransaction();
        session.persist(new ExplicitJavaTypeDescriptorTest.TheEntity(1));
        session.getTransaction().commit();
        session.close();
        // assertions based on the persist call
        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.mutableToDomainCallCount, CoreMatchers.is(1));
        // 1 instead of 0 because of the deep copy call
        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.mutableToDatabaseCallCount, CoreMatchers.is(2));
        // 2 instead of 1 because of the deep copy call
        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.immutableToDomainCallCount, CoreMatchers.is(0));// logical

        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.immutableToDatabaseCallCount, CoreMatchers.is(1));// logical

        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.immutableMutableToDomainCallCount, CoreMatchers.is(0));// was 1 (like mutable) before the JavaTypeDescriptor registration

        MatcherAssert.assertThat(ExplicitJavaTypeDescriptorTest.immutableMutableToDatabaseCallCount, CoreMatchers.is(1));// was 2 (like mutable) before the JavaTypeDescriptor registration

        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.delete(session.byId(ExplicitJavaTypeDescriptorTest.TheEntity.class).getReference(1));
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "T_ENTITY")
    @Cacheable
    public static class TheEntity {
        @Id
        private Integer id;

        @Convert(converter = ExplicitJavaTypeDescriptorTest.MutableConverterImpl.class)
        private ExplicitJavaTypeDescriptorTest.MutableState mutableState;

        @Convert(converter = ExplicitJavaTypeDescriptorTest.ImmutableConverterImpl.class)
        private ExplicitJavaTypeDescriptorTest.ImmutableState immutableState;

        @Convert(converter = ExplicitJavaTypeDescriptorTest.ImmutableMutable2ConverterImpl.class)
        private ExplicitJavaTypeDescriptorTest.MutableState2 immutableMutableState;

        public TheEntity() {
        }

        public TheEntity(Integer id) {
            this.id = id;
            this.mutableState = new ExplicitJavaTypeDescriptorTest.MutableState(id.toString());
            this.immutableState = new ExplicitJavaTypeDescriptorTest.ImmutableState(id.toString());
            this.immutableMutableState = new ExplicitJavaTypeDescriptorTest.MutableState2(id.toString());
        }
    }

    private static int mutableToDatabaseCallCount;

    private static int mutableToDomainCallCount;

    private static int immutableToDatabaseCallCount;

    private static int immutableToDomainCallCount;

    private static int immutableMutableToDatabaseCallCount;

    private static int immutableMutableToDomainCallCount;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Purely mutable state
    public static class MutableState {
        private String state;

        public MutableState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        // mutable
        public void setState(String state) {
            this.state = state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ExplicitJavaTypeDescriptorTest.MutableState that = ((ExplicitJavaTypeDescriptorTest.MutableState) (o));
            return (getState()) != null ? getState().equals(that.getState()) : (that.getState()) == null;
        }

        @Override
        public int hashCode() {
            return (getState()) != null ? getState().hashCode() : 0;
        }
    }

    @Converter
    public static class MutableConverterImpl implements AttributeConverter<ExplicitJavaTypeDescriptorTest.MutableState, String> {
        @Override
        public String convertToDatabaseColumn(ExplicitJavaTypeDescriptorTest.MutableState attribute) {
            (ExplicitJavaTypeDescriptorTest.mutableToDatabaseCallCount)++;
            return attribute == null ? null : attribute.getState();
        }

        @Override
        public ExplicitJavaTypeDescriptorTest.MutableState convertToEntityAttribute(String dbData) {
            (ExplicitJavaTypeDescriptorTest.mutableToDomainCallCount)++;
            return new ExplicitJavaTypeDescriptorTest.MutableState(dbData);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Purely immutable state
    @Immutable
    public static class ImmutableState {
        private final String state;

        public ImmutableState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ExplicitJavaTypeDescriptorTest.ImmutableState that = ((ExplicitJavaTypeDescriptorTest.ImmutableState) (o));
            return getState().equals(that.getState());
        }

        @Override
        public int hashCode() {
            return getState().hashCode();
        }
    }

    @Converter
    public static class ImmutableConverterImpl implements AttributeConverter<ExplicitJavaTypeDescriptorTest.ImmutableState, String> {
        @Override
        public String convertToDatabaseColumn(ExplicitJavaTypeDescriptorTest.ImmutableState attribute) {
            (ExplicitJavaTypeDescriptorTest.immutableToDatabaseCallCount)++;
            return attribute == null ? null : attribute.getState();
        }

        @Override
        public ExplicitJavaTypeDescriptorTest.ImmutableState convertToEntityAttribute(String dbData) {
            (ExplicitJavaTypeDescriptorTest.immutableToDomainCallCount)++;
            return new ExplicitJavaTypeDescriptorTest.ImmutableState(dbData);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Mutable state we treat as immutable
    public static class MutableState2 {
        private String state;

        public MutableState2(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        // mutable
        public void setState(String state) {
            // just a safety net - the idea is that the user is promising to not mutate the internal state
            throw new UnsupportedOperationException("illegal attempt to mutate state");
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ExplicitJavaTypeDescriptorTest.MutableState2 that = ((ExplicitJavaTypeDescriptorTest.MutableState2) (o));
            return (getState()) != null ? getState().equals(that.getState()) : (that.getState()) == null;
        }

        @Override
        public int hashCode() {
            return (getState()) != null ? getState().hashCode() : 0;
        }
    }

    @Converter
    public static class ImmutableMutable2ConverterImpl implements AttributeConverter<ExplicitJavaTypeDescriptorTest.MutableState2, String> {
        @Override
        public String convertToDatabaseColumn(ExplicitJavaTypeDescriptorTest.MutableState2 attribute) {
            (ExplicitJavaTypeDescriptorTest.immutableMutableToDatabaseCallCount)++;
            return attribute == null ? null : attribute.getState();
        }

        @Override
        public ExplicitJavaTypeDescriptorTest.MutableState2 convertToEntityAttribute(String dbData) {
            (ExplicitJavaTypeDescriptorTest.immutableMutableToDomainCallCount)++;
            return new ExplicitJavaTypeDescriptorTest.MutableState2(dbData);
        }
    }
}

