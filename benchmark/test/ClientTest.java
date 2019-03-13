

import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static CodeError.NOT_ENOUGH_FUNDS_BALANCE;


public class ClientTest {
    private Client client = new Client(100);

    @Test
    public void testGetBalance() throws Exception {
        Assert.assertThat(client.getBalance(), CoreMatchers.is(100));
    }

    @Test
    public void testHaveAmountOnBalance() throws Exception {
        Assert.assertFalse(client.haveAmountOnBalance(101));
        Assert.assertTrue(client.haveAmountOnBalance(100));
        Assert.assertTrue(client.haveAmountOnBalance(99));
    }

    @Test
    public void testWithdraw() throws Exception {
        client.withdraw(30);
        Assert.assertThat(client.getBalance(), CoreMatchers.is(70));
    }

    @Test
    public void testAddCodeError() throws Exception {
        final CodeError error = NOT_ENOUGH_FUNDS_BALANCE;
        client.addCodeError(error);
        List<CodeError> codeErrors = client.getCodeErrors();
        Assert.assertThat(codeErrors.size(), CoreMatchers.is(1));
        Assert.assertThat(codeErrors.get(0), CoreMatchers.is(error));
    }

    @Test
    public void testAddProduct() throws Exception {
        Product milk = new Product("Milk", 50);
        client.addProduct(milk);
        List<Product> products = client.getProducts();
        Assert.assertThat(products.size(), CoreMatchers.is(1));
        Assert.assertThat(products.get(0), CoreMatchers.is(milk));
    }
}

