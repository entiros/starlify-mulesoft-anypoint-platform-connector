package com.entiros.starlify.muleconnector.api.dto;

import java.util.List;
import lombok.Data;

@Data
public class Response<T> {
  private List<Link> links;
  private List<T> content;
  private Page page;
}
