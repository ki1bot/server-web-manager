package com.rifqi.servermanager.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<WebConfiguration> configurations = new ArrayList<>();
    private final List<ServerNode> nodes = new ArrayList<>();
    private final List<ServerCertificate> certificates = new ArrayList<>();

    public List<WebConfiguration> getConfigurations() {
        return configurations;
    }

    public List<ServerNode> getNodes() {
        return nodes;
    }

    public List<ServerCertificate> getCertificates() {
        return certificates;
    }
}