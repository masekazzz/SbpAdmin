package ru.sbp.controllers;

//import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.sbp.apihandlers.RequestHandler;
import ru.sbp.mailservice.EmailDetails;
import ru.sbp.mailservice.EmailService;
import ru.sbp.objects.structures.ClientStructure;
import ru.sbp.objects.db.Order;
import ru.sbp.objects.db.QR;
import ru.sbp.objects.db.User;
import ru.sbp.storage.Context;

import java.util.List;
import java.util.Objects;

import static ru.sbp.utils.DbHandler.*;

@Controller
public class ReceiptController {
//    private static final org.apache.logging.log4j.core.Logger logger = (Logger) LogManager.getLogger(ReceiptController.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private Context context;

    @RequestMapping(value = "/getReceipt/{id}", method = RequestMethod.GET)
    public String getEmailForm(Model model, @PathVariable String id) {
        QR qr = getQrByRedirectUrlId(id);
        if (qr == null) {
            return "wrong";
        }

        if (qr.getFiscType() != null && !Objects.equals(qr.getFiscType(), "NOFISC")) {
            return "email";
        } else {
            return "nomail";
        }
    }

    @RequestMapping(value = "/getReceipt/{id}", method = RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute ClientStructure client, Model model, @PathVariable String id) {
        QR qr = getQrByRedirectUrlId(id);
        if (qr == null) {
            return "wrong";
        }
        User user = getUserByQrId(qr.getId());

        EmailDetails details = new EmailDetails();
        details.setRecipient(client.getEmail());
        if (context.isTest()) {
            details.setMsgBody("In production here will be a receipt for paying. Thank you!");
            details.setSubject("A receipt for paying using QR - sandbox");
        }
//        logger.info("Sending receipt " + " qr " + qr.getId() + " to customer");
        List<Order> orders = getOrdersByQrId(qr.getId());
        if (orders == null) {
            return "result";
        }

        for (Order order: orders) {
            if (Objects.equals(order.getPhone(), client.getPhone()) && !order.getIsDone()) {
                // TODO: add adding customer email
//                var resCreateReceipt = RequestHandler.createReceipt(orderStructure, user.getSecretKey());
                var resSaveReceipt = RequestHandler.saveReceipt(order.getReceiptId(), user.getSecretKey());
                order.setDone(true);
                updateOrder(order);
                sendMail(details);
                model.addAttribute("client", client);
            }
        }
        return "result";
    }

    private String sendMail(EmailDetails details) {
        return emailService.sendSimpleMail(details);
    }
}
