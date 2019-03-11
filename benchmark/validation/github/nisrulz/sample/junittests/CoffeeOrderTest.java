package github.nisrulz.sample.junittests;


import org.junit.Assert;
import org.junit.Test;


public class CoffeeOrderTest {
    private static final float PRICE_TEST = 5.0F;

    private CoffeeOrder coffeeOrder;

    @Test
    public void newTest() {
        // Do nothing
        Assert.assertEquals(1, 8);
    }

    @Test
    public void orderIsNotNull() {
        Assert.assertNotNull(coffeeOrder);
    }

    @Test
    public void orderDecrement() {
        coffeeOrder.decrementCoffeeCount();
        Assert.assertEquals(0, coffeeOrder.getCoffeeCount());
        coffeeOrder.setCoffeeCount(25);
        coffeeOrder.decrementCoffeeCount();
        Assert.assertEquals(24, coffeeOrder.getCoffeeCount());
    }

    @Test
    public void orderIncrement() {
        coffeeOrder.incrementCoffeeCount();
        Assert.assertEquals(1, coffeeOrder.getCoffeeCount());
        coffeeOrder.setCoffeeCount(25);
        coffeeOrder.incrementCoffeeCount();
        Assert.assertEquals(26, coffeeOrder.getCoffeeCount());
    }

    @Test
    public void orderTotalPrice() {
        Assert.assertEquals(0.0, coffeeOrder.getTotalPrice(), 0.0F);
        coffeeOrder.setCoffeeCount(25);
        Assert.assertEquals(((CoffeeOrderTest.PRICE_TEST) * 25), coffeeOrder.getTotalPrice(), 0.0F);
    }

    @Test
    public void orderSetCoffeeCount() {
        coffeeOrder.setCoffeeCount((-1));
        Assert.assertEquals(0, coffeeOrder.getCoffeeCount());
        coffeeOrder.setCoffeeCount(25);
        Assert.assertEquals(25, coffeeOrder.getCoffeeCount());
    }
}

