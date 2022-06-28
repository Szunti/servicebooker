package hu.progmasters.servicebooker.controller.helper;

import com.jayway.jsonpath.JsonPath;
import hu.progmasters.servicebooker.controller.BookingCustomerController;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class BookingCustomerHelper {

    private final MockMvc mockMvc;

    private final SpecificPeriodHelper specificPeriod;

    public BookingCustomerHelper(MockMvc mockMvc, SpecificPeriodHelper specificPeriod) {
        this.mockMvc = mockMvc;
        this.specificPeriod = specificPeriod;
    }

    private String urlForCustomer(int customerId) {
        return BookingCustomerController.BASE_URL + "/" + customerId + BookingCustomerController.SUB_URL;
    }

    public ResultActions save(int customerId, String jsonCommand) throws Exception {
        return mockMvc.perform(post(urlForCustomer(customerId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions save(int customerId, int booseId, String start, String end, String comment) throws Exception {
        String jsonCommand = String.format(
                "{\"start\": \"%s\", \"end\": \"%s\", \"comment\": \"%s\", \"booseId\": %s}",
                start, end, comment, booseId);
        return save(customerId, jsonCommand);
    }

    public int saveAndGetId(int customerId, int booseId, String start, String end, String comment) throws Exception {
        MvcResult saveResult = save(customerId, booseId, start, end, comment).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions saveWithPeriod(int customerId, int booseId, String start, String end, String comment)
            throws Exception {
        specificPeriod.save(booseId, start, end, "generated", SpecificPeriodType.ADD_OR_REPLACE);
        return save(customerId, booseId, start, end, comment);
    }

    public int saveWithPeriodAndGetId(int customerId, int booseId, String start, String end, String comment)
            throws Exception {
        MvcResult saveResult = saveWithPeriod(customerId, booseId, start, end, comment).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions findAll(int customerId, String start, String end) throws Exception {
        String queryParams = String.format("?start=%s&end=%s", start, end);
        return mockMvc.perform(get(urlForCustomer(customerId) + queryParams));
    }

    public ResultActions findById(int customerId, int id) throws Exception {
        return mockMvc.perform(get(urlForCustomer(customerId) + "/" + id));
    }

    public ResultActions update(int customerId, int id, String comment) throws Exception {
        String jsonCommand = String.format("{\"comment\": \"%s\"}", comment);
        return mockMvc.perform(put(urlForCustomer(customerId) + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCommand));
    }

    public ResultActions deleteById(int customerId, int id) throws Exception {
        return mockMvc.perform(delete(urlForCustomer(customerId) + "/" + id));
    }


}
