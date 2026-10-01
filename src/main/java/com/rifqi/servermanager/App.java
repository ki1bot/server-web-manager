package com.rifqi.servermanager;

import com.rifqi.servermanager.repository.AppRepository;
import com.rifqi.servermanager.service.ServerManagementService;
import com.rifqi.servermanager.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applyLookAndFeel();

            AppRepository repository = new AppRepository(
                    Path.of("data", "server-manager.dat")
            );

            ServerManagementService service =
                    new ServerManagementService(repository);

            service.seedIfEmpty();

            new MainFrame(service).setVisible(true);
        });
    }

    private static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }
}