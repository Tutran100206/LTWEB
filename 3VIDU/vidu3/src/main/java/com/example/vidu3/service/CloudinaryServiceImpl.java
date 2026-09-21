package com.example.vidu3.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.io.IOException;
@Service @RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;
    @Override public CloudinaryUploadResult upload(MultipartFile file) {
        if (file==null || file.isEmpty()) return null;
        if (file.getSize()>5*1024*1024) throw new BusinessException("Ảnh phải nhỏ hơn hoặc bằng 5 MB.");
        try {
            byte[] bytes=file.getBytes();
            try (var input=new java.io.ByteArrayInputStream(bytes)) {
                var image=javax.imageio.ImageIO.read(input);
                if (image==null || image.getWidth()>8000 || image.getHeight()>8000) throw new BusinessException("Vui lòng chọn ảnh JPG, PNG hoặc GIF hợp lệ, tối đa 8000 × 8000.");
            }
            if (cloudinary.config.apiKey==null || cloudinary.config.apiKey.isBlank()) throw new BusinessException("Chưa cấu hình Cloudinary. Bạn có thể lưu sản phẩm không có ảnh.");
            var result=cloudinary.uploader().upload(bytes,Map.of("folder","3vidu/products","resource_type","image","allowed_formats",java.util.List.of("jpg","png","gif")));
            return new CloudinaryUploadResult((String)result.get("secure_url"),(String)result.get("public_id"));
        } catch (BusinessException ex) { throw ex; }
        catch (IOException | RuntimeException ex) { throw new BusinessException("Không thể tải ảnh lên. Vui lòng kiểm tra cấu hình Cloudinary và thử lại."); }
    }
    @Override public void delete(String publicId) {
        if (publicId==null || publicId.isBlank()) return;
        try { cloudinary.uploader().destroy(publicId,Map.of("invalidate",true)); }
        catch (IOException | RuntimeException ex) { org.slf4j.LoggerFactory.getLogger(getClass()).warn("Không thể xóa ảnh Cloudinary có publicId={}; cần thử lại thủ công.", publicId); }
    }
}
