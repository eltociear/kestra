package io.kestra.webserver.controllers.api;

import io.kestra.core.utils.VersionProvider;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.inject.Inject;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

@Validated
@Controller("/api/v1/docs")
public class DocController {
    @Inject
    @Client("api")
    private HttpClient httpClient;

    @Inject
    protected VersionProvider versionProvider;

    @ExecuteOn(TaskExecutors.IO)
    @Get(value = "{path:.*}", produces = MediaType.APPLICATION_OCTET_STREAM)
    @Operation(tags = {"docs"}, summary = "Get documentation under the given path")
    public StreamedFile index(
        @PathVariable String path,
        HttpRequest<?> httpRequest
    ) throws URISyntaxException {
        return forwardToKestraApi(httpRequest, "/v1/docs/" + path + "/versions/" + versionProvider.getVersion());
    }

    private StreamedFile forwardToKestraApi(HttpRequest<?> originalRequest, String newPath) {
        UriBuilder uriBuilder = UriBuilder.of(originalRequest.getUri())
            .replacePath(originalRequest.getUri().getPath().replaceAll("^[^?]*", newPath));

        byte[] kestraApiResponse = httpClient
            .toBlocking()
            .exchange(
                HttpRequest.create(
                    originalRequest.getMethod(),
                    uriBuilder
                        .build()
                        .toString()
                ),
                byte[].class
            )
            .body();

        if (kestraApiResponse == null) {
            return null;
        }

        return new StreamedFile(new ByteArrayInputStream(kestraApiResponse), MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }
}
