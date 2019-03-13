package it.cosenonjaviste.daggermock.subcomponenterror;


import it.cosenonjaviste.daggermock.DaggerMockRule;
import org.junit.Assert;
import org.junit.Test;


public class SubComponentErrorTest {
    String s = "BBBB";

    Integer i = 1;

    Long l = 1L;

    Short sh = 1;

    @Test
    public void testSubComponentError() throws Throwable {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule(MyComponent.class, new MyModule());
            rule.apply(null, null, this).evaluate();
            Assert.fail();
        } catch (Throwable e) {
            assertThat(e.getMessage()).contains("Error while trying to override subComponents objects").contains("java.lang.Integer").contains("java.lang.Short").doesNotContain("java.lang.Long");
        }
    }
}

