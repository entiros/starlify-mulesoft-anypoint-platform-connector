package com.entiros.starlify.muleconnector.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserProfile {
  public User user;
  public List<Asset> assetList;
}
