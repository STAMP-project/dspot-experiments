package io.protostuff.compiler.java_bean;


import io.protostuff.compiler.it.java_bean.Int32List;
import io.protostuff.compiler.it.java_bean.UnmodifiableInt32List;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration tests for java_bean repeated fields
 *
 * @author Konstantin Shchepanovskyi
 */
public class RepeatedIT {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Test that generated #mergeFrom method can be used multiple times
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMergeTwice() throws Exception {
        Int32List list = Int32List.getSchema().newMessage();
        list.mergeFrom(createInput(42), list);
        list.mergeFrom(createInput(43), list);
        Assert.assertEquals(Arrays.asList(42, 43), list.getNumbersList());
    }

    @Test
    public void testUnmodifiableList() throws Exception {
        UnmodifiableInt32List list = UnmodifiableInt32List.getSchema().newMessage();
        list.mergeFrom(createInput(42), list);
        exception.expect(UnsupportedOperationException.class);
        list.mergeFrom(createInput(43), list);
    }
}

