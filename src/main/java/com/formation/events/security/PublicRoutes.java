package com.formation.events.security;

import java.util.List;

public class PublicRoutes {

  public static final List<String> PUBLIC_PATHS = List.of(
      "/api/auth/**",
      "/h2-console/**");

  public static String[] toArray() {
    return PUBLIC_PATHS.toArray(new String[0]);
  }
}
