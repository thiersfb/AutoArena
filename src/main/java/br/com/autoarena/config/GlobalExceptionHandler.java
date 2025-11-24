package br.com.autoarena.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e, RedirectAttributes redirectAttributes) {
        LOGGER.error("Erro ao processar requisição multipart: ", e);
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "Erro ao processar o upload de arquivo: " + e.getMessage());
        return "redirect:/private/admin/perfil";
    }
}