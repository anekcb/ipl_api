package com.indium.ipl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.ipl.controller.MatchController;
import com.indium.ipl.repository.DeliveryRepository;
import com.indium.ipl.repository.MatchRepository;
import com.indium.ipl.service.CacheService;
import com.indium.ipl.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MatchController.class)
public class MatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private MatchRepository matchRepository;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenInsertMatchDataThenReturns201() throws Exception {
        String jsonData = "{\"key\": \"value\"}";

        mockMvc.perform(post("/api/matches/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isCreated())
                .andExpect(content().string("Match data inserted successfully!"));
    }

    @Test
    public void whenInsertInvalidMatchDataThenReturns400() throws Exception {
        String jsonData = "{\"invalid\": }";
        String errorMessage = "Unrecognized token '}'";

        doThrow(new IOException(errorMessage)).when(matchService).insertMatchData(any(String.class));

        mockMvc.perform(post("/api/matches/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to parse JSON data: " + errorMessage));
    }

    @Test
    public void whenInsertMatchDataWithExceptionThenReturns500() throws Exception {
        String jsonData = "{\"key\": \"value\"}";
        String errorMessage = "Test Exception";

        doThrow(new RuntimeException(errorMessage)).when(matchService).insertMatchData(any(String.class));

        mockMvc.perform(post("/api/matches/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error inserting match data: " + errorMessage));
    }

    @Test
    public void whenGetMatchesByPlayerNameThenReturns200() throws Exception {
        String playerName = "player";
        when(cacheService.getMatchesByPlayerName(playerName, matchRepository)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/matches/player/{playerName}", playerName))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void whenGetCumulativeScoreThenReturns200() throws Exception {
        String playerName = "player";
        when(cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository)).thenReturn(100L);

        mockMvc.perform(get("/api/matches/cumulative-score")
                        .param("playerName", playerName))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    public void whenGetScoresByDateThenReturns200() throws Exception {
        String matchDate = "2021-01-01";
        when(cacheService.getScoresByDate(matchDate, deliveryRepository)).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/matches/score")
                        .param("matchDate", matchDate))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    public void whenGetTopBatsmenThenReturns200() throws Exception {
        int page = 0;
        int size = 10;
        Page<Object[]> emptyPage = Page.empty();

        when(cacheService.getTopBatsmen(page, size, deliveryRepository)).thenReturn(emptyPage);

        mockMvc.perform(get("/api/matches/topbatsmen")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[],\"pageable\":\"INSTANCE\",\"totalPages\":1,\"totalElements\":0,\"last\":true,\"number\":0,\"size\":0,\"sort\":{\"sorted\":false,\"unsorted\":true,\"empty\":true},\"numberOfElements\":0,\"first\":true,\"empty\":true}"));
    }
}