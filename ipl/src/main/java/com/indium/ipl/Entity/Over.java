package com.indium.ipl.Entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "Overs")
public class Over {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "over_id")
    private int overId;

    @Column(name = "over_number")
    private int overNumber;

    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

    @OneToMany(mappedBy = "over", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveries;

    // Constructors, getters and setters
}