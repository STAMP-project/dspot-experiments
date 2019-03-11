package cucumber.runtime.java.needle.test.atm;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AtmServiceBeanTest {
    private final AtmServiceBean bean = new AtmServiceBean();

    @Test
    public void shouldDeposit() {
        bean.deposit(1000);
        Assert.assertThat(bean.getAmount(), Is.is(1000));
    }

    @Test
    public void shouldWithdraw() {
        bean.deposit(1000);
        bean.withdraw(300);
        Assert.assertThat(bean.getAmount(), Is.is(700));
    }
}

