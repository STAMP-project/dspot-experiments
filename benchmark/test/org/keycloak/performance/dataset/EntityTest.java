package org.keycloak.performance.dataset;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.performance.util.Loggable;


/**
 *
 *
 * @author tkyjovsk
 */
public abstract class EntityTest<T extends Entity> implements Loggable {
    private Dataset d1;

    private Dataset d2;

    @Test
    public void testHashCode() {
        EntityTest.logger().info(((this.getClass().getSimpleName()) + " testHashCode()"));
        Iterator<T> e1i = d1EntityStream().iterator();
        Iterator<T> e2i = d2EntityStream().iterator();
        int checkCount = 0;
        while (e1i.hasNext()) {
            T e1 = e1i.next();
            Assert.assertTrue(e2i.hasNext());
            T e2 = e2i.next();
            // logger().info(String.format("entities: %s %s", e1, e2));
            // logger().info(String.format("hashCodes: %s %s", e1.hashCode(), e2.hashCode()));
            Assert.assertEquals(e1.hashCode(), e1.hashCode());
            Assert.assertEquals(e2.hashCode(), e2.hashCode());
            Assert.assertEquals(e1.hashCode(), e2.hashCode());
            checkCount++;
            // if (checkCount > 10) {
            // break;
            // }
        } 
        EntityTest.logger().info(("checkCount: " + checkCount));
        // assertTrue(checkCount > 0);
    }

    @Test
    public void testEquals() {
        EntityTest.logger().info(((this.getClass().getSimpleName()) + " testEquals()"));
        Iterator<T> e1i = d1EntityStream().iterator();
        Iterator<T> e2i = d2EntityStream().iterator();
        int checkCount = 0;
        while (e1i.hasNext()) {
            T e1 = e1i.next();
            Assert.assertTrue(e2i.hasNext());
            T e2 = e2i.next();
            // logger().info(String.format("entities: %s %s", e1, e2));
            Assert.assertTrue(e1.equals(e2));
            Assert.assertTrue(e2.equals(e1));
            checkCount++;
            // if (checkCount > 10) {
            // break;
            // }
        } 
        EntityTest.logger().info(("checkCount: " + checkCount));
        // assertTrue(checkCount > 0);
    }
}

