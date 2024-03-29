package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.MuleService;
import com.entiros.starlify.muleconnector.api.service.StarlifyService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StarlifyServiceImpl implements StarlifyService {
  private final RestTemplate restTemplate;

  private final MuleService muleService;

  @Value("${starlify.url}")
  private String starlifyServer;

  @Override
  public List<NetworkSystem> getSystems(Request request) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", request.getStarlifyKey());
    return restTemplate
        .exchange(
            starlifyServer + "/hypermedia/networks/{networkId}/systems?paged=false",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<List<NetworkSystem>>() {},
            request.getNetworkId())
        .getBody();
  }

  @Override
  public SystemRespDto addSystem(Request request, SystemDto systemDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", request.getStarlifyKey());
    return restTemplate
        .exchange(
            starlifyServer + "/hypermedia/networks/{networkId}/systems",
            HttpMethod.POST,
            new HttpEntity<>(systemDto, headers),
            new ParameterizedTypeReference<SystemRespDto>() {},
            request.getNetworkId())
        .getBody();
  }

  @Override
  public String addService(Request request, ServiceDto serviceDto, String systemId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", request.getStarlifyKey());
    return restTemplate
        .exchange(
            starlifyServer + "/hypermedia/systems/{systemId}/services",
            HttpMethod.POST,
            new HttpEntity<>(serviceDto, headers),
            new ParameterizedTypeReference<String>() {},
            systemId)
        .getBody();
  }

  @Override
  public Response<ServiceRespDto> getServices(Request request, String systemId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", request.getStarlifyKey());

    return restTemplate
        .exchange(
            starlifyServer + "/hypermedia/systems/{systemId}/services",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<Response<ServiceRespDto>>() {},
            systemId)
        .getBody();
  }

  @Override
  public String addServices(Request request, String systemId, Asset asset) {
    String c =
        muleService.getRaml(
            request.getApiKey(),
            asset.getVersionGroup(),
            asset.getGroupId(),
            asset.getAssetId(),
            asset.getVersion());
    String fileName = asset.getAssetId() + ".raml";
    MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
    ContentDisposition contentDisposition =
        ContentDisposition.builder("form-data").name("file").filename(fileName).build();
    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
    HttpEntity<byte[]> fileEntity = new HttpEntity<>(c.getBytes(), fileMap);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.add("X-API-KEY", request.getStarlifyKey());

    return restTemplate
        .exchange(
            starlifyServer + "/hypermedia/systems/{systemId}/services",
            HttpMethod.PUT,
            new HttpEntity<>(body, headers),
            new ParameterizedTypeReference<String>() {},
            systemId)
        .getBody();
  }

  private void addRef(Request request, String systemId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-KEY", request.getStarlifyKey());

    Response<com.entiros.starlify.muleconnector.api.dto.Service> body =
        restTemplate
            .exchange(
                starlifyServer + "/hypermedia/systems/{systemId}/services",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<
                    Response<com.entiros.starlify.muleconnector.api.dto.Service>>() {},
                systemId)
            .getBody();
    List<com.entiros.starlify.muleconnector.api.dto.Service> content = body.getContent();
    for (com.entiros.starlify.muleconnector.api.dto.Service c : content) {
      RefDto dt = new RefDto();
      dt.setName("Test ref");
      ServiceDto sdto = new ServiceDto();
      sdto.setId(c.getId());
      dt.setService(sdto);
      String body1 =
          restTemplate
              .exchange(
                  starlifyServer + "/hypermedia/systems/{systemId}/references",
                  HttpMethod.POST,
                  new HttpEntity<>(dt, headers),
                  new ParameterizedTypeReference<String>() {},
                  systemId)
              .getBody();
      System.out.println(body1);
    }
  }
}
