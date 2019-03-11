package cc.blynk.utils.structure;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.09.16.
 */
public class LimitedArrayDequeTest {
    @Test
    public void testDeque() {
        LimitedArrayDeque<String> limitedArrayDeque = new LimitedArrayDeque(4);
        limitedArrayDeque.add("1");
        limitedArrayDeque.add("2");
        limitedArrayDeque.add("3");
        limitedArrayDeque.add("4");
        limitedArrayDeque.add("5");
        Assert.assertEquals(4, limitedArrayDeque.size());
        int i = 2;
        for (String s : limitedArrayDeque) {
            Assert.assertEquals(("" + (i++)), s);
        }
    }
}

