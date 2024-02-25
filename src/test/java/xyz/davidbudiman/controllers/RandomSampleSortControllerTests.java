package xyz.davidbudiman.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import xyz.davidbudiman.KeepUpToDateApplication;
import xyz.davidbudiman.controllers.records.Response;
import xyz.davidbudiman.services.records.RandomSampleSortRecord;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KeepUpToDateApplication.class)
@AutoConfigureMockMvc
public class RandomSampleSortControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnRandomSampleRecord() throws Exception {
        MvcResult result = this.mockMvc.perform(
                get("/random-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        TypeReference<Response<RandomSampleSortRecord>> typeReference = new TypeReference<>() {};
        Response<RandomSampleSortRecord> response = objectMapper.readValue(contentAsString, typeReference);

        assertNull(response.error());
        assertNotNull(response.data());
        RandomSampleSortRecord record = response.data();
        assertFalse(record.samples().isEmpty());
        assertTrue(Ordering.natural().isOrdered(record.result()));
        assertTrue(record.elapsedTime() > 0);
    }
}
