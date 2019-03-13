package org.robobinding.widget;


import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public abstract class AbstractMultiTypePropertyViewAttributeTest {
    private Map<Class<?>, Class<?>> propertyTypeToViewAttributeTypeMappings;

    @Test
    public void givenPropertyType_whenCreate_thenReturnExpectedPropertyViewAttributeInstance() {
        for (Class<?> propertyType : propertyTypeToViewAttributeTypeMappings.keySet()) {
            Object viewAttribute = createViewAttribute(propertyType);
            Assert.assertThat(viewAttribute, CoreMatchers.instanceOf(propertyTypeToViewAttributeTypeMappings.get(propertyType)));
        }
    }

    protected class TypeMappingBuilder {
        private final Class<?> propertyType;

        public TypeMappingBuilder(Class<?> propertyType) {
            this.propertyType = propertyType;
        }

        public void expectAttributeType(Class<?> attributeType) {
            propertyTypeToViewAttributeTypeMappings.put(propertyType, attributeType);
        }
    }
}

