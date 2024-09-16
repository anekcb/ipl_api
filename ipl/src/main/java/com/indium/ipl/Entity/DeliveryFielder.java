package com.indium.ipl.Entity;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "Deliveryfielder")
public class DeliveryFielder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_fielder_id")
    private int deliveryFielderId;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "fielder_id")
    private Player fielder;


    // Constructors, getters and setters
}