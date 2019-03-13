package org.apereo.cas.adaptors.yubikey;


import ResponseStatus.OK;
import com.yubico.client.v2.VerificationResponse;
import com.yubico.client.v2.YubicoClient;
import java.util.Date;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This is {@link DefaultYubiKeyAccountValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class DefaultYubiKeyAccountValidatorTests {
    @Test
    public void verifyAction() throws Exception {
        val client = Mockito.mock(YubicoClient.class);
        val r = Mockito.mock(VerificationResponse.class);
        Mockito.when(client.verify(ArgumentMatchers.anyString())).thenReturn(r);
        Mockito.when(r.getStatus()).thenReturn(OK);
        Mockito.when(r.getTimestamp()).thenReturn(String.valueOf(new Date().getTime()));
        val v = new DefaultYubiKeyAccountValidator(client);
        Assertions.assertTrue(v.isValid("casuser", "cccccccvlidcrkrrculeevnlcjbngciggidutebbkjrv"));
    }
}

