package com.project.userService.dao;

import com.project.userService.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class TestUserDAOImpl implements UserDAO {

    private final SessionFactory sessionFactory;

    public TestUserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserEntity create(UserEntity user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserEntity> cr = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = cr.from(UserEntity.class);
            cr.select(root);
            cr.where(cb.equal(root.get("id"), id));

            Query<UserEntity> query = session.createQuery(cr);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserEntity> cr = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = cr.from(UserEntity.class);
            cr.select(root);
            cr.orderBy(cb.asc(root.get("id")));

            Query<UserEntity> query = session.createQuery(cr);
            return query.getResultList();
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserEntity updatedUser = session.merge(user);
            transaction.commit();
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserEntity> cr = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = cr.from(UserEntity.class);
            cr.select(root);
            cr.where(cb.equal(root.get("id"), id));

            Query<UserEntity> query = session.createQuery(cr);
            UserEntity user = query.uniqueResult();

            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserEntity> cr = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = cr.from(UserEntity.class);
            cr.select(root);
            cr.where(cb.equal(root.get("email"), email));

            Query<UserEntity> query = session.createQuery(cr);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public List<UserEntity> findByAgeGreaterThan(int age) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserEntity> cr = cb.createQuery(UserEntity.class);
            Root<UserEntity> root = cr.from(UserEntity.class);
            cr.select(root);
            cr.where(cb.gt(root.get("age"), age));
            cr.orderBy(cb.asc(root.get("age")));

            Query<UserEntity> query = session.createQuery(cr);
            return query.getResultList();
        }
    }
}