package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    @Query("select s from SystemLog s order by s.date desc")
    List<SystemLog> getAll();

    @Query("select s from SystemLog s where day(s.date) = :day and month(s.date) = :month and year(s.date) = :year order by s.date desc")
    List<SystemLog> get(@Param("day") int day, @Param("month") int month, @Param("year") int year);

}
