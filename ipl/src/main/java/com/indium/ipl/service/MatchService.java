package com.indium.ipl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indium.ipl.Entity.*;
import com.indium.ipl.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service class responsible for handling match data insertion and related operations.
 * This class uses various repositories to save match-related entities such as matches, events, teams,
 * players, officials, innings, overs, deliveries, etc.
 */
@Service
public class MatchService {

    @Autowired
    private MatchRepository matchesRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchTeamRepository matchTeamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamPlayerRepository teamPlayerRepository;

    @Autowired
    private OfficialRepository officialRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Autowired
    private OverRepository oversRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryFielderRepository deliveryFielderRepository;

    @Autowired
    private PowerplayRepository powerplayRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void insertMatchData(String jsonData) throws IOException {
        JsonNode root = objectMapper.readTree(jsonData);

        // Insert Match
        JsonNode infoNode = root.get("info");
        JsonNode metaNode = root.get("meta");
        Match match = new Match();
        match.setDataVersion(metaNode.get("data_version").asText());
        match.setCreated(LocalDate.parse(metaNode.get("created").asText()));
        match.setRevision(metaNode.get("revision").asInt());
        match.setCity(infoNode.get("city").asText());
        match.setMatchDate(LocalDate.parse(infoNode.path("dates").get(0).asText()));
        match.setGender(infoNode.get("gender").asText());
        match.setMatchType(infoNode.get("match_type").asText());
        match.setOvers(infoNode.get("overs").asInt());
        match.setSeason(infoNode.get("season").asText());
        match.setTeamType(infoNode.get("team_type").asText());
        match.setVenue(infoNode.get("venue").asText());
        match.setTossWinner(infoNode.get("toss").get("winner").asText());
        match.setTossDecision(infoNode.get("toss").get("decision").asText());
        match.setMatchWinner(infoNode.get("outcome").get("winner").asText());
        JsonNode outcomeNode = infoNode.get("outcome");
        if (outcomeNode != null) {
            JsonNode byNode = outcomeNode.get("by");

            if (byNode != null) {
                if (byNode.has("runs") && !byNode.get("runs").isNull()) {
                    match.setWinByRuns(byNode.get("runs").asInt());
                } else {
                    match.setWinByRuns(null);
                }

                if (byNode.has("wickets") && !byNode.get("wickets").isNull()) {
                    match.setWinByWickets(byNode.get("wickets").asInt());
                } else {
                    match.setWinByWickets(null);
                }
            } else {
                match.setWinByRuns(null);
                match.setWinByWickets(null);
            }
        }

        matchesRepository.save(match);

        JsonNode eventNode = infoNode.get("event");
        Event event = new Event();
        event.setMatch(match);
        event.setName(eventNode.get("name").asText());
        event.setMatchNumber(eventNode.get("match_number").asInt());
        eventRepository.save(event);

        Map<String, Team> teamMap = new HashMap<>();
        JsonNode teamsNode = infoNode.get("teams");
        for (JsonNode teamNode : teamsNode) {
            Team team = new Team();
            team.setName(teamNode.asText());
            teamRepository.save(team);
            teamMap.put(teamNode.asText(), team);
        }

        for (Team team : teamMap.values()) {
            MatchTeam matchTeam = new MatchTeam();
            matchTeam.setMatch(match);
            matchTeam.setTeam(team);
            matchTeamRepository.save(matchTeam);
        }

        Map<String, Player> playerMap = new HashMap<>();
        JsonNode playersNode = infoNode.get("players");

        if (playersNode != null) {
            playersNode.fieldNames().forEachRemaining(teamName -> {
                JsonNode playersList = playersNode.get(teamName);

                if (playersList != null && playersList.isArray()) {
                    for (JsonNode playerNode : playersList) {
                        Player player = getOrCreatePlayer(playerNode.asText());

                        if (player != null) {
                            playerMap.put(playerNode.asText(), player);
                        }
                    }
                } else {
                    System.out.println("No players found for team: " + teamName);
                }
            });
        } else {
            System.out.println("No players information found in the JSON.");
        }

        for (Map.Entry<String, Team> teamEntry : teamMap.entrySet()) {
            JsonNode playersList = playersNode.get(teamEntry.getKey());
            for (JsonNode playerNode : playersList) {
                TeamPlayer teamPlayer = new TeamPlayer();
                teamPlayer.setTeam(teamEntry.getValue());
                teamPlayer.setPlayer(playerMap.get(playerNode.asText()));
                teamPlayer.setMatch(match);
                teamPlayerRepository.save(teamPlayer);
            }
        }

        JsonNode officialsNode = infoNode.get("officials");
        insertOfficials(officialsNode.get("match_referees"), match, "Referee");
        insertOfficials(officialsNode.get("reserve_umpires"), match, "Reserve Umpire");
        insertOfficials(officialsNode.get("tv_umpires"), match, "TV Umpire");
        insertOfficials(officialsNode.get("umpires"), match, "Umpire");

        insertInningsAndOvers(root, match, teamMap);
    }

    private void insertOfficials(JsonNode officialsNode, Match match, String type) {
        for (JsonNode officialNode : officialsNode) {
            Official official = new Official();
            official.setMatch(match);
            official.setName(officialNode.asText());
            official.setOfficialType(type);
            officialRepository.save(official);
        }
    }

    private void insertInningsAndOvers(JsonNode root, Match match, Map<String, Team> teamMap) {
        JsonNode inningsArray = root.get("innings");
        if (inningsArray == null || !inningsArray.isArray()) return;

        for (JsonNode inningsNode : inningsArray) {
            String teamName = inningsNode.get("team").asText();
            Team team = teamMap.get(teamName);
            if (team == null) continue;

            Innings innings = new Innings();
            innings.setMatch(match);
            innings.setTeam(team);

            JsonNode targetNode = inningsNode.get("target");
            innings.setTargetRuns(targetNode != null && targetNode.has("runs") && !targetNode.get("runs").isNull()
                    ? targetNode.get("runs").asInt() : null);
            innings.setTargetOvers(targetNode != null && targetNode.has("overs") && !targetNode.get("overs").isNull()
                    ? targetNode.get("overs").asInt() : null);
            inningsRepository.save(innings);
            insertOversAndDeliveries(inningsNode.get("overs"), innings, match);
            insertPowerplayData(root, innings);
        }
    }


    private void insertOversAndDeliveries(JsonNode oversArray, Innings innings, Match match) {
        if (oversArray == null || !oversArray.isArray()) return;
        for (JsonNode overNode : oversArray) {
            int overNumber = overNode.get("over").asInt();
            Over over = new Over();
            over.setInnings(innings);
            over.setOverNumber(overNumber);
            oversRepository.save(over);
            insertDeliveries(overNode.get("deliveries"), over, match);
        }
    }

    private void insertDeliveries(JsonNode deliveriesArray, Over over, Match match) {
        if (deliveriesArray == null || !deliveriesArray.isArray()) return;

        for (JsonNode deliveryNode : deliveriesArray) {
            Delivery delivery = new Delivery();
            delivery.setOver(over);
            delivery.setMatch(match);
            delivery.setBatter(playerRepository.findByName(deliveryNode.get("batter").asText()));
            delivery.setBowler(playerRepository.findByName(deliveryNode.get("bowler").asText()));
            delivery.setNonStriker(playerRepository.findByName(deliveryNode.get("non_striker").asText()));
            delivery.setRunsBatter(deliveryNode.get("runs").get("batter").asInt());
            delivery.setRunsExtras(deliveryNode.get("runs").get("extras").asInt());
            delivery.setRunsTotal(deliveryNode.get("runs").get("total").asInt());
            delivery.setExtrasType(deliveryNode.has("extras_type") ? deliveryNode.get("extras_type").asText() : null);

            if (deliveryNode.has("wickets") && deliveryNode.get("wickets").isArray()) {
                for (JsonNode wicketNode : deliveryNode.get("wickets")) {
                    delivery.setWicketKind(wicketNode.get("kind").asText());
                    delivery.setPlayerOut(playerRepository.findByName(wicketNode.get("player_out").asText()));

                    deliveryRepository.save(delivery);
                    if (wicketNode.has("fielders") && wicketNode.get("fielders").isArray()) {
                        insertDeliveryFielders(wicketNode.get("fielders"), delivery);
                    }
                }
            } else {
                delivery.setWicketKind(null);
                delivery.setPlayerOut(null);
                deliveryRepository.save(delivery);
            }
        }
    }

    private void insertDeliveryFielders(JsonNode fieldersArray, Delivery delivery) {
        if (fieldersArray == null || !fieldersArray.isArray()) return;

        for (JsonNode fielderNode : fieldersArray) {
            DeliveryFielder deliveryFielder = new DeliveryFielder();
            deliveryFielder.setDelivery(delivery);
            deliveryFielder.setFielder(playerRepository.findByName(fielderNode.get("name").asText()));
            deliveryFielderRepository.save(deliveryFielder);
        }
    }


    private void insertPowerplayData(JsonNode root, Innings innings) {
        JsonNode inningsArray = root.get("innings");

        if (inningsArray != null && inningsArray.isArray()) {
            for (JsonNode inningsNode : inningsArray) {
                JsonNode powerplaysArray = inningsNode.get("powerplays");
                if (powerplaysArray != null && powerplaysArray.isArray()) {
                    for (JsonNode powerplayNode : powerplaysArray) {
                        Powerplay powerplay = new Powerplay();
                        powerplay.setInnings(innings);
                        powerplay.setType(powerplayNode.get("type").asText());
                        powerplay.setFromOver(powerplayNode.get("from").asDouble());
                        powerplay.setToOver(powerplayNode.get("to").asDouble());
                        powerplayRepository.save(powerplay);
                    }
                }
            }
        }
    }

    public Player getOrCreatePlayer(String playerName) {
        if (playerName == null || playerName.isEmpty()) return null;
        Optional<Player> existingPlayer = Optional.ofNullable(playerRepository.findByName(playerName));

        if (existingPlayer.isPresent()) {
            return existingPlayer.get();
        } else {
            Player newPlayer = new Player();
            newPlayer.setName(playerName);
            playerRepository.save(newPlayer);
            return newPlayer;
        }
    }
}
