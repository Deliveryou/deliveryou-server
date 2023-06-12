package com.delix.deliveryou.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WithdrawConfirmation {
    long withdrawId;
    long adminId;
}
