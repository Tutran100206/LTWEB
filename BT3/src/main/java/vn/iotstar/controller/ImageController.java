package vn.iotstar.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vn.iotstar.utils.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(urlPatterns = "/image")
public class ImageController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fname = req.getParameter("fname");
        if (fname == null || fname.isBlank() || fname.contains("..") || fname.contains("/") || fname.contains("\\")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path uploadDir = Paths.get(Constant.DIR).toAbsolutePath().normalize();
        Path imagePath = uploadDir.resolve(fname).normalize();
        if (!imagePath.startsWith(uploadDir) || !Files.exists(imagePath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Files.probeContentType(imagePath);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");

        try (InputStream in = Files.newInputStream(imagePath); OutputStream out = resp.getOutputStream()) {
            in.transferTo(out);
        }
    }
}
