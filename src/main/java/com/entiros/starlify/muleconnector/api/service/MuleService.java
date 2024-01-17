package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.AssetDetails;
import com.entiros.starlify.muleconnector.api.dto.UserProfile;
import java.util.List;

public interface MuleService {
  public UserProfile getProfile(String accessToken);

  public List<Asset> getAssets(String accessToken, String orgId);

  public AssetDetails getAssetDetais(
      String accessToken, String groupId, String assetId, String version);

  public String getRaml(
      String accessToken, String versionGroup, String groupId, String assetId, String version);
}
