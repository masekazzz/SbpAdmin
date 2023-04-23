package ru.sbp.utils;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.sbp.Application;
import ru.sbp.objects.structures.OrderStructure;
import ru.sbp.objects.db.Order;
import ru.sbp.objects.db.QR;
import ru.sbp.objects.db.QrUserMapping;
import ru.sbp.objects.db.User;

import java.util.ArrayList;
import java.util.List;

public class DbHandler {
    public static Session getHibernateSession() {
        Session session = Application.sessionFactory.openSession();
        return session;
    }

    public static void changeCurrentOrderId(OrderStructure os) {
        Session session = getHibernateSession();
        session.beginTransaction();
        QR qr = session.get(QR.class, os.getQr().getId());
        qr.setCurrentOrderId(os.getId());
        session.getTransaction().commit();
        session.close();
    }

    public static QR getQrByOrder(OrderStructure order) {
        Session session = getHibernateSession();
        session.beginTransaction();
        QR qr = session.get(QR.class, order.getQr().getId());
        if (qr == null) {
            qr = new QR(order.getQr().getId(), null, 0, 0.0);
            session.persist(qr);
        }
        session.getTransaction().commit();
        session.close();
        return qr;
    }

    public static void updateUserFiscType(String fiscType, String qrId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        QR qr = session.get(QR.class, qrId);
        qr.setFiscType(fiscType);
        session.persist(qr);
        session.getTransaction().commit();
        session.close();
    }

    public static QR getQrById(String id) {
        Session session = getHibernateSession();
        session.beginTransaction();
        QR qr = session.get(QR.class, id);
        if (qr == null) {
            qr = new QR(id, null, 0, 0.0);
            session.persist(qr);
        }
        session.getTransaction().commit();
        session.close();
        return qr;
    }

    public static User getUserByQrId(String qrId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM QrUserMapping where qrId = :qrid").setParameter("qrid", qrId);
        QrUserMapping result = (QrUserMapping) query.uniqueResult();
        User user = session.get(User.class, result.getUserId());
        session.getTransaction().commit();
        session.close();
        return user;
    }

    public static User getUserByUsername(String username) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM User where username = :username").setParameter("username", username);
        User user = (User) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return user;
    }

    public static User getUserById(Long id) {
        Session session = getHibernateSession();
        session.beginTransaction();
        User user = session.get(User.class, id);
        session.getTransaction().commit();
        session.close();
        return user;
    }

    public static QR getQrByRedirectUrlId(String redirectUrlId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM QR where redirectUrlId = :id").setParameter("id", redirectUrlId);
        QR result = (QR) query.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public static List<Order> getOrdersByQrId(String qrId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM Order where qrId = :qrid").setParameter("qrid", qrId);
        List<Order> results = (List<Order>) query.getResultList();
        session.getTransaction().commit();
        session.close();
        return results;
    }

    public static void clearStats(String qrId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Order where qrId = :qrid").setParameter("qrid", qrId).executeUpdate();
        QR qr = session.get(QR.class, qrId);
        qr.setOrderCount(0);
        qr.setTotalSum(0.0);
        session.getTransaction().commit();
        session.close();
    }

    public static void updateQr(QR qr){
        Session session = getHibernateSession();
        session.beginTransaction();
        QR dbQr = session.get(QR.class, qr.getId());
        dbQr.updateQr(qr);
        session.persist(dbQr);
        session.getTransaction().commit();
        session.close();
    }

    public static void updateOrder(Order order) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Order dbOrder = session.get(Order.class, order.getOrderId());
        if (dbOrder != null){
            dbOrder.updateOrder(order);
            session.persist(dbOrder);
        }
        else{
            session.persist(order);
        }
        session.getTransaction().commit();
        session.close();
    }

    public static Order getOrder(String orderId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Order order = session.get(Order.class, orderId);
        session.getTransaction().commit();
        session.close();
        return order;
    }

    public static void setSecretKey(Long userId, String secretKey){
        Session session = getHibernateSession();
        session.beginTransaction();
        User user = session.get(User.class, userId);
        user.setSecretKey(secretKey);
        session.persist(user);
        session.getTransaction().commit();
        session.close();
    }

    public static void linkQrWithUser(String qrId, Long userId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM QrUserMapping where qrId = :qrid and userId = :userid")
                .setParameter("qrid", qrId).setParameter("userid", userId);
        if (query.getResultList().isEmpty()){
            QrUserMapping mapping = new QrUserMapping(userId, qrId);
            session.persist(mapping);
            session.getTransaction().commit();
        }
        session.close();
    }

    public static void createUser(User user) {
        Session session = getHibernateSession();
        session.beginTransaction();
        session.persist(user);
        session.getTransaction().commit();
        session.close();
    }

    public static void deleteLink(Long userId, String qrId) {
        Session session = getHibernateSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM QrUserMapping where qrId = :qrid and userId = :userId")
                .setParameter("qrid", qrId)
                .setParameter("userId", userId)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public static List<QR> getQrsByUserId(Long id) {
        Session session = getHibernateSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM QrUserMapping where userId = :userid").setParameter("userid", id);
        List<QR> result = new ArrayList<>();
        for (Object mapping: query.getResultList()) {
            result.add(getQrById(((QrUserMapping)mapping).getQrId()));
        }
        session.close();
        return result;
    }
}
