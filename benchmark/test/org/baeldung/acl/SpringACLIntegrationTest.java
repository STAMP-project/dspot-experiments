package org.baeldung.acl;


import java.util.List;
import org.baeldung.acl.persistence.dao.NoticeMessageRepository;
import org.baeldung.acl.persistence.entity.NoticeMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, WithSecurityContextTestExecutionListener.class })
public class SpringACLIntegrationTest extends AbstractJUnit4SpringContextTests {
    private static Integer FIRST_MESSAGE_ID = 1;

    private static Integer SECOND_MESSAGE_ID = 2;

    private static Integer THIRD_MESSAGE_ID = 3;

    private static String EDITTED_CONTENT = "EDITED";

    @Configuration
    @ComponentScan("org.baeldung.acl.*")
    public static class SpringConfig {}

    @Autowired
    NoticeMessageRepository repo;

    @Test
    @WithMockUser(username = "manager")
    public void givenUserManager_whenFindAllMessage_thenReturnFirstMessage() {
        List<NoticeMessage> details = repo.findAll();
        Assert.assertNotNull(details);
        Assert.assertEquals(1, details.size());
        Assert.assertEquals(SpringACLIntegrationTest.FIRST_MESSAGE_ID, details.get(0).getId());
    }

    @Test
    @WithMockUser(username = "manager")
    public void givenUserManager_whenFind1stMessageByIdAndUpdateItsContent_thenOK() {
        NoticeMessage firstMessage = repo.findById(SpringACLIntegrationTest.FIRST_MESSAGE_ID);
        Assert.assertNotNull(firstMessage);
        Assert.assertEquals(SpringACLIntegrationTest.FIRST_MESSAGE_ID, firstMessage.getId());
        firstMessage.setContent(SpringACLIntegrationTest.EDITTED_CONTENT);
        repo.save(firstMessage);
        NoticeMessage editedFirstMessage = repo.findById(SpringACLIntegrationTest.FIRST_MESSAGE_ID);
        Assert.assertNotNull(editedFirstMessage);
        Assert.assertEquals(SpringACLIntegrationTest.FIRST_MESSAGE_ID, editedFirstMessage.getId());
        Assert.assertEquals(SpringACLIntegrationTest.EDITTED_CONTENT, editedFirstMessage.getContent());
    }

    @Test
    @WithMockUser(username = "hr")
    public void givenUsernameHr_whenFindMessageById2_thenOK() {
        NoticeMessage secondMessage = repo.findById(SpringACLIntegrationTest.SECOND_MESSAGE_ID);
        Assert.assertNotNull(secondMessage);
        Assert.assertEquals(SpringACLIntegrationTest.SECOND_MESSAGE_ID, secondMessage.getId());
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "hr")
    public void givenUsernameHr_whenUpdateMessageWithId2_thenFail() {
        NoticeMessage secondMessage = new NoticeMessage();
        secondMessage.setId(SpringACLIntegrationTest.SECOND_MESSAGE_ID);
        secondMessage.setContent(SpringACLIntegrationTest.EDITTED_CONTENT);
        repo.save(secondMessage);
    }

    @Test
    @WithMockUser(roles = { "EDITOR" })
    public void givenRoleEditor_whenFindAllMessage_thenReturn3Message() {
        List<NoticeMessage> details = repo.findAll();
        Assert.assertNotNull(details);
        Assert.assertEquals(3, details.size());
    }

    @Test
    @WithMockUser(roles = { "EDITOR" })
    public void givenRoleEditor_whenUpdateThirdMessage_thenOK() {
        NoticeMessage thirdMessage = new NoticeMessage();
        thirdMessage.setId(SpringACLIntegrationTest.THIRD_MESSAGE_ID);
        thirdMessage.setContent(SpringACLIntegrationTest.EDITTED_CONTENT);
        repo.save(thirdMessage);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = { "EDITOR" })
    public void givenRoleEditor_whenFind1stMessageByIdAndUpdateContent_thenFail() {
        NoticeMessage firstMessage = repo.findById(SpringACLIntegrationTest.FIRST_MESSAGE_ID);
        Assert.assertNotNull(firstMessage);
        Assert.assertEquals(SpringACLIntegrationTest.FIRST_MESSAGE_ID, firstMessage.getId());
        firstMessage.setContent(SpringACLIntegrationTest.EDITTED_CONTENT);
        repo.save(firstMessage);
    }
}

