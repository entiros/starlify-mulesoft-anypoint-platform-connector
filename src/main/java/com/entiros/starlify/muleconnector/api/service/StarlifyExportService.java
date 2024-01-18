package com.entiros.starlify.muleconnector.api.service;

import com.entiros.starlify.muleconnector.api.dto.Request;
import com.entiros.starlify.muleconnector.api.dto.RequestItem;
import java.util.concurrent.ExecutionException;

public interface StarlifyExportService {
  RequestItem submitRequest(Request request) throws ExecutionException, InterruptedException;

  RequestItem status(Request request);
}
