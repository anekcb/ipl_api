package com.indium.ipl.Entity;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Teamplayer")
public class TeamPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_player_id")
    private int teamPlayerId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    // Constructors, getters and setters
}