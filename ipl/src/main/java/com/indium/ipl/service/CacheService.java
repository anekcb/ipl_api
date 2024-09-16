package com.indium.ipl.service;

import com.indium.ipl.repository.DeliveryRepository;
import com.indium.ipl.repository.MatchRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CacheService provides several caching methods to improve performance in retrieving match and player statistics.
 */
@Service
public class CacheService {

    @Cacheable(value = "playerMatchesCache", key = "#playerName")
    public List<Object[]> getMatchesByPlayerName(String playerName, MatchRepository matchRepository) {
        return matchRepository.findMatchNumberAndPlayerNameByPlayerName(playerName);
    }

    @Cacheable(value = "cumulativeScoreCache", key = "#playerName")
    public Long getCumulativeScoreByPlayerName(String playerName, DeliveryRepository deliveryRepository) {
        return deliveryRepository.getCumulativeScoreByPlayerName(playerName);
    }

    @Cacheable(value = "scoresByDateCache", key = "#matchDate")
    public Map<Integer, Long> getScoresByDate(String matchDate, DeliveryRepository deliveryRepository) {
        List<Object[]> results = deliveryRepository.findScoresByMatchDate(LocalDate.parse(matchDate));
        Map<Integer, Long> matchScores = new HashMap<>();
        for (Object[] result : results) {
            Integer matchId = (Integer) result[0];
            Long totalRuns = (Long) result[1];
            matchScores.put(matchId, totalRuns);
        }
        return matchScores;
    }

    @Cacheable(value = "topBatsmenCache", key = "#page + '-' + #size")
    public Page<Object[]> getTopBatsmen(int page, int size, DeliveryRepository deliveryRepository) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryRepository.findTopBatsmenByRunsAsc(pageable);
    }

    @CacheEvict(value = {"playerMatchesCache", "cumulativeScoreCache", "scoresByDateCache", "topBatsmenCache"}, allEntries = true)
    public void evictAllCaches() {
    }
}
