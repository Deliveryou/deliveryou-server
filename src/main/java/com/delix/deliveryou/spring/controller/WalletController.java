package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.spring.services.WalletService;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @CrossOrigin
    @GetMapping("/shared/get-info/{shipperId}")
    public ResponseEntity getWalletInfo(@PathVariable long shipperId) {
        try {
            var wallet = walletService.getWalletOfShipper(shipperId);

            if (wallet != null)
                return new ResponseEntity(wallet, HttpStatus.OK);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/shipper/set-account-info")
    public ResponseEntity getWalletInfo(@RequestBody Map<String, String> map) {
        try {
            long walletId = Long.parseLong(map.get("walletId"));
            String _accountNumber = map.get("accountNumber");
            String _accountOwner = map.get("accountOwner");
            String _branch = map.get("branch");

            // these 3 cannot be null
            if (_accountNumber == null || _accountNumber.equals("") ||
                    _accountOwner == null || _accountOwner.equals("") ||
                    _branch == null || _branch.equals(""))
                throw new HttpBadRequestException();

            // get wallet
            var wallet = walletService.getWalletById(walletId);

            if (wallet == null)
                throw new HttpBadRequestException();

            // format these 3 to the right format
            _accountNumber = _accountNumber.replaceAll("\\s+", "");
            _accountOwner = _accountOwner.trim().replaceAll("\\s{2,}", " ");
            _branch = _branch.trim().replaceAll("\\s{2,}", " ");

            // validation
            Pattern pattern = Pattern.compile("\\d{10,}");

            if (!pattern.matcher(_accountNumber).find())
                throw new HttpBadRequestException();

            wallet.setAccountNumber(_accountNumber);
            wallet.setAccountOwner(_accountOwner);
            wallet.setBranch(_branch);

            var savedWallet = walletService.saveWallet(wallet);

            return new ResponseEntity(savedWallet, HttpStatus.OK);

        } catch (HttpBadRequestException | NumberFormatException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shared/drivers-with-phone/{phone}")
    public ResponseEntity getDriversWithPhone(@PathVariable String phone) {
        try {
            var drivers = walletService.getDriversWithPhone(phone);

            if (drivers == null)
                drivers = Collections.emptyList();

            return new ResponseEntity(drivers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/shipper/gift-credits")
    public ResponseEntity giftCredit(@RequestBody Map<String, String> map) {
        try {
            long senderWalletId = Long.parseLong(map.get("senderWalletId"));
            long shipperId = Long.parseLong(map.get("shipperId"));
            long recipientId = Long.parseLong(map.get("recipientId"));
            int amount = Integer.parseInt(map.get("amount"));

            var result = walletService.giftCredit(senderWalletId, shipperId, recipientId, amount);

            return new ResponseEntity((result) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
