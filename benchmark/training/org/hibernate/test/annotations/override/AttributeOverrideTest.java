/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override;


import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class AttributeOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testMapKeyValue() throws Exception {
        Assert.assertTrue(isColumnPresent("PropertyRecord_parcels", "ASSESSMENT"));
        Assert.assertTrue(isColumnPresent("PropertyRecord_parcels", "SQUARE_FEET"));
        Assert.assertTrue(isColumnPresent("PropertyRecord_parcels", "STREET_NAME"));
        // legacy mappings
        Assert.assertTrue(isColumnPresent("LegacyParcels", "ASSESSMENT"));
        Assert.assertTrue(isColumnPresent("LegacyParcels", "SQUARE_FEET"));
        Assert.assertTrue(isColumnPresent("LegacyParcels", "STREET_NAME"));
    }

    @Test
    public void testElementCollection() throws Exception {
        Assert.assertTrue(isColumnPresent("PropertyRecord_unsortedParcels", "ASSESSMENT"));
        Assert.assertTrue(isColumnPresent("PropertyRecord_unsortedParcels", "SQUARE_FEET"));
        // legacy mappings
        Assert.assertTrue(isColumnPresent("PropertyRecord_legacyUnsortedParcels", "ASSESSMENT"));
        Assert.assertTrue(isColumnPresent("PropertyRecord_legacyUnsortedParcels", "SQUARE_FEET"));
    }
}

