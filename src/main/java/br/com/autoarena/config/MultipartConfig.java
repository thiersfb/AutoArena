package br.com.autoarena.config; // Crie um pacote de configuração se não tiver

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

//import javax.servlet.MultipartConfigElement; // Use jakarta.servlet.MultipartConfigElement para Spring Boot 3+
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // max-file-size=10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(100)); // max-request-size=100MB (ajuste conforme necessário)

        // Configura o limite de arquivos/partes
        // Para Spring Boot < 2.5.x, esta configuração de max-file-count pode precisar ser um pouco diferente
        // dependendo da implementação exata do tomcat-fileupload que a versão antiga usava.
        // No entanto, para a maioria dos casos de uso, o maxFileSize e maxRequestSize já cobrem muitos cenários.
        // Se este erro específico persistir, pode ser que sua versão realmente não exponha 'max-file-count'
        // para configuração via MultipartConfigFactory diretamente para Tomcat.
        // Nesse caso, o upgrade é a solução mais garantida.

        // Não há um método direto setMaxFileCount() na MultipartConfigFactory
        // para configurar o limite de "partes" para FileCountLimitExceededException em versões antigas.
        // Este erro geralmente vem do limite interno do commons-fileupload do Tomcat ou do Spring.
        // A melhor aposta para este erro específico sem upgrade é garantir que maxFileSize e maxRequestSize
        // estão corretos e, se o problema for *exatamente* o FileCountLimitExceededException,
        // o upgrade é quase mandatório.

        return factory.createMultipartConfig();
    }
}