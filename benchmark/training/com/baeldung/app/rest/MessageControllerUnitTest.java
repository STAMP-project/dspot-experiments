package com.baeldung.app.rest;


import com.baeldung.app.api.MessageApi;
import com.baeldung.domain.model.Message;
import com.baeldung.domain.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MessageControllerUnitTest {
    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @Test
    public void createMessage_NewMessage_OK() {
        MessageApi messageApi = new MessageApi();
        messageApi.setFrom("me");
        messageApi.setTo("you");
        messageApi.setText("Hello, you!");
        messageController.createMessage(messageApi);
        Message message = new Message();
        message.setFrom("me");
        message.setTo("you");
        message.setText("Hello, you!");
        Mockito.verify(messageService, Mockito.times(1)).deliverMessage(ArgumentMatchers.argThat(new com.baeldung.domain.util.MessageMatcher(message)));
    }
}

