package com.example.demo.controller;
import com.example.demo.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
public class ImageController {
    private final StorageService storage;
    public ImageController(StorageService storage) { this.storage = storage; }
    @GetMapping("/uploads/{name}")
    public ResponseEntity<Resource> image(@PathVariable String name) {
        Resource resource = storage.load(name);
        MediaType type = name.endsWith(".png") ? MediaType.IMAGE_PNG :
            name.endsWith(".gif") ? MediaType.IMAGE_GIF : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(type).header("X-Content-Type-Options", "nosniff").body(resource);
    }
}
