package com.example.linkedcontainer;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 * Tests for RedmineClient.
 */
public class RedmineClientTest {
    private static final String POSTGRES_USERNAME = "redmine";

    private static final String POSTGRES_PASSWORD = "secret";

    private PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6.2").withUsername(RedmineClientTest.POSTGRES_USERNAME).withPassword(RedmineClientTest.POSTGRES_PASSWORD);

    private RedmineContainer redmineContainer = new RedmineContainer("redmine:3.3.2").withLinkToContainer(postgreSQLContainer, "postgres").withEnv("POSTGRES_ENV_POSTGRES_USER", RedmineClientTest.POSTGRES_USERNAME).withEnv("POSTGRES_ENV_POSTGRES_PASSWORD", RedmineClientTest.POSTGRES_PASSWORD);

    @Rule
    public RuleChain chain = RuleChain.outerRule(postgreSQLContainer).around(redmineContainer);

    @Test
    public void canGetIssueCount() throws Exception {
        RedmineClient redmineClient = new RedmineClient(redmineContainer.getRedmineUrl());
        Assert.assertEquals("The issue count can be retrieved.", 0, redmineClient.getIssueCount());
    }
}

