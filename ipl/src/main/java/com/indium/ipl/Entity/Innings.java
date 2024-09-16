package com.indium.ipl.Entity;
import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "Innings")
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "innings_id")
    private int inningsId;

    @Column(name = "target_runs")
    private Integer targetRuns;

    @Column(name = "target_overs")
    private Integer targetOvers;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Over> overs;

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Powerplay> powerplays;

    // Constructors, getters and setters
}