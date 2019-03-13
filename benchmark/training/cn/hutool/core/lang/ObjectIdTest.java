package cn.hutool.core.lang;


import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * ObjectId????
 *
 * @author looly
 */
public class ObjectIdTest {
    @Test
    public void distinctTest() {
        // ??10000?id??????
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            set.add(ObjectId.next());
        }
        Assert.assertEquals(10000, set.size());
    }
}

