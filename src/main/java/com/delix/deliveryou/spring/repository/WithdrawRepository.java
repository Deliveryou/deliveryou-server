package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.Withdraw;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {

    @Query("select case when not exists (select w from Withdraw w where w.wallet.id = :id and w.finished = false) then true else false end")
    boolean canCreateWidthdraw(@Param("id") long walletId);

    @Query("select w from Withdraw w where w.wallet.id = :walletId and w.finished = false")
    Withdraw getPendingWithdraw(@Param("walletId") long walletId);

    @Query("select w from Withdraw w where w.finished = false order by w.date desc")
    List<Withdraw> getAllPendingWithdraw();

    @Modifying
    @Transactional
    @Query("update Withdraw set finished = true where id = :id")
    int confirmWithdraw(@Param("id") long withdrawId);

    @Query("select w from Withdraw w where w.id = :id")
    Withdraw getById(@Param("id") long id);
}
