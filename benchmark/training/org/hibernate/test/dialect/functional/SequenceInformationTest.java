/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import DialectChecks.SupportsSequences;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
@RequiresDialectFeature(SupportsSequences.class)
public class SequenceInformationTest extends BaseEntityManagerFunctionalTestCase {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void test() {
        SequenceInformation productSequenceInfo = sequenceInformation("product_sequence");
        Assert.assertNotNull(productSequenceInfo);
        Assert.assertEquals("product_sequence", productSequenceInfo.getSequenceName().getSequenceName().getText().toLowerCase());
        assertProductSequence(productSequenceInfo);
        SequenceInformation vehicleSequenceInfo = sequenceInformation("vehicle_sequence");
        Assert.assertNotNull(vehicleSequenceInfo);
        Assert.assertEquals("vehicle_sequence", vehicleSequenceInfo.getSequenceName().getSequenceName().getText().toLowerCase());
        assertVehicleSequenceInfo(vehicleSequenceInfo);
    }

    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence")
        @SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", initialValue = 1, allocationSize = 10)
        private Long id;

        private String name;
    }

    @Entity(name = "Vehicle")
    public static class Vehicle {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_sequence")
        @SequenceGenerator(name = "vehicle_sequence", sequenceName = "vehicle_sequence", initialValue = 1, allocationSize = 1)
        private Long id;

        private String name;
    }
}

