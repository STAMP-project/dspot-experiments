package azkaban.utils;


import org.junit.Test;


public class EmailMessageCreatorTest {
    private static final String HOST = "smtp.domain.com";

    private static final int MAIL_PORT = 25;

    private static final String SENDER = "somebody@domain.com";

    private static final String USER = "somebody@domain.com";

    private static final String PASSWORD = "pwd";

    @Test
    public void createMessage() {
        final Props props = new Props();
        props.put("mail.user", EmailMessageCreatorTest.USER);
        props.put("mail.password", EmailMessageCreatorTest.PASSWORD);
        props.put("mail.sender", EmailMessageCreatorTest.SENDER);
        props.put("mail.host", EmailMessageCreatorTest.HOST);
        props.put("mail.port", EmailMessageCreatorTest.MAIL_PORT);
        final EmailMessageCreator creator = new EmailMessageCreator(props);
        final EmailMessage message = creator.createMessage();
        assertThat(message.getMailPort()).isEqualTo(EmailMessageCreatorTest.MAIL_PORT);
        assertThat(message.getBody()).isEmpty();
        assertThat(message.getSubject()).isNull();
    }
}

