package com.indium.ipl.Entity;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "name")
    private String name;

    @Column(name = "match_number")
    private int matchNumber;

    @OneToOne
    @JoinColumn(name = "match_id")
    private Match match;

    // Constructors, getters and setters
}
