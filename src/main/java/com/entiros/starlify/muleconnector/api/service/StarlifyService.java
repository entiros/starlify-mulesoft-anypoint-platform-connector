package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.*;
import java.util.List;

public interface StarlifyService {
  List<NetworkSystem> getSystems(Request request);

  SystemRespDto addSystem(Request request, SystemDto systemDto);

  String addServices(Request request, String systemId, Asset asset);

  String addService(Request request, ServiceDto serviceDto, String systemId);

  Response<ServiceRespDto> getServices(Request request, String systemId);
}
