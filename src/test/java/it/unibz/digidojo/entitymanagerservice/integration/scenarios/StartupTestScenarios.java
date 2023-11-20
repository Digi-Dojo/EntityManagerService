package it.unibz.digidojo.entitymanagerservice.integration.scenarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;

import it.unibz.digidojo.entitymanagerservice.startup.domain.model.Startup;
import it.unibz.digidojo.sharedmodel.marshaller.Marshaller;
import it.unibz.digidojo.sharedmodel.request.StartupRequest;

public class StartupTestScenarios extends BaseScenarios {
    private static final String ENTITY_URL_NAME = "startup";
    private final MockMvc mockMvc;

    private final Marshaller marshaller;

    public StartupTestScenarios(final MockMvc mockMvc, final Marshaller marshaller) {
        super(mockMvc);
        this.mockMvc = mockMvc;
        this.marshaller = marshaller;
    }

    public Startup aStartupIsSuccessfullyCreated(StartupRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        String response = performRestCreateCall(ENTITY_URL_NAME, jsonContent).andExpect(status().isOk())
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString();

        return marshaller.unmarshal(response, Startup.class);
    }

    public MvcResult aStartupIsSuccessfullyUpdated(Long id, StartupRequest request) throws Exception {
        String jsonContent = marshaller.marshal(request);
        return performRestUpdateCall(ENTITY_URL_NAME, id, jsonContent).andExpect(status().isOk()).andReturn();
    }

    public Startup aStartupIsSuccessfullyFetchedById(Long id) throws Exception {
        String response = performRestReadCall(ENTITY_URL_NAME, id).andExpect(status().isOk())
                                                                  .andReturn()
                                                                  .getResponse()
                                                                  .getContentAsString();
        return marshaller.unmarshal(response, Startup.class);
    }

    public Startup aStartupIsIsSuccessfullyFetchedByName(String name) throws Exception {
        String response = getStartupByName(name).andExpect(status().isOk())
                                                .andReturn()
                                                .getResponse()
                                                .getContentAsString();
        return marshaller.unmarshal(response, Startup.class);
    }

    public List<Startup> allStartupsAreSuccessfullyFetched() throws Exception {
        String response = performRestReadAllCall(ENTITY_URL_NAME, new LinkedMultiValueMap<>()).andExpect(status().isOk())
                                                                                              .andReturn()
                                                                                              .getResponse()
                                                                                              .getContentAsString();

        return Arrays.stream(marshaller.unmarshal(response, Startup[].class)).toList();
    }

    private ResultActions getStartupByName(String name) throws Exception {
        return mockMvc.perform(get("/v1/startup/name/{name}", name));
    }
}
