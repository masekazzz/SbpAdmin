package ru.sbp.controllers;

//import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.sbp.apihandlers.RequestHandler;
import ru.sbp.objects.structures.callback.CallbackBodyStructure;
import ru.sbp.objects.structures.Item;
import ru.sbp.objects.structures.ReceiptStructure;
import ru.sbp.objects.db.Order;
import ru.sbp.objects.db.QR;
import ru.sbp.objects.db.User;
import ru.sbp.storage.Context;
import ru.sbp.utils.DbHandler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.sbp.utils.DbHandler.*;

@RestController
public class NotificationController {
//    private static final org.apache.logging.log4j.core.Logger logger = (Logger) LogManager.getLogger(NotificationController.class);
    @Autowired
    private Context context;
    @Autowired
    private ReceiptStructure receipt;

    private ReceiptStructure makeNewReceipt(double price) {
//        logger.info("Making new receipt from template, price " + price);
        ReceiptStructure newReceipt = new ReceiptStructure(receipt);
        newReceipt.setReceiptNumber(java.util.UUID.randomUUID().toString());

        Item newItem = new Item(receipt.getItems().get(0));
        newItem.setPrice(price);
        newItem.setAmount(price);
        List<Item> newItemList = new ArrayList<>();
        newItemList.add(newItem);
        newReceipt.setItems(newItemList);
        newReceipt.setTotal(price);
//        logger.info("Receipt nember: " + newReceipt.getReceiptNumber());
        return newReceipt;
    }

    @PostMapping("/callback")
    @ResponseStatus(value = HttpStatus.OK)
    public void CallbackHandling(
            @RequestBody CallbackBodyStructure callback) {
        QR qr = getQrById(callback.getQrId());
        User user = DbHandler.getUserByQrId(callback.getQrId());

        if (!Objects.equals(qr.getCurrentOrderId(), callback.getOrderId())) {
//            logger.info("Get callback, qr " + callback.getQrId() + " wait orderId " + qr.getCurrentOrderId() + ", get " + callback.getOrderId());
            return;
        }

//        logger.info("Got callback, qr " + callback.getQrId() + " order " + callback.getOrderId());
        // TODO: Check server address
        // TODO: Need to add checking signature

        if (!callback.getStatus().equals("SUCCESS")) {
//            logger.info("Callback status doesn`t equal SUCCESS: " + callback.getStatus());
            return;
        }
//        logger.info("Get fiscType " + user.getFiscType() + " login " + user.getUsername());

//        logger.info("Creating new order for qr " + callback.getQrId());
        var res = RequestHandler.createOrder(callback.getQrId(), user.getSecretKey(), callback.getAmount());
        changeCurrentOrderId(res.getBody());
        qr = DbHandler.getQrById(callback.getQrId());
        qr.setTotalSum(qr.getTotalSum() + res.getBody().getAmount());
        qr.setOrderCount(qr.getOrderCount() + 1);
        updateQr(qr);
        Order order = new Order();
        order.setOrderId(callback.getOrderId());
        order.setFiscalization(qr.getFiscType());
        order.setQrId(callback.getQrId());
        order.setBuyDate(Timestamp.from(Instant.now()));
        order.setAmount(res.getBody().getAmount());
        if (qr.getFiscType() != null && !Objects.equals(qr.getFiscType(), "NOFISC")) {
//            logger.info("Create a new receipt for qr " + callback.getQrId());
            ReceiptStructure newReceipt = makeNewReceipt(callback.getAmount());
            String receiptId = newReceipt.getReceiptNumber();
            order.setReceiptId(receiptId);
            var resCreateReceipt = RequestHandler.createReceipt(newReceipt, user.getSecretKey());
            if (context.isTest()) {
                order.setPhone(context.getPhone());
            }
            updateOrder(order);
//            logger.info("Start waiting to send receipt " + receiptId);
            // TODO: send message if customer doesn`t want a receipt
//            logger.info("Send receipt " + receiptId + " for a default email");
//
//            try {
//                var resSaveReceipt = RequestHandler.saveReceipt(newReceipt.getReceiptNumber(), user.getSecretKey());
//                logger.info("Receipt " + receiptId + " was sent for a default email");
//                order.setDone(true);
//            } catch (Exception e) {
//                logger.info("Receipt " + receiptId + " was sent to a customer");
//            }
        } else {
            updateOrder(order);
        }
    }
}
