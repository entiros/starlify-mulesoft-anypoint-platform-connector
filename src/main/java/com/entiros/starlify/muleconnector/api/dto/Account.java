package com.entiros.starlify.muleconnector.api.dto;

import lombok.Data;

import java.util.ArrayList;
@Data
public class Account{
        public String id;
        public String username;
        public String email;
        public String organizationName;
        public String defaultEnvironment;
        public boolean enabled;
        public OrganizationCloud organization;
        public String csToken;
        public ArrayList<Environment> environments;
        public String activeEnvironment;
    }

