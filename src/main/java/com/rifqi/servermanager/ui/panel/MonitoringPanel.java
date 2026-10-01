package com.rifqi.servermanager.ui.panel;

import com.rifqi.servermanager.model.ServerNode;
import com.rifqi.servermanager.model.WebConfiguration;
import com.rifqi.servermanager.service.ServerManagementService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Locale;

public class MonitoringPanel extends JPanel {
    private final ServerManagementService service;
    private final Runnable onChanged;

    private final DefaultTableModel configurationModel;
    private final DefaultTableModel instanceModel;

    private final JTable configurationTable;
    private final JTable instanceTable;

    private final JTextArea detailArea =
            new JTextArea();

    private final JButton checkSelectedButton =
            new JButton(
                    "Periksa URL Terpilih"
            );

    private final JButton checkAllButton =
            new JButton(
                    "Periksa Semua"
            );

    private List<WebConfiguration> configurationRows =
            List.of();

    public MonitoringPanel(
            ServerManagementService service,
            Runnable onChanged
    ) {
        this.service = service;
        this.onChanged = onChanged;

        this.configurationModel =
                createReadOnlyModel(
                        new Object[]{
                                "Configuration",
                                "Nodes",
                                "Requests",
                                "Errors",
                                "Response Time*",
                                "Status"
                        }
                );

        this.instanceModel =
                createReadOnlyModel(
                        new Object[]{
                                "Instance",
                                "Configuration",
                                "Host",
                                "Port",
                                "Physical Location",
                                "Status"
                        }
                );

        this.configurationTable =
                new JTable(
                        configurationModel
                );

        this.instanceTable =
                new JTable(
                        instanceModel
                );

        buildUi();
        refreshData();
    }

    public void refreshData() {
        configurationRows =
                service.getConfigurations();

        configurationModel.setRowCount(
                0
        );

        for (
                WebConfiguration configuration :
                configurationRows
        ) {
            configurationModel.addRow(
                    new Object[]{
                            configuration.getName(),

                            service.countNodesForConfiguration(
                                    configuration.getName()
                            ),

                            configuration.getRequestCount(),

                            configuration.getErrorCount(),

                            String.format(
                                    Locale.US,
                                    "%.2f seconds",
                                    configuration
                                            .getLastResponseSeconds()
                            ),

                            configuration.getStatus()
                    }
            );
        }

        instanceModel.setRowCount(
                0
        );

        for (
                ServerNode node :
                service.getNodes()
        ) {
            instanceModel.addRow(
                    new Object[]{
                            node.getName(),
                            node.getConfigurationName(),
                            node.getHost(),
                            node.getPort(),
                            node.getLocation(),
                            node.getStatus()
                    }
            );
        }

        updateDetailArea();
    }

    private void buildUi() {
        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        14,
                        14,
                        14
                )
        );

        JLabel title =
                new JLabel(
                        "Monitoring - Overall Configuration Statistics"
                );

        title.setFont(
                title.getFont()
                        .deriveFont(
                                Font.BOLD,
                                20f
                        )
        );

        JLabel description =
                new JLabel(
                        "Daftar konfigurasi yang diterapkan beserta statistik monitoring pada level konfigurasi dan instance."
                );

        JPanel heading =
                new JPanel(
                        new BorderLayout(
                                0,
                                4
                        )
                );

        heading.add(
                title,
                BorderLayout.NORTH
        );

        heading.add(
                description,
                BorderLayout.CENTER
        );

        JButton refreshButton =
                new JButton(
                        "Refresh"
                );

        refreshButton
                .addActionListener(
                        event ->
                                refreshData()
                );

        checkSelectedButton
                .addActionListener(
                        event ->
                                checkSelected()
                );

        checkAllButton
                .addActionListener(
                        event ->
                                checkAll()
                );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        actions.add(
                checkSelectedButton
        );

        actions.add(
                checkAllButton
        );

        actions.add(
                refreshButton
        );

        JPanel top =
                new JPanel(
                        new BorderLayout()
                );

        top.add(
                heading,
                BorderLayout.CENTER
        );

        top.add(
                actions,
                BorderLayout.SOUTH
        );

        configureTable(
                configurationTable
        );

        configureTable(
                instanceTable
        );

        configurationTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (
                                    !event
                                            .getValueIsAdjusting()
                            ) {
                                updateDetailArea();
                            }
                        }
                );

        JPanel configurationTab =
                new JPanel(
                        new BorderLayout()
                );

        configurationTab.add(
                new JScrollPane(
                        configurationTable
                ),
                BorderLayout.CENTER
        );

        JPanel instanceTab =
                new JPanel(
                        new BorderLayout()
                );

        instanceTab.add(
                new JScrollPane(
                        instanceTable
                ),
                BorderLayout.CENTER
        );

        JTabbedPane innerTabs =
                new JTabbedPane();

        innerTabs.addTab(
                "Configurations",
                configurationTab
        );

        innerTabs.addTab(
                "Instances",
                instanceTab
        );

        detailArea.setEditable(
                false
        );

        detailArea.setLineWrap(
                true
        );

        detailArea.setWrapStyleWord(
                true
        );

        detailArea.setRows(
                7
        );

        detailArea.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        JScrollPane detailScroll =
                new JScrollPane(
                        detailArea
                );

        detailScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Monitoring Detail"
                )
        );

        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        innerTabs,
                        detailScroll
                );

        splitPane.setResizeWeight(
                0.72
        );

        splitPane.setOneTouchExpandable(
                true
        );

        add(
                top,
                BorderLayout.NORTH
        );

        add(
                splitPane,
                BorderLayout.CENTER
        );
    }

    private void configureTable(
            JTable table
    ) {
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(
                true
        );

        table.setRowHeight(
                26
        );

        table.getTableHeader()
                .setReorderingAllowed(
                        false
                );
    }

    private void checkSelected() {
        WebConfiguration selected =
                getSelectedConfiguration();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih konfigurasi pada tabel monitoring terlebih dahulu.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        setCheckButtonsEnabled(
                false
        );

        detailArea.setText(
                "Memeriksa "
                        + selected.getUrl()
                        + " ..."
        );

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                service.runHealthCheck(
                        selected.getId()
                );

                return null;
            }

            @Override
            protected void done() {
                setCheckButtonsEnabled(
                        true
                );

                onChanged.run();

                selectConfigurationById(
                        selected
                );
            }
        }.execute();
    }

    private void checkAll() {
        setCheckButtonsEnabled(
                false
        );

        detailArea.setText(
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
                setCheckButtonsEnabled(
                        true
                );

                onChanged.run();

                detailArea.setText(
                        "Pemeriksaan semua konfigurasi selesai."
                );
            }
        }.execute();
    }

    private WebConfiguration getSelectedConfiguration() {
        int selectedRow =
                configurationTable
                        .getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow =
                configurationTable
                        .convertRowIndexToModel(
                                selectedRow
                        );

        return configurationRows.get(
                modelRow
        );
    }

    private void updateDetailArea() {
        WebConfiguration selected =
                getSelectedConfiguration();

        if (selected == null) {
            detailArea.setText(
                    "Pilih nama konfigurasi untuk melihat URL, lokasi fisik, otoritas, jumlah node, request, error, response time, dan status terakhir."
            );

            return;
        }

        detailArea.setText(
                "Configuration : "
                        + selected.getName()
                        + System.lineSeparator()

                        + "URL           : "
                        + selected.getUrl()
                        + System.lineSeparator()

                        + "Physical Loc. : "
                        + selected.getPhysicalLocation()
                        + System.lineSeparator()

                        + "Authority     : "
                        + selected.getAuthority()
                        + System.lineSeparator()

                        + "Nodes         : "
                        + service.countNodesForConfiguration(
                                selected.getName()
                        )
                        + System.lineSeparator()

                        + "Requests      : "
                        + selected.getRequestCount()
                        + System.lineSeparator()

                        + "Errors        : "
                        + selected.getErrorCount()
                        + System.lineSeparator()

                        + "Response Time : "
                        + String.format(
                                Locale.US,
                                "%.2f seconds",
                                selected.getLastResponseSeconds()
                        )
                        + System.lineSeparator()

                        + "Status        : "
                        + selected.getStatus()
        );
    }

    private void setCheckButtonsEnabled(
            boolean enabled
    ) {
        checkSelectedButton.setEnabled(
                enabled
        );

        checkAllButton.setEnabled(
                enabled
        );
    }

    private void selectConfigurationById(
            WebConfiguration configuration
    ) {
        for (
                int modelRow = 0;
                modelRow
                        < configurationRows.size();
                modelRow++
        ) {
            if (
                    configurationRows
                            .get(modelRow)
                            .getId()
                            .equals(
                                    configuration.getId()
                            )
            ) {
                int viewRow =
                        configurationTable
                                .convertRowIndexToView(
                                        modelRow
                                );

                if (viewRow >= 0) {
                    configurationTable
                            .setRowSelectionInterval(
                                    viewRow,
                                    viewRow
                            );
                }

                return;
            }
        }
    }

    private DefaultTableModel createReadOnlyModel(
            Object[] columns
    ) {
        return new DefaultTableModel(
                columns,
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };
    }
}