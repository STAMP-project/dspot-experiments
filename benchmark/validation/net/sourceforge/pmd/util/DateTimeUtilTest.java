/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;


import java.util.Collection;
import net.sourceforge.pmd.ReadableDurationTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brian Remedios
 */
public class DateTimeUtilTest {
    @Test
    public void testConversions() {
        Collection<Object[]> stringNumberPairs = ReadableDurationTest.data();
        for (Object[] stringAndNumber : stringNumberPairs) {
            String result = ((String) (stringAndNumber[0]));
            Integer milliseconds = ((Integer) (stringAndNumber[1]));
            Assert.assertEquals(result, DateTimeUtil.asHoursMinutesSeconds(milliseconds));
        }
    }
}

