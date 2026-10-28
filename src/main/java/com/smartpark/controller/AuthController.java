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

    // Redirige la ruta principal al formulario de inicio de sesión.
    @GetMapping({"/", "/index"})
    public String index() {
        return "redirect:/login";
    }

    // Muestra el inicio de sesión o redirige al dashboard si existe una sesión activa.
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new RegisteredUser());
        return "login";
    }

    // Valida las credenciales y crea la sesión del usuario autenticado.
    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
                        @RequestParam(required = false) String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ingresa tu usuario o correo electrónico y contraseña.");
            return "redirect:/login";
        }

        if (!userService.authenticate(email, password)) {
            redirectAttributes.addFlashAttribute("error", "Las credenciales son incorrectas o el usuario no está registrado.");
            return "redirect:/login";
        }

        session.setAttribute("loggedIn", true);
        session.setAttribute("username", email);
        return "redirect:/dashboard";
    }

    // Prepara el formulario y muestra la página de registro.
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisteredUser());
        return "register";
    }

    // Registra un nuevo usuario con los datos enviados por el formulario.
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

    // Invalida la sesión actual y devuelve al usuario al inicio de sesión.
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
