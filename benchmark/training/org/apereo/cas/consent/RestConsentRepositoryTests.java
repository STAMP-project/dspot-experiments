package org.apereo.cas.consent;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.val;
import org.apereo.cas.config.CasConsentRestConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.jooq.lambda.Unchecked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * This is {@link RestConsentRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Tag("RestfulApi")
@SpringBootTest(classes = { CasConsentRestConfiguration.class })
public class RestConsentRepositoryTests extends BaseConsentRepositoryTests {
    private static final String CONSENT = "/consent";

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final Function<ConsentDecision, String> HANDLER = Unchecked.function(RestConsentRepositoryTests.MAPPER::writeValueAsString);

    private final Map<String, ConsentRepository> repos = new HashMap<>();

    @Test
    @Override
    public void verifyConsentDecisionIsNotFound() {
        val decision = BUILDER.build(SVC, REG_SVC, "casuser", CollectionUtils.wrap("attribute", "value"));
        val body = RestConsentRepositoryTests.HANDLER.apply(decision);
        val repo = getRepository("verifyConsentDecisionIsNotFound");
        val server = RestConsentRepositoryTests.getNewServer(((RestConsentRepository) (repo)));
        server.expect(manyTimes(), requestTo(RestConsentRepositoryTests.CONSENT)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
        val exp = server.expect(manyTimes(), requestTo(RestConsentRepositoryTests.CONSENT));
        Assertions.assertNotNull(exp);
        exp.andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        super.verifyConsentDecisionIsNotFound();
        server.verify();
    }

    @Test
    @Override
    public void verifyConsentDecisionIsFound() {
        val decision = BUILDER.build(SVC, REG_SVC, "casuser2", CollectionUtils.wrap("attribute", "value"));
        decision.setId(100);
        val body = RestConsentRepositoryTests.HANDLER.apply(decision);
        val repo = getRepository("verifyConsentDecisionIsFound");
        val server = RestConsentRepositoryTests.getNewServer(((RestConsentRepository) (repo)));
        server.expect(once(), requestTo(RestConsentRepositoryTests.CONSENT)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo(RestConsentRepositoryTests.CONSENT)).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo("/consent/100")).andExpect(method(HttpMethod.DELETE)).andRespond(withSuccess());
        val exp = server.expect(once(), requestTo(RestConsentRepositoryTests.CONSENT));
        Assertions.assertNotNull(exp);
        exp.andExpect(method(HttpMethod.GET)).andRespond(withServerError());
        super.verifyConsentDecisionIsFound();
        server.verify();
    }
}

