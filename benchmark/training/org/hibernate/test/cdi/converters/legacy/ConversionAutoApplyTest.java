/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.cdi.converters.legacy;


import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test AttributeConverter functioning in case where entity type is derived from jdbc type.
 *
 * @author Karthik Abram
 */
@TestForIssue(jiraKey = "HHH-10549")
public class ConversionAutoApplyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testConverterIsNotIncorrectlyApplied() {
        ConversionAutoApplyTest.Widget w = new ConversionAutoApplyTest.Widget();
        w.setId(1);
        w.setDimension(new BigDecimal("1.0"));
        w.setCost(new ConversionAutoApplyTest.Money("2.0"));
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(w);
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        ConversionAutoApplyTest.Widget recorded = em.find(ConversionAutoApplyTest.Widget.class, 1);
        Assert.assertEquals(1, recorded.getId());
        em.remove(recorded);
        em.getTransaction().commit();
        em.close();
    }

    public static class Money extends BigDecimal {
        public Money(String value) {
            super(value);
        }

        public Money(BigDecimal value) {
            super(value.toString());
        }

        @Override
        public BigDecimal add(BigDecimal augend) {
            return new ConversionAutoApplyTest.Money(this.add(augend).setScale(10, RoundingMode.HALF_EVEN));
        }
    }

    @Converter(autoApply = true)
    public static class MoneyConverter implements AttributeConverter<ConversionAutoApplyTest.Money, BigDecimal> {
        @Override
        public BigDecimal convertToDatabaseColumn(ConversionAutoApplyTest.Money attribute) {
            return attribute == null ? null : new BigDecimal(attribute.toString());
        }

        @Override
        public ConversionAutoApplyTest.Money convertToEntityAttribute(BigDecimal dbData) {
            return dbData == null ? null : new ConversionAutoApplyTest.Money(dbData.toString());
        }
    }

    @Entity
    @Table(name = "Widget")
    public static class Widget {
        private int id;

        private BigDecimal dimension;

        private ConversionAutoApplyTest.Money cost;

        @Id
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public BigDecimal getDimension() {
            return dimension;
        }

        public void setDimension(BigDecimal dimension) {
            this.dimension = dimension;
        }

        @Column(name = "cost")
        public ConversionAutoApplyTest.Money getCost() {
            return cost;
        }

        public void setCost(ConversionAutoApplyTest.Money cost) {
            this.cost = cost;
        }
    }
}

