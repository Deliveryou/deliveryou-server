package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.pojo.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository  extends JpaRepository<Wallet, Long> {

    @Query("select w from Wallet w where w.shipper.id = :id")
    Wallet getWalletOfShipper(@Param("id") long shipperId);

    @Query("select w from Wallet w where w.id = :id")
    Wallet getWalletById(@Param("id") long walletId);

    @Query("update Wallet set credit = credit + :amount where id = :walletId")
    @Modifying
    @Transactional
    int increaseWalletCredit(@Param("walletId") long walletId, @Param("amount") double amount);

    @Query("update Wallet set credit = credit - :amount where id = :walletId")
    @Modifying
    @Transactional
    int decreaseWalletCredit(@Param("walletId") long walletId, @Param("amount") double amount);

    @Query("select case when w.credit - :amount >= 0 then true else false end from Wallet w where w.id = :walletId")
    @Transactional
    boolean canDecreaseWalletCredit(@Param("walletId") long walletId, @Param("amount") double amount);

    @Transactional
    @Modifying
    @Query("update Wallet set credit = credit + :amount where id = :walletId ")
    int deposit(@Param("walletId") long walletId, @Param("amount") double amount);

}
