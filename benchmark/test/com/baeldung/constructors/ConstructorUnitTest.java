package com.baeldung.constructors;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.logging.Logger;
import org.junit.Test;


public class ConstructorUnitTest {
    static final Logger LOGGER = Logger.getLogger(ConstructorUnitTest.class.getName());

    @Test
    public void givenNoExplicitContructor_whenUsed_thenFails() {
        BankAccount account = new BankAccount();
        assertThatThrownBy(() -> {
            account.toString();
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void givenNoArgumentConstructor_whenUsed_thenSucceeds() {
        BankAccountEmptyConstructor account = new BankAccountEmptyConstructor();
        assertThatCode(() -> {
            account.toString();
        }).doesNotThrowAnyException();
    }

    @Test
    public void givenParameterisedConstructor_whenUsed_thenSucceeds() {
        LocalDateTime opened = LocalDateTime.of(2018, Month.JUNE, 29, 6, 30, 0);
        BankAccountParameterizedConstructor account = new BankAccountParameterizedConstructor("Tom", opened, 1000.0F);
        assertThatCode(() -> {
            account.toString();
        }).doesNotThrowAnyException();
    }

    @Test
    public void givenCopyContructor_whenUser_thenMaintainsLogic() {
        LocalDateTime opened = LocalDateTime.of(2018, Month.JUNE, 29, 6, 30, 0);
        BankAccountCopyConstructor account = new BankAccountCopyConstructor("Tim", opened, 1000.0F);
        BankAccountCopyConstructor newAccount = new BankAccountCopyConstructor(account);
        assertThat(account.getName()).isEqualTo(newAccount.getName());
        assertThat(account.getOpened()).isNotEqualTo(newAccount.getOpened());
        assertThat(newAccount.getBalance()).isEqualTo(0.0F);
    }
}

