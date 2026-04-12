package com.project.userService.dao;

import com.project.userService.entity.User;
import com.project.userService.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    @Override
    public User create(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User created successfully: {}", user);
            return user;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            logger.debug("Finding user by id: {}", id);
            return Optional.ofNullable(user);
        } catch (PersistenceException e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to find user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            cr.select(root);
            cr.orderBy(cb.asc(root.get("id")));

            Query<User> query = session.createQuery(cr);
            List<User> users = query.getResultList();
            logger.info("Found {} users", users.size());
            return users;
        } catch (PersistenceException e) {
            logger.error("Error finding all users: {}", e.getMessage());
            throw new RuntimeException("Failed to find all users: " + e.getMessage(), e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", updatedUser);
            return updatedUser;
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("User deleted successfully with id: {}", id);
            } else {
                logger.warn("User not found with id: {}", id);
            }
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting user with id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            cr.select(root);
            cr.where(cb.equal(root.get("email"), email));

            Query<User> query = session.createQuery(cr);
            User user = query.getSingleResult();
            logger.debug("Finding user by email: {}", email);
            return Optional.ofNullable(user);
        } catch (PersistenceException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByAgeGreaterThan(int age) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cr = cb.createQuery(User.class);
            Root<User> root = cr.from(User.class);
            cr.select(root);
            cr.where(cb.gt(root.get("age"), age));
            cr.orderBy(cb.asc(root.get("age")));

            Query<User> query = session.createQuery(cr);
            List<User> users = query.getResultList();
            logger.info("Found {} users older than {}", users.size(), age);
            return users;
        } catch (PersistenceException e) {
            logger.error("Error finding users older than {}: {}", age, e.getMessage());
            throw new RuntimeException("Failed to find users by age: " + e.getMessage(), e);
        }
    }
}
