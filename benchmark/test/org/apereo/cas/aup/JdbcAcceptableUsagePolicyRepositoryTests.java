package org.apereo.cas.aup;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcAcceptableUsagePolicyRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = { "cas.acceptableUsagePolicy.jdbc.tableName=aup_table", "cas.acceptableUsagePolicy.aupAttributeName=accepted" })
public class JdbcAcceptableUsagePolicyRepositoryTests extends BaseJdbcAcceptableUsagePolicyRepositoryTests {
    @Test
    public void verifyRepositoryAction() {
        verifyRepositoryAction("casuser", CollectionUtils.wrap("accepted", "false"));
    }

    @Test
    public void determinePrincipalId() {
        val principalId = determinePrincipalId("casuser", CollectionUtils.wrap("accepted", "false"));
        Assertions.assertEquals("casuser", principalId);
    }
}

