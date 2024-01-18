package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.AssetDetails;
import com.entiros.starlify.muleconnector.api.dto.UserProfile;
import com.entiros.starlify.muleconnector.api.service.MuleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MuleServiceImpl implements MuleService {

  private final RestTemplate restTemplate;

  @Value("${mulesoft.server.url}")
  private String apiServer;

  @Override
  public UserProfile getProfile(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    ResponseEntity<UserProfile> response =
        restTemplate.exchange(
            apiServer + "/accounts/api/me",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<>() {});
    UserProfile userProfile = response.getBody();
    userProfile.setAssetList(getAssets(accessToken, userProfile.getUser().organizationId));
    for (Asset asset : userProfile.getAssetList()) {
      asset.setAssetDetails(
          getAssetDetais(accessToken, asset.getGroupId(), asset.getAssetId(), asset.getVersion()));
    }
    return userProfile;
  }

  @Override
  public List<Asset> getAssets(String accessToken, String orgId) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    ResponseEntity<List<Asset>> response =
        restTemplate.exchange(
            apiServer + "/exchange/api/v2/assets/search?masterOrganizationId={orgId}",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<>() {},
            orgId);
    return response.getBody();
  }

  @Override
  public AssetDetails getAssetDetais(
      String accessToken, String groupId, String assetId, String version) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    ResponseEntity<AssetDetails> response =
        restTemplate.exchange(
            apiServer + "/exchange/api/v2/assets/{groupId}/{assetId}/{version}/asset",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<>() {},
            groupId,
            assetId,
            version);
    return response.getBody();
  }

  @Override
  public String getRaml(
      String accessToken, String versionGroup, String groupId, String assetId, String version) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    ResponseEntity<String> response =
        restTemplate.exchange(
            apiServer
                + "/exchange/api/{versionGroup}/assets/{groupId}/{assetId}/{version}/api/root",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            new ParameterizedTypeReference<>() {},
            versionGroup,
            groupId,
            assetId,
            version);
    return response.getBody();
  }
}
