package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.model.ContributionGraphData;
import com.delix.deliveryou.spring.model.DistributionData;
import com.delix.deliveryou.spring.model.MonthlyRevenueInput;
import com.delix.deliveryou.spring.model.MonthlyRevenueResult;
import com.delix.deliveryou.spring.pojo.DeliveryPackage;
import com.delix.deliveryou.spring.pojo.TransactionHistory;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.DeliveryPackageRepository;
import com.delix.deliveryou.spring.repository.TransactionHistoryRepository;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.delix.deliveryou.spring.repository.extender.DeliveryPackageRepositoryExtender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class PackageService {
    @Autowired
    private DeliveryPackageRepositoryExtender packageRepositoryExtender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryPackageRepository packageRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    public List<DeliveryPackage> getAllPackages(long userId) {
        if (userId < 1)
            return Collections.emptyList();

        return packageRepository.getAllPackages(userId);
    }

    /**
     * @param packageId
     * @return true if the package has no assigned driver, false if cannot be assigned or [packageId] is invalid
     */
    public boolean canAssignDriver(long packageId) {
        return packageRepository.canAssignDriver(packageId);
    }

    public boolean cancelPackage(long packageId) {
        int result = packageRepository.cancelPackage(packageId);
        return (result > 0);
    }

    public boolean finishDelivering(long packageId) {
        var deliveryPackage = getPackage(packageId);

        if (deliveryPackage == null)
            return false;

        var wallet = walletService.getWalletOfShipper(deliveryPackage.getShipper().getId());

        if (wallet == null)
            return false;

        var promo = deliveryPackage.getPromotion();
        var originalPrice = deliveryPackage.getPrice() ;

        if (promo != null) {
            originalPrice = originalPrice / (1 - promo.getDiscountPercentage());
        }

        // decrease point from wallet
        double amount = Math.floor(0.2 * originalPrice / 100);

        var decreased = walletService.decreaseCredit(wallet.getId(), amount);

        if (!decreased) {
            return false;
        }

        int result = packageRepository.finishDelivering(packageId);

        if (result > 0) {
            var system = ((JWTUserDetails) userService.loadUserById(6l)).getUserObject();
            transactionHistoryRepository.save(new TransactionHistory(
                    wallet,
                    system,
                    -amount,
                    "Delivery fee: " + amount,
                    OffsetDateTime.now()
            ));

            if (promo != null) {
                var promoPrice = promo.getDiscountPercentage() * originalPrice;
                var promoDiscount = (promoPrice > promo.getMaximumDiscountAmount()) ? promo.getMaximumDiscountAmount() : promoPrice;
                var increased = walletService.increaseCredit(wallet.getId(), Math.floor(promoDiscount / 100));

                transactionHistoryRepository.save(new TransactionHistory(
                        wallet,
                        system,
                        Math.floor(promoDiscount / 100),
                        "Promotion refund: " + Math.floor(promoDiscount / 100),
                        OffsetDateTime.now()
                ));
            }
            return true;
        }
        walletService.increaseCredit(wallet.getId(), amount);
        return false;
    }

    public List<DeliveryPackage> packageHistory(long userId, int startIndex, int endIndex) {
        try {
            var user = userRepository.getUserById(userId);


            if (user.getRole().getId() == UserRole.USER.getId()) {

            } else if (user.getRole().getId() == UserRole.SHIPPER.getId()) {

            } else
                return Collections.emptyList();

            return packageRepositoryExtender.packageHistory(userId, startIndex, endIndex);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public DeliveryPackage getPackage(long packageId) {
        return packageRepository.getPackage(packageId);
    }

    public boolean confirmPickup(long packageId) {
        var result = packageRepository.confirmPickup(packageId);
        return result > 0;
    }

    public List<ContributionGraphData> getAllPackagesInMonth(int month, int year, long shipperId) {
        var list = packageRepository.getAllPackagesInMonth(month, year, shipperId);
        if (list == null)
            return Collections.emptyList();

        var resultList = list.stream().map(data -> {
            String date = ((OffsetDateTime) data[0]).toString();
            return new ContributionGraphData(date, (Long) data[1]);
        }).toList();
        return resultList;
    }

    public List<MonthlyRevenueResult> shipperRevenuesWithinMonths(long shipperId, List<String> months) {
        var list = new ArrayList<MonthlyRevenueResult>(months.size());

        for (var month : months) {
            var tokens = month.split("-");
            var _month = Integer.parseInt(tokens[0]);
            var _year = Integer.parseInt(tokens[1]);

            var revenue = packageRepository.shipperRevenueInMonth(shipperId, _month, _year);
            list.add(new MonthlyRevenueResult(month, revenue));
        }
        return list;
    }

    public List<MonthlyRevenueResult> systemRevenuesWithinMonths(List<String> months) {
        var list = new ArrayList<MonthlyRevenueResult>(months.size());

        for (var month : months) {
            var tokens = month.split("-");
            var _month = Integer.parseInt(tokens[0]);
            var _year = Integer.parseInt(tokens[1]);

            var revenue = packageRepository.systemRevenuePerMonth(_month, _year);
            list.add(new MonthlyRevenueResult(month, revenue));
        }
        return list;
    }

    public int totalPackagesThisMonth(long userId) {
        var date = LocalDate.now();
        return packageRepository.totalPackagesOfMonth(userId, date.getMonthValue(), date.getYear());
    }

    public int totalSpendingThisMonth(long userId) {
        var date = LocalDate.now();
        return packageRepository.totalSpendingThisMonth(userId, date.getMonthValue(), date.getYear());
    }

    public int allTimePackages(long userId) {
        return packageRepository.allTimePackages(userId);
    }

    public int allTimeSpending(long userId) {
        return packageRepository.allTimeSpending(userId);
    }

    public int countAllPackages() {
        return packageRepository.countAllPackages();
    }

    public List<DistributionData> categoryDistribution() {
        var list = packageRepository.categoryDistribution();

        if (list != null && list.size() > 0)
            return list.stream().map(data -> new DistributionData((String) data[0], (long) data[1])).toList();

        return Collections.emptyList();
    }

    public boolean cancelWaiting(long packageId) {
        var result = packageRepository.delete(packageId);
        return result > 0;
    }

}
