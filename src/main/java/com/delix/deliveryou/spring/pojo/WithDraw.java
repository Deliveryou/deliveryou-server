package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.util.UUID;

@Entity
@Table(name = "withdraw")
@Data
@NoArgsConstructor
public class WithDraw {
    @Id
    @Column(name = "withdraw_id")
    private UUID withDrawId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "finished")
    private Boolean finished;

    @Column(name = "date")
    private DateFormat date;

    @Column(name = "is_valid_within_days")
    private double isValidWithinDays;
}
