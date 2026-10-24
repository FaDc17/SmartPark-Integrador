package com.smartpark.controller;

import com.smartpark.model.RegisteredUser;
import com.smartpark.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new RegisteredUser());
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
                        @RequestParam(required = false) String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ingresa tu usuario o correo electrónico y contraseña.");
            return "redirect:/login";
        }

        RegisteredUser user = userService.authenticate(email, password);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Las credenciales son incorrectas o el usuario no está registrado.");
            return "redirect:/login";
        }

        session.setAttribute("loggedIn", true);
        session.setAttribute("username", user.getEmail());
        // Perfil: la cuenta única de administrador ve todo; el resto entra como cajero.
        session.setAttribute("rol", userService.esAdmin(user.getEmail()) ? "ADMIN" : "CAJERO");
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisteredUser());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                           @RequestParam String apellido,
                           @RequestParam String email,
                           @RequestParam String password,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.save(new RegisteredUser(nombre, apellido, email, password));
            redirectAttributes.addFlashAttribute("success", "Registro exitoso");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}