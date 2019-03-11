package org.baeldung.bddmockito;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class BDDMockitoIntegrationTest {
    PhoneBookService phoneBookService;

    PhoneBookRepository phoneBookRepository;

    String momContactName = "Mom";

    String momPhoneNumber = "01234";

    String xContactName = "x";

    String tooLongPhoneNumber = "01111111111111";

    @Test
    public void givenValidContactName_whenSearchInPhoneBook_thenRetunPhoneNumber() {
        BDDMockito.given(phoneBookRepository.contains(momContactName)).willReturn(true);
        BDDMockito.given(phoneBookRepository.getPhoneNumberByContactName(momContactName)).will((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(momContactName)) {
                return momPhoneNumber;
            } else {
                return null;
            }
        });
        String phoneNumber = phoneBookService.search(momContactName);
        BDDMockito.then(phoneBookRepository).should().contains(momContactName);
        BDDMockito.then(phoneBookRepository).should().getPhoneNumberByContactName(momContactName);
        Assert.assertEquals(phoneNumber, momPhoneNumber);
    }

    @Test
    public void givenInvalidContactName_whenSearch_thenRetunNull() {
        BDDMockito.given(phoneBookRepository.contains(xContactName)).willReturn(false);
        String phoneNumber = phoneBookService.search(xContactName);
        BDDMockito.then(phoneBookRepository).should().contains(xContactName);
        BDDMockito.then(phoneBookRepository).should(Mockito.never()).getPhoneNumberByContactName(xContactName);
        Assert.assertEquals(phoneNumber, null);
    }

    @Test
    public void givenValidContactNameAndPhoneNumber_whenRegister_thenSucceed() {
        BDDMockito.given(phoneBookRepository.contains(momContactName)).willReturn(false);
        phoneBookService.register(momContactName, momPhoneNumber);
        Mockito.verify(phoneBookRepository).insert(momContactName, momPhoneNumber);
    }

    @Test
    public void givenEmptyPhoneNumber_whenRegister_thenFail() {
        BDDMockito.given(phoneBookRepository.contains(momContactName)).willReturn(false);
        phoneBookService.register(xContactName, "");
        BDDMockito.then(phoneBookRepository).should(Mockito.never()).insert(momContactName, momPhoneNumber);
    }

    @Test
    public void givenLongPhoneNumber_whenRegister_thenFail() {
        BDDMockito.given(phoneBookRepository.contains(xContactName)).willReturn(false);
        BDDMockito.willThrow(new RuntimeException()).given(phoneBookRepository).insert(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(tooLongPhoneNumber));
        try {
            phoneBookService.register(xContactName, tooLongPhoneNumber);
            Assert.fail("Should throw exception");
        } catch (RuntimeException ex) {
        }
        BDDMockito.then(phoneBookRepository).should(Mockito.never()).insert(momContactName, tooLongPhoneNumber);
    }

    @Test
    public void givenExistentContactName_whenRegister_thenFail() {
        BDDMockito.given(phoneBookRepository.contains(momContactName)).willThrow(new RuntimeException("Name already exist"));
        try {
            phoneBookService.register(momContactName, momPhoneNumber);
            Assert.fail("Should throw exception");
        } catch (Exception ex) {
        }
        BDDMockito.then(phoneBookRepository).should(Mockito.never()).insert(momContactName, momPhoneNumber);
    }
}

