package com.system.email.infra.model.gmail;

import java.io.Serializable;

public record EmailPayloadHeaders(String name, String value) implements Serializable {}
