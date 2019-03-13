package org.javaee7.el.standalone;


import javax.el.ELProcessor;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class ELResolverTest {
    private ELProcessor elProcessor;

    @Test
    public void should_pick_in_the_array() {
        Object result = elProcessor.eval("a = [1, 2, 3]; a[1]");
        Assert.assertEquals(2L, result);
    }

    @Test
    public void should_add() {
        Object result = elProcessor.eval("((x,y) -> x+y)(4, 5)");
        Assert.assertEquals(9L, result);
    }
}

