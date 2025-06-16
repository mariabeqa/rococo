package org.rococo.service;

import org.rococo.data.CountryEntity;
import org.rococo.data.repository.CountryRepository;
import org.rococo.exception.NotFoundException;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
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
    }

    @Override
    public void findCountryById(CountryByIdRequest request, StreamObserver<CountryResponse> responseObserver) {
        Optional<CountryEntity> byId = countryRepository.findById(UUID.fromString(request.getId()));

        if (byId.isPresent()) {
            responseObserver.onNext(
                    CountryResponse.newBuilder()
                            .setCountry(toGrpc(byId.get()))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            throw new NotFoundException("Country with id " + request.getId() + " not found");
        }
    }

    @Override
    public void findCountryByName(CountryByNameRequest request, StreamObserver<CountryResponse> responseObserver) {
        Optional<CountryEntity> byName = countryRepository.findByName(request.getName());

        if (byName.isPresent()) {
            responseObserver.onNext(
                    CountryResponse.newBuilder()
                            .setCountry(toGrpc(byName.get()))
                            .build()
            );
            responseObserver.onCompleted();
        } else {
            throw new NotFoundException("Country with id " + request.getName() + " not found");
        }
    }

}
