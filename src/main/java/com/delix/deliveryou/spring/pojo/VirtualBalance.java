package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "virtual_balance")
@Data
@NoArgsConstructor
public class VirtualBalance {
    @Id
    @Column(name = "virtual_balance_id")
    private UUID virtualBalanceId;

    @Column(name = "balance")
    private double balance;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_owner")
    private String accountOwner;

    @Column(name = "branch")
    private String branch;
}
