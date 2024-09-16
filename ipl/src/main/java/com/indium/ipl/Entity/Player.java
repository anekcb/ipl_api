package com.indium.ipl.Entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "Player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private int playerId;

    @Column(name = "name")
    private String name;

    @Column(name = "registry_id")
    private String registryId;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamPlayer> teamPlayers;

    @OneToMany(mappedBy = "batter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveriesAsBatter;

    @OneToMany(mappedBy = "bowler", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveriesAsBowler;

    @OneToMany(mappedBy = "nonStriker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveriesAsNonStriker;

    @OneToMany(mappedBy = "playerOut", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveriesPlayerOut;

    @OneToMany(mappedBy = "fielder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryFielder> deliveryFielders;


    // Constructors, getters and setters
}