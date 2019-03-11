/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.manytoone;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11463")
public class EmbeddedIdManyToOneForeignKeyTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    public void testJoinTableForeignKeyToNonAuditTables() {
        // there should only be references to REVINFO and not to the Customer or Address tables
        for (Table table : metadata().getDatabase().getDefaultNamespace().getTables()) {
            if (table.getName().equals("CustomerAddress_AUD")) {
                for (ForeignKey foreignKey : table.getForeignKeys().values()) {
                    Assert.assertEquals("REVINFO", foreignKey.getReferencedTable().getName());
                }
            }
        }
    }

    @Audited
    @Entity(name = "Customer")
    public static class Customer {
        @Id
        private Integer id;

        @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
        @OneToMany
        @JoinTable(name = "CustomerAddress")
        @AuditJoinTable(name = "CustomerAddress_AUD")
        @JoinColumn(name = "customerId", foreignKey = @javax.persistence.ForeignKey(name = "FK_CUSTOMER_ADDRESS"))
        private List<EmbeddedIdManyToOneForeignKeyTest.CustomerAddress> addresses = new ArrayList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<EmbeddedIdManyToOneForeignKeyTest.CustomerAddress> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<EmbeddedIdManyToOneForeignKeyTest.CustomerAddress> addresses) {
            this.addresses = addresses;
        }
    }

    @Audited
    @Entity(name = "Address")
    public static class Address {
        @Id
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Embeddable
    public static class CustomerAddressId implements Serializable {
        @ManyToOne
        private EmbeddedIdManyToOneForeignKeyTest.Address address;

        @ManyToOne
        private EmbeddedIdManyToOneForeignKeyTest.Customer customer;

        public EmbeddedIdManyToOneForeignKeyTest.Address getAddress() {
            return address;
        }

        public void setAddress(EmbeddedIdManyToOneForeignKeyTest.Address address) {
            this.address = address;
        }

        public EmbeddedIdManyToOneForeignKeyTest.Customer getCustomer() {
            return customer;
        }

        public void setCustomer(EmbeddedIdManyToOneForeignKeyTest.Customer customer) {
            this.customer = customer;
        }
    }

    @Audited
    @Entity(name = "CustomerAddress")
    public static class CustomerAddress {
        @EmbeddedId
        private EmbeddedIdManyToOneForeignKeyTest.CustomerAddressId id;
    }
}

