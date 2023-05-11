package com.delix.deliveryou.spring.configuration.websocket;

import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommunicableUserContainer {
    @Autowired
    private UserService userService;
    private final int SCHEDULE_PERIOD = 10;
    private final LinkedHashMap<Long, ScheduledFuture> onlineUsers;
    private final Set<Long> regularUsers;
    private final Set<Long> shippers;
    private final Set<Long> admins;
    private ScheduledExecutorService executorService;
    private boolean enableLogs;

    public CommunicableUserContainer() {
        onlineUsers = new LinkedHashMap<>();
        regularUsers = new LinkedHashSet<>();
        shippers = new LinkedHashSet<>();
        admins = new LinkedHashSet<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        enableLogs = false;
    }

    private boolean removeUserWithRole(long userId) {
        try {
            boolean contained = false;

            if (regularUsers.contains(userId))
                contained = regularUsers.remove(userId);
            else if (shippers.contains(userId))
                contained = shippers.remove(userId);
            else
                contained = admins.remove(userId);

            return contained;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * May need id validation beforehand
     * @param userId
     */
    private boolean addUserWithRole(long userId) {
        try {
            boolean notContained = false;
            if (userService.assertRole(userId, UserRole.USER))
                notContained = regularUsers.add(userId);
            else if (userService.assertRole(userId, UserRole.SHIPPER))
                notContained = shippers.add(userId);
            else if (userService.assertRole(userId, UserRole.ADMIN))
                notContained = admins.add(userId);

            return notContained;
        } catch (Exception e) {
            return false;
        }
    }


    public record UserWithRole(long userId, UserRole role) {}

    /**
     *
     * @param userId
     * @return null if no user with (userId) is online
     */
    private UserWithRole getUserWithRole(long userId) {
        if (regularUsers.contains(userId))
            return new UserWithRole(userId, UserRole.USER);
        else if (shippers.contains(userId))
            return new UserWithRole(userId, UserRole.SHIPPER);
        else if (admins.contains(userId))
            return new UserWithRole(userId, UserRole.ADMIN);
        return null;
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
            removeUserWithRole(userId);
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
            addUserWithRole(userId);

            log(true, "[CommunicableUserContainer] - Info: ", "registerAsActive -> true (added/updated user as active)");
            return true;
        } catch (Exception e) {
            log("[CommunicableUserContainer] - Exception: ", e.getMessage());
            return false;
        }
    }

    public boolean isOnline(long userId) {
        return onlineUsers.containsKey(userId);
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
