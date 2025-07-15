package org.rococo.service;

import com.google.protobuf.Empty;
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
        try {
            int page = request.getPage();
            int size = request.getSize();

            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Invalid pagination parameters");
            }

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
        } catch (IllegalArgumentException ex) {

            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        } catch (Exception e) {

            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void findMuseumById(MuseumByIdRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            Optional<MuseumEntity> byId = museumRepository.findById(UUID.fromString(request.getMuseumId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Museum with id '%s' not found", request.getMuseumId()))
                        .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(
                MuseumResponse.newBuilder()
                    .setMuseum(MuseumEntity.toGrpc(byId.get()))
                    .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void updateMuseum(MuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            Optional<MuseumEntity> byId = museumRepository.findById(UUID.fromString(request.getMuseum().getId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Museum with id '%s' not found", request.getMuseum().getId()))
                        .asRuntimeException()
                );
                return;
            }

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

        } catch (NotFoundException ex) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(String.format("Country with id '%s' not found",
                        request.getMuseum().getGeo().getCountry().getId()))
                    .asRuntimeException()
            );

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void addMuseum(MuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            if (request.getMuseum().getTitle().isBlank()) {
                throw new IllegalArgumentException("Title must not be blank");
            }

            if (request.getMuseum().getDescription().isBlank()) {
                throw new IllegalArgumentException("Description must not be blank");
            }

            if (request.getMuseum().getPhoto().isBlank()) {
                throw new IllegalArgumentException("Photo must not be blank");
            }

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
        } catch (NotFoundException ex) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(String.format("Country with id '%s' not found",
                        request.getMuseum().getGeo().getCountry().getId()))
                    .asRuntimeException()
            );
        }
        catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void deleteMuseum(MuseumByIdRequest request, StreamObserver<Empty> responseObserver) {
        try {
            Optional<MuseumEntity> byId = museumRepository.findById(UUID.fromString(request.getMuseumId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Museum with id '%s' not found", request.getMuseumId()))
                        .asRuntimeException()
                );
                return;
            }

            museumRepository.deleteById(UUID.fromString(request.getMuseumId()));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal server error")
                    .withCause(e)
                    .asRuntimeException()
            );
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
