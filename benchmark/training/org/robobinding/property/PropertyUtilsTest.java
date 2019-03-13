package org.robobinding.property;


import com.google.common.collect.Sets;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class PropertyUtilsTest {
    @Test
    public void whenGetPropertyNames_thenReturnExpectedPropertyNames() {
        Set<String> propertyNames = PropertyUtils.getPropertyNames(PropertyUtilsTest.Bean.class);
        Assert.assertEquals(Sets.newHashSet(PropertyUtilsTest.Bean.PROPERTY1, PropertyUtilsTest.Bean.PROPERTY2), propertyNames);
    }

    @Test
    public void whenGetPropertyNames_thenReturnZero() {
        Set<String> propertyNames = PropertyUtils.getPropertyNames(PropertyUtilsTest.MalformedBean.class);
        Assert.assertThat(propertyNames.size(), Matchers.is(0));
    }

    public static class Bean {
        public static final String PROPERTY1 = "property1";

        public static final String PROPERTY2 = "property2";

        public Bean() {
        }

        public String getProperty1() {
            return "property1";
        }

        public int getProperty2() {
            return 0;
        }

        public double nonProperty() {
            return 0.0;
        }

        public boolean getNonPropertyWithParameter(String p1) {
            return true;
        }
    }

    public static class MalformedBean {
        public void setMalformedSetter1() {
        }

        public void setMalformedSetter2(Object o1, Object o2) {
        }
    }
}

