package org.rococo.service.api;

import org.rococo.grpc.*;
import org.rococo.model.CountryJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.rococo.model.CountryJson.fromGrpc;

@Component
public class CountryGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(CountryGrpcClient.class);

    @GrpcClient("grpcCountriesClient")
    private RococoCountriesServiceGrpc.RococoCountriesServiceBlockingStub countriesClient;

    public @Nonnull Page<CountryJson> getAll(@Nullable String title,
                                             @Nonnull Pageable pageable) {
        try {
            CountriesPageRequest request = CountriesPageRequest.newBuilder()
                            .setTitle(title != null ? title : "")
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build();

            CountriesPageResponse response = countriesClient.getAll(request);
            List<CountryJson> countries = response.getCountriesList()
                    .stream()
                    .map(CountryJson::fromGrpc)
                    .toList();

            return new PageImpl<>(countries, PageRequest.of(request.getPage(), request.getSize()), response.getTotalElements());

        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull CountryJson findCountryById(@Nonnull String id) {
        try {
            CountryByIdRequest request = CountryByIdRequest.newBuilder()
                    .setId(id)
                    .build();
            return fromGrpc(countriesClient.findCountryById(request).getCountry());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull CountryJson findCountryByName(@Nonnull String name) {
        try {
            CountryByNameRequest request = CountryByNameRequest.newBuilder()
                    .setName(name)
                    .build();
            return fromGrpc(countriesClient.findCountryByName(request).getCountry());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
