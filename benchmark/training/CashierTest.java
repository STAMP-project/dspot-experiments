

import CodeError.NOT_ENOUGH_FUNDS_BALANCE;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * This example about how to test method call using Mockito.
 */
public class CashierTest {
    private static final int costMilk = 50;

    private Cashier cashier = new Cashier();

    /**
     * Here we create Mock for testing.
     * We test Cashier not Product and Client. That is why we will replace them on Mock.
     * For create Mock I use my method {@link CashierTest#createMockMilk()}
     * and {@link CashierTest#createMockClient(boolean)}.
     * You can create Mock with use Annotation {@link org.mockito.Mock}. Example this {@link CreateMockWithAnnotation}.
     */
    private Product milk = CashierTest.createMockMilk();

    private Client client = CashierTest.createMockClient(true);

    /**
     * Here we to test methods call for {@link Client}.
     */
    @Test
    public void testToSellCallMethodInClient() throws Exception {
        cashier.toSell(client, milk);
        /* Next, we check call methods in client.
        Here we say check call method in client haveAmountOnBalance() whit param value is costMilk.
        If method not call or call two or more times, test failed.
         */
        Mockito.verify(client).haveAmountOnBalance(CashierTest.costMilk);
        Mockito.verify(client).withdraw(CashierTest.costMilk);
        Mockito.verify(client).addProduct(milk);
    }

    /**
     * This we test method {@link Product#getCost()} to call only.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testToSellCallMethodInProduct() throws Exception {
        cashier.toSell(client, milk);
        /* Also we cen check how many times use method.
        Here we say milk most call only one method getCost(). Otherwise test failed.
         */
        Mockito.verify(milk, Mockito.only()).getCost();
    }

    /**
     * Here we test behavior if not enough funds balance.
     */
    @Test
    public void ifNotEnoughFundsBalance() throws Exception {
        Client clientPoor = CashierTest.createMockClient(false);
        cashier.toSell(clientPoor, milk);
        Mockito.verify(clientPoor).haveAmountOnBalance(CashierTest.costMilk);
        Mockito.verify(clientPoor).addCodeError(NOT_ENOUGH_FUNDS_BALANCE);
        Mockito.verify(client, Mockito.never()).withdraw(CashierTest.costMilk);
        Mockito.verify(client, Mockito.never()).addProduct(milk);
    }
}

