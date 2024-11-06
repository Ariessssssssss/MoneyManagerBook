package com.example.moneymanager.model.dao;

import java.io.File;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface DatabaseOperations {
    File getDatabaseFile();
    void closeDatabase();
    void openDatabase();
    boolean exportDatabase(File sourceFile, OutputStream outputStream);
    boolean importDatabase(InputStream inputStream, File destFile);
}
