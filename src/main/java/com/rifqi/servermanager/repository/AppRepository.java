package com.rifqi.servermanager.repository;

import com.rifqi.servermanager.model.AppState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppRepository {
    private final Path filePath;
    private final boolean firstRun;

    private AppState state;

    public AppRepository(Path filePath) {
        this.filePath = filePath;
        this.firstRun = Files.notExists(filePath);
        this.state = load();
    }

    public synchronized AppState state() {
        return state;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public synchronized void save() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (
                    ObjectOutputStream output =
                            new ObjectOutputStream(
                                    Files.newOutputStream(filePath)
                            )
            ) {
                output.writeObject(state);
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Gagal menyimpan data aplikasi: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private AppState load() {
        if (!Files.exists(filePath)) {
            return new AppState();
        }

        try (
                ObjectInputStream input =
                        new ObjectInputStream(
                                Files.newInputStream(filePath)
                        )
        ) {
            Object value = input.readObject();

            if (value instanceof AppState appState) {
                return appState;
            }

            return new AppState();

        } catch (
                IOException
                | ClassNotFoundException exception
        ) {
            return new AppState();
        }
    }
}