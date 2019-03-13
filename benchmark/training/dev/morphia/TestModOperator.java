package dev.morphia;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestModOperator extends TestBase {
    @Test
    public void mod() {
        getMorphia().map(TestModOperator.Inventory.class);
        getDs().save(new TestModOperator.Inventory("Flowers", 8));
        getDs().save(new TestModOperator.Inventory("Candy", 2));
        getDs().save(new TestModOperator.Inventory("Basketballs", 12));
        List<TestModOperator.Inventory> list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).filter("quantity mod", new Integer[]{ 4, 0 }).order("name").find());
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("Basketballs", list.get(0).name);
        Assert.assertEquals("Flowers", list.get(1).name);
        list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).filter("quantity mod", new Integer[]{ 4, 2 }).order("name").find());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Candy", list.get(0).name);
        list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).filter("quantity mod", new Integer[]{ 6, 0 }).order("name").find());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Basketballs", list.get(0).name);
        list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).field("quantity").mod(4, 0).order("name").find());
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("Basketballs", list.get(0).name);
        Assert.assertEquals("Flowers", list.get(1).name);
        list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).field("quantity").mod(4, 2).order("name").find());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Candy", list.get(0).name);
        list = TestBase.toList(getDs().find(TestModOperator.Inventory.class).field("quantity").mod(6, 0).order("name").find());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Basketballs", list.get(0).name);
    }

    @Entity
    public static class Inventory {
        @Id
        private ObjectId id;

        private Integer quantity;

        private String name;

        public Inventory() {
        }

        public Inventory(final String name, final Integer quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return String.format("Inventory{quantity=%d, name='%s'}", quantity, name);
        }
    }
}

