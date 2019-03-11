/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.annotations.embeddables.attributeOverrides;


import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.BasicType;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicAttributeOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8630")
    public void testIt() {
        final PersistentClass entityBinding = metadata().getEntityBinding(BasicAttributeOverrideTest.AggregatedTypeValue.class.getName());
        final Property attributesBinding = entityBinding.getProperty("attributes");
        final Map attributesMap = ((Map) (attributesBinding.getValue()));
        final SimpleValue mapKey = ExtraAssertions.assertTyping(SimpleValue.class, attributesMap.getIndex());
        final BasicType mapKeyType = ExtraAssertions.assertTyping(BasicType.class, mapKey.getType());
        Assert.assertTrue(String.class.equals(mapKeyType.getReturnedClass()));
        // let's also make sure the @MapKeyColumn got applied
        MatcherAssert.assertThat(mapKey.getColumnSpan(), CoreMatchers.is(1));
        final Column mapKeyColumn = ExtraAssertions.assertTyping(javax.persistence.Column.class, mapKey.getColumnIterator().next());
        MatcherAssert.assertThat(mapKeyColumn.getName(), CoreMatchers.equalTo("attribute_name"));
    }

    @Embeddable
    public static class TypeValue {
        String type;

        @javax.persistence.Column(columnDefinition = "TEXT")
        String value;
    }

    @Entity
    @Table(name = "AGG_TYPE")
    public static class AggregatedTypeValue {
        @Id
        UUID id;

        @Embedded
        @AttributeOverrides({ @AttributeOverride(name = "type", column = @javax.persistence.Column(name = "content_type")), @AttributeOverride(name = "value", column = @javax.persistence.Column(name = "content_value")) })
        BasicAttributeOverrideTest.TypeValue content;

        @CollectionTable(name = "ATTRIBUTES")
        @ElementCollection(fetch = FetchType.EAGER)
        @MapKeyColumn(name = "attribute_name")
        @AttributeOverrides({ @AttributeOverride(name = "value.type", column = @javax.persistence.Column(name = "attribute_type")), @AttributeOverride(name = "value.value", column = @javax.persistence.Column(name = "attribute_value")) })
        java.util.Map<String, BasicAttributeOverrideTest.TypeValue> attributes;
    }
}

