package liquibase.serializer;


import liquibase.sql.visitor.PrependSqlVisitor;
import org.junit.Assert;
import org.junit.Test;


public class ReflectionSerializerTest {
    @Test
    public void getValue() {
        PrependSqlVisitor visitor = new PrependSqlVisitor();
        visitor.setValue("ValHere");
        visitor.setApplyToRollback(true);
        Assert.assertEquals("ValHere", ReflectionSerializer.getInstance().getValue(visitor, "value"));
        Assert.assertEquals(true, ReflectionSerializer.getInstance().getValue(visitor, "applyToRollback"));
    }
}

