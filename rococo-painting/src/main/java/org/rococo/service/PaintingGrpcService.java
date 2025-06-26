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
        Page<PaintingEntity> paintings = request.getTitle().isEmpty()
                ? paintingRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                : paintingRepository.findAllByTitleContainsIgnoreCase(
                request.getTitle(), PageRequest.of(request.getPage(), request.getSize())
        );

        Page<Painting> paintingPages = new PageImpl<>(
                paintings.stream()
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
    }

    @Override
    public void findPaintingById(PaintingByIdRequest request, StreamObserver<PaintingResponse> responseObserver) {
        Optional<PaintingEntity> byId = paintingRepository.findById(
                UUID.fromString(request.getPaintingId())
        );

        if (byId.isPresent()) {
            responseObserver.onNext(
                    PaintingResponse.newBuilder()
                            .setPainting(toGrpc(byId.get()))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(
                            String.format("Painting with the id '%s' not found", request.getPaintingId())
                    ).asException());
        }
    }

    @Override
    public void findPaintingByAuthorId(PaintingByAuthorIdPageRequest request, StreamObserver<PaintingsPageResponse> responseObserver) {
        Page<PaintingEntity> allByArtistId = paintingRepository.findAllByArtistId(
                UUID.fromString(request.getAuthorId()),
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
    }

    @Override
    public void addPainting(AddPaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
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
    }

    @Override
    public void updatePainting(PaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        Optional<PaintingEntity> paintingEntity = paintingRepository.findById(UUID.fromString(request.getPainting().getId()));
        if (paintingEntity.isPresent()) {
            PaintingEntity entityToUpdate = paintingEntity.get();
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
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(
                            String.format("Painting with the id '%s' not found", request.getPainting().getId())
                    ).asException()
            );
        }
    }

    @Override
    public void deletePainting(DeletePaintingRequest request, StreamObserver<Empty> responseObserver) {
        paintingRepository.deleteById(UUID.fromString(request.getId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
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
                        () -> new NotFoundException(String.format("Художник c id '%s' не найден", artistId))
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
