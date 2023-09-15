package com.entiros.starlify.muleconnector.api.dto;

import lombok.Data;

@Data
public class OrganizationCloud {
    public String id;
    public String csId;
    public String name;
    public boolean enabled;
    public GlobalDeployment globalDeployment;
    public Plan plan;
    public boolean downloadApplicationsEnabled;
    public boolean persistentQueuesEncryptionEnabled;
    public boolean osV1Disabled;
    public boolean deploymentGroupEnabled;
    public boolean loggingCustomLog4jEnabled;
    public Usage usage;
    public Multitenancy multitenancy;
}
