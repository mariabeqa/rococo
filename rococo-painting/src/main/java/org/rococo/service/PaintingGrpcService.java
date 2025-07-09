package org.rococo.service;

import com.google.protobuf.Empty;
import org.rococo.data.PaintingEntity;
import org.rococo.data.repository.PaintingRepository;
import org.rococo.ex.NotFoundException;
import org.rococo.grpc.*;
import org.rococo.service.api.ArtistGrpcClient;
import org.rococo.service.api.MuseumGrpcClient;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@GrpcService
public class PaintingGrpcService extends RococoPaintingsServiceGrpc.RococoPaintingsServiceImplBase {

    private final PaintingRepository paintingRepository;
    private final MuseumGrpcClient museumGrpcClient;
    private final ArtistGrpcClient artistGrpcClient;

    @Autowired
    public PaintingGrpcService(PaintingRepository paintingRepository,
                               MuseumGrpcClient museumGrpcClient,
                               ArtistGrpcClient artistGrpcClient) {
        this.paintingRepository = paintingRepository;
        this.museumGrpcClient = museumGrpcClient;
        this.artistGrpcClient = artistGrpcClient;
    }

    @Override
    public void getAll(PaintingsPageRequest request, StreamObserver<PaintingsPageResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize();

            if (page < 0 || size <= 0) {
                throw new IllegalArgumentException("Invalid pagination parameters");
            }

            Page<PaintingEntity> paintings = request.getTitle().isEmpty()
                ? paintingRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                : paintingRepository.findAllByTitleContainsIgnoreCase(
                request.getTitle(), PageRequest.of(request.getPage(), request.getSize())
            );

            Page<Painting> paintingPages = new PageImpl<>(
                paintings.stream()
                    .map(this::toGrpc)
                    .collect(Collectors.toList())
            );

            responseObserver.onNext(
                PaintingsPageResponse.newBuilder()
                    .addAllPaintings(paintingPages.getContent())
                    .setTotalElements(paintingPages.getTotalElements())
                    .setTotalPages(paintingPages.getTotalPages())
                    .setFirst(paintingPages.isFirst())
                    .setLast(paintingPages.isLast())
                    .setSize(paintingPages.getSize())
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
    public void findPaintingById(PaintingByIdRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            Optional<PaintingEntity> byId = paintingRepository.findById(
                UUID.fromString(request.getPaintingId())
            );

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Painting with id '%s' not found", request.getPaintingId()))
                        .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(
                PaintingResponse.newBuilder()
                    .setPainting(toGrpc(byId.get()))
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
    public void findPaintingByAuthorId(PaintingByAuthorIdPageRequest request, StreamObserver<PaintingsPageResponse> responseObserver) {
        try {

            if (request.getAuthorId().isBlank()) {
                throw new IllegalArgumentException("Author id must not be blank");
            }

            Artist artist = getArtistById(request.getAuthorId());

            Page<PaintingEntity> allByArtistId = paintingRepository.findAllByArtistId(
                UUID.fromString(artist.getId()),
                PageRequest.of(request.getPage(), request.getSize())
            );

            Page<Painting> paintingPages = new PageImpl<>(
                allByArtistId.stream()
                    .map(pe -> toGrpc(pe))
                    .collect(Collectors.toList())
            );

            responseObserver.onNext(
                PaintingsPageResponse.newBuilder()
                    .addAllPaintings(paintingPages.getContent())
                    .setTotalElements(paintingPages.getTotalElements())
                    .setTotalPages(paintingPages.getTotalPages())
                    .setFirst(paintingPages.isFirst())
                    .setLast(paintingPages.isLast())
                    .setSize(paintingPages.getSize())
                    .build()
            );
            responseObserver.onCompleted();

        } catch (NotFoundException ex) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );

        } catch (IllegalArgumentException e) {
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
    public void addPainting(AddPaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            if (request.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title must not be blank");
            }

            if (request.getDescription().isBlank()) {
                throw new IllegalArgumentException("Description must not be blank");
            }

            if (request.getContent().isBlank()) {
                throw new IllegalArgumentException("Content must not be blank");
            }

            PaintingEntity entityToSave = new PaintingEntity();
            entityToSave.setTitle(request.getTitle());
            entityToSave.setDescription(request.getDescription());
            entityToSave.setContent(request.getContent().getBytes(StandardCharsets.UTF_8));

            Artist artist = getArtistById(request.getArtistId().getId());
            entityToSave.setArtistId(UUID.fromString(artist.getId()));

            Museum museum = getMuseumById(request.getMuseumId().getId());
            entityToSave.setMuseumId(UUID.fromString(museum.getId()));

            PaintingEntity saved = paintingRepository.save(entityToSave);

            responseObserver.onNext(PaintingResponse.newBuilder()
                .setPainting(toGrpc(saved))
                .build()
            );
            responseObserver.onCompleted();
        } catch (NotFoundException ex) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(ex.getMessage())
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
    public void updatePainting(PaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {

        try {
            Optional<PaintingEntity> byId = paintingRepository.findById(UUID.fromString(request.getPainting().getId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Painting with id '%s' not found", request.getPainting().getId()))
                        .asRuntimeException()
                );
                return;
            }

            PaintingEntity entityToUpdate = byId.get();
            entityToUpdate.setTitle(request.getPainting().getTitle());
            entityToUpdate.setDescription(request.getPainting().getDescription());
            entityToUpdate.setContent(request.getPainting().getContent().getBytes(StandardCharsets.UTF_8));
            entityToUpdate.setArtistId(UUID.fromString(request.getPainting().getArtist().getId()));
            entityToUpdate.setMuseumId(UUID.fromString(request.getPainting().getMuseum().getId()));
            PaintingEntity saved = paintingRepository.save(entityToUpdate);
            responseObserver.onNext(
                PaintingResponse.newBuilder()
                    .setPainting(toGrpc(saved))
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
    public void deletePainting(DeletePaintingRequest request, StreamObserver<Empty> responseObserver) {
        try {
            Optional<PaintingEntity> byId = paintingRepository.findById(UUID.fromString(request.getId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Painting with id '%s' not found", request.getId()))
                        .asRuntimeException()
                );
                return;
            }

            paintingRepository.deleteById(UUID.fromString(request.getId()));
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

    public Painting toGrpc(PaintingEntity entity) {
        Museum museum = getMuseumById(entity.getMuseumId().toString());
        Artist artist = getArtistById(entity.getArtistId().toString());

        return Painting.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setContent(new String(entity.getContent()))
                .setMuseum(
                        Museum.newBuilder()
                                .setId(entity.getMuseumId().toString())
                                .setTitle(museum.getTitle())
                                .setDescription(museum.getDescription())
                                .setPhoto(museum.getPhoto())
                                .setGeo(
                                        GeoLocation.newBuilder()
                                                .setCity(museum.getGeo().getCity())
                                                .setCountry(museum.getGeo().getCountry())
                                                .build()
                                )
                                .build()
                )
                .setArtist(
                        Artist.newBuilder()
                                .setId(entity.getArtistId().toString())
                                .setName(artist.getName())
                                .setBio(artist.getBio())
                                .setPhoto(artist.getPhoto())
                                .build()
                )
                .build();
    }

    private @Nonnull Artist getArtistById(String artistId) {
        ArtistResponse artistResponse = artistGrpcClient.findArtistById(artistId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Artist with id '%s' not found", artistId))
                );
        return artistResponse.getArtist();
    }

    private @Nonnull Museum getMuseumById(String museumId) {
        MuseumResponse museumResponse = museumGrpcClient.findMuseumById(museumId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Музей c id '%s' не найден", museumId))
                );
        return museumResponse.getMuseum();
    }

}
