package ru.marchenko.NauJava.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.marchenko.NauJava.entity.Task;

import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Task> findByTitleAndDescription(String title, String description) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);

        query.select(root)
                .where(cb.and(
                        cb.equal(root.get("title"), title),
                        cb.equal(root.get("description"), description)
                ));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> searchByKeyword(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);
        Root<Task> root = query.from(Task.class);

        String pattern = "%" + keyword + "%";
        query.select(root)
                .where(cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("description"), pattern)
                ));

        return entityManager.createQuery(query).getResultList();
    }
}
