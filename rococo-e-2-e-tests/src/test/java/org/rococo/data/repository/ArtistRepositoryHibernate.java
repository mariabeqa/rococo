package org.rococo.data.repository;

import org.rococo.data.DataBase;
import org.rococo.data.entity.ArtistEntity;
import org.rococo.data.jpa.EmfContext;
import org.rococo.data.jpa.JpaService;

import java.util.List;
import java.util.stream.Collectors;

public class ArtistRepositoryHibernate extends JpaService {

    public ArtistRepositoryHibernate() {
        super(EmfContext.INSTANCE.getEmf(DataBase.ARTIST).createEntityManager());
    }

    public List<ArtistEntity> allArtists(int pageNumber, int pageSize, String query) {
        if (query == null) {
            flush();
            return em.createQuery("SELECT a FROM ArtistEntity a", ArtistEntity.class)
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        } else {
            flush();
            return em.createQuery(
                    "SELECT a FROM ArtistEntity a WHERE LOWER(a.name) LIKE :query",
                    ArtistEntity.class)
                .setParameter("query", "%" + query.toLowerCase() + "%")
                .setFirstResult((pageNumber) * pageSize)
                .setMaxResults(pageSize)
                .getResultStream()
                .collect(Collectors.toList());
        }
    }
}
