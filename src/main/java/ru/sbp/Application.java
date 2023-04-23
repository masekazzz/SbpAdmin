package ru.sbp;

import jakarta.annotation.PostConstruct;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import ru.sbp.apihandlers.RequestHandler;
import ru.sbp.objects.db.Order;
import ru.sbp.objects.db.QrUserMapping;
import ru.sbp.objects.db.QR;
import ru.sbp.objects.db.User;
import ru.sbp.storage.Context;
import ru.sbp.utils.DbHandler;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Application {
    @Autowired
    private Context context;
    public static SessionFactory sessionFactory;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void createSessionFactory() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(QR.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(QrUserMapping.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();
    }


}

