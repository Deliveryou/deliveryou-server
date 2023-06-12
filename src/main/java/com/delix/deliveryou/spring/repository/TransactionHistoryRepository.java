package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query("select th from TransactionHistory th where th.wallet.id = :walletId order by th.creationTime desc")
    List<TransactionHistory> getHistoriesByWalletId(@Param("walletId") long walletId);

    @Query("select th from TransactionHistory th where th.wallet.shipper.id = :shipperId order by th.creationTime desc")
    List<TransactionHistory> getHistoriesByShipperId(@Param("shipperId") long shipperId);

}
