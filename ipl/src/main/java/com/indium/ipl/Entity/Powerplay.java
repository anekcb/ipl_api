package com.indium.ipl.Entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Powerplay")
public class Powerplay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "powerplay_id")
    private int powerplayId;

    @Column(name = "from_over")
    private double fromOver;

    @Column(name = "to_over")
    private double toOver;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

    // Constructors, getters and setters
}