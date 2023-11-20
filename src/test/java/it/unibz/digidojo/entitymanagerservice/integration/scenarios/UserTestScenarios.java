package it.unibz.digidojo.entitymanagerservice.integration.scenarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import it.unibz.digidojo.entitymanagerservice.user.domain.model.User;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.UpdateUserRequest;
import it.unibz.digidojo.sharedmodel.request.UserRequest;

public class UserTestScenarios extends BaseScenarios {
    private static final String ENTITY_URL_NAME = "user";
    private final MockMvc mockMvc;

    private final Marshaller marshaller;

    public UserTestScenarios(final MockMvc mockMvc, final Marshaller marshaller) {
        super(mockMvc);
        this.mockMvc = mockMvc;
        this.marshaller = marshaller;
    }

    public User aUserIsSuccessfullyCreated(UserRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        String response = performRestCreateCall(ENTITY_URL_NAME, jsonContent).andExpect(status().isOk())
                                                                    .andReturn()
                                                                    .getResponse()
                                                                    .getContentAsString();

        return marshaller.unmarshal(response, User.class);
    }

    public void aUserIsSuccessfullyDeletedById(Long id) throws Exception {
        performRestDeleteCall(ENTITY_URL_NAME, id).andExpect(status().isOk());
    }

    public User aUserIsSuccessfullyFetchedByEmail(String email) throws Exception {
        String response = getUserByEmail(email).andExpect(status().isOk())
                                               .andReturn()
                                               .getResponse()
                                               .getContentAsString();
        return marshaller.unmarshal(response, User.class);
    }

    public void aUserCannotBeFetchedById(Long id) throws Exception {
        performRestReadCall(ENTITY_URL_NAME, id).andExpect(status().isBadRequest());
    }

    public void aUserIsSuccessfullyUpdated(Long id, UpdateUserRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        performRestUpdateCall(ENTITY_URL_NAME, id, jsonContent).andExpect(status().isOk()).andReturn();
    }

    public void aUserSuccessfullyLogIn(UserRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        logIn(jsonContent).andExpect(status().isOk()).andReturn();
    }

    private ResultActions getUserByEmail(String email) throws Exception {
        return mockMvc.perform(get("/v1/user/email/{email}", email));
    }

    private ResultActions logIn(String jsonContent) throws Exception {
        return mockMvc.perform(post("/v1/user/login")
                .contentType("application/json")
                .content(jsonContent));
    }
}
