package org.rococo.service;

import com.google.protobuf.Empty;
import org.rococo.grpc.*;
import org.rococo.data.ArtistEntity;
import org.rococo.data.repository.ArtistRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

@GrpcService
public class ArtistGrpcService extends RococoArtistsServiceGrpc.RococoArtistsServiceImplBase {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistGrpcService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void getAll(ArtistsPageRequest request, StreamObserver<ArtistsPageResponse> responseObserver) {
        Page<ArtistEntity> artists = request.getTitle().isEmpty()
                ? artistRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                : artistRepository.findAllByNameContainsIgnoreCase(
                request.getTitle(), PageRequest.of(request.getPage(), request.getSize())
        );

        Page<Artist> artistPages = artists.map(ArtistEntity::toGrpc);

        responseObserver.onNext(
                ArtistsPageResponse.newBuilder()
                        .addAllArtists(artistPages.getContent())
                        .setTotalElements(artistPages.getTotalElements())
                        .setTotalPages(artistPages.getTotalPages())
                        .setFirst(artistPages.isFirst())
                        .setLast(artistPages.isLast())
                        .setSize(artistPages.getSize())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void findArtistById(ArtistByIdRequest request, StreamObserver<ArtistResponse> responseObserver) {
        Optional<ArtistEntity> byId = artistRepository.findById(UUID.fromString(request.getArtistId()));

        if (byId.isPresent()) {
            responseObserver.onNext(
                    ArtistResponse.newBuilder()
                            .setArtist(ArtistEntity.toGrpc(byId.get()))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription(
                    String.format("Artist with id '%s' not found", request.getArtistId())
            ).asException());
        }
    }

    @Override
    public void updateArtist(ArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        Optional<ArtistEntity> byId = artistRepository.findById(UUID.fromString(request.getArtist().getId()));

        if (byId.isPresent()) {
            ArtistEntity artist = new ArtistEntity();
            artist.setId(UUID.fromString(request.getArtist().getId()));
            artist.setName(request.getArtist().getName());
            artist.setBiography(request.getArtist().getBio());
            artist.setPhoto(request.getArtist().getPhoto().getBytes());

            ArtistEntity saved = artistRepository.save(artist);

            responseObserver.onNext(
                    ArtistResponse.newBuilder()
                            .setArtist(ArtistEntity.toGrpc(saved))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription(
                    String.format("Artist with id '%s' not found", request.getArtist().getId())
            ).asException());
        }
    }

    @Override
    public void addArtist(ArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity artist = new ArtistEntity();
        artist.setName(request.getArtist().getName());
        artist.setBiography(request.getArtist().getBio());
        artist.setPhoto(request.getArtist().getPhoto().getBytes());

        ArtistEntity saved = artistRepository.save(artist);
        responseObserver.onNext(
                ArtistResponse.newBuilder()
                        .setArtist(ArtistEntity.toGrpc(saved))
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void deleteArtist(ArtistRequest request, StreamObserver<Empty> responseObserver) {
        artistRepository.deleteById(UUID.fromString(request.getArtist().getId()));
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
