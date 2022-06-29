package hu.progmasters.servicebooker.controller.helper;

import com.jayway.jsonpath.JsonPath;
import hu.progmasters.servicebooker.controller.SpecificPeriodController;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class SpecificPeriodHelper {

    private final MockMvc mockMvc;

    public SpecificPeriodHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private String urlForBoose(int booseId) {
        return SpecificPeriodController.BASE_URL + "/" + booseId + SpecificPeriodController.SUB_URL;
    }

    public ResultActions save(int booseId, String jsonCommand) throws Exception {
        return mockMvc.perform(post(urlForBoose(booseId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions save(int booseId, String start, String end,
                              String comment, SpecificPeriodType type) throws Exception {
        String jsonCommand = String.format(
                "{\"start\": \"%s\", \"end\": \"%s\", \"comment\": \"%s\", \"type\": \"%s\"}",
                start, end, comment, type);
        return save(booseId, jsonCommand);
    }

    public int saveAndGetId(int booseId, String start, String end,
                            String comment, SpecificPeriodType type) throws Exception {
        MvcResult saveResult = save(booseId, start, end, comment, type).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions findAll(int booseId, String start, String end, SpecificPeriodType type) throws Exception {
        String queryParams = String.format("?start=%s&end=%s%s", start, end, type != null ? "&type=" + type : "");
        return mockMvc.perform(get(urlForBoose(booseId) + queryParams));
    }

    public ResultActions findById(int booseId, int id) throws Exception {
        return mockMvc.perform(get(urlForBoose(booseId) + "/" + id));
    }

    public ResultActions update(int booseId, int id, String comment) throws Exception {
        String jsonCommand = String.format("{\"comment\": \"%s\"}", comment);
        return mockMvc.perform(put(urlForBoose(booseId) + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }
}
