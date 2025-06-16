package org.rococo.service;

import org.rococo.data.CountryEntity;
import org.rococo.data.MuseumEntity;
import org.rococo.data.repository.CountryRepository;
import org.rococo.data.repository.MuseumRepository;
import org.rococo.exception.NotFoundException;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

@GrpcService
public class MuseumGrpcService extends RococoMuseumsServiceGrpc.RococoMuseumsServiceImplBase {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public MuseumGrpcService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAll(MuseumsPageRequest request, StreamObserver<MuseumsPageResponse> responseObserver) {
        Page<MuseumEntity> museums = request.getTitle().isEmpty()
                ? museumRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                : museumRepository.findAllByTitleContainsIgnoreCase(
                request.getTitle(), PageRequest.of(request.getPage(), request.getSize())
        );

        Page<Museum> museumPages = museums.map(MuseumEntity::toGrpc);

        responseObserver.onNext(
                MuseumsPageResponse.newBuilder()
                        .addAllMuseums(museumPages.getContent())
                        .setTotalElements(museumPages.getTotalElements())
                        .setTotalPages(museumPages.getTotalPages())
                        .setFirst(museumPages.isFirst())
                        .setLast(museumPages.isLast())
                        .setSize(museumPages.getSize())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void findMuseumById(MuseumByIdRequest request, StreamObserver<MuseumResponse> responseObserver) {
        Optional<MuseumEntity> byId = museumRepository.findById(UUID.fromString(request.getMuseumId()));

        if (byId.isPresent()) {
            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(MuseumEntity.toGrpc(byId.get()))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription(
                    String.format("Museum with id '%s' not found", request.getMuseumId())
            ).asException());
        }
    }

    @Override
    public void updateMuseum(MuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        Optional<MuseumEntity> byId = museumRepository.findById(UUID.fromString(request.getMuseum().getId()));

        if (byId.isPresent()) {
            MuseumEntity museum = new MuseumEntity();
            String countryId = request.getMuseum().getGeo().getCountry().getId();
            CountryEntity country = !countryId.isEmpty()
                    ? getRequiredCountry(UUID.fromString(countryId))
                    : getRequiredCountry(request.getMuseum().getGeo().getCountry().getName());
            museum.setCountry(country);
            museum.setId(UUID.fromString(request.getMuseum().getId()));
            museum.setTitle(request.getMuseum().getTitle());
            museum.setDescription(request.getMuseum().getDescription());
            museum.setCity(request.getMuseum().getGeo().getCity());
            museum.setPhoto(request.getMuseum().getPhoto().getBytes());

            MuseumEntity saved = museumRepository.save(museum);
            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(MuseumEntity.toGrpc(saved))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription(
                    String.format("Museum with id '%s' not found", request.getMuseum().getId())
            ).asException());
        }
    }

    @Override
    public void addMuseum(MuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        Optional<MuseumEntity> byTitle = museumRepository.findByTitle(request.getMuseum().getTitle());

        if (byTitle.isEmpty()) {
            MuseumEntity museum = new MuseumEntity();
            String countryId = request.getMuseum().getGeo().getCountry().getId();
            CountryEntity country = !countryId.isEmpty()
                    ? getRequiredCountry(UUID.fromString(countryId))
                    : getRequiredCountry(request.getMuseum().getGeo().getCountry().getName());
            museum.setCountry(country);
            museum.setTitle(request.getMuseum().getTitle());
            museum.setDescription(request.getMuseum().getDescription());
            museum.setCity(request.getMuseum().getGeo().getCity());
            museum.setPhoto(request.getMuseum().getPhoto().getBytes());

            MuseumEntity saved = museumRepository.save(museum);
            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(MuseumEntity.toGrpc(saved))
                            .build()

            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.ALREADY_EXISTS.withDescription(
                            String.format("Museum with the title '%s' already exists", request.getMuseum().getTitle())
                    ).asException());
        }
    }

    private @Nonnull CountryEntity getRequiredCountry(@Nonnull UUID id) {
        return countryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Страна не найдена по id: " + id)
        );
    }

    private @Nonnull CountryEntity getRequiredCountry(@Nonnull String name) {
        return countryRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("Страна не найдена по имени: " + name)
        );
    }

}
