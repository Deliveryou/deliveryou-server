package com.delix.deliveryou.spring.configuration.websocket;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommunicableUserContainer {
    private final int SCHEDULE_PERIOD = 10;
    private final LinkedHashMap<Long, ScheduledFuture> onlineUsers;
    private ScheduledExecutorService executorService;
    private boolean enableLogs;

    public CommunicableUserContainer() {
        onlineUsers = new LinkedHashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        enableLogs = false;
    }

    private boolean verifyUserId(Long userId) {
        return (userId != null && userId > 0l);
    }

    public boolean registerAsInactive(Long userId) {
        var schedule = onlineUsers.get(userId);

        if (schedule != null && schedule.cancel(true)) {
            log(true, "[CommunicableUserContainer] - Info: " + "registerAsInactive -> canceled schedule");
        }

        var res = onlineUsers.remove(userId) != null;
        if (res) {
            log(true, "[CommunicableUserContainer] - Info: " + "registerAsInactive -> true (removed user [" + userId + "] from container)");
            return true;
        }
        log(true, "[CommunicableUserContainer] - Error: " + "registerAsInactive -> true (failed to remove user [" + userId + "] from container)");
        return false;
    }

    public boolean registerAsActive(Long userId) {
        if (!verifyUserId(userId)) {
            log(true, "[CommunicableUserContainer] - Error: ", "registerAsActive -> false (invalid userId)");
            return false;
        }

        try {
            var schedule = executorService.schedule(() -> registerAsInactive(userId), SCHEDULE_PERIOD, TimeUnit.SECONDS);
            if (onlineUsers.containsKey(userId)) {
                onlineUsers.get(userId).cancel(true);
            }

            onlineUsers.put(userId, schedule);
            log(true, "[CommunicableUserContainer] - Info: ", "registerAsActive -> true (added/updated user as active)");
            return true;
        } catch (Exception e) {
            log("[CommunicableUserContainer] - Exception: ", e.getMessage());
            return false;
        }
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public LinkedHashMap<Long, ScheduledFuture> getOnlineUsers() {
        return onlineUsers;
    }

    public CommunicableUserContainer enableLogs(boolean enable) {
        enableLogs = enable;
        return this;
    }

    public CommunicableUserContainer enableLogs() {
        return  enableLogs(true);
    }

    private void log(String... message) {
        log(false, message);
    }
    private void log(boolean withTimeStamp, String... message) {
        if (enableLogs) {
            System.out.println(Arrays.stream(message).collect(Collectors.joining()) + " [" + LocalTime.now() + "]");
        }
    }

}
