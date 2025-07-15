package org.rococo.data.repository;

import org.rococo.data.DataBase;
import org.rococo.data.entity.MuseumEntity;
import org.rococo.data.jpa.EmfContext;
import org.rococo.data.jpa.JpaService;

import java.util.List;
import java.util.stream.Collectors;


public class MuseumRepositoryHibernate extends JpaService {

    public MuseumRepositoryHibernate() {
        super(EmfContext.INSTANCE.getEmf(DataBase.MUSEUM).createEntityManager());
    }

    public List<MuseumEntity> allMuseums(int pageNumber, int pageSize, String query) {
        if (query == null) {
            flush();
            return em.createQuery("SELECT m FROM MuseumEntity m", MuseumEntity.class)
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        } else {
            flush();
            return em.createQuery(
                    "SELECT m FROM MuseumEntity m WHERE LOWER(m.title) LIKE :query",
                    MuseumEntity.class)
                .setParameter("query", "%" + query.toLowerCase() + "%")
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        }
    }

}
