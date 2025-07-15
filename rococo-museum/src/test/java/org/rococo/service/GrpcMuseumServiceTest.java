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
import org.rococo.data.CountryEntity;
import org.rococo.data.MuseumEntity;
import org.rococo.data.repository.CountryRepository;
import org.rococo.data.repository.MuseumRepository;
import org.rococo.grpc.*;
import org.rococo.utils.ImageUtil;
import org.rococo.utils.grpc.MuseumBuilder;
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
import static org.rococo.utils.grpc.MuseumBuilder.*;

@ExtendWith(MockitoExtension.class)
public class GrpcMuseumServiceTest {
    //1st entity
    private MuseumEntity firstMuseum;
    private final String firstMuseumId = UUID.randomUUID().toString();
    private CountryEntity country;
    //2d entity
    private MuseumEntity secondMuseum;
    private final String secondMuseumId = UUID.randomUUID().toString();
    private final String secondMuseumTitle = "Эрмитаж";
    private final String secondMuseumDescription = "Российский государственный художественный и культурно-исторический музей в Санкт-Петербурге";

    @Mock
    private MuseumRepository museumRepository;
    @Mock
    private CountryRepository countryRepository;

    private final MuseumBuilder museumBuilder = new MuseumBuilder();
    private MuseumGrpcService museumGrpcService;
    private StreamRecorder<MuseumResponse> responseObserver;

    @BeforeEach
    void setUp() {
        firstMuseum = new MuseumEntity();
        firstMuseum.setId(UUID.fromString(firstMuseumId));
        firstMuseum.setTitle(MUSEUM_TITLE);
        firstMuseum.setDescription(MUSEUM_DESCRIPTION);
        firstMuseum.setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH).getBytes());
        firstMuseum.setCity(CITY);
        country = new CountryEntity();
        country.setId(UUID.fromString(COUNTRY_ID));
        country.setName(COUNTRY_NAME);
        firstMuseum.setCountry(country);

        secondMuseum = new MuseumEntity();
        secondMuseum.setId(UUID.fromString(secondMuseumId));
        secondMuseum.setTitle(secondMuseumTitle);
        secondMuseum.setDescription(secondMuseumDescription);
        secondMuseum.setPhoto("".getBytes());
        secondMuseum.setCity(CITY);
        secondMuseum.setCountry(country);

        museumGrpcService = new MuseumGrpcService(museumRepository, countryRepository);
        responseObserver = StreamRecorder.create();
    }

    @Test
    void getAllShouldReturnCorrectMuseumList() throws ExecutionException, InterruptedException, TimeoutException {
        MuseumsPageRequest request = MuseumsPageRequest.newBuilder()
            .setTitle("")
            .setPage(0)
            .setSize(10)
            .build();

        when(museumRepository.findAll(PageRequest.of(request.getPage(), request.getSize())))
            .thenReturn(new PageImpl<>(List.of(firstMuseum, secondMuseum), PageRequest.of(request.getPage(), request.getSize()), 2));

        StreamRecorder<MuseumsPageResponse> pageResponseObserver = StreamRecorder.create();

        museumGrpcService.getAll(request, pageResponseObserver);
        List<Museum> museums = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getMuseumsList();

        assertEquals(2, museums.size());
        assertEquals(firstMuseumId, museums.getFirst().getId());
        assertEquals(MUSEUM_TITLE, museums.getFirst().getTitle());
        assertEquals(MUSEUM_DESCRIPTION, museums.getFirst().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            museums.getFirst().getPhoto()
        );
        assertEquals(secondMuseumId, museums.getLast().getId());
        assertEquals(secondMuseumTitle, museums.getLast().getTitle());
        assertEquals(secondMuseumDescription, museums.getLast().getDescription());
        assertEquals(
            "",
            museums.getLast().getPhoto()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void getAllShouldReturnCorrectMuseumIfTitleIsPassed() throws ExecutionException, InterruptedException, TimeoutException {
        MuseumsPageRequest request = MuseumsPageRequest.newBuilder()
            .setTitle("Ру")
            .setPage(0)
            .setSize(10)
            .build();

        when(museumRepository.findAllByTitleContainsIgnoreCase(any(String.class), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(firstMuseum), PageRequest.of(request.getPage(), request.getSize()), 1));

        StreamRecorder<MuseumsPageResponse> pageResponseObserver = StreamRecorder.create();

        museumGrpcService.getAll(request, pageResponseObserver);
        List<Museum> museums = pageResponseObserver.firstValue().get(1, TimeUnit.SECONDS).getMuseumsList();

        assertEquals(1, museums.size());
        assertEquals(firstMuseumId, museums.getFirst().getId());
        assertEquals(MUSEUM_TITLE, museums.getFirst().getTitle());
        assertEquals(MUSEUM_DESCRIPTION, museums.getFirst().getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            museums.getFirst().getPhoto()
        );
        assertNull(pageResponseObserver.getError());
    }

    @Test
    void findMuseumByIdShouldReturnCorrectMuseum() {
        final MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
            .setMuseumId(firstMuseumId)
            .build();

        when(museumRepository.findById(eq(UUID.fromString(firstMuseumId))))
            .thenReturn(Optional.of(firstMuseum));

        museumGrpcService.findMuseumById(request, responseObserver);

        Museum museum = responseObserver.getValues().getFirst().getMuseum();
        assertEquals(firstMuseumId, museum.getId());
        assertEquals(MUSEUM_TITLE, museum.getTitle());
        assertEquals(MUSEUM_DESCRIPTION, museum.getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            museum.getPhoto()
        );
        assertNull(responseObserver.getError());
    }

    @Test
    void findMuseumByIdShouldReturnNotFoundIfMuseumNotFound() {
        final UUID notExistingMuseum = UUID.randomUUID();
        final MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
            .setMuseumId(notExistingMuseum.toString())
            .build();

        when(museumRepository.findById(eq(notExistingMuseum)))
            .thenReturn(Optional.empty());

        museumGrpcService.findMuseumById(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Museum with id '%s' not found", request.getMuseumId()),
            ex.getStatus().getDescription());
    }

    @Test
    void updateMuseumShouldReturnUpdatedMuseum() {
        final String newMuseumName = "Третьяковская галерея";
        final String newMuseumDescription = "Российский федеральный государственный художественный музей в Москве";
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder
                        .withId(firstMuseumId)
                        .withTitle(newMuseumName)
                        .withDescription(newMuseumDescription)
                        .withPhoto(IMAGE_PATH)
                        .withDefaultGeo()
                        .build()
            )
            .build();

        when(museumRepository.findById(eq(UUID.fromString(firstMuseumId))))
            .thenReturn(Optional.of(firstMuseum));

        when(countryRepository.findById(eq(UUID.fromString(COUNTRY_ID))))
            .thenReturn(Optional.of(country));

        when(museumRepository.save(any(MuseumEntity.class)))
            .thenAnswer(answer -> answer.getArguments()[0]);

        museumGrpcService.updateMuseum(request, responseObserver);

        Museum museum = responseObserver.getValues().getFirst().getMuseum();
        assertEquals(firstMuseumId, museum.getId());
        assertEquals(newMuseumName, museum.getTitle());
        assertEquals(newMuseumDescription, museum.getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            museum.getPhoto()
        );
        assertNull(responseObserver.getError());

        verify(museumRepository, times(1)).save(any(MuseumEntity.class));
        verify(countryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void updateMuseumShouldReturnNotFoundIfMuseumNotFound() {
        final UUID notExistingMuseum = UUID.randomUUID();
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder
                    .withDefaults()
                    .withId(notExistingMuseum.toString())
                    .build()
            )
            .build();

        when(museumRepository.findById(eq(notExistingMuseum)))
            .thenReturn(Optional.empty());

        museumGrpcService.updateMuseum(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Museum with id '%s' not found", notExistingMuseum),
            ex.getStatus().getDescription());
    }

    @Test
    void createMuseumShouldReturnCreatedMuseum() {
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder.withDefaults()
                    .withId("")
                    .build()
            )
            .build();

        when(countryRepository.findById(eq(UUID.fromString(COUNTRY_ID))))
            .thenReturn(Optional.of(country));

        when(museumRepository.save(any(MuseumEntity.class)))
            .thenAnswer(invocation -> {
                MuseumEntity museum = invocation.getArgument(0);
                museum.setId(UUID.randomUUID());  // simulate DB-generated ID
                return museum;
            });

        museumGrpcService.addMuseum(request, responseObserver);

        Museum museum = responseObserver.getValues().getFirst().getMuseum();
        assertEquals(MUSEUM_TITLE, museum.getTitle());
        assertEquals(MUSEUM_DESCRIPTION, museum.getDescription());
        assertEquals(
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            museum.getPhoto()
        );
        assertNull(responseObserver.getError());

        verify(museumRepository, times(1)).save(any(MuseumEntity.class));
    }

    @Test
    void createMuseumShouldReturnInvalidArgumentIfTitleIsBlank() {
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder.withDefaults()
                    .withTitle("")
                    .build()
            )
            .build();

        museumGrpcService.addMuseum(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Title must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void createMuseumShouldReturnInvalidArgumentIfDescriptionIsBlank() {
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder.withDefaults()
                    .withDescription("")
                    .build()
            )
            .build();

        museumGrpcService.addMuseum(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Description must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void createArtistShouldReturnInvalidArgumentIfPhotoIsBlank() {
        final MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                museumBuilder.withDefaults()
                    .withoutPhoto()
                    .build()
            )
            .build();

        museumGrpcService.addMuseum(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Photo must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void deleteMuseumShouldReturnNotFoundIfMuseumNotFound() {
        final UUID notExistingMuseum = UUID.randomUUID();
        final MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
            .setMuseumId(notExistingMuseum.toString())
            .build();


        when(museumRepository.findById(eq(notExistingMuseum)))
            .thenReturn(Optional.empty());

        StreamRecorder<Empty> emptyResponseObserver = StreamRecorder.create();
        museumGrpcService.deleteMuseum(request, emptyResponseObserver);

        assertInstanceOf(StatusRuntimeException.class, emptyResponseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) emptyResponseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("Museum with id '%s' not found", notExistingMuseum),
            ex.getStatus().getDescription());
    }

}
