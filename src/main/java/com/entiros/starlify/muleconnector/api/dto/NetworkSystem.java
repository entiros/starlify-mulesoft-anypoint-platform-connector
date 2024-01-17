package com.entiros.starlify.muleconnector.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class NetworkSystem extends BaseDto {
  private List<Reference> references;
}
