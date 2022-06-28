package hu.progmasters.servicebooker.controller.helper;

import com.jayway.jsonpath.JsonPath;
import hu.progmasters.servicebooker.controller.BooseController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class BooseHelper {

    private final MockMvc mockMvc;

    public BooseHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions save(String jsonCommand) throws Exception {
        return mockMvc.perform(post(BooseController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions save(String name, String description) throws Exception {
        String jsonCommand = String.format("{\"name\": \"%s\", \"description\": \"%s\"}", name, description);
        return save(jsonCommand);
    }

    public int saveAndGetId(String name, String description) throws Exception {
        MvcResult saveResult = save(name, description).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions findAll() throws Exception {
        return mockMvc.perform(get(BooseController.BASE_URL));
    }

    public ResultActions findById(int id) throws Exception {
        return mockMvc.perform(get(BooseController.BASE_URL + "/" + id));
    }

    public ResultActions update(int id, String jsonCommand) throws Exception {
        return mockMvc.perform(put(BooseController.BASE_URL + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions update(int id, String name, String description) throws Exception {
        String jsonCommand = String.format("{\"name\": \"%s\", \"description\": \"%s\"}", name, description);
        return update(id, jsonCommand);
    }

    public ResultActions deleteById(int id) throws Exception {
        return mockMvc.perform(delete(BooseController.BASE_URL + "/" + id));
    }
}
