package com.rifqi.servermanager.service;

import com.rifqi.servermanager.model.ServerCertificate;
import com.rifqi.servermanager.model.ServerNode;
import com.rifqi.servermanager.model.WebConfiguration;
import com.rifqi.servermanager.repository.AppRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerManagementService {
    private final AppRepository repository;
    private final HttpClient httpClient;

    public ServerManagementService(
            AppRepository repository
    ) {
        this.repository = repository;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public synchronized List<WebConfiguration> getConfigurations() {
        List<WebConfiguration> result =
                new ArrayList<>(
                        repository.state().getConfigurations()
                );

        result.sort(
                Comparator.comparing(
                        WebConfiguration::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return result;
    }

    public synchronized List<ServerNode> getNodes() {
        List<ServerNode> result =
                new ArrayList<>(
                        repository.state().getNodes()
                );

        result.sort(
                Comparator.comparing(
                        ServerNode::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return result;
    }

    public synchronized List<ServerCertificate> getCertificates() {
        List<ServerCertificate> result =
                new ArrayList<>(
                        repository.state().getCertificates()
                );

        result.sort(
                Comparator.comparing(
                        ServerCertificate::getAlias,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return result;
    }

    public synchronized void saveConfiguration(
            WebConfiguration configuration
    ) {
        Optional<WebConfiguration> existing =
                findConfiguration(configuration.getId());

        if (existing.isPresent()) {
            WebConfiguration current = existing.get();

            String oldName = current.getName();

            current.setName(configuration.getName());
            current.setUrl(configuration.getUrl());
            current.setPhysicalLocation(
                    configuration.getPhysicalLocation()
            );
            current.setAuthority(
                    configuration.getAuthority()
            );
            current.setStatus(
                    configuration.getStatus()
            );

            if (!oldName.equals(configuration.getName())) {
                for (
                        ServerNode node :
                        repository.state().getNodes()
                ) {
                    if (
                            node.getConfigurationName()
                                    .equals(oldName)
                    ) {
                        node.setConfigurationName(
                                configuration.getName()
                        );
                    }
                }
            }

        } else {
            repository.state()
                    .getConfigurations()
                    .add(configuration);
        }

        repository.save();
    }

    public synchronized void deleteConfiguration(
            UUID id
    ) {
        Optional<WebConfiguration> existing =
                findConfiguration(id);

        if (existing.isEmpty()) {
            return;
        }

        String name = existing.get().getName();

        repository.state()
                .getConfigurations()
                .removeIf(
                        item -> item.getId().equals(id)
                );

        repository.state()
                .getNodes()
                .removeIf(
                        node ->
                                node.getConfigurationName()
                                        .equals(name)
                );

        repository.save();
    }

    public synchronized void saveNode(
            ServerNode node
    ) {
        Optional<ServerNode> existing =
                findNode(node.getId());

        if (existing.isPresent()) {
            ServerNode current = existing.get();

            current.setName(
                    node.getName()
            );

            current.setConfigurationName(
                    node.getConfigurationName()
            );

            current.setHost(
                    node.getHost()
            );

            current.setPort(
                    node.getPort()
            );

            current.setLocation(
                    node.getLocation()
            );

            current.setStatus(
                    node.getStatus()
            );

        } else {
            repository.state()
                    .getNodes()
                    .add(node);
        }

        repository.save();
    }

    public synchronized void deleteNode(
            UUID id
    ) {
        repository.state()
                .getNodes()
                .removeIf(
                        item -> item.getId().equals(id)
                );

        repository.save();
    }

    public synchronized void saveCertificate(
            ServerCertificate certificate
    ) {
        Optional<ServerCertificate> existing =
                findCertificate(
                        certificate.getId()
                );

        if (existing.isPresent()) {
            ServerCertificate current =
                    existing.get();

            current.setAlias(
                    certificate.getAlias()
            );

            current.setOwner(
                    certificate.getOwner()
            );

            current.setIssuer(
                    certificate.getIssuer()
            );

            current.setValidUntil(
                    certificate.getValidUntil()
            );

        } else {
            repository.state()
                    .getCertificates()
                    .add(certificate);
        }

        repository.save();
    }

    public synchronized void deleteCertificate(
            UUID id
    ) {
        repository.state()
                .getCertificates()
                .removeIf(
                        item -> item.getId().equals(id)
                );

        repository.save();
    }

    public synchronized int countNodesForConfiguration(
            String configurationName
    ) {
        return (int) repository.state()
                .getNodes()
                .stream()
                .filter(
                        node ->
                                node.getConfigurationName()
                                        .equals(configurationName)
                )
                .count();
    }

    public void runHealthCheck(
            UUID configurationId
    ) {
        String url;

        synchronized (this) {
            Optional<WebConfiguration> configuration =
                    findConfiguration(
                            configurationId
                    );

            if (configuration.isEmpty()) {
                return;
            }

            url = configuration.get().getUrl();
        }

        long startedAt =
                System.nanoTime();

        boolean error = false;
        String status = "Online";

        try {
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(url)
                            )
                            .timeout(
                                    Duration.ofSeconds(8)
                            )
                            .GET()
                            .build();

            HttpResponse<Void> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.discarding()
                    );

            if (response.statusCode() >= 400) {
                error = true;
                status = "Error";
            }

        } catch (Exception exception) {
            error = true;
            status = "Offline";
        }

        double responseSeconds =
                (
                        System.nanoTime()
                                - startedAt
                )
                        / 1_000_000_000.0;

        synchronized (this) {
            Optional<WebConfiguration> configuration =
                    findConfiguration(
                            configurationId
                    );

            if (configuration.isEmpty()) {
                return;
            }

            WebConfiguration current =
                    configuration.get();

            current.registerRequest(
                    error,
                    responseSeconds
            );

            current.setStatus(
                    status
            );

            for (
                    ServerNode node :
                    repository.state().getNodes()
            ) {
                if (
                        node.getConfigurationName()
                                .equals(current.getName())
                ) {
                    node.setStatus(
                            status
                    );
                }
            }

            repository.save();
        }
    }

    public void runAllHealthChecks() {
        List<UUID> ids =
                getConfigurations()
                        .stream()
                        .map(
                                WebConfiguration::getId
                        )
                        .toList();

        for (UUID id : ids) {
            runHealthCheck(id);
        }
    }

    public synchronized void seedIfEmpty() {
        if (
                !repository.state()
                        .getConfigurations()
                        .isEmpty()
        ) {
            return;
        }

        WebConfiguration newCluster =
                new WebConfiguration(
                        "newcluster",
                        "http://localhost:8080",
                        "Jakarta - Rack A",
                        "Administrator",
                        "Unknown"
                );

        newCluster.setMonitoringMetrics(
                0,
                0,
                0.0
        );

        WebConfiguration test =
                new WebConfiguration(
                        "test",
                        "http://localhost:8081",
                        "Jakarta - Rack B",
                        "Operator",
                        "Unknown"
                );

        test.setMonitoringMetrics(
                599,
                0,
                3.12
        );

        repository.state()
                .getConfigurations()
                .add(newCluster);

        repository.state()
                .getConfigurations()
                .add(test);

        repository.state()
                .getNodes()
                .add(
                        new ServerNode(
                                "node-01",
                                "newcluster",
                                "127.0.0.1",
                                8080,
                                "Rack A1",
                                "Unknown"
                        )
                );

        repository.state()
                .getNodes()
                .add(
                        new ServerNode(
                                "node-02",
                                "newcluster",
                                "127.0.0.1",
                                8082,
                                "Rack A2",
                                "Unknown"
                        )
                );

        repository.state()
                .getNodes()
                .add(
                        new ServerNode(
                                "node-03",
                                "newcluster",
                                "127.0.0.1",
                                8083,
                                "Rack A3",
                                "Unknown"
                        )
                );

        repository.state()
                .getNodes()
                .add(
                        new ServerNode(
                                "node-test",
                                "test",
                                "127.0.0.1",
                                8081,
                                "Rack B1",
                                "Unknown"
                        )
                );

        repository.state()
                .getCertificates()
                .add(
                        new ServerCertificate(
                                "server-local",
                                "CN=localhost",
                                "Self Signed",
                                LocalDate.now()
                                        .plusYears(1)
                        )
                );

        repository.save();
    }

    private Optional<WebConfiguration> findConfiguration(
            UUID id
    ) {
        return repository.state()
                .getConfigurations()
                .stream()
                .filter(
                        item ->
                                item.getId()
                                        .equals(id)
                )
                .findFirst();
    }

    private Optional<ServerNode> findNode(
            UUID id
    ) {
        return repository.state()
                .getNodes()
                .stream()
                .filter(
                        item ->
                                item.getId()
                                        .equals(id)
                )
                .findFirst();
    }

    private Optional<ServerCertificate> findCertificate(
            UUID id
    ) {
        return repository.state()
                .getCertificates()
                .stream()
                .filter(
                        item ->
                                item.getId()
                                        .equals(id)
                )
                .findFirst();
    }
}