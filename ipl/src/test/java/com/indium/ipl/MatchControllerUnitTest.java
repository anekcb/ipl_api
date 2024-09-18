package com.indium.ipl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.ipl.controller.MatchController;
import com.indium.ipl.repository.DeliveryRepository;
import com.indium.ipl.repository.MatchRepository;
import com.indium.ipl.service.CacheService;
import com.indium.ipl.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MatchControllerUnitTest {

	@InjectMocks
	private MatchController matchController;

	@Mock
	private MatchService matchService;

	@Mock
	private MatchRepository matchRepository;

	@Mock
	private DeliveryRepository deliveryRepository;

	@Mock
	private CacheService cacheService;

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInsertMatchDataSuccess() throws Exception {
		String jsonData = "{}";

		doNothing().when(matchService).insertMatchData(jsonData);
		doNothing().when(cacheService).evictAllCaches();

		ResponseEntity<String> response = matchController.insertMatchData(jsonData);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Match data inserted successfully!", response.getBody());
		verify(matchService, times(1)).insertMatchData(jsonData);
		verify(cacheService, times(1)).evictAllCaches();
	}

	@Test
	public void testInsertMatchDataJsonParseException() throws Exception {
		String jsonData = "{";

		doThrow(new IOException("Invalid JSON")).when(matchService).insertMatchData(jsonData);

		ResponseEntity<String> response = matchController.insertMatchData(jsonData);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Failed to parse JSON data: Invalid JSON", response.getBody());
	}

	@Test
	public void testInsertMatchDataOtherException() throws Exception {
		String jsonData = "{}";

		doThrow(new RuntimeException("Unexpected Error")).when(matchService).insertMatchData(jsonData);

		ResponseEntity<String> response = matchController.insertMatchData(jsonData);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Error inserting match data: Unexpected Error", response.getBody());
	}

	@Test
	public void testGetMatchesByPlayerName() throws Exception {
		String playerName = "John Doe";
		List<Object[]> matches = Collections.singletonList(new Object[]{});

		when(cacheService.getMatchesByPlayerName(playerName, matchRepository)).thenReturn(matches);
		when(objectMapper.writeValueAsString(matches)).thenReturn("[]");

		ResponseEntity<List<Object[]>> response = matchController.getMatchesByPlayerName(playerName);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(matches, response.getBody());
		verify(kafkaTemplate, times(1)).send("match-logs-topic", "[]");
	}

	@Test
	public void testGetCumulativeScore() throws Exception {
		String playerName = "John Doe";
		Long score = 100L;

		when(cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository)).thenReturn(score);
		when(objectMapper.writeValueAsString(score)).thenReturn("100");

		Long result = matchController.getCumulativeScore(playerName);

		assertEquals(score, result);
		verify(kafkaTemplate, times(1)).send("match-logs-topic", "100");
	}

	@Test
	public void testGetScoresByDate() throws Exception {
		String matchDate = "2022-01-01";
		Map<Integer, Long> scores = Collections.singletonMap(1, 100L);

		when(cacheService.getScoresByDate(matchDate, deliveryRepository)).thenReturn(scores);
		when(objectMapper.writeValueAsString(scores)).thenReturn("{\"1\":100}");

		Map<Integer, Long> result = matchController.getScoresByDate(matchDate);

		assertEquals(scores, result);
		verify(kafkaTemplate, times(1)).send("match-logs-topic", "{\"1\":100}");
	}

	@Test
	public void testGetTopBatsmen() throws Exception {
		int page = 0;
		int size = 10;
		List<Object[]> batsmenList = Collections.singletonList(new Object[]{});
		Page<Object[]> batsmen = new PageImpl<>(batsmenList);

		when(cacheService.getTopBatsmen(page, size, deliveryRepository)).thenReturn(batsmen);
		when(objectMapper.writeValueAsString(batsmen.getContent())).thenReturn("[]");

		Page<Object[]> result = matchController.getTopBatsmen(page, size);

		assertEquals(batsmen, result);
		verify(kafkaTemplate, times(1)).send("match-logs-topic", "[]");
	}
}
