package org.apereo.cas.memcached.kryo;


import com.esotericsoftware.kryo.KryoException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.security.auth.login.AccountNotFoundException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.authentication.AcceptUsersAuthenticationHandler;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.mock.MockServiceTicket;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.support.MultiTimeUseOrTimeoutExpirationPolicy;
import org.apereo.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link CasKryoTranscoder} class.
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@Slf4j
public class CasKryoTranscoderTests {
    private static final String ST_ID = "ST-1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890ABCDEFGHIJK";

    private static final String TGT_ID = "TGT-1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890ABCDEFGHIJK-cas1";

    private static final String USERNAME = "handymanbob";

    private static final String PASSWORD = "foo";

    private static final String NICKNAME_KEY = "nickname";

    private static final String NICKNAME_VALUE = "bob";

    private final CasKryoTranscoder transcoder;

    private final Map<String, Object> principalAttributes;

    public CasKryoTranscoderTests() {
        val classesToRegister = new ArrayList<Class>();
        classesToRegister.add(MockServiceTicket.class);
        classesToRegister.add(MockTicketGrantingTicket.class);
        this.transcoder = new CasKryoTranscoder(new CasKryoPool(classesToRegister));
        this.principalAttributes = new HashMap<>();
        this.principalAttributes.put(CasKryoTranscoderTests.NICKNAME_KEY, CasKryoTranscoderTests.NICKNAME_VALUE);
    }

    @Test
    public void verifyEncodeDecodeTGTImpl() {
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val bldr = new org.apereo.cas.authentication.DefaultAuthenticationBuilder(new DefaultPrincipalFactory().createPrincipal("user", new HashMap(this.principalAttributes)));
        bldr.setAttributes(new HashMap(this.principalAttributes));
        bldr.setAuthenticationDate(ZonedDateTime.now());
        bldr.addCredential(new org.apereo.cas.authentication.metadata.BasicCredentialMetaData(userPassCredential));
        bldr.addFailure("error", new AccountNotFoundException());
        bldr.addSuccess("authn", new org.apereo.cas.authentication.DefaultAuthenticationHandlerExecutionResult(new AcceptUsersAuthenticationHandler(""), new org.apereo.cas.authentication.metadata.BasicCredentialMetaData(userPassCredential)));
        val expectedTGT = new org.apereo.cas.ticket.TicketGrantingTicketImpl(CasKryoTranscoderTests.TGT_ID, RegisteredServiceTestUtils.getService(), null, bldr.build(), new NeverExpiresExpirationPolicy());
        val ticket = expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, RegisteredServiceTestUtils.getService(), new NeverExpiresExpirationPolicy(), false, true);
        val result1 = transcoder.encode(expectedTGT);
        val resultTicket = transcoder.decode(result1);
        Assertions.assertEquals(expectedTGT, resultTicket);
        val result2 = transcoder.encode(ticket);
        val resultStTicket1 = transcoder.decode(result2);
        Assertions.assertEquals(ticket, resultStTicket1);
        val resultStTicket2 = transcoder.decode(result2);
        Assertions.assertEquals(ticket, resultStTicket2);
    }

    @Test
    public void verifyEncodeDecode() {
        val tgt = new MockTicketGrantingTicket(CasKryoTranscoderTests.USERNAME);
        val expectedST = new MockServiceTicket(CasKryoTranscoderTests.ST_ID, RegisteredServiceTestUtils.getService(), tgt);
        Assertions.assertEquals(expectedST, transcoder.decode(transcoder.encode(expectedST)));
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.USERNAME);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        internalProxyTest();
    }

    @Test
    public void verifyEncodeDecodeTGTWithUnmodifiableMap() {
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, new HashMap(this.principalAttributes));
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithUnmodifiableList() {
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val values = new ArrayList<String>();
        values.add(CasKryoTranscoderTests.NICKNAME_VALUE);
        val newAttributes = new HashMap<String, Object>();
        newAttributes.put(CasKryoTranscoderTests.NICKNAME_KEY, new ArrayList(values));
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, newAttributes);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithLinkedHashMap() {
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, new LinkedHashMap(this.principalAttributes));
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithListOrderedMap() {
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, this.principalAttributes);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithUnmodifiableSet() {
        val newAttributes = new HashMap<String, Object>();
        newAttributes.put(CasKryoTranscoderTests.NICKNAME_KEY, Collections.unmodifiableSet(CollectionUtils.wrapSet(CasKryoTranscoderTests.NICKNAME_VALUE)));
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, newAttributes);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithSingleton() {
        val newAttributes = new HashMap<String, Object>();
        newAttributes.put(CasKryoTranscoderTests.NICKNAME_KEY, Collections.singleton(CasKryoTranscoderTests.NICKNAME_VALUE));
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, newAttributes);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeTGTWithSingletonMap() {
        val newAttributes = Collections.<String, Object>singletonMap(CasKryoTranscoderTests.NICKNAME_KEY, CasKryoTranscoderTests.NICKNAME_VALUE);
        val userPassCredential = new UsernamePasswordCredential(CasKryoTranscoderTests.USERNAME, CasKryoTranscoderTests.PASSWORD);
        val expectedTGT = new MockTicketGrantingTicket(CasKryoTranscoderTests.TGT_ID, userPassCredential, newAttributes);
        expectedTGT.grantServiceTicket(CasKryoTranscoderTests.ST_ID, null, null, false, true);
        val result = transcoder.encode(expectedTGT);
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
        Assertions.assertEquals(expectedTGT, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeRegisteredService() {
        val service = RegisteredServiceTestUtils.getRegisteredService("helloworld");
        val result = transcoder.encode(service);
        Assertions.assertEquals(service, transcoder.decode(result));
        Assertions.assertEquals(service, transcoder.decode(result));
    }

    @Test
    public void verifySTWithServiceTicketExpirationPolicy() {
        transcoder.getKryo().getClassResolver().reset();
        val tgt = new MockTicketGrantingTicket(CasKryoTranscoderTests.USERNAME);
        val expectedST = new MockServiceTicket(CasKryoTranscoderTests.ST_ID, RegisteredServiceTestUtils.getService(), tgt);
        val step = new MultiTimeUseOrTimeoutExpirationPolicy.ServiceTicketExpirationPolicy(1, 600);
        expectedST.setExpiration(step);
        val result = transcoder.encode(expectedST);
        Assertions.assertEquals(expectedST, transcoder.decode(result));
        Assertions.assertEquals(expectedST, transcoder.decode(result));
    }

    @Test
    public void verifyEncodeDecodeNonRegisteredClass() {
        val tgt = new MockTicketGrantingTicket(CasKryoTranscoderTests.USERNAME);
        val expectedST = new MockServiceTicket(CasKryoTranscoderTests.ST_ID, RegisteredServiceTestUtils.getService(), tgt);
        val step = new CasKryoTranscoderTests.UnregisteredServiceTicketExpirationPolicy(1, 600);
        expectedST.setExpiration(step);
        try {
            transcoder.encode(expectedST);
            throw new AssertionError("Unregistered class is not allowed by Kryo");
        } catch (final KryoException e) {
            LOGGER.trace(e.getMessage(), e);
        } catch (final Exception e) {
            throw new AssertionError("Unexpected exception due to not resetting Kryo between de-serializations with unregistered class.");
        }
    }

    /**
     * Class for testing Kryo unregistered class handling.
     */
    @ToString(callSuper = true)
    private static class UnregisteredServiceTicketExpirationPolicy extends MultiTimeUseOrTimeoutExpirationPolicy {
        private static final long serialVersionUID = -1704993954986738308L;

        /**
         * Instantiates a new Service ticket expiration policy.
         *
         * @param numberOfUses
         * 		the number of uses
         * @param timeToKillInSeconds
         * 		the time to kill in seconds
         */
        UnregisteredServiceTicketExpirationPolicy(final int numberOfUses, final long timeToKillInSeconds) {
            super(numberOfUses, timeToKillInSeconds);
        }
    }
}

