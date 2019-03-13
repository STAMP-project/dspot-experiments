package com.iota.iri.service;


import com.iota.iri.IXI;
import com.iota.iri.Iota;
import com.iota.iri.conf.IotaConfig;
import com.iota.iri.controllers.TransactionViewModel;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.ResponseSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Windows developer notes:
 * For running this tests on windows you need the RocksDB dependencies. You need to install the
 * Visual C++ Redistributable for Visual Studio 2015 x64 from
 * https://www.microsoft.com/en-us/download/confirmation.aspx?id=48145
 * Make sure your Java JDK is a 64x version and your JAVA_HOME is set correctly.
 */
public class APIIntegrationTests {
    private static final Boolean spawnNode = true;// can be changed to false to use already deployed node


    private static final String portStr = "14266";

    private static final String hostName = "http://localhost";

    // No result should ever take a minute
    private static final int SOCKET_TIMEOUT = 60000;

    // Expect to connect to any service worldwide in under 100 ms
    // and to any online machine local in 1 ms. The 50 ms default value is a suggested compromise.
    private static final int CONNECTION_TIMEOUT = 50;

    private static ResponseSpecification specSuccessResponse;

    private static ResponseSpecification specErrorResponse;

    // Constants used in tests
    private static final String[] URIS = new String[]{ "udp://8.8.8.8:14266", "udp://8.8.8.5:14266" };

    private static final String[] ADDRESSES = new String[]{ "RVORZ9SIIP9RCYMREUIXXVPQIPHVCNPQ9HZWYKFWYWZRE9JQKG9REPKIASHUUECPSQO9JT9XNMVKWYGVA" };

    private static final String[] HASHES = new String[]{ "OAATQS9VQLSXCLDJVJJVYUGONXAXOFMJOZNSYWRZSWECMXAQQURHQBJNLD9IOFEPGZEPEMPXCIVRX9999" };

    // Trytes of "VHBRBB9EWCPDKYIBEZW9XVX9AOBQKSCKSTMJLGBANQ99PR9HGYNH9AJWTMHJQBDJHZVWHZMXPILS99999"
    private static final String[] TRYTES = new String[]{ "QBTCHDEADDPCXCSCEAXCBDEAXCCDHDPCGDEAUCCDFDEAGDIDDDDDCDFDHDXCBDVCEAHDWCTCEAHDPCBDVC9DTCEABDTCHDKDCDFDZCEAQCMDEAGDDDPCADADXCBDVCEAHDFDPCBDGDPCRCHDXCCDBDGDSAEAPBCDFDEAADCDFDTCEAXCBDUCCDFDADPCHDXCCDBDQAEAJDXCGDXCHDDBEAWCHDHDDDDBTATAXCCDHDPCGDDDPCADSARCCDADTASAEAHBHBHBHBHBEAFDPCBDSCCDADEAKDXCZCXCDDTCSCXCPCEAPCFDHDXCRC9DTCDBEAJGDHACDHUBBCDCVBDCEAWBKBRBWBDCNBEAZBKBBCRBKBEAHBHBHBHBHBEAJGIIFDIIZCGDID9DIDEAWBPCWCADIDSCEAZBPCGDWCPCEAMACCIDFDZCXCGDWCDBEAJGIIFDIIZCGDID9DIDEAWBPCWCADIDHDEAZBPCEAPCEBEAVABB9BYAEAEAEAXAVAEATBID9DMDEAVACBXAVANAQAEAKDPCGDEAPCBDEAYBHDHDCDADPCBDEAPCFDADMDEAVCTCBDTCFDPC9DEAPCBDSCEAGDHDPCHDTCGDADPCBDEACDUCEATCHDWCBDXCRCEAQBTCCDFDVCXCPCBDEAQCPCRCZCVCFDCDIDBDSCSAJ9J9J9GBGBEAOBPCFD9DMDEA9DXCUCTCEAPCBDSCEARCPCFDTCTCFDEAGBGBJ9WBPCWCADIDSCEAZBPCGDWCPCEAKDPCGDEAQCCDFDBDEAXCBDEAVABB9BYAEAXCBDEAUBCDQCID9DTCHDXCQAEAHDWCTCBDEADDPCFDHDEACDUCEAHDWCTCEAYBHDHDCDADPCBDEAOBADDDXCFDTCEAZCBDCDKDBDEAQCMDEAXCHDGDEACCIDFDZCXCGDWCEABDPCADTCEAJGIIFDIIZCGDIDQAEAXCBDEAHDWCTCEADDFDTCGDTCBDHDRASCPCMDEAKBSCYCPCFDPCEAFDTCVCXCCDBDEACDUCEAHDWCTCEAACTCDDIDQC9DXCRCEACDUCEAQBTCCDFDVCXCPCSAJ9KBUCHDTCFDEAVACBUACBQAEAWBPCWCADIDSCEAZBPCGDWCPCEAHDCDCDZCEADDPCFDHDEAXCBDEAHDWCTCEAADCDSCTCFDBDXCNDPCHDXCCDBDEACDUCEAHDWCTCEAYBHDHDCDADPCBDEAPCFDADMDEAIDBDSCTCFDEAHDWCTCEAPCIDGDDDXCRCTCGDEACDUCEAQBTCFDADPCBDEARBXCVCWCEAMBCDADADPCBDSCSAEARBTCEAGDTCFDJDTCSCEAPCGDEAHDWCTCEAWBXCBDXCGDHDTCFDEACDUCEAZBIDQC9DXCRCEAFCCDFDZCGDEAXCBDEAHDWCTCEAMBDCZBEAVCCDJDTCFDBDADTCBDHDSAJ9FCWCTCBDEAFCCDFD9DSCEAFCPCFDEASBEAQCFDCDZCTCEACDIDHDEAXCBDEAVACBVAYAQAEAWBPCWCADIDSCEAZBPCGDWCPCEACDDDDDCDGDTCSCEAHDWCTCEAYBHDHDCDADPCBDEADDPCFDHDXCRCXCDDPCHDXCCDBDEAXCBDEAJDXCTCKDEACDUCEAHDWCTCEAIDBDDDFDTCDDPCFDTCSCBDTCGDGDEACDUCEAHDWCTCEAPCFDADTCSCEAUCCDFDRCTCGDSAEARBTCEAKDPCGDEAZCBDCDKDBDEAPCGDEAPCBDEACDIDHDGDDDCDZCTCBDEAQCIDHDEAPCEAFDTCGDDDTCRCHDTCSCEAUCXCVCIDFDTCEAXCBDEAHDWCTCEAMBCDADADXCHDHDTCTCEACDUCEADCBDXCCDBDEAPCBDSCEAZBFDCDVCFDTCGDGDEAMAMBDCZBNASAEAVBPCHDTCFDEAXCBDEAHDWCTCEAKDPCFDQAEAWBPCWCADIDSCEAZBPCGDWCPCEAGDTCFDJDTCSCSASASA99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999OFFLINE9SPAM9ADDRESS99999999999999999999999999999999999999999999999999999999TYPPI999999999999999999999999999SWTASPAM9DOT9COM9999TYPPI99ZDDIDYD99999999999999999999CKGSVHJSB9ULTHWRTKZBLXQRTZUVLYJDTGUFSIPZDDZWGOLHSUBYVFQDJLJQVID9UYIYZYSNXCKJWHP9WPYVGHICFZRMUWPLH9NNBWGXRXBCOYXCYQHSVGUGJJ9PJBSQLGUHFXAKFYCMLWSEWTDZTQMCJWEXS999999LBYUIRQ9GUXYQSJGYDPKTBZILTCYQIXFFIZECBMECIIXBOVY9SDTYQKGNKBDBLRCOBBQGSJTVGMA9999IOTASPAM9DOT9COM9999TYPPI99CDQASXQKE999999999MMMMMMMMMNZB9999999UME99999999999999" };

    private static final String NULL_HASH = "999999999999999999999999999999999999999999999999999999999999999999999999999999999";

    private static final String[] NULL_HASH_LIST = new String[]{ APIIntegrationTests.NULL_HASH };

    private static Iota iota;

    private static API api;

    private static IXI ixi;

    private static IotaConfig configuration;

    private static final Logger log = LoggerFactory.getLogger(APIIntegrationTests.class);

    static {
        RestAssured.port = Integer.parseInt(APIIntegrationTests.portStr);
        RestAssured.baseURI = APIIntegrationTests.hostName;
        // Define response specification for http status code 200
        APIIntegrationTests.specSuccessResponse = new ResponseSpecBuilder().expectStatusCode(200).expectBody(Matchers.containsString("duration")).build();
        // Define response specification for http status code 500
        APIIntegrationTests.specErrorResponse = new ResponseSpecBuilder().expectStatusCode(400).expectBody(Matchers.containsString("duration")).build();
    }

    @Test
    public void sendNonJsonBody() {
        APIIntegrationTests.given().body("thisIsInvalidJson").when().post("/").then().spec(APIIntegrationTests.specErrorResponse).body(Matchers.containsString("Invalid JSON syntax"));
    }

    @Test
    public void shouldTestGetNodeInfo() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "getNodeInfo");
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("appName")).body(Matchers.containsString("appVersion")).body(Matchers.containsString("duration")).body(Matchers.containsString("jreAvailableProcessors")).body(Matchers.containsString("jreFreeMemory")).body(Matchers.containsString("jreMaxMemory")).body(Matchers.containsString("jreTotalMemory")).body(Matchers.containsString("jreVersion")).body(Matchers.containsString("latestMilestone")).body(Matchers.containsString("latestMilestoneIndex")).body(Matchers.containsString("jreAvailableProcessors")).body(Matchers.containsString("latestSolidSubtangleMilestone")).body(Matchers.containsString("latestSolidSubtangleMilestoneIndex")).body(Matchers.containsString("milestoneStartIndex")).body(Matchers.containsString("lastSnapshottedMilestoneIndex")).body(Matchers.containsString("neighbors")).body(Matchers.containsString("packetsQueueSize")).body(Matchers.containsString("time")).body(Matchers.containsString("tips")).body(Matchers.containsString("transactionsToRequest"));
    }

    @Test
    public void shouldTestGetIotaConfig() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "getNodeAPIConfiguration");
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("maxFindTransactions")).body(Matchers.containsString("maxRequestsList")).body(Matchers.containsString("maxGetTrytes")).body(Matchers.containsString("maxBodyLength")).body(Matchers.containsString("testNet")).body(Matchers.containsString("milestoneStartIndex"));
    }

    @Test
    public void shouldTestGetNeighbors() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "getNeighbors");
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("neighbors")).body(Matchers.containsString("address")).body(Matchers.containsString("numberOfAllTransactions")).body(Matchers.containsString("numberOfInvalidTransactions")).body(Matchers.containsString("numberOfNewTransactions"));
    }

    @Test
    public void shouldTestAddNeighbors() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "addNeighbors");
        request.put("uris", APIIntegrationTests.URIS);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("addedNeighbors"));
    }

    @Test
    public void shouldTestRemoveNeighbors() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "removeNeighbors");
        request.put("uris", APIIntegrationTests.URIS);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("removedNeighbors"));
    }

    @Test
    public void shouldTestGetTips() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "getTips");
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("hashes"));
    }

    @Test
    public void shouldTestFindTransactions() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "findTransactions");
        request.put("addresses", APIIntegrationTests.ADDRESSES);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("hashes"));
    }

    @Test
    public void shouldTestGetTrytes() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "getTrytes");
        request.put("hashes", APIIntegrationTests.HASHES);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("trytes"));
    }

    @Test
    public void shouldTestBroadcastTransactions() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "broadcastTransactions");
        request.put("trytes", APIIntegrationTests.TRYTES);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).log().all().and();
    }

    @Test
    public void shouldTestStoreTransactions() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "storeTransactions");
        request.put("trytes", APIIntegrationTests.TRYTES);
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).log().all().and();
    }

    @Test
    public void shouldTestattachToTangle() {
        final Map<String, Object> request = new HashMap<>();
        request.put("command", "attachToTangle");
        request.put("trytes", APIIntegrationTests.TRYTES);
        request.put("trunkTransaction", APIIntegrationTests.NULL_HASH);
        request.put("branchTransaction", APIIntegrationTests.NULL_HASH);
        request.put("minWeightMagnitude", APIIntegrationTests.configuration.getMwm());
        APIIntegrationTests.given().body(APIIntegrationTests.gson().toJson(request)).when().post("/").then().spec(APIIntegrationTests.specSuccessResponse).body(Matchers.containsString("trytes"));
    }

    @Test
    public void shouldSendTransactionAndFetchByAddress() {
        List<Object> trytes = sendTransfer(APIIntegrationTests.TRYTES);
        String temp = ((String) (trytes.get(0)));
        String hash = getHash(temp);
        String[] addresses = new String[]{ temp.substring(((TransactionViewModel.ADDRESS_TRINARY_OFFSET) / 3), (((TransactionViewModel.ADDRESS_TRINARY_OFFSET) + (TransactionViewModel.ADDRESS_TRINARY_SIZE)) / 3)) };// extract address from trytes

        List<Object> hashes = findTransactions("addresses", addresses);
        Assert.assertThat(hashes, Matchers.hasItem(hash));
    }

    @Test
    public void shouldSendTransactionAndFetchByTag() {
        List<Object> trytes = sendTransfer(APIIntegrationTests.TRYTES);
        String temp = ((String) (trytes.get(0)));
        String hash = getHash(temp);
        // Tag
        String[] tags = new String[]{ temp.substring(((TransactionViewModel.TAG_TRINARY_OFFSET) / 3), (((TransactionViewModel.TAG_TRINARY_OFFSET) + (TransactionViewModel.TAG_TRINARY_SIZE)) / 3)) };// extract address from trytes

        List<Object> hashes = findTransactions("tags", tags);
        Assert.assertThat(hashes, Matchers.hasItem(hash));
        // ObsoleteTag
        String[] obsoleteTags = new String[]{ temp.substring(((TransactionViewModel.OBSOLETE_TAG_TRINARY_OFFSET) / 3), (((TransactionViewModel.OBSOLETE_TAG_TRINARY_OFFSET) + (TransactionViewModel.OBSOLETE_TAG_TRINARY_SIZE)) / 3)) };// extract address from trytes

        hashes = findTransactions("tags", obsoleteTags);
        Assert.assertThat(hashes, Matchers.hasItem(hash));
    }
}

