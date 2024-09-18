package com.indium.ipl;

import com.indium.ipl.Entity.*;
import com.indium.ipl.repository.*;
import com.indium.ipl.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MatchServiceUnitTest {
    @Mock
    private MatchRepository matchesRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MatchTeamRepository matchTeamRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private TeamPlayerRepository teamPlayerRepository;
    @Mock
    private OfficialRepository officialRepository;
    @Mock
    private InningsRepository inningsRepository;
    @Mock
    private OverRepository oversRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private DeliveryFielderRepository deliveryFielderRepository;
    @Mock
    private PowerplayRepository powerplayRepository;
    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void testInsertMatchData() throws IOException {
        String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/335998.json")));

        when(matchesRepository.save(any())).thenReturn(null);
        when(eventRepository.save(any())).thenReturn(null);
        when(teamRepository.save(any())).thenReturn(null);
        when(matchTeamRepository.save(any())).thenReturn(null);
        when(playerRepository.save(any())).thenReturn(null);
        when(teamPlayerRepository.save(any())).thenReturn(null);
        when(officialRepository.save(any())).thenReturn(null);
        when(inningsRepository.save(any())).thenReturn(null);
        when(oversRepository.save(any())).thenReturn(null);
        when(deliveryRepository.save(any())).thenReturn(null);
        when(deliveryFielderRepository.save(any())).thenReturn(null);
        when(powerplayRepository.save(any())).thenReturn(null);

        matchService.insertMatchData(jsonData);

        verify(matchesRepository).save(any());
        verify(eventRepository).save(any());
        verify(teamRepository, times(2)).save(any()); // Two teams
        verify(matchTeamRepository, times(2)).save(any()); // Two match teams
        verify(playerRepository, atLeast(4)).save(any()); // At least 4 players (adjust as needed)
        verify(teamPlayerRepository, atLeast(4)).save(any()); // At least 4 team players
        verify(officialRepository, atLeast(4)).save(any()); // At least 4 officials
        verify(inningsRepository, atLeast(1)).save(any()); // At least 1 call
        verify(oversRepository, atLeast(38)).save(any());
        verify(deliveryRepository, atLeast(229)).save(any());
        verify(powerplayRepository, atLeast(4)).save(any());
    }

    @Test
    public void testGetOrCreatePlayerExistingPlayer() {
        String playerName = "John Doe";
        Player existingPlayer = new Player();
        existingPlayer.setName(playerName);
        when(playerRepository.findByName(playerName)).thenReturn(existingPlayer);
        Player result = matchService.getOrCreatePlayer(playerName);
        assertNotNull(result);
        assertEquals(playerName, result.getName());
        verify(playerRepository, never()).save(any());
    }

    @Test
    public void testGetOrCreatePlayerNewPlayer() {
        String playerName = "New Player";
        when(playerRepository.findByName(playerName)).thenReturn(null);
        Player result = matchService.getOrCreatePlayer(playerName);
        assertNotNull(result);
        assertEquals(playerName, result.getName());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

}