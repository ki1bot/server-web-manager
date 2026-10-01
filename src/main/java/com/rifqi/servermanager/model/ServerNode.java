package com.rifqi.servermanager.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ServerNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private String configurationName;
    private String host;
    private int port;
    private String location;
    private String status;

    public ServerNode(
            String name,
            String configurationName,
            String host,
            int port,
            String location,
            String status
    ) {
        this(
                UUID.randomUUID(),
                name,
                configurationName,
                host,
                port,
                location,
                status
        );
    }

    public ServerNode(
            UUID id,
            String name,
            String configurationName,
            String host,
            int port,
            String location,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.configurationName = configurationName;
        this.host = host;
        this.port = port;
        this.location = location;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}