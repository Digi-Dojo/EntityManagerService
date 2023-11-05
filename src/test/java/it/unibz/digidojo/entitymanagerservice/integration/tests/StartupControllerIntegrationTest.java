package it.unibz.digidojo.entitymanagerservice.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

import it.unibz.digidojo.entitymanagerservice.integration.scenarios.StartupTestScenarios;
import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.StartupRequest;
import it.unibz.digidojo.sharedmodel.request.StartupRequestBuilder;

// TODO: Remake the tests to more use cases and components
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@Import(value = Marshaller.class)
public class StartupControllerIntegrationTest {
    private StartupTestScenarios given, when, then;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Marshaller marshaller;

    @BeforeEach
    void setUp() {
        given = when = then = new StartupTestScenarios(mockMvc, marshaller);
    }

    @Test
    public void shouldGetTheStartupWithTheSpecifiedNameWhenItIsCreated() throws Exception {
        when.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        Startup startup = then.aStartupIsIsSuccessfullyFetchedByName("DigiDojo");
        assertEquals("DigiDojo", startup.getName());
    }


    @Test
    public void shouldGetTheStartupWithTheNewSpecifiedNameWhenItIsUpdated() throws Exception {
        Startup startup = when.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        when.aStartupIsSuccessfullyUpdated(startup.getId(), StartupRequestBuilder.builder().name("NewDigiDojo").build());
        startup = then.aStartupIsIsSuccessfullyFetchedByName("NewDigiDojo");
        assertEquals("NewDigiDojo", startup.getName());
    }

    @Test
    public void shouldGetTheStartupWithTheSpecifiedNameWhenItsDescriptionIsUpdated() throws Exception {
        Startup startup = when.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        when.aStartupIsSuccessfullyUpdated(
                startup.getId(),
                StartupRequestBuilder.builder().description("a smart way to create startups").build()
        );
        startup = then.aStartupIsSuccessfullyFetchedById(startup.getId());
        assertEquals("a smart way to create startups", startup.getDescription());
    }

    @Test
    public void shouldGetAllStartupsWhenTheyExist() throws Exception {
        when.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        when.aStartupIsSuccessfullyCreated(new StartupRequest("LessSuccessfulStartup", "DigiDojo is better"));

        List<Startup> startups = then.allStartupsAreSuccessfullyFetched();
        assertEquals(2, startups.size());
    }
}
