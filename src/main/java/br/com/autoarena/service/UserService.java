package br.com.autoarena.service;

import br.com.autoarena.model.User;
import br.com.autoarena.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false) // O PasswordEncoder pode não ser necessário para todas as operações do UserService
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // Método para encontrar todos os usuários (necessário para o dropdown na página)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true) // Adicione se necessário
    public Optional<User> findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.ifPresent(user -> entityManager.refresh(user)); // <--- ADICIONADO AQUI também
        return userOptional;
    }

    @Transactional(readOnly = true) // Garante que a operação é transacional e somente leitura
    public Optional<User> findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        // Se o usuário for encontrado, force o refresh para garantir que os dados estejam atualizados do DB
        userOptional.ifPresent(user -> entityManager.refresh(user)); // <--- ADICIONADO AQUI
        return userOptional;
    }



    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) { // Método atualizado para findByEmail
        return userRepository.findByEmail(email);
    }


    @Transactional // Garante que a operação seja atômica
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + userId));

        // Garante que a roleName esteja em maiúsculas para consistência
        String roleToAdd = roleName.toUpperCase();

        // VALIDAÇÃO: Verifica se a role já existe antes de tentar adicionar
        if (user.getRoles().contains(roleToAdd)) {
            throw new IllegalArgumentException("Usuário '" + user.getUsername() + "' já possui a role '" + roleToAdd + "'.");
        }

        // Adiciona a role ao Set<String> roles.
        // O Hibernate/JPA cuidará de inserir na tabela 'user_roles'
        // automaticamente ao salvar o usuário, graças ao @ElementCollection.
        boolean added = user.getRoles().add(roleName.toUpperCase()); // Converta para maiúsculas para consistência (ex: ADMIN)

        if (!added) {
            // Opcional: Lançar exceção se a role já existe para o usuário
            // throw new IllegalArgumentException("Usuário já possui a role: " + roleName);
            System.out.println("DEBUG: Usuário " + user.getUsername() + " já possui a role " + roleName);
        }

        userRepository.save(user); // Salva o usuário, atualizando as roles na tabela 'user_roles'
    }

    @Transactional // NOVO MÉTODO PARA REMOVER ROLE
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + userId));

        // Converta para maiúsculas para garantir que a comparação seja consistente
        // com a forma como as roles são armazenadas (ex: ADMIN)
        boolean removed = user.getRoles().remove(roleName.toUpperCase());

        if (!removed) {
            throw new IllegalArgumentException("Usuário não possui a role: " + roleName);
        }

        userRepository.save(user); // Salva o usuário, removendo a role da tabela 'user_roles'
    }

    public User registerNewUser(User user) throws Exception {

        // Validação para verificar se o login já existe na base de dados
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("O login " + user.getUsername() + " já está em uso.");
        }
        // Validação para verificar se o email já existe na base de dados
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("O e-mail " + user.getEmail() + " já está em uso.");
        }
        // Validação básica para o formato do email (opcional, pode ser mais robusta)
        if (!isValidEmail(user.getEmail())) {
            throw new Exception("Formato de e-mail inválido.");
        }

        if (passwordEncoder != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // Salvar o novo usuário
        return userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        // Implemente uma validação de formato de e-mail mais robusta se necessário
        // Por exemplo, usando regex: Pattern.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", email);
        return email != null && email.contains("@") && email.contains(".");
    }

    // NOVO MÉTODO: updatePassword
    public void updatePassword(User user, String newPassword) {
        // Codifica a nova senha antes de salvar
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public User save(User user) {
        // Flag para identificar se é um novo usuário ou uma edição
        boolean isNewUser = user.getId() == null;
        String finalPasswordToSave;

        // --- Lógica para Senha (mantida e ligeiramente ajustada para clareza) ---
        if (isNewUser) {
            // Se for um novo usuário, a senha é obrigatória e deve ser encodada
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("A senha não pode ser vazia para um novo usuário.");
            }
            finalPasswordToSave = passwordEncoder.encode(user.getPassword());
        } else {
            // Se for uma edição de usuário existente
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização."));

            // Se uma nova senha foi fornecida (e não está vazia)
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // Se a nova senha não é o hash atual (significa que o usuário digitou uma nova senha)
                if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                    //finalPasswordToSave = passwordEncoder.encode(user.getPassword());
                    finalPasswordToSave = user.getPassword();
                } else {
                    // A senha fornecida é igual ao hash existente (não precisa re-hashear)
                    finalPasswordToSave = existingUser.getPassword();
                }
            } else {
                // Nenhuma nova senha fornecida, manter a senha existente
                finalPasswordToSave = existingUser.getPassword();
            }
        }
        user.setPassword(finalPasswordToSave);

        // --- Lógica para a Foto (mantida como está) ---
        // Certifique-se de que este bloco permanece para manter a foto se nenhuma nova for enviada
        if (!isNewUser && (user.getFotoData() == null || user.getFotoData().length == 0)) {
            User existingUserWithPhoto = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado ao tentar recuperar a foto antiga."));
            user.setFotoData(existingUserWithPhoto.getFotoData());
        }

        // --- Validações adicionais para User (mantidas como no seu código) ---
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário não pode ser vazio.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        if (user.getNome() == null || user.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (user.getSobrenome() == null || user.getSobrenome().trim().isEmpty()) {
            throw new IllegalArgumentException("Sobrenome não pode ser vazio.");
        }
        if (user.getDataNascimento() == null || user.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Data de nascimento inválida ou usuário muito jovem (mínimo 18 anos).");
        }

        // Validação de unicidade para username e email (se não for o próprio usuário sendo editado)
        userRepository.findByUsername(user.getUsername()).ifPresent(existingUser -> {
            if (user.getId() == null || !existingUser.getId().equals(user.getId())) {
                throw new IllegalArgumentException("Já existe um usuário com este nome de usuário.");
            }
        });

        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            if (user.getId() == null || !existingUser.getId().equals(user.getId())) {
                throw new IllegalArgumentException("Já existe um usuário com este email.");
            }
        });

        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Obtém o usuário atualmente logado
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserName = principal.getUsername();

        // 1. Verifica se o usuário a ser inativado é o próprio usuário logado
        if (user.getUsername().equals(currentUserName)) {
            throw new IllegalArgumentException("Você não pode inativar sua própria conta.");
        }

        // 2. Verifica se o usuário a ser inativado tem a role 'ADMIN'
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".contains(role));

        if (isAdmin) {
            throw new IllegalArgumentException("Não é permitido inativar um usuário com a permissão de administrador.");
        }

        // Se passar pelas validações, inverte o status e salva
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private boolean isBCryptHash(String s) {
        // Verifica se a string é não nula, começa com "$2a$10$", e tem um comprimento típico de hash BCrypt
        // (BCrypt hashes são geralmente 60 caracteres)
        return s != null && s.startsWith("$2a$10$") && s.length() >= 50;
    }

}