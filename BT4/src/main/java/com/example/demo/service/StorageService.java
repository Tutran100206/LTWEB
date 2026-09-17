package com.example.demo.service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private final Path root;
    public StorageService(@Value("${app.storage.location}") String location) throws IOException {
        root = Path.of(location).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }
    public String replace(MultipartFile file, String oldName) {
        if (file == null || file.isEmpty()) return oldName;
        if (file.getSize() > 5 * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ảnh tối đa 5 MB");
        String format;
        try (var input = ImageIO.createImageInputStream(file.getInputStream())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IOException("Invalid image");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!Set.of("png", "jpeg", "gif").contains(format)
                    || (long) reader.getWidth(0) * reader.getHeight(0) > 25000000L)
                    throw new IOException("Unsupported image");
            } finally { reader.dispose(); }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ nhận ảnh PNG, JPEG, GIF hợp lệ, tối đa 25 triệu pixel");
        }
        String name = UUID.randomUUID() + "." + format;
        Path target = root.resolve(name);
        try (var input = file.getInputStream()) {
            Files.copy(input, target);
        } catch (IOException e) {
            remove(name);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lưu ảnh");
        }
        // A database rollback must not leave the newly uploaded file behind.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) remove(oldName);
                else remove(name);
            }
        });
        return name;
    }
    public void deleteAfterCommit(String name) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { remove(name); }
        });
    }
    private void remove(String name) {
        if (name == null || name.isBlank()) return;
        Path path = root.resolve(name).normalize();
        if (!path.getParent().equals(root)) return;
        try { Files.deleteIfExists(path); }
        catch (IOException e) { log.warn("Không thể xóa ảnh {}", name, e); }
    }
    public Resource load(String name) {
        if (!name.matches("[0-9a-f-]{36}\\.(png|jpeg|gif)"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh");
        Path path = root.resolve(name);
        if (!Files.isRegularFile(path))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh");
        return new FileSystemResource(path);
    }
}
