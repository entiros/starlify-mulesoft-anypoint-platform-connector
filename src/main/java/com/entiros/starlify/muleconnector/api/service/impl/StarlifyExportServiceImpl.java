package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.CloudhubService;
import com.entiros.starlify.muleconnector.api.service.ExchangeService;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import com.entiros.starlify.muleconnector.api.service.StarlifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarlifyExportServiceImpl implements StarlifyExportService {

    private final ExchangeService exchangeService;
    private final StarlifyService starlifyService;
    private final CloudhubService cloudhubService;

    private Map<String, Map<String, NetworkSystem>> cachedNetworkSystems = new ConcurrentHashMap<>();
    private Map<String, RequestItem> statusMap = new ConcurrentHashMap<>();
    private Map<String, CloudhubRequestItem> cloudStatusMap = new ConcurrentHashMap<>();


    private void processRequest(Request request) {
        ((RequestItem) request).setStatus(RequestItem.Status.IN_PROCESS);
        UserProfile userProfile = exchangeService.getProfile(request.getApiKey());
        List<Asset> assetList = userProfile.getAssetList();

        List<NetworkSystem> systems = starlifyService.getSystems(request);

        this.populateSystems(request, systems);

        Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
        for (Asset a : assetList) {
            try {
                log.info("Started asset:" + a.getName() + " id:" + a.getAssetId());
                NetworkSystem networkSystem = existingSystems != null ? existingSystems.get(a.getName()) : null;
                String systemId = null;
                if (networkSystem == null) {
                    SystemDto systemDto = this.createSystemDto(request, a.getName(), a.getDescription());
                    SystemRespDto systemRespDto = starlifyService.addSystem(request, systemDto);
                    systemId = systemRespDto.getId();
                } else {
                    systemId = networkSystem.getId();
                }
                starlifyService.addServices(request, systemId, a);

                ((RequestItem) request).setStatus(RequestItem.Status.DONE);
                log.info("Started asset:" + a.getName());
            } catch (Throwable t) {
                log.error("Error while processing asset:" + a.getName(), t);
                ((RequestItem) request).setStatus(RequestItem.Status.ERROR);
            }
        }
    }

    private CloudhubRequest processCloudRequest(CloudhubRequest request) {
        ((CloudhubRequestItem) request).setStatus(CloudhubRequestItem.Status.IN_PROCESS);
        Account account = cloudhubService.getAccount(request.getApiKey());
        String a = cloudhubService.getApplications(request.getApiKey(),account.getOrganization().getCsId(), request.getEnvironment());
        return request;
    }





    @Override
    public RequestItem submitRequest(Request request) throws ExecutionException, InterruptedException {
        RequestItem workItem = new RequestItem();
        workItem.setStatus(RequestItem.Status.NOT_STARTED);
        workItem.setStarlifyKey(request.getStarlifyKey());
        workItem.setApiKey(request.getApiKey());
        workItem.setNetworkId(request.getNetworkId());
        statusMap.put(request.getNetworkId(), workItem);
        CompletableFuture.runAsync(() -> {
            processRequest(workItem);
        });
        return workItem;
    }

    @Override
    public CloudhubRequest submitCloudRequest(CloudhubRequest cloudhubRequest){
        CloudhubRequestItem workItem = new CloudhubRequestItem();
        workItem.setStatus(CloudhubRequestItem.Status.NOT_STARTED);
        workItem.setStarlifyKey(cloudhubRequest.getStarlifyKey());
        workItem.setApiKey(cloudhubRequest.getApiKey());
        workItem.setNetworkId(cloudhubRequest.getNetworkId());
        workItem.setEnvironment(cloudhubRequest.getEnvironment());
        cloudStatusMap.put(cloudhubRequest.getNetworkId(), workItem);
        //CompletableFuture.runAsync(() -> {

        //});
        return processCloudRequest(workItem);
    }

    @Override
    public RequestItem status(Request request) {
        return statusMap.get(request.getNetworkId());
    }






    private SystemDto createSystemDto(Request request, String name, String description) {
        SystemDto s = new SystemDto();
        String id = UUID.randomUUID().toString();
        s.setId(id);
        s.setName(name);
        Network n = new Network();
        n.setId(request.getNetworkId());
        s.setNetwork(n);
        s.setDescription(description);
        return s;
    }

    private synchronized void populateSystems(Request request, List<NetworkSystem> networkSystems) {
        if (networkSystems != null && !networkSystems.isEmpty()) {
            Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
            if (existingSystems == null) {
                existingSystems = new ConcurrentHashMap<>();
                cachedNetworkSystems.put(request.getNetworkId(), existingSystems);
            }
            for (NetworkSystem ns : networkSystems) {
                existingSystems.put(ns.getName(), ns);
            }
        }
    }


}
