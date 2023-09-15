package com.entiros.starlify.muleconnector.api.dto;

import lombok.Data;

@Data
public class CloudhubRequest {
        private String starlifyKey;
        private String apiKey;
        private String networkId;
        private String environment;

}
