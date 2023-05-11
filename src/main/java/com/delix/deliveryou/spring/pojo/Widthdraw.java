package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "withdraw")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Widthdraw {
    @Id
    @Column(name = "withdraw_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "virtual_balance_id")
    private Wallet wallet;

    @Column(name = "amount")
    private double amount;

    @Column(name = "finished")
    private boolean finished;

    @Column(name = "date")
    private double date;

    @Column(name = "is_valid_within_days")
    private double validWithinDays;
}
