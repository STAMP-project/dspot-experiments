/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetomany;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * What is done:
 *    ___                   ___
 *   |   |                 |   |
 *    -> 1                  -> 1
 *       |   -transform->     / \
 *       2                   2   3
 *       |
 *     	 3
 *
 * @author Burkhard Graves
 * @author Gail Badner
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class AbstractRecursiveBidirectionalOneToManyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToManyMoveElement() {
        init();
        transformMove();
        check(false);
        delete();
    }

    @Test
    public void testOneToManyMoveElementWithDirtySimpleProperty() {
        init();
        transformMoveWithDirtySimpleProperty();
        check(true);
        delete();
    }

    @Test
    public void testOneToManyReplaceList() {
        init();
        transformReplace();
        check(false);
        delete();
    }
}

