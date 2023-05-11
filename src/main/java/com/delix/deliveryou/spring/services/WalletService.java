package com.delix.deliveryou.spring.services;

import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.Wallet;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.delix.deliveryou.spring.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;

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

    public boolean increaseCredit(long walletId, int amount) {
        if (amount <= 0)
            return false;

        var result = walletRepository.increaseWalletCredit(walletId, amount);
        return result > 0;
    }

    public boolean decreaseCredit(long walletId, int amount) {
        if (amount <= 0 || !canDecreaseCredit(walletId, amount))
            return false;

        var result = walletRepository.decreaseWalletCredit(walletId, amount);
        return result > 0;
    }

    public boolean canDecreaseCredit(long walletId, int amount) {
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

        return true;
    }

}
