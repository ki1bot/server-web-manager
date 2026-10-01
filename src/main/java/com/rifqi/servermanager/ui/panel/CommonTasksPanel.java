package com.rifqi.servermanager.ui.panel;

import com.rifqi.servermanager.service.ServerManagementService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.IntConsumer;

public class CommonTasksPanel extends JPanel {
    private final ServerManagementService service;
    private final Runnable onChanged;
    private final IntConsumer tabSwitcher;

    private final JLabel configurationCountLabel =
            new JLabel();

    private final JLabel nodeCountLabel =
            new JLabel();

    private final JLabel certificateCountLabel =
            new JLabel();

    private final JLabel statusLabel =
            new JLabel("Siap.");

    private final JButton healthCheckButton =
            new JButton("Periksa Semua Server");

    public CommonTasksPanel(
            ServerManagementService service,
            Runnable onChanged,
            IntConsumer tabSwitcher
    ) {
        this.service = service;
        this.onChanged = onChanged;
        this.tabSwitcher = tabSwitcher;

        buildUi();
        refreshData();
    }

    public void refreshData() {
        configurationCountLabel.setText(
                "Configurations: "
                        + service
                        .getConfigurations()
                        .size()
        );

        nodeCountLabel.setText(
                "Nodes: "
                        + service
                        .getNodes()
                        .size()
        );

        certificateCountLabel.setText(
                "Certificates: "
                        + service
                        .getCertificates()
                        .size()
        );
    }

    private void buildUi() {
        setLayout(
                new BorderLayout(
                        12,
                        12
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        JLabel title =
                new JLabel(
                        "Common Tasks"
                );

        title.setFont(
                title.getFont()
                        .deriveFont(
                                Font.BOLD,
                                22f
                        )
        );

        JLabel intro =
                new JLabel(
                        "Kelola konfigurasi server web, node, sertifikat, lokasi fisik, otoritas, URL, dan monitoring dari satu aplikasi."
                );

        JPanel heading =
                new JPanel();

        heading.setLayout(
                new BoxLayout(
                        heading,
                        BoxLayout.Y_AXIS
                )
        );

        heading.add(title);

        heading.add(
                Box.createVerticalStrut(6)
        );

        heading.add(intro);

        JPanel summary =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                24,
                                10
                        )
                );

        summary.setBorder(
                BorderFactory.createTitledBorder(
                        "Ringkasan Sistem"
                )
        );

        summary.add(
                configurationCountLabel
        );

        summary.add(
                nodeCountLabel
        );

        summary.add(
                certificateCountLabel
        );

        JButton configurationsButton =
                new JButton(
                        "Kelola Configurations"
                );

        JButton nodesButton =
                new JButton(
                        "Kelola Nodes"
                );

        JButton certificatesButton =
                new JButton(
                        "Kelola Certificates"
                );

        JButton monitoringButton =
                new JButton(
                        "Buka Monitoring"
                );

        configurationsButton
                .addActionListener(
                        event ->
                                tabSwitcher.accept(1)
                );

        nodesButton
                .addActionListener(
                        event ->
                                tabSwitcher.accept(2)
                );

        certificatesButton
                .addActionListener(
                        event ->
                                tabSwitcher.accept(3)
                );

        monitoringButton
                .addActionListener(
                        event ->
                                tabSwitcher.accept(4)
                );

        healthCheckButton
                .addActionListener(
                        event ->
                                runAllChecks()
                );

        JPanel taskButtons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                8
                        )
                );

        taskButtons.setBorder(
                BorderFactory.createTitledBorder(
                        "Aksi Cepat"
                )
        );

        taskButtons.add(
                configurationsButton
        );

        taskButtons.add(
                nodesButton
        );

        taskButtons.add(
                certificatesButton
        );

        taskButtons.add(
                monitoringButton
        );

        taskButtons.add(
                healthCheckButton
        );

        JPanel center =
                new JPanel();

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS
                )
        );

        center.add(summary);

        center.add(
                Box.createVerticalStrut(10)
        );

        center.add(taskButtons);

        center.add(
                Box.createVerticalStrut(12)
        );

        center.add(statusLabel);

        add(
                heading,
                BorderLayout.NORTH
        );

        add(
                center,
                BorderLayout.CENTER
        );
    }

    private void runAllChecks() {
        healthCheckButton.setEnabled(
                false
        );

        statusLabel.setText(
                "Memeriksa semua URL konfigurasi..."
        );

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.runAllHealthChecks();
                return null;
            }

            @Override
            protected void done() {
                healthCheckButton.setEnabled(
                        true
                );

                statusLabel.setText(
                        "Pemeriksaan selesai. Buka tab Monitoring untuk melihat hasil terbaru."
                );

                onChanged.run();
            }
        }.execute();
    }
}