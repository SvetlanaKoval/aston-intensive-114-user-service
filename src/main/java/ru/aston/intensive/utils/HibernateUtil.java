package ru.aston.intensive.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

            Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .getMetadataBuilder()
                .build();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Exception ex) {
            throw new AppException("Initial SessionFactory creation failed.", ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
