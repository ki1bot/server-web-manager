package com.rifqi.servermanager.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class ServerCertificate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String alias;
    private String owner;
    private String issuer;
    private LocalDate validUntil;

    public ServerCertificate(
            String alias,
            String owner,
            String issuer,
            LocalDate validUntil
    ) {
        this(
                UUID.randomUUID(),
                alias,
                owner,
                issuer,
                validUntil
        );
    }

    public ServerCertificate(
            UUID id,
            String alias,
            String owner,
            String issuer,
            LocalDate validUntil
    ) {
        this.id = id;
        this.alias = alias;
        this.owner = owner;
        this.issuer = issuer;
        this.validUntil = validUntil;
    }

    public UUID getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getStatus() {
        return validUntil.isBefore(LocalDate.now())
                ? "Expired"
                : "Valid";
    }
}