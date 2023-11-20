package it.unibz.digidojo.entitymanagerservice.integration.scenarios;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.entitymanagerservice.teammember.domain.model.TeamMember;
import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.StartupRequest;
import it.unibz.digidojo.sharedmodel.request.TeamMemberRequest;
import it.unibz.digidojo.sharedmodel.request.UserRequest;

public class TeamMemberTestScenarios extends BaseScenarios {
    private static final String ENTITY_URL_NAME = "team-member";
    private static final String STARTUP_URL_NAME = "startup";
    private static final String USER_URL_NAME = "user";

    private final MockMvc mockMvc;

    private final Marshaller marshaller;
    private final ObjectMapper objectMapper;

    public TeamMemberTestScenarios(final MockMvc mockMvc, final Marshaller marshaller, final ObjectMapper objectMapper) {
        super(mockMvc);
        this.mockMvc = mockMvc;
        this.marshaller = marshaller;
        this.objectMapper = objectMapper;
    }

    public Startup aStartupIsSuccessfullyCreated(StartupRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        String response = performRestCreateCall(STARTUP_URL_NAME, jsonContent).andExpect(status().isOk())
                                                                              .andReturn()
                                                                              .getResponse()
                                                                              .getContentAsString();

        return marshaller.unmarshal(response, Startup.class);
    }

    public User aUserIsSuccessfullyCreated(UserRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        String response = performRestCreateCall(USER_URL_NAME, jsonContent).andExpect(status().isOk())
                                                                           .andReturn()
                                                                           .getResponse()
                                                                           .getContentAsString();

        return marshaller.unmarshal(response, User.class);
    }

    public TeamMember aTeamMemberIsSuccessfullyCreated(TeamMemberRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        String response = performRestCreateCall(ENTITY_URL_NAME, jsonContent).andExpect(status().isOk())
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString();

        return marshaller.unmarshal(response, TeamMember.class);
    }

    public TeamMember aTeamMemberIsSuccessfullyFetchedById(Long id) throws Exception {
        String response = performRestReadCall(ENTITY_URL_NAME, id).andExpect(status().isOk())
                                                                  .andReturn()
                                                                  .getResponse()
                                                                  .getContentAsString();
        return marshaller.unmarshal(response, TeamMember.class);
    }

    public List<TeamMember> aTeamMemberListIsSuccessfullyFetched(TeamMemberRequest queryParams) throws Exception {
        Map<String, String> queryParamsAsMap = objectMapper.convertValue(queryParams, new TypeReference<>() {
        });

        String response = performRestReadAllCall(
                ENTITY_URL_NAME,
                mapToMultiValueMap(queryParamsAsMap)
        ).andExpect(status().isOk())
         .andReturn()
         .getResponse()
         .getContentAsString();

        return Arrays.stream(marshaller.unmarshal(response, TeamMember[].class)).toList();
    }

    public MvcResult aTeamMemberIsSuccessfullyUpdated(Long id, TeamMemberRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        return performRestUpdateCall(ENTITY_URL_NAME, id, jsonContent).andExpect(status().isOk()).andReturn();
    }
}
