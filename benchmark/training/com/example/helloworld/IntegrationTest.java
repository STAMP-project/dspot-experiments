package com.example.helloworld;


import com.example.helloworld.api.Saying;
import com.example.helloworld.core.Person;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class IntegrationTest {
    private static final String TMP_FILE = IntegrationTest.createTempFile();

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");

    public static final DropwizardAppExtension<HelloWorldConfiguration> RULE = new DropwizardAppExtension(HelloWorldApplication.class, IntegrationTest.CONFIG_PATH, ConfigOverride.config("database.url", ("jdbc:h2:" + (IntegrationTest.TMP_FILE))));

    @Test
    public void testHelloWorld() throws Exception {
        final Optional<String> name = Optional.of("Dr. IntegrationTest");
        final Saying saying = IntegrationTest.RULE.client().target((("http://localhost:" + (IntegrationTest.RULE.getLocalPort())) + "/hello-world")).queryParam("name", name.get()).request().get(Saying.class);
        assertThat(saying.getContent()).isEqualTo(IntegrationTest.RULE.getConfiguration().buildTemplate().render(name));
    }

    @Test
    public void testPostPerson() throws Exception {
        final Person person = new Person("Dr. IntegrationTest", "Chief Wizard");
        final Person newPerson = postPerson(person);
        assertThat(newPerson.getId()).isNotNull();
        assertThat(newPerson.getFullName()).isEqualTo(person.getFullName());
        assertThat(newPerson.getJobTitle()).isEqualTo(person.getJobTitle());
    }

    @Test
    public void testRenderingPersonFreemarker() throws Exception {
        testRenderingPerson("view_freemarker");
    }

    @Test
    public void testRenderingPersonMustache() throws Exception {
        testRenderingPerson("view_mustache");
    }

    @Test
    public void testLogFileWritten() throws IOException {
        // The log file is using a size and time based policy, which used to silently
        // fail (and not write to a log file). This test ensures not only that the
        // log file exists, but also contains the log line that jetty prints on startup
        final Path log = Paths.get("./logs/application.log");
        assertThat(log).exists();
        final String actual = new String(Files.readAllBytes(log), StandardCharsets.UTF_8);
        assertThat(actual).contains(("0.0.0.0:" + (IntegrationTest.RULE.getLocalPort())));
    }
}

