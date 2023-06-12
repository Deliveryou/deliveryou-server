package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.model.WalletDeposit;
import com.delix.deliveryou.spring.model.WithdrawConfirmation;
import com.delix.deliveryou.spring.pojo.*;
import com.delix.deliveryou.spring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WithdrawRepository withdrawRepository;
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;
    @Autowired
    private UserService userService;
    @Value("${wallet.conversion-rate}")
    private int CONVERSION_RATE;
    @Autowired
    private RatingRepository ratingRepository;


    public Wallet getWalletOfShipper(long shipperId) {
        return walletRepository.getWalletOfShipper(shipperId);
    }

    public Wallet getWalletById(long walletId) {
        return walletRepository.getWalletById(walletId);
    }

    /**
     * @return saved wallet, never be null, otherwise throws exception
     */
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public List<User> getDriversWithPhone(String phone) {
        return userRepository.getDriverWithPhone(phone);
    }

    public boolean increaseCredit(long walletId, double amount) {
        if (amount <= 0 || walletId < 1)
            return false;

        var result = walletRepository.increaseWalletCredit(walletId, amount);
        return result > 0;
    }

    public boolean decreaseCredit(long walletId, double amount) {
        if (amount <= 0 || walletId < 1 || !canDecreaseCredit(walletId, amount))
            return false;

        var result = walletRepository.decreaseWalletCredit(walletId, amount);
        return result > 0;
    }

    public boolean canDecreaseCredit(long walletId, double amount) {
        return walletRepository.canDecreaseWalletCredit(walletId, amount);
    }

    public boolean giftCredit(long senderWalletId, long shipperId, long recipientId, int amount) {
        var senderWallet = getWalletById(senderWalletId);
        var recipientWallet = getWalletOfShipper(recipientId);

        if (senderWallet == null || recipientWallet == null || amount < 0 ||
                senderWallet.getShipper().getId() != shipperId || !userRepository.isShipper(shipperId))
            return false;

        var decreaseSenderCreditResult  = decreaseCredit(senderWalletId, amount);
        // if failed to decrease sender's credits -> false
        if (!decreaseSenderCreditResult)
            return false;

        // try to increase recipient's credits
        var increaseRecipientCreditResult = increaseCredit(recipientWallet.getId(), amount);
        // failed to increase recipient's credits
        if (!increaseRecipientCreditResult) {
            increaseCredit(senderWalletId, amount);
            return false;
        }

        // save log of sender
        transactionHistoryRepository.save(new TransactionHistory(
                senderWallet,
                senderWallet.getShipper(),
                -amount,
                "Gifted " + amount + " credits",
                OffsetDateTime.now()
        ));

        // save log of recipient
        transactionHistoryRepository.save(new TransactionHistory(
                recipientWallet,
                senderWallet.getShipper(),
                amount,
                "Received " + amount + " credits",
                OffsetDateTime.now()
        ));

        return true;
    }

    public Withdraw createWithdraw(Withdraw widthdraw) {
        return withdrawRepository.save(widthdraw);
    }

    public boolean canCreateWithdrawRequest(long walletId) {
        return withdrawRepository.canCreateWidthdraw(walletId);
    }

    public Withdraw getPendingWithdraw(long walletId) {
        return withdrawRepository.getPendingWithdraw(walletId);
    }

    public List<Withdraw> getAllPendingWithdraw() {
        return withdrawRepository.getAllPendingWithdraw();
    }

    public List<TransactionHistory> getTransactionHistoriesByWalletId(long walletId) {
        return transactionHistoryRepository.getHistoriesByWalletId(walletId);
    }

    public boolean deposit(WalletDeposit walletDeposit) {
        if (walletDeposit == null || walletDeposit.getAmount() < 1 || walletDeposit.getAdminId() < 1 || walletDeposit.getPhotoUrl() == null || walletDeposit.getWalletId() < 1)
            return false;

        var admin = ((JWTUserDetails) userService.loadUserById(walletDeposit.getAdminId())).getUser();

        if (admin == null)
            return false;

        var wallet = getWalletById(walletDeposit.getWalletId());

        if (wallet == null)
            return false;

        var result = walletRepository.deposit(walletDeposit.getWalletId(), walletDeposit.getAmount());

        if (result > 0) {
            transactionHistoryRepository.save(new TransactionHistory(
                    wallet,
                    admin,
                    Math.ceil(walletDeposit.getAmount() / CONVERSION_RATE),
                    "Deposited " + walletDeposit.getAmount(),
                    walletDeposit.getPhotoUrl(),
                    OffsetDateTime.now()
            ));
            return true;
        }
        return false;
    }

    public Withdraw getWithdrawById(long id) {
        return withdrawRepository.getById(id);
    }

    public boolean confirmWithdraw( WithdrawConfirmation withdrawConfirmation) {
        if (withdrawConfirmation == null || withdrawConfirmation.getAdminId() < 1 || withdrawConfirmation.getWithdrawId() < 1)
            return false;

        var withdraw = getWithdrawById(withdrawConfirmation.getWithdrawId());

        if (withdraw == null)
            return false;

        var admin = ((JWTUserDetails) userService.loadUserById(withdrawConfirmation.getAdminId())).getUser();

        if (admin == null)
            return false;

        var result = withdrawRepository.confirmWithdraw(withdraw.getId());

        if (result > 0) {
            transactionHistoryRepository.save(new TransactionHistory(
                    withdraw.getWallet(),
                    admin,
                    -withdraw.getAmount(),
                    "Withdrawed " + withdraw.getAmount(),
                    OffsetDateTime.now()
            ));
            return true;
        }
        return false;
    }


}
