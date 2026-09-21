package com.example.vidu3.controller;

import com.example.vidu3.dto.ProductDTO;
import com.example.vidu3.security.CustomUserDetails;
import com.example.vidu3.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequiredArgsConstructor @RequestMapping("/products")
public class ProductController {
    private final ProductService products;
    private final UserService users;
    @GetMapping public String list(@RequestParam(defaultValue="") String keyword,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@AuthenticationPrincipal CustomUserDetails actor,Model model) {
        model.addAttribute("items",products.search(keyword,page,size,actor)); model.addAttribute("keyword",keyword); model.addAttribute("baseUrl","/products"); return "products/list";
    }
    @GetMapping("/create") public String create(@AuthenticationPrincipal CustomUserDetails actor,Model model) { model.addAttribute("form",new ProductDTO()); owners(actor,model); return "products/form"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails actor,Model model) { model.addAttribute("form",products.get(id,actor)); owners(actor,model); return "products/form"; }
    @PostMapping public String create(@Valid @ModelAttribute("form") ProductDTO form,BindingResult errors,@RequestParam(required=false) MultipartFile image,@AuthenticationPrincipal CustomUserDetails actor,Model model,RedirectAttributes redirect) {
        form.setId(null); return save(null,form,errors,image,actor,model,redirect);
    }
    @PostMapping("/{id}") public String update(@PathVariable Long id,@Valid @ModelAttribute("form") ProductDTO form,BindingResult errors,@RequestParam(required=false) MultipartFile image,@AuthenticationPrincipal CustomUserDetails actor,Model model,RedirectAttributes redirect) {
        form.setId(id); return save(id,form,errors,image,actor,model,redirect);
    }
    private String save(Long id,ProductDTO form,BindingResult errors,MultipartFile image,CustomUserDetails actor,Model model,RedirectAttributes redirect) {
        owners(actor,model);
        if (errors.hasErrors()) return "products/form";
        try { products.save(id,form,image,actor); }
        catch (BusinessException ex) { errors.reject("product",ex.getMessage()); return "products/form"; }
        redirect.addFlashAttribute("success","Đã lưu sản phẩm."); return "redirect:/products";
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,@AuthenticationPrincipal CustomUserDetails actor,RedirectAttributes redirect) {
        products.delete(id,actor); redirect.addFlashAttribute("success","Đã xóa sản phẩm."); return "redirect:/products";
    }
    private void owners(CustomUserDetails actor,Model model) { if (actor.isAdmin()) model.addAttribute("owners",users.owners()); }
}
