/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ast;


import antlr.ASTFactory;
import antlr.collections.AST;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for ASTUtil.
 */
public class ASTUtilTest extends BaseUnitTestCase {
    private ASTFactory factory = new ASTFactory();

    @Test
    public void testCreate() throws Exception {
        AST n = ASTUtil.create(factory, 1, "one");
        Assert.assertNull(n.getFirstChild());
        Assert.assertEquals("one", n.getText());
        Assert.assertEquals(1, n.getType());
    }

    @Test
    public void testCreateTree() throws Exception {
        AST[] tree = new AST[4];
        AST grandparent = tree[0] = ASTUtil.create(factory, 1, "grandparent");
        AST parent = tree[1] = ASTUtil.create(factory, 2, "parent");
        AST child = tree[2] = ASTUtil.create(factory, 3, "child");
        AST baby = tree[3] = ASTUtil.create(factory, 4, "baby");
        AST t = ASTUtil.createTree(factory, tree);
        Assert.assertSame(t, grandparent);
        Assert.assertSame(parent, t.getFirstChild());
        Assert.assertSame(child, t.getFirstChild().getFirstChild());
        Assert.assertSame(baby, t.getFirstChild().getFirstChild().getFirstChild());
    }

    @Test
    public void testFindPreviousSibling() throws Exception {
        AST child1 = ASTUtil.create(factory, 2, "child1");
        AST child2 = ASTUtil.create(factory, 3, "child2");
        AST n = factory.make(new AST[]{ ASTUtil.create(factory, 1, "parent"), child1, child2 });
        Assert.assertSame(child1, ASTUtil.findPreviousSibling(n, child2));
        Exception e = null;
        try {
            ASTUtil.findPreviousSibling(child1, null);
        } catch (Exception x) {
            e = x;
        }
        Assert.assertNotNull(e);
    }
}

