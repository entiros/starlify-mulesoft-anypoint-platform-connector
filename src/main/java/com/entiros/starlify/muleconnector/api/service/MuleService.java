package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.AssetDetails;
import com.entiros.starlify.muleconnector.api.dto.UserProfile;
import java.util.List;

public interface MuleService {
  UserProfile getProfile(String accessToken);

  List<Asset> getAssets(String accessToken, String orgId);

  AssetDetails getAssetDetais(
      String accessToken, String groupId, String assetId, String version);

  String getRaml(
      String accessToken, String versionGroup, String groupId, String assetId, String version);
}
