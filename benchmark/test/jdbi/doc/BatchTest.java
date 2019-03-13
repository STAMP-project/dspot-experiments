package jdbi.doc;


import java.util.Arrays;
import java.util.Collection;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.core.statement.Batch;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class BatchTest {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testSimpleBatch() {
        // tag::simpleBatch[]
        Batch batch = handle.createBatch();
        batch.add("INSERT INTO fruit VALUES(0, 'apple')");
        batch.add("INSERT INTO fruit VALUES(1, 'banana')");
        int[] rowsModified = batch.execute();
        // end::simpleBatch[]
        assertThat(rowsModified).containsExactly(1, 1);
        assertThat(handle.createQuery("SELECT count(1) FROM fruit").mapTo(int.class).findOnly().intValue()).isEqualTo(2);
    }

    // tag::sqlObjectBatch[]
    @Test
    public void testSqlObjectBatch() {
        BatchTest.BasketOfFruit basket = handle.attach(BatchTest.BasketOfFruit.class);
        int[] rowsModified = basket.fillBasket(Arrays.asList(new BatchTest.Fruit(0, "apple"), new BatchTest.Fruit(1, "banana")));
        assertThat(rowsModified).containsExactly(1, 1);
        assertThat(basket.countFruit()).isEqualTo(2);
    }

    public interface BasketOfFruit {
        @SqlBatch("INSERT INTO fruit VALUES(:id, :name)")
        int[] fillBasket(@BindBean
        Collection<BatchTest.Fruit> fruits);

        @SqlQuery("SELECT count(1) FROM fruit")
        int countFruit();
    }

    // end::sqlObjectBatch[]
    public class Fruit {
        private final int id;

        private final String name;

        Fruit(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

