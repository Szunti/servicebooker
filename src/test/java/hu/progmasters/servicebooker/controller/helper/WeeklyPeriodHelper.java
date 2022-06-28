package hu.progmasters.servicebooker.controller.helper;

import com.jayway.jsonpath.JsonPath;
import hu.progmasters.servicebooker.controller.WeeklyPeriodController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class WeeklyPeriodHelper {

    private final MockMvc mockMvc;

    public WeeklyPeriodHelper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private String urlForBoose(int booseId) {
        return WeeklyPeriodController.BASE_URL + "/" + booseId + WeeklyPeriodController.SUB_URL;
    }

    public ResultActions save(int booseId, String jsonCommand) throws Exception {
        return mockMvc.perform(post(urlForBoose(booseId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions save(int booseId, String start, String end, String comment) throws Exception {
        String jsonCommand = String.format(
                "{\"start\": \"%s\", \"end\": \"%s\", \"comment\": \"%s\"}", start, end, comment);
        return save(booseId, jsonCommand);
    }

    public int saveAndGetId(int booseId, String start, String end, String comment) throws Exception {
        MvcResult saveResult = save(booseId, start, end, comment).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions findAll(int booseId) throws Exception {
        return mockMvc.perform(get(urlForBoose(booseId)));
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
