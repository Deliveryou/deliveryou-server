package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.SystemLog;
import com.delix.deliveryou.spring.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class LogService {
    @Autowired
    private SystemLogRepository systemLogRepository;

    public List<SystemLog> getAllSystemLogs() {
        return systemLogRepository.getAll();
    }

    public boolean saveSystemLog(SystemLog log) {
        if (log == null)
            return false;

        try {
            systemLogRepository.save(log);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SystemLog> getSystemLogs(long millis) throws DateTimeException {
        LocalDateTime date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDateTime();

        return systemLogRepository.get(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }
}
