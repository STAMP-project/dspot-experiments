package com.piggymetrics.notification.controller;


import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.service.RecipientService;
import com.sun.security.auth.UserPrincipal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipientControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RecipientController recipientController;

    @Mock
    private RecipientService recipientService;

    private MockMvc mockMvc;

    @Test
    public void shouldSaveCurrentRecipientSettings() throws Exception {
        Recipient recipient = getStubRecipient();
        String json = RecipientControllerTest.mapper.writeValueAsString(recipient);
        mockMvc.perform(put("/recipients/current").principal(new UserPrincipal(recipient.getAccountName())).contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void shouldGetCurrentRecipientSettings() throws Exception {
        Recipient recipient = getStubRecipient();
        Mockito.when(recipientService.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        mockMvc.perform(get("/recipients/current").principal(new UserPrincipal(recipient.getAccountName()))).andExpect(jsonPath("$.accountName").value(recipient.getAccountName())).andExpect(status().isOk());
    }
}

