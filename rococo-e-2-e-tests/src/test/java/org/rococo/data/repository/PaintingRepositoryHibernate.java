package org.rococo.data.repository;

import org.rococo.data.DataBase;
import org.rococo.data.entity.PaintingEntity;
import org.rococo.data.jpa.EmfContext;
import org.rococo.data.jpa.JpaService;

import java.util.List;
import java.util.stream.Collectors;

public class PaintingRepositoryHibernate extends JpaService {

    public PaintingRepositoryHibernate() {
        super(EmfContext.INSTANCE.getEmf(DataBase.PAINTING).createEntityManager());
    }

    public List<PaintingEntity> allPaintings(int pageNumber, int pageSize, String query) {
        if (query == null) {
            flush();
            return em.createQuery("SELECT p FROM PaintingEntity p", PaintingEntity.class)
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        } else {
            flush();
            return em.createQuery(
                    "SELECT p FROM PaintingEntity p WHERE LOWER(p.title) LIKE :query",
                    PaintingEntity.class)
                .setParameter("query", "%" + query.toLowerCase() + "%")
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        }
    }
}
