package com.entiros.starlify.muleconnector.api.dto;

import lombok.Data;

@Data
public class CloudhubRequestItem extends CloudhubRequest{
    private CloudhubRequestItem.Status status;
    private String errorMessage;

    public enum Status {
        NOT_STARTED,
        IN_PROCESS,
        DONE,
        ERROR
    }
}
