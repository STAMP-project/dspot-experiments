package io.protostuff.compiler.custom;


import E.A;
import io.protostuff.compiler.it.custom.PluralToSingularTestMessage;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for custom formats - SINGULAR & PLURAL See src/test/stg/java_bean_with_plural_to_singular_format.java.stg :
 * {@code <field.name; format="PC&&SINGULAR">} transforms {@code field.name} to PascalCase in singular form, for example
 * - {@code errors} -> {@code Error}
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class PluralToSingularIT {
    @Test
    public void testGeneratedClass() throws Exception {
        PluralToSingularTestMessage message = new PluralToSingularTestMessage();
        message.addError(A);
        Assert.assertEquals(1, message.getErrorCount());
        Assert.assertEquals(Collections.singletonList(A), message.getErrorList());
    }
}

