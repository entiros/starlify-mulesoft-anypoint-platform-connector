package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.Account;
import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.AssetDetails;


import java.util.List;

public interface CloudhubService {
    public Account getAccount(String accessToken);

    public List<Asset> getAssets(String accessToken, String orgId);

    public AssetDetails getAssetDetails(String accessToken, String groupId, String assetId, String version);

    public String getRaml(String accessToken, String versionGroup,String groupId, String assetId, String version);

    public String getApplications(String accessToken,String organisationId, String environmentID);
}
