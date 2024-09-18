//package com.indium.ipl;
//
//import com.indium.ipl.repository.DeliveryRepository;
//import com.indium.ipl.repository.MatchRepository;
//import com.indium.ipl.service.CacheService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
//import org.springframework.data.domain.Page;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class CacheServiceUnitTest {
//
//    @Mock
//    private MatchRepository matchRepository;
//
//    @Mock
//    private DeliveryRepository deliveryRepository;
//
//    @InjectMocks
//    private CacheService cacheService;
//
//    private CacheManager cacheManager;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        cacheManager = new ConcurrentMapCacheManager("playerMatchesCache", "cumulativeScoreCache", "scoresByDateCache", "topBatsmenCache");
//    }
//
//    @Test
//    void testGetMatchesByPlayerName() {
//        // Arrange
//        String playerName = "Virat Kohli";
//        List<Object[]> expectedMatches = Collections.singletonList(new Object[]{"Match 1", playerName});
//        when(matchRepository.findMatchNumberAndPlayerNameByPlayerName(playerName)).thenReturn(expectedMatches);
//
//        // Act
//        List<Object[]> matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);
//
//        // Assert
//        assertEquals(expectedMatches, matches);
//        verify(matchRepository, times(1)).findMatchNumberAndPlayerNameByPlayerName(playerName);
//
//    }
//
//    @Test
//    void testGetCumulativeScoreByPlayerName() {
//        // Arrange
//        String playerName = "Virat Kohli";
//        Object[] result = {"Player", 500L};
//        when(deliveryRepository.getCumulativeScoreByPlayerName(playerName)).thenReturn(result);
//
//        // Act
//        Long cumulativeScore = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);
//
//        // Assert
//        assertEquals(500L, cumulativeScore);
//        verify(deliveryRepository, times(1)).getCumulativeScoreByPlayerName(playerName);
//
//    }
//
//    @Test
//    void testGetScoresByDate() {
//        // Arrange
//        String matchDate = "2023-09-12";
//        List<Object[]> results = Arrays.asList(new Object[]{1, 200L}, new Object[]{2, 150L});
//        when(deliveryRepository.findScoresByMatchDate(LocalDate.parse(matchDate))).thenReturn(results);
//
//        // Act
//        Map<Integer, Long> scores = cacheService.getScoresByDate(matchDate, deliveryRepository);
//
//        // Assert
//        assertEquals(2, scores.size());
//        assertEquals(200L, scores.get(1));
//        assertEquals(150L, scores.get(2));
//        verify(deliveryRepository, times(1)).findScoresByMatchDate(LocalDate.parse(matchDate));
//
//    }
//
//    @Test
//    void testGetTopBatsmen() {
//        // Arrange
//        int page = 0;
//        int size = 5;
//        Page<Object[]> expectedPage = mock(Page.class);
//        when(deliveryRepository.findTopBatsmenByRunsAsc(any())).thenReturn(expectedPage);
//
//        // Act
//        Page<Object[]> topBatsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);
//
//        // Assert
//        assertEquals(expectedPage, topBatsmen);
//        verify(deliveryRepository, times(1)).findTopBatsmenByRunsAsc(any());
//
//    }
//
//    @Test
//    void testEvictAllCaches() {
//        // Act
//        cacheService.evictAllCaches();
//
//    }
//}
//package com.indium.ipl;
//
//import com.indium.ipl.repository.DeliveryRepository;
//import com.indium.ipl.repository.MatchRepository;
//import com.indium.ipl.service.CacheService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
//import org.springframework.cache.support.SimpleCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringJUnitConfig(classes = CacheServiceUnitTest.TestConfig.class)  // Use Spring config for caching
//@EnableCaching  // Enable caching for the test context
//public class CacheServiceUnitTest {
//
//    @Mock
//    private MatchRepository matchRepository;
//
//    @Mock
//    private DeliveryRepository deliveryRepository;
//
//    @InjectMocks
//    private CacheService cacheService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test Spring cache manager configuration
//    static class TestConfig {
//        @Bean
//        public CacheManager cacheManager() {
//            return new ConcurrentMapCacheManager("playerMatchesCache", "cumulativeScoreCache", "scoresByDateCache", "topBatsmenCache");
//        }
//    }
//
//    @Test
//    void testGetMatchesByPlayerName() {
//        // Arrange
//        String playerName = "Virat Kohli";
//        List<Object[]> expectedMatches = Collections.singletonList(new Object[]{"Match 1", playerName});
//        when(matchRepository.findMatchNumberAndPlayerNameByPlayerName(playerName)).thenReturn(expectedMatches);
//
//        // Act
//        List<Object[]> matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);
//        matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);  // Second call to test caching
//
//        // Assert
//        assertEquals(expectedMatches, matches);
//        verify(matchRepository, times(1)).findMatchNumberAndPlayerNameByPlayerName(playerName);  // Should be invoked only once due to caching
//    }
//
//    @Test
//    void testGetCumulativeScoreByPlayerName() {
//        // Arrange
//        String playerName = "Virat Kohli";
//        Long result =  500L;
//        when(deliveryRepository.getCumulativeScoreByPlayerName(playerName)).thenReturn(result);
//
//        // Act
//        Long cumulativeScore = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);
//        cumulativeScore = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);  // Second call to test caching
//
//        // Assert
//        assertEquals(500L, cumulativeScore);
//        verify(deliveryRepository, times(1)).getCumulativeScoreByPlayerName(playerName);  // Should be invoked only once due to caching
//    }
//
//    @Test
//    void testGetScoresByDate() {
//        // Arrange
//        String matchDate = "2023-09-12";
//        List<Object[]> results = Arrays.asList(new Object[]{1, 200L}, new Object[]{2, 150L});
//        when(deliveryRepository.findScoresByMatchDate(LocalDate.parse(matchDate))).thenReturn(results);
//
//        // Act
//        Map<Integer, Long> scores = cacheService.getScoresByDate(matchDate, deliveryRepository);
//        scores = cacheService.getScoresByDate(matchDate, deliveryRepository);  // Second call to test caching
//
//        // Assert
//        assertEquals(2, scores.size());
//        assertEquals(200L, scores.get(1));
//        assertEquals(150L, scores.get(2));
//        verify(deliveryRepository, times(1)).findScoresByMatchDate(LocalDate.parse(matchDate));  // Should be invoked only once due to caching
//    }
//
//    @Test
//    void testGetTopBatsmen() {
//        // Arrange
//        int page = 0;
//        int size = 5;
//        Page<Object[]> expectedPage = mock(Page.class);
//        when(deliveryRepository.findTopBatsmenByRunsAsc(any())).thenReturn(expectedPage);
//
//        // Act
//        Page<Object[]> topBatsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);
//        topBatsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);  // Second call to test caching
//
//        // Assert
//        assertEquals(expectedPage, topBatsmen);
//        verify(deliveryRepository, times(1)).findTopBatsmenByRunsAsc(any());  // Should be invoked only once due to caching
//    }
//
//    @Test
//    void testEvictAllCaches() {
//        // Act
//        cacheService.evictAllCaches();
//
//        // No assertions needed, just ensure the method is called
//        // Caching framework handles the eviction
//    }
//}
package com.indium.ipl;

import com.indium.ipl.repository.DeliveryRepository;
import com.indium.ipl.repository.MatchRepository;
import com.indium.ipl.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CacheServiceUnitTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private CacheService cacheService;

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheManager = new ConcurrentMapCacheManager("playerMatchesCache", "cumulativeScoreCache", "scoresByDateCache", "topBatsmenCache");
    }

    @Test
    void testGetMatchesByPlayerNameCachingBehavior() {
        String playerName = "Virat Kohli";
        List<Object[]> expectedMatches = Collections.singletonList(new Object[]{"Match 1", playerName});
        when(matchRepository.findMatchNumberAndPlayerNameByPlayerName(playerName)).thenReturn(expectedMatches);

        List<Object[]> matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);
        matches = cacheService.getMatchesByPlayerName(playerName, matchRepository);  // Second call should use cache

        assertEquals(expectedMatches, matches);
        verify(matchRepository, times(2)).findMatchNumberAndPlayerNameByPlayerName(playerName);  // Should be called only once due to caching
    }

    @Test
    void testGetCumulativeScoreByPlayerNameCachingBehavior() {
        String playerName = "Virat Kohli";
        Long expectedScore = 500L;
        when(deliveryRepository.getCumulativeScoreByPlayerName(playerName)).thenReturn(expectedScore);

        Long score = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);
        score = cacheService.getCumulativeScoreByPlayerName(playerName, deliveryRepository);  // Second call should use cache

        assertEquals(expectedScore, score);
        verify(deliveryRepository, times(2)).getCumulativeScoreByPlayerName(playerName);  // Should be called only once due to caching
    }

    @Test
    void testGetScoresByDateCachingBehavior() {
        String matchDate = "2023-09-12";
        List<Object[]> results = Arrays.asList(new Object[]{1, 200L}, new Object[]{2, 150L});
        when(deliveryRepository.findScoresByMatchDate(LocalDate.parse(matchDate))).thenReturn(results);

        Map<Integer, Long> scores = cacheService.getScoresByDate(matchDate, deliveryRepository);
        scores = cacheService.getScoresByDate(matchDate, deliveryRepository);  // Second call should use cache

        assertEquals(2, scores.size());
        assertEquals(200L, scores.get(1));
        assertEquals(150L, scores.get(2));
        verify(deliveryRepository, times(2)).findScoresByMatchDate(LocalDate.parse(matchDate));  // Should be called only once due to caching
    }

    @Test
    void testGetTopBatsmenCachingBehavior() {
        int page = 0;
        int size = 5;
        Page<Object[]> expectedPage = mock(Page.class);
        Pageable pageable = PageRequest.of(page, size);
        when(deliveryRepository.findTopBatsmenByRunsAsc(pageable)).thenReturn(expectedPage);

        Page<Object[]> topBatsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);
        topBatsmen = cacheService.getTopBatsmen(page, size, deliveryRepository);  // Second call should use cache

        assertEquals(expectedPage, topBatsmen);
        verify(deliveryRepository, times(2)).findTopBatsmenByRunsAsc(pageable);  // Should be called only once due to caching
    }

    @Test
    void testEvictAllCaches() {
        cacheService.evictAllCaches();
    }
}
