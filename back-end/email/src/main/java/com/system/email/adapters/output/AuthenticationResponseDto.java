package com.system.email.adapters.output;

import java.io.Serializable;

public record AuthenticationResponseDto(String access_token) implements Serializable {}
