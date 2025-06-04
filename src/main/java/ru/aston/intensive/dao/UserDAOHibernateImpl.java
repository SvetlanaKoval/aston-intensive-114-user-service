package ru.aston.intensive.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;
import ru.aston.intensive.utils.HibernateUtil;

@Slf4j
public class UserDAOHibernateImpl implements UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAOHibernateImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public Long save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            log.debug("DAO: Saved new user={}", user);
            transaction.commit();

            return user.getId();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new AppException("Save failed", e);
        }
    }

    @Override
    public User findById(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            log.debug("DAO: Fetching user by id={}", userId);
            User existingUser = getExistingUser(userId, session);
            log.debug("DAO: Found user={}", existingUser);
            return existingUser;
        } catch (HibernateException e) {
            throw new AppException("Get failed", e);
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            log.debug("DAO: Updating existing user: new name={}, new email={}", user.getName(), user.getEmail());
            User existingUser = getExistingUser(user.getId(), session);
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            log.debug("DAO: Updated user={}", existingUser);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new AppException("Update failed", e);
        }
    }

    @Override
    public void delete(Long userId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User existingUser = getExistingUser(userId, session);
            log.debug("DAO: Deleting user: {}", existingUser);
            session.remove(existingUser);
            log.debug("DAO: Deleted user={}", existingUser);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new AppException("Delete failed", e);
        }
    }

    private User getExistingUser(Long userId, Session session) {
        User existingUser = session.get(User.class, userId);
        if (existingUser == null) {
            throw new AppException(String.format("User with id=%s not found", userId));
        }
        return existingUser;
    }

}
