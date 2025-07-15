package com.example.hotdeal.domain.notification.infra;

import com.example.hotdeal.domain.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class NotifyInsertRepositoryImpl implements NotifyInsertRepository {

    private final SessionFactory sessionFactory;

    public void insertNotifications(List<Notification> notifications) {
        StatelessSession session = sessionFactory.openStatelessSession();
        session.beginTransaction();

        try {
            log.info("저장 시작 size = {}", notifications.size());
            for(Notification note : notifications){
                session.insert(note);
            }
            session.getTransaction().commit();
            log.info("저장 끝");
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
