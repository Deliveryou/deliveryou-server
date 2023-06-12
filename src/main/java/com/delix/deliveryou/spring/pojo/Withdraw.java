package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Table(name = "withdraw")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Withdraw {
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @Column(name = "date")
    private OffsetDateTime date;

    @Column(name = "is_valid_within_days")
    private double validWithinDays;
}
