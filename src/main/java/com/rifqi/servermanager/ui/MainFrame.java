package com.rifqi.servermanager.ui;

import com.rifqi.servermanager.service.ServerManagementService;
import com.rifqi.servermanager.ui.panel.CertificatesPanel;
import com.rifqi.servermanager.ui.panel.CommonTasksPanel;
import com.rifqi.servermanager.ui.panel.ConfigurationsPanel;
import com.rifqi.servermanager.ui.panel.MonitoringPanel;
import com.rifqi.servermanager.ui.panel.NodesPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Font;

public class MainFrame extends JFrame {
    private final JTabbedPane tabs =
            new JTabbedPane();

    private final CommonTasksPanel commonTasksPanel;
    private final ConfigurationsPanel configurationsPanel;
    private final NodesPanel nodesPanel;
    private final CertificatesPanel certificatesPanel;
    private final MonitoringPanel monitoringPanel;

    public MainFrame(
            ServerManagementService service
    ) {
        super(
                "Sistem Manajemen Server Web"
        );

        commonTasksPanel =
                new CommonTasksPanel(
                        service,
                        this::refreshAll,
                        tabs::setSelectedIndex
                );

        configurationsPanel =
                new ConfigurationsPanel(
                        service,
                        this::refreshAll
                );

        nodesPanel =
                new NodesPanel(
                        service,
                        this::refreshAll
                );

        certificatesPanel =
                new CertificatesPanel(
                        service,
                        this::refreshAll
                );

        monitoringPanel =
                new MonitoringPanel(
                        service,
                        this::refreshAll
                );

        buildUi();
        refreshAll();
    }

    private void buildUi() {
        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                1180,
                720
        );

        setMinimumSize(
                new java.awt.Dimension(
                        960,
                        620
                )
        );

        setLocationRelativeTo(null);

        JPanel banner =
                new JPanel(
                        new BorderLayout()
                );

        banner.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        18,
                        10,
                        18
                )
        );

        JLabel title =
                new JLabel(
                        "Sistem Manajemen Server Web"
                );

        title.setFont(
                title.getFont()
                        .deriveFont(
                                Font.BOLD,
                                28f
                        )
        );

        JLabel subtitle =
                new JLabel(
                        "Manajemen konfigurasi, node, sertifikat, lokasi fisik, otoritas, URL, dan monitoring server."
                );

        banner.add(
                title,
                BorderLayout.NORTH
        );

        banner.add(
                subtitle,
                BorderLayout.SOUTH
        );

        tabs.addTab(
                "Common Tasks",
                commonTasksPanel
        );

        tabs.addTab(
                "Configurations",
                configurationsPanel
        );

        tabs.addTab(
                "Nodes",
                nodesPanel
        );

        tabs.addTab(
                "Server Certificates",
                certificatesPanel
        );

        tabs.addTab(
                "Monitoring",
                monitoringPanel
        );

        setLayout(
                new BorderLayout()
        );

        add(
                banner,
                BorderLayout.NORTH
        );

        add(
                tabs,
                BorderLayout.CENTER
        );
    }

    private void refreshAll() {
        commonTasksPanel.refreshData();
        configurationsPanel.refreshData();
        nodesPanel.refreshData();
        certificatesPanel.refreshData();
        monitoringPanel.refreshData();
    }
}