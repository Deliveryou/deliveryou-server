package com.delix.deliveryou.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WalletDeposit {
    private double amount;
    private String photoUrl;
    private long walletId;
    private long adminId;
}
