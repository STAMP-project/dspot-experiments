package org.web3j.protocol.pantheon;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.protocol.ResponseTester;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.pantheon.response.PantheonEthAccountsMapResponse;


public class ResponseTest extends ResponseTester {
    @Test
    public void testClicqueGetSigners() {
        buildResponse(("{\n" + ((((("    \"jsonrpc\": \"2.0\",\n" + "    \"id\": 1,\n") + "    \"result\": [\"0x42eb768f2244c8811c63729a21a3569731535f06\",") + "\"0x7ffc57839b00206d1ad20c69a1981b489f772031\",") + "\"0xb279182d99e65703f0076e4812653aab85fca0f0\"]\n") + "}")));
        EthAccounts ethAccounts = deserialiseResponse(EthAccounts.class);
        Assert.assertThat(ethAccounts.getAccounts().toString(), CoreMatchers.is(("[0x42eb768f2244c8811c63729a21a3569731535f06, " + ("0x7ffc57839b00206d1ad20c69a1981b489f772031, " + "0xb279182d99e65703f0076e4812653aab85fca0f0]"))));
    }

    @Test
    public void testClicqueProposals() {
        buildResponse(("{\n" + ((("    \"jsonrpc\": \"2.0\",\n" + "    \"id\": 1,\n") + "    \"result\": {\"0x42eb768f2244c8811c63729a21a3569731535f07\": false,") + "\"0x12eb759f2222d7711c63729a45c3585731521d01\": true}\n}")));
        PantheonEthAccountsMapResponse mapResponse = deserialiseResponse(PantheonEthAccountsMapResponse.class);
        Assert.assertThat(mapResponse.getAccounts().toString(), CoreMatchers.is(("{0x42eb768f2244c8811c63729a21a3569731535f07=false, " + "0x12eb759f2222d7711c63729a45c3585731521d01=true}")));
    }
}

