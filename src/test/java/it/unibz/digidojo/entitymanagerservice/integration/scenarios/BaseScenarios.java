package it.unibz.digidojo.entitymanagerservice.integration.scenarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public abstract class BaseScenarios {
    private final MockMvc mockMvc;

    public BaseScenarios(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    protected ResultActions performRestCreateCall(String entity, String jsonContent) throws Exception {
        return mockMvc.perform(
                post("/v1/{entity}", entity)
                        .contentType("application/json")
                        .content(jsonContent)
        );
    }

    protected ResultActions performRestReadCall(String entity, Long id) throws Exception {
        return mockMvc.perform(get("/v1/{entity}/{id}", entity, id));
    }

    protected ResultActions performRestReadAllCall(String entity, MultiValueMap<String, String> queryParams) throws Exception {
        return mockMvc.perform(get("/v1/{entity}", entity).queryParams(queryParams));
    }

    protected ResultActions performRestUpdateCall(String entity, Long id, String jsonContent) throws Exception {
        return mockMvc.perform(
                patch("/v1/{entity}/{id}", entity, id)
                        .contentType("application/json")
                        .content(jsonContent)
        );
    }

    protected ResultActions performRestDeleteCall(String entity, Long id) throws Exception {
        return mockMvc.perform(delete("/v1/{entity}/{id}", entity, id));
    }

    protected <K, V> MultiValueMap<K, V> mapToMultiValueMap(Map<K, V> map) {
        MultiValueMap<K, V> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach(multiValueMap::add);
        return multiValueMap;
    }
}
