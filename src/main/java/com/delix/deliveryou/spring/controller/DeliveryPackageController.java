package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.api.sendbird.SendBird;
import com.delix.deliveryou.api.sendbird.SendBirdChannel;
import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.exception.InternalServerHttpException;
import com.delix.deliveryou.spring.component.DeliveryChargeAdvisor;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.model.MonthlyRevenueInput;
import com.delix.deliveryou.spring.pojo.*;
import com.delix.deliveryou.spring.services.*;
import com.delix.deliveryou.utility.JsonResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api")
public class DeliveryPackageController {
    @Autowired
    private DeliveryChargeAdvisor.Advisor chargeAdvisor;
    @Autowired
    private UserService userService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private LocationIQ locationIQ;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;
    @Autowired
    private SendBird sendBird;
    @Autowired
    private MatchingService matchingService;
    @Autowired
    private PackageService packageService;

    @CrossOrigin
    @PostMapping("/user/package/advisor-price")
    public ResponseEntity getAdvisorPrice(@RequestBody Map<String, String> map) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            double startingPoint_lat = Double.parseDouble(map.get("starting_point_lat"));
            double startingPoint_lon = Double.parseDouble(map.get("starting_point_lon"));
            double destination_lat = Double.parseDouble(map.get("destination_lat"));
            double destination_lon = Double.parseDouble(map.get("destination_lon"));
            String creationTimeRaw = map.get("creation_time");
            // hh:mm:ss(:ns)
            String[] partials = creationTimeRaw.split(":");
            LocalTime creationTime = LocalTime.of(
                    Integer.parseInt(partials[0]),
                    Integer.parseInt(partials[1]),
                    Integer.parseInt(partials[2])
            );

            DeliveryChargeAdvisor.AdvisorResponse response = chargeAdvisor.getAdvisorPrice(
                    new LocationIQ.Coordinate(startingPoint_lat, startingPoint_lon),
                    new LocationIQ.Coordinate(destination_lat, destination_lon),
                    creationTime
            );

            if (response == null)
                throw new InternalServerHttpException();

            return new ResponseEntity(JsonResponseBody.build(
                    "price", response.price(),
                    "distance", response.distance()
            ), httpStatus);

        } catch (NumberFormatException | DateTimeException | NullPointerException exception) {
            httpStatus = HttpStatus.BAD_REQUEST;
            exception.printStackTrace();
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        return new ResponseEntity(httpStatus);
    }

    @CrossOrigin
    @PostMapping("/user/package/upload")
    public ResponseEntity createPackage(@RequestBody DeliveryPackage deliveryPackage) {
        try {
            System.out.println("result: " + deliveryPackage);
            User sender = ((JWTUserDetails) userService.loadUserById(deliveryPackage.getUser().getId())).getUserObject();
            Promotion promotion = promotionService.loadPromotion(deliveryPackage.getPromotion().getId());
            Address senderAddress = locationIQ.reverseGeo(new LocationIQ.Coordinate(
                    Double.parseDouble(deliveryPackage.getSenderAddress().getLatitude()),
                    Double.parseDouble(deliveryPackage.getSenderAddress().getLongitude())
            ));
            Address recipientAddress = locationIQ.reverseGeo(new LocationIQ.Coordinate(
                    Double.parseDouble(deliveryPackage.getRecipientAddress().getLatitude()),
                    Double.parseDouble(deliveryPackage.getRecipientAddress().getLongitude())
            ));
            PackageType packageType = PackageType.getTypeByName(deliveryPackage.getPackageType().getName());

            if (senderAddress == null || recipientAddress == null || packageType == null)
                throw new HttpBadRequestException();

            OffsetDateTime createTime = OffsetDateTime.now();

            deliveryPackage.setUser(sender);
            deliveryPackage.setPromotion(promotion);
            deliveryPackage.setSenderAddress(senderAddress);
            deliveryPackage.setRecipientAddress(recipientAddress);
            deliveryPackage.setPackageType(packageType);
            deliveryPackage.setCreationDate(createTime);
            deliveryPackage.setStatus(PackageDeliveryStatus.PENDING);

            System.out.println("------ package: " + deliveryPackage);

            var savedPackage = deliveryService.savePackage(deliveryPackage);

            // start matching service
            matchingService.matchPotentialDrivers(deliveryPackage);

            return new ResponseEntity(JsonResponseBody.build(
                    "packageId", savedPackage.getId()
            ), HttpStatus.OK);

        } catch (UsernameNotFoundException | LocationIQ.InvalidGeoParams | HttpBadRequestException ex) {
            ex.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shared/package/get-active-package/{userId}")
    public ResponseEntity getActivePackage(@PathVariable long userId) {
        try {
            var deliveryPackage = deliveryService.getActivePackage(userId);
            return new ResponseEntity(deliveryPackage, HttpStatus.OK);
        } catch (HttpBadRequestException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shared/package/get-current-package/{userId}")
    public ResponseEntity getCurrentPackage(@PathVariable long userId) {
        try {
            var deliveryPackage = deliveryService.getCurrentPackage(userId);
            return new ResponseEntity(deliveryPackage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @return true the package can be assigned with a new driver
     */
    private boolean preAcceptValidation(long packageId) {
        var result = packageService.canAssignDriver(packageId);
        // notify

        return result;
    }

    @CrossOrigin
    @PostMapping("/shipper/package/accept-request")
    public ResponseEntity acceptPackageRequest(@RequestBody Map<String, String> map) {
        try {
            // read [packageId] and [shipperId] from request
            long packageId = Long.parseLong(map.get("packageId"));
            long shipperId = Long.parseLong(map.get("shipperId"));

            if (!preAcceptValidation(packageId)) {
                System.out.println(">>>> [ACCEPT_REQUEST]: cannot accept request");
                return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
            }

            System.out.println(">>>> [ACCEPT_REQUEST]: can accept request");

            // load full object from db
            var deliveryPackage = deliveryService.getPackage(packageId);
            var shipper = ((JWTUserDetails) userService.loadUserById(shipperId)).getUserObject();

            // throw error if no id is associated with any object
            if (deliveryPackage == null)
                throw new HttpBadRequestException();

            // assign shipper with the package
            deliveryPackage.setShipper(shipper);
//            deliveryPackage.setStatus(PackageDeliveryStatus.DELIVERING);

            // update package state/info to db
            if (deliveryService.updatePackage(deliveryPackage) == null)
                throw new InternalServerHttpException();

            var chatSession = tryGetOrCreateChatSession(deliveryPackage.getUser(), shipper);

            // add session to db, only add if not exist
            var addResult = chatService.addSession(chatSession);

            if (addResult == -1)
                throw new Exception("Failed to add new chat session");

            // ws: notify shipper about new package
            messagingTemplate.convertAndSendToUser(String.valueOf(shipperId), "/notification/package", deliveryPackage);
            messagingTemplate.convertAndSendToUser(String.valueOf(deliveryPackage.getUser().getId()), "/notification/chat", chatSession);
            messagingTemplate.convertAndSendToUser(String.valueOf(deliveryPackage.getUser().getId()), "/notification/package/driver-matched", "matched");

            return new ResponseEntity(HttpStatus.OK);
        } catch (NumberFormatException | HttpBadRequestException | UsernameNotFoundException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param user
     * @param shipper
     * @return a ChatSession instance if already exists, if not then create a new one, null if fails
     */
    private ChatSession tryGetOrCreateChatSession(User user, User shipper) {
        var chat = chatService.getChatSession(user.getId(), shipper.getId());

        if (chat != null) {
            System.out.println("Use char session from db");
            return chat;
        }

        final var res = new Object() {
            ChatSession session = null;
        };

        createChatChannel(
                user,
                shipper,
                responseChannel -> {
                    res.session = new ChatSession();
                    var sessionId = new ChatSessionId(user, shipper);
                    res.session.setChatSessionId(sessionId);
                    res.session.setFirstCreated(OffsetDateTime.now());
                    res.session.setChannelUrl(responseChannel.getChannel_url());
                    System.out.println("[Sendbird] channel created url=" + responseChannel.getChannel_url());
                },
                e -> {
                    System.out.println("[Sendbird] Cannot create chat channel");
                    e.printStackTrace();
                }
        );

        return res.session;
    }

    private void createChatChannel(User user, User shipper, Consumer<SendBirdChannel.ResponseChannel> onCreated, Consumer<Exception> onError) {

        sendBird.createGroupChannel(
                user,
                shipper,
                responseChannel -> {
                    // channel created
                    boolean sent = false;
                    int attempt = 3;

                    while (!sent && attempt > 0) {
                        sent = sendBird.SendFirstMessage(responseChannel);
                        attempt--;
                    }

                    onCreated.accept(responseChannel);

                },
                onError
        );

    }

    @CrossOrigin
    @GetMapping("/shipper/package/cancel/{packageId}")
    public ResponseEntity cancelPackageByShipper(@PathVariable long packageId) {
        try {
            var deliveryPackage = packageService.getPackage(packageId);

            if (deliveryPackage == null)
                throw new HttpBadRequestException();

            var result = packageService.cancelPackage(packageId);

            if (result) {
                messagingTemplate.convertAndSendToUser(String.valueOf(deliveryPackage.getUser().getId()), "/notification/package/canceled", "canceled");
                return new ResponseEntity(HttpStatus.OK);
            } else
                return new ResponseEntity(HttpStatus.NOT_MODIFIED);

        } catch (HttpBadRequestException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shipper/package/done/{packageId}")
    public ResponseEntity finishDelivering(@PathVariable long packageId) {
        try {
            var deliveryPackage = packageService.getPackage(packageId);

            if (deliveryPackage == null)
                throw new HttpBadRequestException();

            var result = packageService.finishDelivering(packageId);

            if (result) {
                messagingTemplate.convertAndSendToUser(String.valueOf(deliveryPackage.getUser().getId()), "/notification/package/finished", "finished");
                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        } catch (HttpBadRequestException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/shared/package/history")
    public ResponseEntity packageHistory(@RequestBody Map<String, String> map) {
        try {
            long userId = Long.parseLong(map.get("userId"));
            int startIndex = Integer.parseInt(map.get("startIndex"));
            int endIndex = Integer.parseInt(map.get("endIndex"));

            if (userId < 1 || startIndex < 0 || endIndex < 0 || startIndex > endIndex)
                throw new HttpBadRequestException();

            var list = packageService.packageHistory(userId, startIndex, endIndex);
            return new ResponseEntity(list, HttpStatus.OK);

        } catch (NumberFormatException | HttpBadRequestException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/shared/package/get-package-by-id/{packageId}")
    public ResponseEntity getPackageById(@PathVariable long packageId) {
        try {
            var result = packageService.getPackage(packageId);
            if (result != null)
                return new ResponseEntity(result, HttpStatus.OK);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //            deliveryPackage.setStatus(PackageDeliveryStatus.DELIVERING);

    @CrossOrigin
    @GetMapping("/shipper/package/confirm-pickup/{packageId}")
    public ResponseEntity confirmPickup(@PathVariable long packageId) {
        try {
            var deliveryPackage = packageService.getPackage(packageId);

            if (deliveryPackage == null)
                throw new HttpBadRequestException();

            var result = packageService.confirmPickup(packageId);

            if (result) {
                messagingTemplate.convertAndSendToUser(String.valueOf(deliveryPackage.getUser().getId()), "/notification/package/driver-confirmed", "confirmed");
                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);

        } catch (HttpBadRequestException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/user/cancel-waiting/{packageId}")
    public ResponseEntity cancelWaiting(@PathVariable long packageId) {
        try {
            var result = packageService.cancelWaiting(packageId);

            return new ResponseEntity((result) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
