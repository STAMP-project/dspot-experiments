/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.reventity.trackmodifiedentities;


import org.hibernate.envers.test.entities.reventity.trackmodifiedentities.ExtendedRevisionEntity;
import org.hibernate.envers.test.entities.reventity.trackmodifiedentities.ExtendedRevisionListener;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests proper behavior of revision entity that extends {@link DefaultTrackingModifiedEntitiesRevisionEntity}.
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class ExtendedRevisionEntityTest extends DefaultTrackingEntitiesTest {
    @Test
    public void testCommentPropertyValue() {
        ExtendedRevisionEntity ere = getAuditReader().findRevision(ExtendedRevisionEntity.class, 1);
        Assert.assertEquals(ExtendedRevisionListener.COMMENT_VALUE, ere.getComment());
    }
}

