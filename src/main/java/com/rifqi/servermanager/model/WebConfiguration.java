package com.rifqi.servermanager.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class WebConfiguration implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private String url;
    private String physicalLocation;
    private String authority;
    private String status;
    private long requestCount;
    private long errorCount;
    private double lastResponseSeconds;

    public WebConfiguration(
            String name,
            String url,
            String physicalLocation,
            String authority,
            String status
    ) {
        this(
                UUID.randomUUID(),
                name,
                url,
                physicalLocation,
                authority,
                status,
                0,
                0,
                0.0
        );
    }

    public WebConfiguration(
            UUID id,
            String name,
            String url,
            String physicalLocation,
            String authority,
            String status,
            long requestCount,
            long errorCount,
            double lastResponseSeconds
    ) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.physicalLocation = physicalLocation;
        this.authority = authority;
        this.status = status;
        this.requestCount = requestCount;
        this.errorCount = errorCount;
        this.lastResponseSeconds = lastResponseSeconds;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(String physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public double getLastResponseSeconds() {
        return lastResponseSeconds;
    }

    public void registerRequest(
            boolean error,
            double responseSeconds
    ) {
        requestCount++;

        if (error) {
            errorCount++;
        }

        lastResponseSeconds = responseSeconds;
    }

    public void setMonitoringMetrics(
            long requestCount,
            long errorCount,
            double lastResponseSeconds
    ) {
        this.requestCount = requestCount;
        this.errorCount = errorCount;
        this.lastResponseSeconds = lastResponseSeconds;
    }
}