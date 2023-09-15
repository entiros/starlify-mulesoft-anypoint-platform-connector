package com.entiros.starlify.muleconnector.api.dto;

import java.util.ArrayList;

public class Plan {
    public double maxProductionWorkers;
    public double maxSandboxWorkers;
    public ArrayList<WorkerType> workerTypes;
    public int maxStandardConnectors;
    public int maxPremiumConnectors;
    public int maxStaticIps;
    public int maxDeploymentGroups;
    public boolean apiFabric;
    public boolean secureDataGateway;
    public boolean directVpn;
}
