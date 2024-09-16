package com.indium.ipl.Entity;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Official")
public class Official {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "official_id")
    private int officialId;

    @Column(name = "name")
    private String name;

    @Column(name = "official_type")
    private String officialType;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    // Constructors, getters and setters
}