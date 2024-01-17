package com.entiros.starlify.muleconnector.api.service.impl;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.MuleService;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import com.entiros.starlify.muleconnector.api.service.StarlifyService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarlifyExportServiceImpl implements StarlifyExportService {

  private final MuleService muleService;
  private final StarlifyService starlifyService;

  private Map<String, Map<String, NetworkSystem>> cachedNetworkSystems = new ConcurrentHashMap<>();
  private Map<String, RequestItem> statusMap = new ConcurrentHashMap<>();

  private void processRequest(Request request) {
    ((RequestItem) request).setStatus(RequestItem.Status.IN_PROCESS);
    UserProfile userProfile = muleService.getProfile(request.getApiKey());
    List<Asset> assetList = userProfile.getAssetList();

    List<NetworkSystem> systems = starlifyService.getSystems(request);

    this.populateSystems(request, systems);

    Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
    for (Asset a : assetList) {
      try {
        log.info("Started asset:" + a.getName() + " id:" + a.getAssetId());
        NetworkSystem networkSystem =
            existingSystems != null ? existingSystems.get(a.getName()) : null;
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

  @Override
  public RequestItem submitRequest(Request request)
      throws ExecutionException, InterruptedException {
    RequestItem workItem = new RequestItem();
    workItem.setStatus(RequestItem.Status.NOT_STARTED);
    workItem.setStarlifyKey(request.getStarlifyKey());
    workItem.setApiKey(request.getApiKey());
    workItem.setNetworkId(request.getNetworkId());
    statusMap.put(request.getNetworkId(), workItem);
    CompletableFuture.runAsync(
        () -> {
          processRequest(workItem);
        });
    return workItem;
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
