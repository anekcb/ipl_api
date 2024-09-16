package com.indium.ipl.Entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Matchteam")
public class MatchTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_team_id")
    private int matchTeamId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // Constructors, getters and setters
}