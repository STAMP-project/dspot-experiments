/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.legacy;


import java.io.Serializable;
import org.hibernate.HibernateException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple testcase to illustrate HB-992
 *
 * @author Wolfgang Voelkl, michael
 */
public class OneToOneCacheTest extends LegacyTestCase {
    private Serializable generatedId;

    @Test
    public void testOneToOneCache() throws HibernateException {
        // create a new MainObject
        createMainObject();
        // load the MainObject
        readMainObject();
        // create and add Ojbect2
        addObject2();
        // here the newly created Object2 is written to the database
        // but the MainObject does not know it yet
        MainObject mainObject = readMainObject();
        Assert.assertNotNull(mainObject.getObj2());
        // after evicting, it works.
        sessionFactory().getCache().evictEntityRegion(MainObject.class);
        mainObject = readMainObject();
        Assert.assertNotNull(mainObject.getObj2());
    }
}

