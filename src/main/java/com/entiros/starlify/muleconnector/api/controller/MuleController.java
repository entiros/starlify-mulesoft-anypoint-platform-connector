package com.entiros.starlify.muleconnector.api.controller;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MuleController {

  private final StarlifyExportService starlifyExportService;

  @PostMapping("/status")
  public RequestItem getStatus(@RequestBody Request request) {
    return starlifyExportService.status(request);
  }

  @PostMapping("/submitRequest")
  public RequestItem processRequest(@RequestBody Request request)
      throws ExecutionException, InterruptedException {
    log.info("mule key recieved {}", request.getApiKey());
    return starlifyExportService.submitRequest(request);
  }
}
