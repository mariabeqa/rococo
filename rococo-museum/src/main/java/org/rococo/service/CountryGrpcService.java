package org.rococo.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.data.CountryEntity;
import org.rococo.data.repository.CountryRepository;
import org.rococo.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

import static org.rococo.data.CountryEntity.toGrpc;

@GrpcService
public class CountryGrpcService extends RococoCountriesServiceGrpc.RococoCountriesServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryGrpcService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAll(CountriesPageRequest request, StreamObserver<CountriesPageResponse> responseObserver) {
        try {
            Page<CountryEntity> countries = (request.getTitle().isEmpty())
                    ? countryRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                    : countryRepository.findAllByNameContainsIgnoreCase(
                    request.getTitle(), PageRequest.of(request.getPage(), request.getSize())
            );
            Page<Country> countryPages = countries.map(CountryEntity::toGrpc);

            responseObserver.onNext(
                    CountriesPageResponse
                            .newBuilder()
                            .addAllCountries(countryPages)
                            .setTotalElements(countryPages.getTotalElements())
                            .setTotalPages(countryPages.getTotalPages())
                            .setFirst(countryPages.isFirst())
                            .setLast(countryPages.isLast())
                            .setSize(countryPages.getSize())
                            .build());
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
    public void findCountryById(CountryByIdRequest request, StreamObserver<CountryResponse> responseObserver) {
        try {
            Optional<CountryEntity> byId = countryRepository.findById(UUID.fromString(request.getId()));

            if (byId.isEmpty()) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription(String.format("Country with id '%s' not found", request.getId()))
                                .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(
                    CountryResponse.newBuilder()
                            .setCountry(toGrpc(byId.get()))
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
    public void findCountryByName(CountryByNameRequest request, StreamObserver<CountryResponse> responseObserver) {
        try {
            Optional<CountryEntity> byName = countryRepository.findByName(request.getName());

            if (byName.isEmpty()) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription(String.format("Country with name '%s' not found", request.getName()))
                                .asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(
                    CountryResponse.newBuilder()
                            .setCountry(CountryEntity.toGrpc(byName.get()))
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

}
