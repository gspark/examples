package com.shrill.redisQueue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonCommonUtil {

  public static ObjectMapper getMapperInstance() {

    ObjectMapper om = new ObjectMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    om.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    om.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    om.disable(SerializationFeature.INDENT_OUTPUT);

    return om;
  }
}
