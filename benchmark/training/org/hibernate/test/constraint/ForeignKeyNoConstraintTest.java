/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.constraint;


import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class ForeignKeyNoConstraintTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12975")
    public void testPrimaryKeyJoinColumnForeignKeyNoConstraint() {
        for (Namespace namespace : metadata().getDatabase().getNamespaces()) {
            for (Table table : namespace.getTables()) {
                if ("Car".equals(table.getName())) {
                    Assert.assertEquals(0, table.getForeignKeys().size());
                }
            }
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12975")
    public void testMapsIdJoinColumnForeignKeyNoConstraint() {
        for (Namespace namespace : metadata().getDatabase().getNamespaces()) {
            for (Table table : namespace.getTables()) {
                if ("Post".equals(table.getName())) {
                    Assert.assertEquals(0, table.getForeignKeys().size());
                }
            }
        }
    }

    @Entity(name = "Car")
    public static class Car {
        @Id
        private Integer id;

        @PrimaryKeyJoinColumn
        @OneToOne(optional = false)
        @JoinColumn(name = "V_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        private ForeignKeyNoConstraintTest.VehicleNumber vehicleNumber;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public ForeignKeyNoConstraintTest.VehicleNumber getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(ForeignKeyNoConstraintTest.VehicleNumber vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }
    }

    @Entity(name = "VehicleNumber")
    public static class VehicleNumber {
        @Id
        private Integer id;

        private String value;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Entity(name = "Post")
    public static class Post {
        @Id
        private Integer id;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        @JoinColumn(name = "PD_ID", foreignKey = @ForeignKey(name = "FK_PD", value = ConstraintMode.NO_CONSTRAINT))
        private ForeignKeyNoConstraintTest.PostDetails postDetails;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public ForeignKeyNoConstraintTest.PostDetails getPostDetails() {
            return postDetails;
        }

        public void setPostDetails(ForeignKeyNoConstraintTest.PostDetails postDetails) {
            this.postDetails = postDetails;
        }
    }

    @Entity(name = "PostDetails")
    public static class PostDetails {
        @Id
        private Integer id;

        private String userName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}

