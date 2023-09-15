package com.entiros.starlify.muleconnector.api.controller;

import com.entiros.starlify.muleconnector.api.dto.*;
import com.entiros.starlify.muleconnector.api.service.CloudhubService;
import com.entiros.starlify.muleconnector.api.service.StarlifyExportService;
import com.entiros.starlify.muleconnector.integration.EnvironmentsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MuleController {

    private final CloudhubService cloudhubService;
    private final EnvironmentsMapper environmentsMapper;

    private final StarlifyExportService starlifyExportService;

    @PostMapping("/status")
    public RequestItem getStatus(@RequestBody Request request) {
        return starlifyExportService.status(request);
    }

    @PostMapping("/submitRequest")
    public RequestItem processRequest(@RequestBody Request request) throws ExecutionException, InterruptedException {
        log.info("mule key recieved {}", request.getApiKey());
        return starlifyExportService.submitRequest(request);
    }

    @PostMapping("/submitCloudhubRequest")
    public CloudhubRequest  processCloudRequest(@RequestBody CloudhubRequest cloudhubRequest){
        return starlifyExportService.submitCloudRequest(cloudhubRequest);
    }
    @GetMapping("/environments")
    public List<Environment> getEnvironments(@RequestHeader(name = "accesstoken") String accessToken){
        return  environmentsMapper.map(cloudhubService.getAccount(accessToken));
    }
}

