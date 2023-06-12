package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Table(name = "transaction_history")
@Data
@ToString
@NoArgsConstructor
public class TransactionHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "virtual_balance_id")
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User User;

    @Column(name = "amount")
    private double amount;

    @Column(name = "content")
    private String content;

    @Column(name = "photo_url")
    private String photoUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "creation_time")
    private OffsetDateTime creationTime;

    public TransactionHistory(Wallet wallet, com.delix.deliveryou.spring.pojo.User user, double amount, String content, OffsetDateTime creationTime) {
        this.wallet = wallet;
        User = user;
        this.amount = amount;
        this.content = content;
        this.creationTime = creationTime;
    }

    public TransactionHistory(Wallet wallet, com.delix.deliveryou.spring.pojo.User user, double amount, String content, String photoUrl, OffsetDateTime creationTime) {
        this.wallet = wallet;
        User = user;
        this.amount = amount;
        this.content = content;
        this.photoUrl = photoUrl;
        this.creationTime = creationTime;
    }
}
