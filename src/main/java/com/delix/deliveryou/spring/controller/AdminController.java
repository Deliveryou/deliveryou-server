package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.api.sendbird.SendBird;
import com.delix.deliveryou.api.sendbird.SendBirdUser;
import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.model.WalletDeposit;
import com.delix.deliveryou.spring.model.WithdrawConfirmation;
import com.delix.deliveryou.spring.pojo.Promotion;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.services.PackageService;
import com.delix.deliveryou.spring.services.PromotionService;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.spring.services.WalletService;
import com.delix.deliveryou.utility.JsonResponseBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.delix.deliveryou.spring.model.SearchFilterType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private DeliveryChargeAdvisor.Advisor advisor;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private SendBird sendBird;

    @PostMapping(value = "/all-regular-users")
    @CrossOrigin
    public ResponseEntity allRegularUsers(@RequestBody SearchFilter filter) {
        try {
            List<User> list = userService.getUsersWithFilter(UserRole.USER, filter);

            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/all-shippers")
    @CrossOrigin
    public ResponseEntity allShippers(@RequestBody SearchFilter filter) {
        try {
            List<User> list = userService.getUsersWithFilter(UserRole.SHIPPER, filter);
            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ban-user/{userId}")
    @CrossOrigin
    public ResponseEntity banUser(@PathVariable long userId) {
        try {
            boolean res = userService.markUserAsDeleted(userId, true);
            return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unban-user/{userId}")
    @CrossOrigin
    public ResponseEntity unbanUser(@PathVariable long userId) {
        try {
            boolean res = userService.markUserAsDeleted(userId, false);
            return new ResponseEntity((res) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-delivery-packages/{userId}")
    @CrossOrigin
    public ResponseEntity allPackages(@PathVariable long userId) {
        try {
            var list = packageService.getAllPackages(userId);
            return new ResponseEntity(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/server-config/charge-advisor/get-config")
    @CrossOrigin
    public ResponseEntity getChargeAdvisorConfig() {
        try {
            var config = advisor.getAdvisorConfig();
            return new ResponseEntity(config, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/add-shipper")
    public ResponseEntity register(@RequestBody User user) {
        try {
            var savedUser = userService.addUser(user, UserRole.SHIPPER);

            sendBird.createUser(
                    new SendBirdUser.RequestUser(String.valueOf(savedUser.getId()), savedUser.getFirstName() + " " + savedUser.getLastName(), savedUser.getProfilePictureUrl()),
                    responseUser -> {
                        System.out.println(">>>>>>>>> Sendbird: created new user");
                    },
                    exception -> {
                        System.out.println(">>>>>>>>> Sendbird error: failed to create user");
                        exception.printStackTrace();
                    }
            );

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/promo/can-use-code/{code}")
    public ResponseEntity canUsePromoCode(@PathVariable String code) {
        try {
            var result = promotionService.canUsePromoCode(code);

            return new ResponseEntity((result) ? HttpStatus.OK : HttpStatus.FOUND);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/promo/add")
    public ResponseEntity addPromotion(@RequestBody Promotion promotion) {
        try {
            var id = promotionService.addPromotion(promotion);

            if (id > 0)
                return new ResponseEntity(id ,HttpStatus.OK);
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/promo")
    public ResponseEntity addPromotion(@RequestParam(required = false) String search) {
        try {
            var list = promotionService.searchForPromotions(search);

            return new ResponseEntity(list, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/get-wallet/{shipperId}")
    public ResponseEntity getWallet(@PathVariable Long shipperId) {
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
    @PostMapping("/deposit-wallet")
    public ResponseEntity depositWallet(@RequestBody WalletDeposit walletDeposit) {
        try {
            var result = walletService.deposit(walletDeposit);

            return new ResponseEntity((result) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/get-pending-withdraw")
    public ResponseEntity getWithdrawRequest() {
        try {
            var list = walletService.getAllPendingWithdraw();

            if (list != null)
                return new ResponseEntity(list, HttpStatus.OK);

            return new ResponseEntity(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/confirm-withdraw")
    public ResponseEntity confirmWi(@RequestBody WithdrawConfirmation withdrawConfirmation) {
        try {
            var result = walletService.confirmWithdraw(withdrawConfirmation);

            return new ResponseEntity((result) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/get-rating-list")
    public ResponseEntity getRatingList() {
        try {
            var result = userService.getRatingList();

            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/mark-rating/{ratingId}")
    public ResponseEntity markRating(@PathVariable("ratingId") long ratingId, @RequestBody Map<String, String> map) {
        try {
            boolean marked = Boolean.parseBoolean(map.get("marked"));

            userService.markedRating(ratingId, marked);

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
