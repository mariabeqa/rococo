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
import org.rococo.data.PaintingEntity;
import org.rococo.data.repository.PaintingRepository;
import org.rococo.grpc.*;
import org.rococo.service.api.ArtistGrpcClient;
import org.rococo.service.api.MuseumGrpcClient;
import org.rococo.utils.ImageUtil;
import org.rococo.utils.grpc.ArtistBuilder;
import org.rococo.utils.grpc.MuseumBuilder;
import org.rococo.utils.grpc.PaintingBuilder;
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
import static org.rococo.utils.grpc.ArtistBuilder.ARTIST_ID;
import static org.rococo.utils.grpc.MuseumBuilder.MUSEUM_ID;

@ExtendWith(MockitoExtension.class)
public class GrpcPaintingServiceTest {

    private static final String IMAGE_PATH = "img/painting/raduga.jpg";
    private static final String PAINTING_TITLE = "Радуга";
    private static final String PAINTING_DESCRIPTION = "Картина «Радуга» считается одним из шедевров позднего периода творчества Куинджи";

    //1st entity
    private PaintingEntity firstPainting;
    private final UUID firstPaintingId = UUID.randomUUID();
    //2d entity
    private PaintingEntity secondPainting;
    private final UUID secondPaintingId = UUID.randomUUID();
    private final String secondPaintingTitle = "Эрмитаж";
    private final String secondPaintingDescription = "Российский государственный художественный и культурно-исторический музей в Санкт-Петербурге";

    @Mock
    private PaintingRepository paintingRepository;
    @Mock
    private MuseumGrpcClient museumGrpcClient;
    @Mock
    private ArtistGrpcClient artistGrpcClient;

    private PaintingGrpcService paintingGrpcService;
    private StreamRecorder<PaintingResponse> responseObserver;
    private final ArtistBuilder artistBuilder = new ArtistBuilder();
    private final MuseumBuilder museumBuilder = new MuseumBuilder();
    private final PaintingBuilder paintingBuilder = new PaintingBuilder();

    @BeforeEach
    void setUp() {
        firstPainting = new PaintingEntity();
        firstPainting.setId(firstPaintingId);
        firstPainting.setTitle(PAINTING_TITLE);
        firstPainting.setDescription(PAINTING_DESCRIPTION);
        firstPainting.setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH).getBytes());
        firstPainting.setArtistId(ARTIST_ID);
        firstPainting.setMuseumId(MUSEUM_ID);

        secondPainting = new PaintingEntity();
        secondPainting.setId(secondPaintingId);
        secondPainting.setTitle(secondPaintingTitle);
        secondPainting.setDescription(secondPaintingDescription);
        secondPainting.setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH).getBytes());
        secondPainting.setArtistId(ARTIST_ID);
        secondPainting.setMuseumId(MUSEUM_ID);

        paintingGrpcService = new PaintingGrpcService(
            paintingRepository,
            museumGrpcClient,
            artistGrpcClient
        );
        responseObserver = StreamRecorder.create();
    }

    @Test
    void getAllShouldReturnCorrectPaintingsList() throws ExecutionException, InterruptedException, TimeoutException {
        PaintingsPageRequest request = PaintingsPageRequest.newBuilder()
            .setTitle("")
            .setPage(0)
            .setSize(10)
            .build();

        when(paintingRepository.findAll(PageRequest.of(request.getPage(), request.getSize())))
            .thenReturn(new PageImpl<>(List.of(firstPainting, secondPainting), PageRequest.of(request.getPage(), request.getSize()), 2));

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                        .setMuseum(museumBuilder.withDefaults().build())
                        .build()
            ));

        StreamRecorder<PaintingsPageResponse> pageResponseObserver = StreamRecorder.create();

        paintingGrpcService.getAll(request, pageResponseObserver);
        List<Painting> paintings = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getPaintingsList();

        assertEquals(2, paintings.size());
        assertEquals(firstPaintingId.toString(), paintings.getFirst().getId());
        assertEquals(PAINTING_TITLE, paintings.getFirst().getTitle());
        assertEquals(PAINTING_DESCRIPTION, paintings.getFirst().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            paintings.getFirst().getContent()
        );
        assertEquals(secondPaintingId.toString(), paintings.getLast().getId());
        assertEquals(secondPaintingTitle, paintings.getLast().getTitle());
        assertEquals(secondPaintingDescription, paintings.getLast().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            paintings.getFirst().getContent()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void getAllShouldReturnCorrectPaintingIfTitleIsPassed() throws ExecutionException, InterruptedException, TimeoutException {
        PaintingsPageRequest request = PaintingsPageRequest.newBuilder()
            .setTitle("Ра")
            .setPage(0)
            .setSize(10)
            .build();

        when(paintingRepository.findAllByTitleContainsIgnoreCase(any(String.class), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(firstPainting), PageRequest.of(request.getPage(), request.getSize()), 1));

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                    .setMuseum(museumBuilder.withDefaults().build())
                    .build()
            ));

        StreamRecorder<PaintingsPageResponse> pageResponseObserver = StreamRecorder.create();

        paintingGrpcService.getAll(request, pageResponseObserver);
        List<Painting> paintings = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getPaintingsList();

        assertEquals(1, paintings.size());
        assertEquals(firstPaintingId.toString(), paintings.getFirst().getId());
        assertEquals(PAINTING_TITLE, paintings.getFirst().getTitle());
        assertEquals(PAINTING_DESCRIPTION, paintings.getFirst().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            paintings.getFirst().getContent()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void findPaintingByIdShouldReturnCorrectPainting() {
        final PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
            .setPaintingId(firstPaintingId.toString())
            .build();

        when(paintingRepository.findById(eq(firstPaintingId)))
            .thenReturn(Optional.of(firstPainting));

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                    .setMuseum(museumBuilder.withDefaults().build())
                    .build()
            ));

        paintingGrpcService.findPaintingById(request, responseObserver);

        Painting painting = responseObserver.getValues().getFirst().getPainting();
        assertEquals(firstPaintingId.toString(), painting.getId());
        assertEquals(PAINTING_TITLE, painting.getTitle());
        assertEquals(PAINTING_DESCRIPTION, painting.getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            painting.getContent()
        );
        assertNull(responseObserver.getError());
    }

    @Test
    void findPaintingByIAuthorIdShouldReturnCorrectPainting() {
        final PaintingByAuthorIdPageRequest request = PaintingByAuthorIdPageRequest.newBuilder()
            .setAuthorId(ARTIST_ID.toString())
            .setPage(0)
            .setSize(10)
            .build();

        when(paintingRepository.findAllByArtistId(
            ARTIST_ID, PageRequest.of(request.getPage(), request.getSize())))
            .thenReturn(new PageImpl<>(List.of(firstPainting), PageRequest.of(request.getPage(), request.getSize()), 2));

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                    .setMuseum(museumBuilder.withDefaults().build())
                    .build()
            ));

        StreamRecorder<PaintingsPageResponse> pageResponseObserver = StreamRecorder.create();

        paintingGrpcService.findPaintingByAuthorId(request, pageResponseObserver);

        List<PaintingsPageResponse> response = pageResponseObserver.getValues();
        List<Painting> paintings = response.getFirst().getPaintingsList();
        assertEquals(1, paintings.size());
        assertEquals(firstPaintingId.toString(),paintings.getFirst().getId());
        assertEquals(PAINTING_TITLE, paintings.getFirst().getTitle());
        assertEquals(PAINTING_DESCRIPTION, paintings.getFirst().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            paintings.getFirst().getContent()
        );
        assertNull(responseObserver.getError());
    }

    @Test
    void findPaintingByIAuthorIdShouldReturnInvalidArgumentIfAuthorIdIsEmpty() {
        final PaintingByAuthorIdPageRequest request = PaintingByAuthorIdPageRequest.newBuilder()
            .setAuthorId("")
            .setPage(0)
            .setSize(10)
            .build();

        StreamRecorder<PaintingsPageResponse> pageResponseObserver = StreamRecorder.create();

        paintingGrpcService.findPaintingByAuthorId(request, pageResponseObserver);

        assertInstanceOf(StatusRuntimeException.class, pageResponseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) pageResponseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Author id must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void findPaintingByIdShouldReturnNotFoundIfPaintingIsNotFound() {
        final UUID notExistingPainting = UUID.randomUUID();
        final PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
            .setPaintingId(notExistingPainting.toString())
            .build();

        when(paintingRepository.findById(eq(notExistingPainting)))
            .thenReturn(Optional.empty());

        paintingGrpcService.findPaintingById(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Painting with id '%s' not found", request.getPaintingId()),
            ex.getStatus().getDescription());
    }

    @Test
    void findPaintingByAuthorIdShouldReturnNotFoundIfPaintingIsNotFound() {
        final UUID notExistingAuthor = UUID.randomUUID();

        final PaintingByAuthorIdPageRequest request = PaintingByAuthorIdPageRequest.newBuilder()
            .setAuthorId(notExistingAuthor.toString())
            .setPage(0)
            .setSize(10)
            .build();

        when(artistGrpcClient.findArtistById(eq(notExistingAuthor.toString())))
            .thenReturn(Optional.empty());

        StreamRecorder<PaintingsPageResponse> pageResponseObserver = StreamRecorder.create();

        paintingGrpcService.findPaintingByAuthorId(request, pageResponseObserver);

        assertInstanceOf(StatusRuntimeException.class, pageResponseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) pageResponseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Artist with id '%s' not found", request.getAuthorId()),
            ex.getStatus().getDescription());
    }

    @Test
    void updatePaintingShouldReturnUpdatedPainting() {
        final String newTitle = PAINTING_TITLE + " edited";
        final String newDescription = PAINTING_DESCRIPTION + " edited";
        final String newPath = "img/painting/dnepr.jpg";
        final PaintingRequest request = PaintingRequest.newBuilder()
            .setPainting(
                paintingBuilder
                    .withId(firstPaintingId.toString())
                    .withTitle(newTitle)
                    .withDescription(newDescription)
                    .withContent(newPath)
                    .withMuseum(museumBuilder.withDefaults().build())
                    .withArtist(artistBuilder.withDefaults().build())
                    .build()
            )
                .build();

        when(paintingRepository.findById(eq(firstPaintingId)))
            .thenReturn(Optional.of(firstPainting));

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                    .setMuseum(museumBuilder.withDefaults().build())
                    .build()
            ));

        when(paintingRepository.save(any(PaintingEntity.class)))
            .thenAnswer(answer -> answer.getArguments()[0]);

        paintingGrpcService.updatePainting(request, responseObserver);

        Painting painting = responseObserver.getValues().getFirst().getPainting();
        assertEquals(firstPaintingId.toString(), painting.getId());
        assertEquals(newTitle, painting.getTitle());
        assertEquals(newDescription, painting.getDescription());
        assertEquals(ImageUtil.getEncodedImageFromClasspath(newPath), painting.getContent());
        assertNull(responseObserver.getError());

        verify(paintingRepository, times(1)).save(any(PaintingEntity.class));
        verify(paintingRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void updatePaintingShouldReturnNotFoundIfPaintingNotFound() {
        final UUID notExistingPainting = UUID.randomUUID();
        final PaintingRequest request = PaintingRequest.newBuilder()
            .setPainting(
                paintingBuilder
                    .withDefaults()
                    .withId(notExistingPainting.toString())
                    .build()
            )
            .build();

        when(paintingRepository.findById(eq(notExistingPainting)))
            .thenReturn(Optional.empty());

        paintingGrpcService.updatePainting(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Painting with id '%s' not found", notExistingPainting),
            ex.getStatus().getDescription());
    }

    @Test
    void addPaintingShouldReturnCreatedPainting() {
        final AddPaintingRequest request = AddPaintingRequest.newBuilder()
            .setTitle(PAINTING_TITLE)
            .setDescription(PAINTING_DESCRIPTION)
            .setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
            .setMuseumId(MuseumId.newBuilder().setId(MUSEUM_ID.toString()).build())
            .setArtistId(ArtistId.newBuilder().setId(ARTIST_ID.toString()).build())
            .build();

        when(artistGrpcClient.findArtistById(eq(ARTIST_ID.toString())))
            .thenAnswer(invocationOnMock -> Optional.of(
                ArtistResponse.newBuilder()
                    .setArtist(artistBuilder.withDefaults().build())
                    .build()
            ));

        when(museumGrpcClient.findMuseumById(eq(MUSEUM_ID.toString())))
            .thenAnswer(invocation -> Optional.of(
                MuseumResponse.newBuilder()
                    .setMuseum(museumBuilder.withDefaults().build())
                    .build()
            ));

        when(paintingRepository.save(any(PaintingEntity.class)))
            .thenReturn(firstPainting);

        paintingGrpcService.addPainting(request, responseObserver);

        Painting painting = responseObserver.getValues().getFirst().getPainting();
        assertEquals(PAINTING_TITLE, painting.getTitle());
        assertEquals(PAINTING_DESCRIPTION,painting.getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            painting.getContent()
        );
        assertEquals(MUSEUM_ID.toString(),painting.getMuseum().getId());
        assertEquals(ARTIST_ID.toString(),painting.getArtist().getId());
        assertNull(responseObserver.getError());

        verify(paintingRepository, times(1)).save(any(PaintingEntity.class));
    }

    @Test
    void createPaintingShouldReturnInvalidArgumentIfTitleIsBlank() {
        final AddPaintingRequest request = AddPaintingRequest.newBuilder()
            .setTitle("")
            .setDescription(PAINTING_DESCRIPTION)
            .setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
            .setMuseumId(MuseumId.newBuilder().setId(MUSEUM_ID.toString()).build())
            .setArtistId(ArtistId.newBuilder().setId(ARTIST_ID.toString()).build())
            .build();

        paintingGrpcService.addPainting(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Title must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void createPaintingShouldReturnInvalidArgumentIfDescriptionIsBlank() {
        final AddPaintingRequest request = AddPaintingRequest.newBuilder()
            .setTitle(PAINTING_TITLE)
            .setDescription("")
            .setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
            .setMuseumId(MuseumId.newBuilder().setId(MUSEUM_ID.toString()).build())
            .setArtistId(ArtistId.newBuilder().setId(ARTIST_ID.toString()).build())
            .build();

        paintingGrpcService.addPainting(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Description must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void createPaintingShouldReturnInvalidArgumentIfContentIsBlank() {
        final AddPaintingRequest request = AddPaintingRequest.newBuilder()
            .setTitle(PAINTING_TITLE)
            .setDescription(PAINTING_DESCRIPTION)
            .setContent("")
            .setMuseumId(MuseumId.newBuilder().setId(MUSEUM_ID.toString()).build())
            .setArtistId(ArtistId.newBuilder().setId(ARTIST_ID.toString()).build())
            .build();

        paintingGrpcService.addPainting(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Content must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void deletePaintingShouldReturnNotFoundIfPaintingNotFound() {
        final UUID notExistingPainting = UUID.randomUUID();
        final DeletePaintingRequest request = DeletePaintingRequest.newBuilder()
            .setId(notExistingPainting.toString())
            .setTitle(PAINTING_TITLE)
            .setDescription(PAINTING_DESCRIPTION)
            .setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
            .setMuseumId(MuseumId.newBuilder().setId(MUSEUM_ID.toString()).build())
            .setArtistId(ArtistId.newBuilder().setId(ARTIST_ID.toString()).build())
            .build();

        when(paintingRepository.findById(eq(notExistingPainting)))
            .thenReturn(Optional.empty());

        StreamRecorder<Empty> emptyResponseObserver = StreamRecorder.create();
        paintingGrpcService.deletePainting(request, emptyResponseObserver);

        assertInstanceOf(StatusRuntimeException.class, emptyResponseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) emptyResponseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Painting with id '%s' not found", notExistingPainting),
            ex.getStatus().getDescription());
    }
}
