package org.rococo.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.data.ArtistEntity;
import org.rococo.data.repository.ArtistRepository;
import org.rococo.grpc.*;
import org.rococo.utils.ImageUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrpcArtistServiceTest {

    private static final String ARTIST_NAME = "Архип Куинджи";
    private static final String ARTIST_BIO = "Биография Архипа Куинджи";
    private static final String IMAGE_PATH = "img/artist/kuindzhi.jpg";
    private static final String IMAGE_PATH_NEW = "img/artist/malevich.jpg";

    //1st entity
    private ArtistEntity firstArtist;
    private final String firstArtistId = UUID.randomUUID().toString();
    //2d entity
    private ArtistEntity secondArtist;
    private final String secondArtistId = UUID.randomUUID().toString();
    private final String secondArtistName = "Иван Шишкин";
    private final String secondArtistBio = "Биография Ивана Шишкина";

    @Mock
    private ArtistRepository artistRepository;

    private ArtistGrpcService artistGrpcService;
    private StreamRecorder<ArtistResponse> responseObserver;


    @BeforeEach
    void setUp() {
        firstArtist = new ArtistEntity();
        firstArtist.setId(UUID.fromString(firstArtistId));
        firstArtist.setName(ARTIST_NAME);
        firstArtist.setBiography(ARTIST_BIO);
        firstArtist.setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH).getBytes());

        secondArtist = new ArtistEntity();
        secondArtist.setId(UUID.fromString(secondArtistId));
        secondArtist.setName(secondArtistName);
        secondArtist.setBiography(secondArtistBio);
        secondArtist.setPhoto("".getBytes());

        artistGrpcService = new ArtistGrpcService(artistRepository);
        responseObserver = StreamRecorder.create();
    }

    @Test
    void allArtistsShouldReturnCorrectArtistList() throws ExecutionException, InterruptedException, TimeoutException {
        ArtistsPageRequest request = ArtistsPageRequest.newBuilder()
                .setTitle("")
                .setPage(0)
                .setSize(10)
                .build();

        when(artistRepository.findAll(PageRequest.of(request.getPage(), request.getSize())))
                .thenReturn(new PageImpl<>(List.of(firstArtist, secondArtist), PageRequest.of(request.getPage(), request.getSize()), 2));

        StreamRecorder<ArtistsPageResponse> pageResponseObserver = StreamRecorder.create();

        artistGrpcService.getAll(request, pageResponseObserver);
        List<Artist> artists = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getArtistsList();

        assertEquals(2, artists.size());
        assertEquals(firstArtistId, artists.getFirst().getId());
        assertEquals(ARTIST_NAME, artists.getFirst().getName());
        assertEquals(ARTIST_BIO, artists.getFirst().getBio());
        assertEquals(
                ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
                artists.getFirst().getPhoto()
        );
        assertEquals(secondArtistId, artists.getLast().getId());
        assertEquals(secondArtistName, artists.getLast().getName());
        assertEquals(secondArtistBio, artists.getLast().getBio());
        assertEquals(
                "",
                artists.getLast().getPhoto()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void allArtistsShouldReturnCorrectUserIfTitleIsPassed() throws ExecutionException, InterruptedException, TimeoutException {
        ArtistsPageRequest request = ArtistsPageRequest.newBuilder()
                .setTitle("Ар")
                .setPage(0)
                .setSize(10)
                .build();

        when(artistRepository.findAllByNameContainsIgnoreCase(any(String.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(firstArtist), PageRequest.of(request.getPage(), request.getSize()), 1));

        StreamRecorder<ArtistsPageResponse> pageResponseObserver = StreamRecorder.create();

        artistGrpcService.getAll(request, pageResponseObserver);
        List<Artist> artists = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getArtistsList();

        assertEquals(1, artists.size());
        assertEquals(firstArtistId, artists.getFirst().getId());
        assertEquals(ARTIST_NAME, artists.getFirst().getName());
        assertEquals(ARTIST_BIO, artists.getFirst().getBio());
        assertEquals(
                ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
                artists.getFirst().getPhoto()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void findArtistByIdShouldReturnCorrectUser() {
        final ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
                .setArtistId(firstArtistId)
                .build();

        when(artistRepository.findById(eq(UUID.fromString(firstArtistId))))
                .thenReturn(Optional.of(firstArtist));

        artistGrpcService.findArtistById(request, responseObserver);

        Artist artist = responseObserver.getValues().getFirst().getArtist();
        assertEquals(firstArtistId, artist.getId());
        assertEquals(ARTIST_NAME, artist.getName());
        assertEquals(ARTIST_BIO, artist.getBio());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            artist.getPhoto()
        );
        assertNull(responseObserver.getError());
    }

    @Test
    void findArtistByIdShouldReturnNotFoundIfUserNotFound() {
        final UUID notExistingArtist = UUID.randomUUID();
        final ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
                .setArtistId(notExistingArtist.toString())
                .build();

        when(artistRepository.findById(eq(notExistingArtist)))
                .thenReturn(Optional.empty());

        artistGrpcService.findArtistById(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Artist with id '%s' not found", request.getArtistId()),
                ex.getStatus().getDescription());
    }

    @Test
    void updateArtistShouldReturnUpdatedArtist() {
        final String newArtistName = "Казимир Малевич";
        final String newArtistBio = "Биография Казимира Малевича";
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                        .setId(firstArtistId)
                        .setName(newArtistName)
                        .setBio(newArtistBio)
                        .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH_NEW))
                )
                .build();

        when(artistRepository.findById(eq(UUID.fromString(firstArtistId))))
                .thenReturn(Optional.of(firstArtist));

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(answer -> answer.getArguments()[0]);

        artistGrpcService.updateArtist(request, responseObserver);

        Artist artist = responseObserver.getValues().getFirst().getArtist();
        assertEquals(firstArtistId, artist.getId());
        assertEquals(newArtistName, artist.getName());
        assertEquals(newArtistBio, artist.getBio());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH_NEW),
            artist.getPhoto()
        );
        assertNull(responseObserver.getError());

        verify(artistRepository, times(1)).save(any(ArtistEntity.class));
    }

    @Test
    void updateArtistShouldReturnNotFoundIfUserNotFound() {
        final UUID notExistingArtist = UUID.randomUUID();
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setId(notExistingArtist.toString())
                                .setName(ARTIST_NAME)
                                .setBio(ARTIST_BIO)
                                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                )
                .build();

        when(artistRepository.findById(eq(notExistingArtist)))
                .thenReturn(Optional.empty());

        artistGrpcService.updateArtist(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Artist with id '%s' not found", notExistingArtist),
                ex.getStatus().getDescription());
    }

    @Test
    void createArtistShouldReturnCreatedArtist() {
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setName(ARTIST_NAME)
                                .setBio(ARTIST_BIO)
                                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                )
                .build();

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenAnswer(answer -> answer.getArguments()[0]);

        artistGrpcService.addArtist(request, responseObserver);

        Artist artist = responseObserver.getValues().getFirst().getArtist();
        assertEquals(ARTIST_NAME, artist.getName());
        assertEquals(ARTIST_BIO, artist.getBio());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            artist.getPhoto()
        );
        assertNull(responseObserver.getError());

        verify(artistRepository, times(1)).save(any(ArtistEntity.class));
    }

    @Test
    void createArtistShouldReturnInvalidArgumentIfUsernameIsBlank() {
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setName("")
                                .setBio(ARTIST_BIO)
                                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                )
                .build();

        artistGrpcService.addArtist(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Name must not be blank",
                ex.getStatus().getDescription());
    }

    @Test
    void createArtistShouldReturnInvalidArgumentIfBiographyIsBlank() {
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setName(ARTIST_NAME)
                                .setBio("")
                                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                )
                .build();

        artistGrpcService.addArtist(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Biography must not be blank",
                ex.getStatus().getDescription());
    }

    @Test
    void createArtistShouldReturnInvalidArgumentIfPhotoIsBlank() {
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setName(ARTIST_NAME)
                                .setBio(ARTIST_BIO)
                                .setPhoto("")
                )
                .build();

        artistGrpcService.addArtist(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Photo must not be blank",
                ex.getStatus().getDescription());
    }

    @Test
    void deleteArtistShouldReturnNotFoundIfUserNotFound() {
        final UUID notExistingArtist = UUID.randomUUID();
        final ArtistRequest request = ArtistRequest.newBuilder()
                .setArtist(
                        Artist.newBuilder()
                                .setId(notExistingArtist.toString())
                                .setName(ARTIST_NAME)
                                .setBio(ARTIST_BIO)
                                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                )
                .build();

        when(artistRepository.findById(eq(notExistingArtist)))
                .thenReturn(Optional.empty());

        StreamRecorder<Empty> emptyResponseObserver = StreamRecorder.create();
        artistGrpcService.deleteArtist(request, emptyResponseObserver);

        assertInstanceOf(StatusRuntimeException.class, emptyResponseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) emptyResponseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Artist with id '%s' not found", notExistingArtist),
                ex.getStatus().getDescription());
    }
}
