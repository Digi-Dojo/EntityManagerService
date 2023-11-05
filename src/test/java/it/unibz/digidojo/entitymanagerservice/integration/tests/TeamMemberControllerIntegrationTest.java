package it.unibz.digidojo.entitymanagerservice.integration.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import it.unibz.digidojo.entitymanagerservice.integration.scenarios.TeamMemberTestScenarios;
import it.unibz.digidojo.entitymanagerservice.startup.domain.Startup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.TeamMember;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.StartupRequest;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequestBuilder;
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
public class TeamMemberControllerIntegrationTest {
    private TeamMemberTestScenarios given, when, then;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Marshaller marshaller;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        given = when = then = new TeamMemberTestScenarios(mockMvc, marshaller, objectMapper);
    }

    @Test
    public void shouldGetOnlyOneTeamMemberWhenItExists() throws Exception {
        Startup startup = given.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        User user = given.aUserIsSuccessfullyCreated(new UserRequest("Foo", "foo123", "foo@bar.com"));

        TeamMember teamMember = when.aTeamMemberIsSuccessfullyCreated(new TeamMemberRequest(user.getId(), startup.getId(), "Designer"));

        List<TeamMember> teamMemberList = then.aTeamMemberListIsSuccessfullyFetched(
                TeamMemberRequestBuilder.builder().userId(user.getId()).startupId(startup.getId()).build()
        );
        assertEquals(1, teamMemberList.size());
        assertEquals(teamMember.getId(), teamMemberList.get(0).getId());
    }

    @Test
    public void shouldGetOnlyTheTeamMembersOfTheStartup() throws Exception {
        Startup startup = given.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        List<User> users = List.of(
                given.aUserIsSuccessfullyCreated(new UserRequest("Foo", "foo123", "foo@bar.com")),
                given.aUserIsSuccessfullyCreated(new UserRequest("Bar", "bar123", "bar@foo.com"))
        );

        List<TeamMember> teamMembers = users.stream()
                                            .map(user -> {
                                                try {
                                                    return when.aTeamMemberIsSuccessfullyCreated(
                                                            new TeamMemberRequest(user.getId(), startup.getId(), "Developer")
                                                    );
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })
                                            .toList();

        List<TeamMember> fetchedTeamMembers = then.aTeamMemberListIsSuccessfullyFetched(
                TeamMemberRequestBuilder.builder().startupId(startup.getId()).build()
        );

        assertEquals(2, fetchedTeamMembers.size());
        for (int i = 0; i < fetchedTeamMembers.size(); i++) {
            assertEquals(teamMembers.get(i).getId(), fetchedTeamMembers.get(i).getId());
        }
    }

    @Test
    public void shouldGetTheTeamMemberByIdWhenItExists() throws Exception {
        Startup startup = given.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        User user = given.aUserIsSuccessfullyCreated(new UserRequest("Foo", "foo123", "foo@bar.com"));

        TeamMember teamMember = when.aTeamMemberIsSuccessfullyCreated(new TeamMemberRequest(user.getId(), startup.getId(), "Designer"));

        TeamMember fetchedTeamMember = then.aTeamMemberIsSuccessfullyFetchedById(teamMember.getId());
        assertEquals(teamMember.getId(), fetchedTeamMember.getId());
    }

    @Test
    public void shouldGetTheTeamMemberWithTheNewRoleWhenItIsUpdated() throws Exception {
        Startup startup = given.aStartupIsSuccessfullyCreated(new StartupRequest("DigiDojo", "a fun way to create startups"));
        User user = given.aUserIsSuccessfullyCreated(new UserRequest("Foo", "foo123", "foo@bar.com"));
        TeamMember teamMember = given.aTeamMemberIsSuccessfullyCreated(new TeamMemberRequest(user.getId(), startup.getId(), "Designer"));

        when.aTeamMemberIsSuccessfullyUpdated(teamMember.getId(), TeamMemberRequestBuilder.builder().role("Developer").build());

        TeamMember fetchedTeamMember = then.aTeamMemberIsSuccessfullyFetchedById(teamMember.getId());
        assertEquals("Developer", fetchedTeamMember.getRole());
    }
}
