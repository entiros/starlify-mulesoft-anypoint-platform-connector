package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.Account;
import com.entiros.starlify.muleconnector.api.dto.Asset;
import com.entiros.starlify.muleconnector.api.dto.AssetDetails;
import com.entiros.starlify.muleconnector.api.dto.UserProfile;
import com.entiros.starlify.muleconnector.api.service.CloudhubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.json.simple.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudhubServiceImpl implements CloudhubService {

    @Value("${mulesoft.server.url}")
    private String apiServer;


    private WebClient anypointClient;

    @PostConstruct
    public void setup(){
        anypointClient = WebClient.builder().baseUrl(apiServer).build();
    }
    @Override
    public Account getAccount(String accessToken) {
        // cloudhub/api/account # gives enviroment  even defaultEnvironment and organizationsID
         Account account = anypointClient
                 .get()
                 .uri(uriBuilder ->
                 uriBuilder.path("/cloudhub/api/account").build())
                 .header("Authorization", "bearer "+ accessToken)
                 .retrieve().bodyToMono(Account.class).block();
        return account;
    }


    @Override
    public List<Asset> getAssets(String accessToken, String orgId) {

        return null;
    }

    @Override
    public AssetDetails getAssetDetails(String accessToken, String groupId, String assetId, String version) {
        return null;
    }

    @Override
    public String getRaml(String accessToken, String versionGroup, String groupId, String assetId, String version) {
        return null;
    }

    @Override
    public String getApplications(String accessToken, String organisationId, String environmentID) {
        JsonObject requestJson =new JsonObject();
        Map<String, List<String>> organisation=new ConcurrentHashMap<>();
        List<String> enviromentList =new ArrayList<>();
        enviromentList.add(environmentID);
        organisation.put(organisationId, enviromentList);
        requestJson.put("selectedEnvIdsByOrgId", organisation);
        //https://anypoint.mulesoft.com/visualizer/api/v4/organizations/{orgid}/applications-info
        //TODO send back better error message
        // POST app name to starlify as system
        log.info(requestJson.toString());
        String test =anypointClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("visualizer/api/v4/organizations/{orgid}/applications-info")
                        .build(organisationId))
                .header("Authorization", "bearer "+ accessToken)
                .header("Content-Type","application/json")
                .body(BodyInserters.fromValue(requestJson))
                .retrieve().bodyToMono(String.class).block();
        log.info(test);
        return test;
    }
}
