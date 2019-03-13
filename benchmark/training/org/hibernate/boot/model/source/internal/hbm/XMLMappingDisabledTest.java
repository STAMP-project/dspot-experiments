/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.model.source.internal.hbm;


import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Verifies that setting org.hibernate.cfg.AvailableSettings#XML_MAPPING_ENABLED to
 * false actually ignores the mapping files.
 */
public class XMLMappingDisabledTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void xmlMappedEntityIsIgnored() throws Exception {
        // If this booted we're good: the XML mapping used in this test is invalid.
    }
}

