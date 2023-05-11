package com.delix.deliveryou.spring.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "virtual_balance")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @Column(name = "virtual_balance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "shipper_id")
    private User shipper;

    @Column(name = "balance")
    private double credit;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_owner")
    private String accountOwner;

    @Column(name = "branch")
    private String branch;
}
