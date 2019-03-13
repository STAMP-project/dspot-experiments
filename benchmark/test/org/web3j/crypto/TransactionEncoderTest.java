package org.web3j.crypto;


import java.math.BigInteger;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;


public class TransactionEncoderTest {
    @Test
    public void testSignMessage() {
        byte[] signedMessage = TransactionEncoder.signMessage(TransactionEncoderTest.createEtherTransaction(), SampleKeys.CREDENTIALS);
        String hexMessage = Numeric.toHexString(signedMessage);
        Assert.assertThat(hexMessage, CoreMatchers.is(("0xf85580010a840add5355887fffffffffffffff80" + (("1c" + "a046360b50498ddf5566551ce1ce69c46c565f1f478bb0ee680caf31fbc08ab727") + "a01b2f1432de16d110407d544f519fc91b84c8e16d3b6ec899592d486a94974cd0"))));
    }

    @Test
    public void testEtherTransactionAsRlpValues() {
        List<RlpType> rlpStrings = TransactionEncoder.asRlpValues(TransactionEncoderTest.createEtherTransaction(), new Sign.SignatureData(((byte) (0)), new byte[32], new byte[32]));
        Assert.assertThat(rlpStrings.size(), CoreMatchers.is(9));
        Assert.assertThat(rlpStrings.get(3), IsEqual.equalTo(RlpString.create(new BigInteger("add5355", 16))));
    }

    @Test
    public void testContractAsRlpValues() {
        List<RlpType> rlpStrings = TransactionEncoder.asRlpValues(TransactionEncoderTest.createContractTransaction(), null);
        Assert.assertThat(rlpStrings.size(), CoreMatchers.is(6));
        Assert.assertThat(rlpStrings.get(3), CoreMatchers.is(RlpString.create("")));
    }

    @Test
    public void testEip155Encode() {
        Assert.assertThat(TransactionEncoder.encode(TransactionEncoderTest.createEip155RawTransaction(), ((byte) (1))), CoreMatchers.is(Numeric.hexStringToByteArray(("0xec098504a817c800825208943535353535353535353535353535353535353535880de0" + "b6b3a764000080018080"))));
    }

    @Test
    public void testEip155Transaction() {
        // https://github.com/ethereum/EIPs/issues/155
        Credentials credentials = Credentials.create("0x4646464646464646464646464646464646464646464646464646464646464646");
        Assert.assertThat(TransactionEncoder.signMessage(TransactionEncoderTest.createEip155RawTransaction(), ((byte) (1)), credentials), CoreMatchers.is(Numeric.hexStringToByteArray(("0xf86c098504a817c800825208943535353535353535353535353535353535353535880" + (("de0b6b3a76400008025a028ef61340bd939bc2195fe537567866003e1a15d" + "3c71ff63e1590620aa636276a067cbe9d8997f761aecb703304b3800ccf55") + "5c9f3dc64214b297fb1966a3b6d83")))));
    }
}

