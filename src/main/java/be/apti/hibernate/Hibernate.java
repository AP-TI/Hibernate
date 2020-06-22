package be.apti.hibernate;

import be.apti.hibernate.model.Laptop;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.ls.LSOutput;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Hibernate {
    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().addPackage("be.apti.hibernate").addAnnotatedClass(Laptop.class).buildSessionFactory();
        } catch (HibernateException exception) {
            throw new ExceptionInInitializerError(exception);
        }
        addData(new Laptop("MSI", "Die van de Kobe", LocalDate.of(2018, 04, 02)));
        addData(new Laptop("Apple", "Die van Turpal", LocalDate.of(2018, 04, 02)));
        addData(new Laptop("Apple", "MacBook Pro van Maxim", LocalDate.of(2018, 04, 02)));
        getAllLaptops().forEach(System.out::println);
        //deleteLaptopByType("MacBook Pro van Maxim");
        updateLaptopByType("MacBook Pro van Maxim", "MBP van Maxim");
        getAllLaptops().forEach(System.out::println);

    }

    private static void addData(Laptop laptop) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(laptop);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static List<Laptop> getAllLaptops() {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);
                criteriaQuery.from(Laptop.class);
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static List<Laptop> getLaptopByVendor(String vendor) {
        List<Laptop> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("vendor"), vendor));
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }

    private static void deleteLaptopByType(String type) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(session::delete);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static void updateLaptopByType(String type, String newType) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Laptop> criteriaQuery = builder.createQuery(Laptop.class);

                Root<Laptop> root = criteriaQuery.from(Laptop.class);
                criteriaQuery.where(builder.equal(root.get("type"), type));
                session.createQuery(criteriaQuery).getResultList().forEach(laptop -> {
                    laptop.setType(newType);
                    session.update(laptop);
                });
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
    }

}
