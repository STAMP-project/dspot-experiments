/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.manytomany.defaults;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Tests names generated for @JoinTable and @JoinColumn for unidirectional and bidirectional
 * many-to-many associations using the "legacy JPA" naming strategy, which does not comply
 * with JPA spec in all cases.  See HHH-9390 for more information.
 *
 * NOTE: expected primary table names and join columns are explicit here to ensure that
 * entity names/tables and PK columns are not changed (which would invalidate these test cases).
 *
 * @author Gail Badner
 */
public class ManyToManyImplicitNamingTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBidirNoOverrides() {
        // Employee.contactInfo.phoneNumbers: associated entity: PhoneNumber
        // both have @Entity with no name configured and default primary table names;
        // Primary table names default to unqualified entity classes.
        // PK column for Employee.id: id (default)
        // PK column for PhoneNumber.phNumber: phNumber (default)
        // bidirectional association
        checkDefaultJoinTablAndJoinColumnNames(Employee.class, "contactInfo.phoneNumbers", "employees", "Employee_PhoneNumber", "employees_id", "phoneNumbers_phNumber");
    }

    @Test
    public void testBidirOwnerPKOverride() {
        // Store.customers; associated entity: KnownClient
        // both have @Entity with no name configured and default primary table names
        // Primary table names default to unqualified entity classes.
        // PK column for Store.id: sId
        // PK column for KnownClient.id: id (default)
        // bidirectional association
        checkDefaultJoinTablAndJoinColumnNames(Store.class, "customers", "stores", "Store_KnownClient", "stores_sId", "customers_id");
    }

    @Test
    public void testUnidirOwnerPKAssocEntityNamePKOverride() {
        // Store.items; associated entity: Item
        // Store has @Entity with no name configured and no @Table
        // Item has @Entity(name="ITEM") and no @Table
        // PK column for Store.id: sId
        // PK column for Item: iId
        // unidirectional
        checkDefaultJoinTablAndJoinColumnNames(Store.class, "items", null, "Store_ITEM", "Store_sId", "items_iId");
    }

    @Test
    public void testUnidirOwnerPKAssocPrimaryTableNameOverride() {
        // Store.implantedIn; associated entity: City
        // Store has @Entity with no name configured and no @Table
        // City has @Entity with no name configured and @Table(name = "tbl_city")
        // PK column for Store.id: sId
        // PK column for City.id: id (default)
        // unidirectional
        checkDefaultJoinTablAndJoinColumnNames(Store.class, "implantedIn", null, "Store_tbl_city", "Store_sId", "implantedIn_id");
    }

    @Test
    public void testUnidirOwnerPKAssocEntityNamePrimaryTableOverride() {
        // Store.categories; associated entity: Category
        // Store has @Entity with no name configured and no @Table
        // Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
        // PK column for Store.id: sId
        // PK column for Category.id: id (default)
        // unidirectional
        checkDefaultJoinTablAndJoinColumnNames(Store.class, "categories", null, "Store_CATEGORY_TAB", "Store_sId", "categories_id");
    }

    @Test
    public void testUnidirOwnerEntityNamePKAssocPrimaryTableOverride() {
        // Item.producedInCities: associated entity: City
        // Item has @Entity(name="ITEM") and no @Table
        // City has @Entity with no name configured and @Table(name = "tbl_city")
        // PK column for Item: iId
        // PK column for City.id: id (default)
        // unidirectional
        checkDefaultJoinTablAndJoinColumnNames(Item.class, "producedInCities", null, "ITEM_tbl_city", "ITEM_iId", "producedInCities_id");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9390")
    public void testUnidirOwnerEntityNamePrimaryTableOverride() {
        // Category.clients: associated entity: KnownClient
        // Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
        // KnownClient has @Entity with no name configured and no @Table
        // PK column for Category.id: id (default)
        // PK column for KnownClient.id: id (default)
        // unidirectional
        // legacy behavior would use the table name in the generated join column.
        checkDefaultJoinTablAndJoinColumnNames(Category.class, "clients", null, "CATEGORY_TAB_KnownClient", "CATEGORY_TAB_id", "clients_id");
    }
}

