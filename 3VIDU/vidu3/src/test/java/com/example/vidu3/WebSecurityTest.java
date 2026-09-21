package com.example.vidu3;
import com.example.vidu3.entity.Role;
import com.example.vidu3.entity.User;
import com.example.vidu3.repository.UserRepository;
import com.example.vidu3.security.*;
import com.example.vidu3.controller.*;
import com.example.vidu3.service.*;
import com.example.vidu3.mapper.UserMapperImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
@ExtendWith(SpringExtension.class) @WebAppConfiguration @ContextConfiguration(classes=WebSecurityTest.Config.class)
class WebSecurityTest {
    @org.springframework.boot.test.context.TestConfiguration @EnableWebMvc @EnableWebSecurity
    @Import({SecurityConfig.class,CustomUserDetailsService.class,PageController.class,CurrentUserAdvice.class,UserProfileService.class,UserMapperImpl.class,AuthController.class,ProductController.class,UserController.class,GlobalExceptionHandler.class})
    static class Config {
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean AuthService authService() { return mock(AuthService.class); }
        @Bean ProductService productService() { return mock(ProductService.class); }
        @Bean UserService userService() { return mock(UserService.class); }
        @Bean ThymeleafViewResolver viewResolver() {
            var resolver=new ClassLoaderTemplateResolver(); resolver.setPrefix("templates/"); resolver.setSuffix(".html"); resolver.setCharacterEncoding("UTF-8");
            var engine=new SpringTemplateEngine(); engine.setTemplateResolver(resolver); 
            var views=new ThymeleafViewResolver(); views.setTemplateEngine(engine); views.setCharacterEncoding("UTF-8"); return views;
        }
    }
    @Autowired WebApplicationContext context;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    MockMvc mvc;
    CustomUserDetails principal;
    User account;
    @BeforeEach void setup() {
        reset(users); reset(context.getBean(AuthService.class),org.springframework.test.util.AopTestUtils.getUltimateTargetObject(context.getBean(UserService.class)),context.getBean(ProductService.class));
        mvc=webAppContextSetup(context).apply(springSecurity()).build();
        Role role=new Role(); role.setName("USER"); role.setId(1L);
        account=new User(); account.setId(1L); account.setEmail("user@example.com"); account.setFullName("Nguyễn Minh An"); account.setPassword(encoder.encode("Demo@12345")); account.setEnabled(true); account.setRole(role); account.setUsername("user");
        when(users.findById(1L)).thenReturn(Optional.of(account)); when(users.findByUsernameOrEmail("user","user")).thenReturn(Optional.of(account)); when(users.findByUsernameOrEmail("user@example.com","user@example.com")).thenReturn(Optional.of(account));
        principal=new CustomUserDetails(account);
    }
    CustomUserDetails admin() { var role=new Role(); role.setName("ADMIN"); account.setRole(role); return new CustomUserDetails(account); }
    @Test void homeAndLoginRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string(containsString("_csrf"))).andExpect(content().string(containsString("name=\"login\"")));
    }
    @Test void anonymousMustLogin() throws Exception { mvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login")); }
    @Test void validLoginCreatesSession() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login","user").param("password","Demo@12345"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard")).andExpect(authenticated());
    }
    @Test void emailLoginWorks() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login","user@example.com").param("password","Demo@12345")).andExpect(authenticated());
    }
    @Test void wrongPasswordAndDisabledAccountAreRejected() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("login","user").param("password","wrong")).andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
        account.setEnabled(false);
        mvc.perform(post("/login").with(csrf()).param("login","user").param("password","Demo@12345")).andExpect(unauthenticated());
    }
    @Test void csrfRequiredAndLogoutWorks() throws Exception {
        mvc.perform(post("/login").param("login","user").param("password","Demo@12345")).andExpect(status().isForbidden());
        mvc.perform(post("/logout").with(user(principal))).andExpect(status().isForbidden());
        mvc.perform(post("/logout").with(user(principal)).with(csrf())).andExpect(redirectedUrl("/login?logout")).andExpect(unauthenticated());
    }
    @Test void headerAndRoleProtectionWork() throws Exception {
        mvc.perform(get("/dashboard").with(user(principal))).andExpect(status().isOk()).andExpect(content().string(containsString("Nguyễn Minh An"))).andExpect(content().string(containsString("user@example.com"))) ;
        mvc.perform(get("/dashboard/admin").with(user(principal))).andExpect(status().isForbidden());
        mvc.perform(get("/dashboard/admin").with(user(admin()))).andExpect(status().isOk());
        mvc.perform(get("/access-denied")).andExpect(status().isForbidden());
        mvc.perform(get("/error")).andExpect(status().isNotFound());
    }

    @Test void publicAuthFormsRender() throws Exception {
        for (String path : List.of("/register","/verify-otp","/forgot-password","/reset-password")) {
            mvc.perform(get(path)).andExpect(status().isOk()).andExpect(content().string(containsString("_csrf")));
        }
    }
    @Test void registrationValidationIsVisible() throws Exception {
        mvc.perform(post("/register").with(csrf()).param("username","a").param("email","bad").param("fullName","").param("password","x").param("confirmPassword","y"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("không khớp")));
        verifyNoInteractions(context.getBean(AuthService.class));
    }
    @Test void userManagementRequiresAdminAndListsRender() throws Exception {
        mvc.perform(get("/users").with(user(principal))).andExpect(status().isForbidden());
        var admin=admin();
        UserService service=org.springframework.test.util.AopTestUtils.getUltimateTargetObject(context.getBean(UserService.class));
        when(service.search("",0,10)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(service.owners()).thenReturn(List.of());
        mvc.perform(get("/users").with(user(admin))).andExpect(status().isOk());
        mvc.perform(get("/users/create").with(user(admin))).andExpect(status().isOk()).andExpect(content().string(containsString("_csrf")));
        var form=new com.example.vidu3.dto.UserFormDTO(); form.setId(5L); form.setUsername("editor"); form.setFullName("Biên tập viên"); form.setEmail("editor@example.com");
        when(service.get(5L)).thenReturn(form);
        mvc.perform(get("/users/5/edit").with(user(admin))).andExpect(status().isOk());
        var products=context.getBean(ProductService.class);
        when(products.search(eq(""),eq(0),eq(10),any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        mvc.perform(get("/products").with(user(principal))).andExpect(status().isOk());
        mvc.perform(get("/products/create").with(user(principal))).andExpect(status().isOk()).andExpect(content().string(containsString("_csrf")));
        mvc.perform(get("/products/create").with(user(admin))).andExpect(status().isOk());
    }

}
