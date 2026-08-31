package vn.iotstar.controller.admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.utils.Constant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/categories",
        "/admin/category/add",
        "/admin/category/insert",
        "/admin/category/edit",
        "/admin/category/update",
        "/admin/category/delete"
})
@MultipartConfig
public class CategoryController extends HttpServlet {
    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();

        if (url.endsWith("/admin/categories")) {
            try {
                List<Category> list = cateService.findAll();
                req.setAttribute("listcate", list);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/views/admin/category-list.jsp");
                dispatcher.forward(req, resp);
            } catch (RuntimeException e) {
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Khong the tai du lieu category tu CSDL.");
            }
            return;
        }

        if (url.endsWith("/admin/category/add")) {
            RequestDispatcher dispatcher = req.getRequestDispatcher("/views/admin/category-add.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if (url.endsWith("/admin/category/edit")) {
            Integer id = parseInt(req.getParameter("id"));
            if (id == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id hợp lệ");
                return;
            }
            Category category = cateService.findById(id);
            if (category == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy category");
                return;
            }
            req.setAttribute("cate", category);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/views/admin/category-edit.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if (url.endsWith("/admin/category/delete")) {
            Integer id = parseInt(req.getParameter("id"));
            if (id == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu id hợp lệ");
                return;
            }
            cateService.delete(id);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();
        if (url.endsWith("/admin/category/insert")) {
            insertCategory(req, resp);
            return;
        }
        if (url.endsWith("/admin/category/update")) {
            updateCategory(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void insertCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String categoryName = req.getParameter("categoryname");
        if (categoryName == null || categoryName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Category name không được rỗng");
            return;
        }

        Category category = new Category();
        category.setCategoryname(categoryName.trim());
        category.setStatus(parseInt(req.getParameter("status"), 0));
        category.setImages(resolveImage(req, null));
        cateService.insert(category);
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private void updateCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Integer categoryId = parseInt(req.getParameter("categoryid"));
        if (categoryId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu categoryid hợp lệ");
            return;
        }

        Category category = cateService.findById(categoryId);
        if (category == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy category");
            return;
        }

        String categoryName = req.getParameter("categoryname");
        if (categoryName == null || categoryName.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Category name không được rỗng");
            return;
        }

        category.setCategoryname(categoryName.trim());
        category.setStatus(parseInt(req.getParameter("status"), category.getStatus()));
        category.setImages(resolveImage(req, category.getImages()));
        cateService.update(category);
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private String resolveImage(HttpServletRequest req, String oldImage) throws IOException, ServletException {
        String imageUrl = req.getParameter("images");
        Part part = req.getPart("images1");

        if (part != null && part.getSize() > 0) {
            String stored = saveUploadFile(part);
            deleteOldLocalImage(oldImage);
            return stored;
        }

        if (imageUrl != null && !imageUrl.isBlank()) {
            if (oldImage != null && imageUrl.trim().equals(oldImage)) {
                return oldImage;
            }
            deleteOldLocalImage(oldImage);
            return imageUrl.trim();
        }

        if (oldImage != null && !oldImage.isBlank()) {
            return oldImage;
        }
        return "avatar.png";
    }

    private String saveUploadFile(Part part) throws IOException {
        String submitted = part.getSubmittedFileName();
        String base = submitted == null ? "" : Paths.get(submitted).getFileName().toString();

        String extension = "";
        int dot = base.lastIndexOf('.');
        if (dot >= 0 && dot < base.length() - 1) {
            extension = "." + base.substring(dot + 1);
        }

        String fileName = System.currentTimeMillis() + extension;
        Path uploadDir = Paths.get(Constant.DIR);
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(fileName);
        try (var inputStream = part.getInputStream()) {
            Files.copy(inputStream, filePath);
        }
        return fileName;
    }

    private void deleteOldLocalImage(String image) {
        if (isRemoteUrl(image) || image == null || image.isBlank()) {
            return;
        }
        try {
            Path uploadDir = Paths.get(Constant.DIR).toAbsolutePath().normalize();
            Path path = uploadDir.resolve(image).normalize();
            if (path.startsWith(uploadDir)) {
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
            // Không làm crash luồng CRUD nếu xóa file cũ thất bại
        }
    }

    private boolean isRemoteUrl(String image) {
        return image != null && (image.startsWith("http://") || image.startsWith("https://"));
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseInt(String value, int defaultValue) {
        Integer v = parseInt(value);
        return v == null ? defaultValue : v;
    }
}
