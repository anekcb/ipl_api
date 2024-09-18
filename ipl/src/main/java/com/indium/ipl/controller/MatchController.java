package com.indium.ipl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.ipl.repository.DeliveryRepository;
import com.indium.ipl.repository.MatchRepository;
import com.indium.ipl.service.CacheService;
import com.indium.ipl.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * MatchController is a REST controller that manages APIs related to match data.
 * It contains endpoints to insert match data, fetch match information by player name,
 * get cumulative scores, scores by match date, and top batsmen information.
 */
@Tag(name = "Match Data", description = "Endpoints to manage match data")
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Insert match data",
            description = "Insert match data into the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match data inserted successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to parse JSON data"),
            @ApiResponse(responseCode = "500", description = "Error inserting match data")
    })
    @PostMapping("/insert")
    public ResponseEntity<String> insertMatchData(@Parameter(description = "JSON formatted match data", required = true)
                                                  @RequestBody String jsonData) {
        try {
            matchService.insertMatchData(jsonData);
            cacheService.evictAllCaches();
            return new ResponseEntity<>("Match data inserted successfully!", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to parse JSON data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error inserting match data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get matches by player name",
            description = "Fetch matches information by a player's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/player/{playerName}")
    public ResponseEntity<List<Object[]>> getMatchesByPlayerName(@Parameter(description = "Name of the player", example = "Sachin Tendulkar", required = true)
                                                                 @PathVariable String playerName) {
        List<Object[]> matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);
        try {
            String jsonMatches = objectMapper.writeValueAsString(matches);
            logger.info(jsonMatches);
            kafkaTemplate.send("match-logs-topic", jsonMatches);
        } catch (IOException e) {
            logger.info("Error serializing matches data: " + e.getMessage());
            kafkaTemplate.send("match-logs-topic", "Error serializing matches data: " + e.getMessage());
        }
        return ResponseEntity.ok(matches);
    }

    @Operation(summary = "Get cumulative score by player name",
            description = "Get cumulative score of a player by their name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cumulative score retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/cumulative-score")
    public Long getCumulativeScore(@Parameter(description = "Name of the player", example = "Sachin Tendulkar", required = true)
                                   @RequestParam("playerName") String playerName) {
        Long score = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);
        try {
            String jsonScore = objectMapper.writeValueAsString(score);
            logger.info(jsonScore);
            kafkaTemplate.send("match-logs-topic", jsonScore);
        } catch (IOException e) {
            logger.info("Error serializing cumulative score data: " + e.getMessage());
            kafkaTemplate.send("match-logs-topic", "Error serializing cumulative score data: " + e.getMessage());
        }
        return score;
    }

    @Operation(summary = "Get scores by match date",
            description = "Get scores by match date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scores retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/score")
    public Map<Integer, Long> getScoresByDate(@Parameter(description = "Date of the match", example = "2023-04-15", required = true)
                                              @RequestParam String matchDate) {
        Map<Integer, Long> scores = cacheService.getScoresByDate(matchDate, deliveryRepository);
        try {
            String jsonScores = objectMapper.writeValueAsString(scores);
            logger.info(jsonScores);
            kafkaTemplate.send("match-logs-topic", jsonScores);
        } catch (IOException e) {
            logger.info("Error serializing scores data: " + e.getMessage());
            kafkaTemplate.send("match-logs-topic", "Error serializing scores data: " + e.getMessage());
        }
        return scores;
    }

    @Operation(summary = "Get top batsmen",
            description = "Get a paginated list of top batsmen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top batsmen retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/topbatsmen")
    public Page<Object[]> getTopBatsmen(@Parameter(description = "Page number for pagination", example = "0")
                                        @RequestParam(defaultValue = "0") int page,
                                        @Parameter(description = "Number of records per page", example = "10")
                                        @RequestParam(defaultValue = "10") int size) {
        Page<Object[]> batsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);
        try {
            String jsonBatsmen = objectMapper.writeValueAsString(batsmen.getContent());
            logger.info(jsonBatsmen);
            kafkaTemplate.send("match-logs-topic", jsonBatsmen);
        } catch (IOException e) {
            logger.info("Error serializing top batsmen data: " + e.getMessage());
            kafkaTemplate.send("match-logs-topic", "Error serializing top batsmen data: " + e.getMessage());
        }
        return batsmen;
    }
}