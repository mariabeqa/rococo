package guru.qa.rococo.data;

import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.GeoLocation;
import guru.qa.rococo.grpc.Museum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
@Setter
@Entity
@Table(name = "museum")
public class MuseumEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column
    private String description;

    @Column
    private String city;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private CountryEntity country;

//    @OneToMany(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "museum")
//    private Set<PaintingEntity> paintings = new HashSet<>();
//
//    public void addPaintings(PaintingEntity... paintings) {
//        for (PaintingEntity painting : paintings) {
//            this.paintings.add(painting);
//            painting.setMuseum(this);
//        }
//    }
//
//    public void removePaintings(PaintingEntity painting) {
//        this.paintings.remove(painting);
//        painting.setMuseum(null);
//    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MuseumEntity that = (MuseumEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static @Nullable Museum toGrpc(@Nullable MuseumEntity entity) {
        return (entity != null)
                ? Museum.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setPhoto(new String(entity.getPhoto(), UTF_8))
                .setGeo(
                        GeoLocation.newBuilder()
                                .setCity(entity.getCity())
                                .setCountry(
                                        Country.newBuilder()
                                                .setId(entity.getCountry().getId().toString())
                                                .setName(entity.getCountry().getName())
                                                .build()
                                )
                                .build()
                )
                .build()
                : null;
    }
}

