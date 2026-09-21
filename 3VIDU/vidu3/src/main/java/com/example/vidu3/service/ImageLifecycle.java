package com.example.vidu3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.*;
@Component @RequiredArgsConstructor
public class ImageLifecycle {
    private final CloudinaryService cloudinary;
    public void afterCommitDelete(String publicId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { cloudinary.delete(publicId); }
        });
    }
    public void rollbackDelete(String publicId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) { if (status!=STATUS_COMMITTED) cloudinary.delete(publicId); }
        });
    }
}
