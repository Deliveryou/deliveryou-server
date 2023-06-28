package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SystemService {
    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;
        private static int COMMISSION_RATE = 20;

    public boolean updateCommissionRate(long adminId, int rate) {
        var admin = ((JWTUserDetails) userService.loadUserById(adminId)).getUser();

        if (admin == null)
            return false;

        rate = (rate < 10) ? 10 : (Math.min(rate, 90));

        if (rate == COMMISSION_RATE)
            return false;

        var prev = COMMISSION_RATE;
        COMMISSION_RATE = rate;

        var log = new SystemLog(
                LogEvent.COMMISSION_RATE_SAVE,
                LocalDateTime.now(),
                String.valueOf(prev),
                String.valueOf(rate),
                admin
        );
        return logService.saveSystemLog(log);
    }
}
