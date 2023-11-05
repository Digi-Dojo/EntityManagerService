package it.unibz.digidojo.entitymanagerservice.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import it.unibz.digidojo.entitymanagerservice.integration.scenarios.UserTestScenarios;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.UpdateUserRequestBuilder;
import it.unibz.digidojo.sharedmodel.request.UserRequest;

// TODO: Remake the tests to more use cases and components
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@Import(value = Marshaller.class)
public class UserControllerIntegrationTest {
    private UserTestScenarios given, when, then;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Marshaller marshaller;

    @BeforeEach
    void setUp() {
        given = when = then = new UserTestScenarios(mockMvc, marshaller);
    }

    @Test
    public void shouldGetTheUserWithTheSpecifiedEMailWhenItIsCreated() throws Exception {
        User user = when.aUserIsSuccessfullyCreated(new UserRequest("Foo", "banana123", "foo@bar.com"));

        user = then.aUserIsSuccessfullyFetchedByEmail(user.getEmailAddress());
        assertEquals("Foo", user.getName());
    }

    @Test
    public void shouldDeleteTheUserWhenItExists() throws Exception {
        User user = given.aUserIsSuccessfullyCreated(new UserRequest("Bar", "digidojo", "bar@foo.com"));

        when.aUserIsSuccessfullyDeletedById(user.getId());

        then.aUserCannotBeFetchedById(user.getId());
    }

    @Test
    public void shouldGetTheUserWithTheSpecifiedEmailWhenItsUpdated() throws Exception {
        UserRequest request = new UserRequest("Test", "digidojo", "old@unibz.it");
        User user = given.aUserIsSuccessfullyCreated(request);

        when.aUserIsSuccessfullyUpdated(user.getId(), UpdateUserRequestBuilder.builder()
                                                                              .currentPassword(request.password())
                                                                              .emailAddress("new@unibz.it")
                                                                              .build()
        );

        then.aUserIsSuccessfullyFetchedByEmail("new@unibz.it");
    }

    @Test
    public void shouldLogInWhenUserExists() throws Exception {
        User user = given.aUserIsSuccessfullyCreated(new UserRequest("Test", "digidojo", "test@unibz.it"));
        String passwordWithoutHashing = "digidojo";

        then.aUserSuccessfullyLogIn(new UserRequest(user.getName(), passwordWithoutHashing, user.getEmailAddress()));
    }
}

