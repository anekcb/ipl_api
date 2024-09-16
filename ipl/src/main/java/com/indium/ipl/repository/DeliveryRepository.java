//package com.indium.ipl.repository;
//
//import com.indium.ipl.Entity.Delivery;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
//
//    // Cumulative score by player name
////    @Query("SELECT SUM(d.runsTotal) as cumulativeScore FROM Delivery d " +
////            "WHERE d.batter.name = :playerName " +
////            "GROUP BY d.batter.name")
//    @Query("SELECT SUM(d.runsTotal) as cumulativeScore FROM Delivery d " +
//            "JOIN d.batter p " +
//            "WHERE p.name = :playerName " +
//            "GROUP BY p.name")
//    Object[] getCumulativeScoreByPlayerName(@Param("playerName") String playerName);
//
//    // Wickets by player name (as bowler or playerOut)
//    @Query("SELECT COUNT(d) FROM Delivery d " +
//            "WHERE (d.bowler.name = :playerName OR d.playerOut.name = :playerName) " +
//            "AND d.wicketKind IS NOT NULL")
//    Long findWicketsByPlayerName(@Param("playerName") String playerName);
//
//    // Scores by match date
//    @Query("SELECT d.match.matchId, SUM(d.runsTotal) AS totalRuns FROM Delivery d " +
//            "WHERE d.match.matchDate = :matchDate " +
//            "GROUP BY d.match.matchId")
//    List<Object[]> findScoresByMatchDate(@Param("matchDate") LocalDate matchDate);
//
//    // Top batsmen by runs, ordered in ascending order
//    @Query("SELECT d.batter.name, SUM(d.runsBatter) AS totalRuns " +
//            "FROM Delivery d " +
//            "GROUP BY d.batter.name " +
//            "ORDER BY totalRuns ASC")
//    Page<Object[]> findTopBatsmenByRunsAsc(Pageable pageable);
//}
package com.indium.ipl.repository;

import com.indium.ipl.Entity.Delivery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;

public interface DeliveryRepository extends CrudRepository<Delivery,Integer> {

    @Query("SELECT SUM(d.runsTotal) as cumulativeScore FROM Delivery d " +
            "JOIN d.batter p " +
            "WHERE p.name = :playerName " +
            "GROUP BY p.name")
    Long getCumulativeScoreByPlayerName(@Param("playerName") String playerName);

    @Query("SELECT COUNT(d) FROM Delivery d " +
            "WHERE (d.bowler.name = :playerName OR d.playerOut.name = :playerName) " +
            "AND d.wicketKind IS NOT NULL")
    Long findWicketsByPlayerName(@Param("playerName") String playerName);

    @Query("SELECT d.match.matchId, SUM(d.runsTotal) AS totalRuns FROM Delivery d " +
            "WHERE d.match.matchDate = :matchDate " +
            "GROUP BY d.match.matchId")
    List<Object[]> findScoresByMatchDate(@Param("matchDate") LocalDate matchDate);

    @Query("SELECT d.batter.name, SUM(d.runsBatter) AS totalRuns " +
            "FROM Delivery d " +
            "GROUP BY d.batter.name " +
            "ORDER BY totalRuns ASC")
    Page<Object[]> findTopBatsmenByRunsAsc(Pageable pageable);
}
