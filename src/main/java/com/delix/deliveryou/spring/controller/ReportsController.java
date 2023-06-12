package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.spring.configuration.websocket.CommunicableUserContainer;
import com.delix.deliveryou.spring.model.MonthlyRevenueInput;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.services.PackageService;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReportsController {
    @Autowired
    private PackageService packageService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommunicableUserContainer userContainer;

    @CrossOrigin
    @PostMapping("/shipper/package/reports/packages-per-month")
    public ResponseEntity packagesPerMonth(@RequestBody Map<String, String> map) {
        try {
            long shipperId = Long.parseLong(map.get("shipperId"));
            int year = Integer.parseInt(map.get("year"));
            int month = Integer.parseInt(map.get("month"));

            var list = packageService.getAllPackagesInMonth(month, year, shipperId);

            return new ResponseEntity(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/shipper/package/reports/revenues-of-months")
//    @PostMapping("/auth/auth-test1")
    public ResponseEntity revenuesOfMonths(@RequestBody MonthlyRevenueInput monthlyRevenueInput) {
        try {
            var result = packageService.shipperRevenuesWithinMonths(monthlyRevenueInput.getUserId(), monthlyRevenueInput.getMonths());
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/admin/package/reports/revenues-of-months")
//    @PostMapping("/auth/auth-test1")
    public ResponseEntity adminRevenuesOfMonths(@RequestBody MonthlyRevenueInput monthlyRevenueInput) {
        try {
            var result = packageService.systemRevenuesWithinMonths(monthlyRevenueInput.getMonths());
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shipper/package/reports/quick-reports/{shipperId}")
    public ResponseEntity shipperQuickReports(@PathVariable long shipperId) {
        try {
            var packagesThisMonth = packageService.totalPackagesThisMonth(shipperId);
            var allTimePackages = packageService.allTimePackages(shipperId);

            return new ResponseEntity(JsonResponseBody.build(
                    "packagesThisMonth", packagesThisMonth,
                    "allTimePackages", allTimePackages
            ), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/user/package/reports/quick-reports/{userId}")
    public ResponseEntity userQuickReports(@PathVariable long userId) {
        try {
            var packagesThisMonth = packageService.totalPackagesThisMonth(userId);
            var allTimePackages = packageService.allTimePackages(userId);
            var totalSpendingThisMonth = packageService.totalSpendingThisMonth(userId);
            var allTimeSpending = packageService.allTimeSpending(userId);

            return new ResponseEntity(JsonResponseBody.build(
                    "packagesThisMonth", packagesThisMonth,
                    "allTimePackages", allTimePackages,
                    "spendingThisMonth", totalSpendingThisMonth,
                    "allTimeSpending", allTimeSpending
            ), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/admin/package/reports/quick-reports")
    public ResponseEntity adminQuickReports() {
        try {
            var allTimePackages = packageService.countAllPackages();
            var totalUsers = userService.countSystemUsersWithRole(UserRole.USER.getId());
            var totalShipper = userService.countSystemUsersWithRole(UserRole.SHIPPER.getId());
            var onlineShippers = userContainer.countOnlineShippers();
            var onlineUsers = userContainer.countOnlineUsers();

            return new ResponseEntity(JsonResponseBody.build(
                    "allTimePackages", allTimePackages,
                    "totalUsers", totalUsers,
                    "totalShipper", totalShipper,
                    "onlineShippers", onlineShippers,
                    "onlineUsers", onlineUsers
            ), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/admin/package/reports/category-distribution")
    public ResponseEntity categoryDistribution() {
        try {
            var list = packageService.categoryDistribution();

            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
