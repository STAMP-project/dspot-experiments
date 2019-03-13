/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.documentation;


import java.util.List;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.testframework.PmdRuleTst;
import org.junit.Assert;
import org.junit.Test;


public class CommentRequiredTest extends PmdRuleTst {
    @Test
    public void allCommentTypesIgnored() {
        CommentRequiredRule rule = new CommentRequiredRule();
        Assert.assertNull("By default, the rule should be functional", rule.dysfunctionReason());
        List<PropertyDescriptor<?>> propertyDescriptors = CommentRequiredTest.getProperties(rule);
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            CommentRequiredTest.setPropertyValue(rule, property, "Ignored");
        }
        Assert.assertNotNull("All properties are ignored, rule should be dysfunctional", rule.dysfunctionReason());
        // now, try out combinations: only one of the properties is required.
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            CommentRequiredTest.setPropertyValue(rule, property, "Required");
            Assert.assertNull((("The property " + (property.name())) + " is set to required, the rule should be functional."), rule.dysfunctionReason());
            CommentRequiredTest.setPropertyValue(rule, property, "Ignored");
        }
    }
}

