package hu.progmasters.servicebooker.controller.helper;

import com.jayway.jsonpath.JsonPath;
import hu.progmasters.servicebooker.controller.BookingBooseController;
import hu.progmasters.servicebooker.domain.entity.SpecificPeriodType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class BookingBooseHelper {

    private final MockMvc mockMvc;

    private final BookingCustomerHelper bookingCustomerHelper;
    private final SpecificPeriodHelper specificPeriod;

    public BookingBooseHelper(MockMvc mockMvc, BookingCustomerHelper bookingCustomerHelper, SpecificPeriodHelper specificPeriod) {
        this.mockMvc = mockMvc;
        this.bookingCustomerHelper = bookingCustomerHelper;
        this.specificPeriod = specificPeriod;
    }

    private String urlForBoose(int booseId) {
        return BookingBooseController.BASE_URL + "/" + booseId + BookingBooseController.SUB_URL;
    }

    public ResultActions save(int booseId, int customerId, String start, String end, String comment) throws Exception {
        return bookingCustomerHelper.save(customerId, booseId, start, end, comment);
    }

    public int saveAndGetId(int booseId, int customerId, String start, String end, String comment) throws Exception {
        return bookingCustomerHelper.saveAndGetId(customerId, booseId, start, end, comment);
    }

    public ResultActions saveWithPeriod(int booseId, int customerId, String start, String end, String comment)
            throws Exception {
        specificPeriod.save(booseId, start, end, "generated", SpecificPeriodType.ADD_OR_REPLACE);
        return save(booseId, customerId, start, end, comment);
    }

    public int saveWithPeriodAndGetId(int booseId, int customerId, String start, String end, String comment)
            throws Exception {
        MvcResult saveResult = saveWithPeriod(booseId, customerId, start, end, comment).andReturn();
        return JsonPath.read(saveResult.getResponse().getContentAsString(), "$.id");
    }

    public ResultActions findAll(int booseId, String start, String end) throws Exception {
        String queryParams = String.format("?start=%s&end=%s", start, end);
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

    public ResultActions deleteById(int booseId, int id) throws Exception {
        return mockMvc.perform(delete(urlForBoose(booseId) + "/" + id));
    }
}
