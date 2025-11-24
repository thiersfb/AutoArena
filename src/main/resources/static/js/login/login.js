// Este arquivo combina o JS original do HTML com o scripty.js

/**************** SLIDE ALTERNAR LOGIN/CADASTRAR ****************/
const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

if(signUpButton){
  signUpButton.addEventListener('click', () => {
      container.classList.add("right-panel-active");
  });
}

if(signInButton){
  signInButton.addEventListener('click', () => {
      container.classList.remove("right-panel-active");
  });
}
/****************************************************************/

/**************** ICONE OLHO OCULAR/EXIBIR SENHA ****************/
//senha login
const toggleLogPassword = document.querySelector("#toggleLogPassword");
const logPassword = document.querySelector("#password");

if (toggleLogPassword && logPassword) {
    toggleLogPassword.addEventListener('click', function (e) {
        const type = logPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        logPassword.setAttribute('type', type);
        this.classList.toggle('bi-eye');
    });
}

//Senha Cadastro Login
const toggleCadPassword = document.querySelector("#toggleCadPassword");
const cadPassword = document.querySelector("#j_newpassword");

if (toggleCadPassword && cadPassword) {
    toggleCadPassword.addEventListener('click', function (e) {
        const type = cadPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        cadPassword.setAttribute('type', type);
        this.classList.toggle('bi-eye');
    });
}

//Senha Confirma Cadastro Login
const toggleCadConfPassword = document.querySelector("#toggleCadConfPassword");
const cadConfPassword = document.querySelector("#j_newpasswordconf");

if (toggleCadConfPassword && cadConfPassword) {
    toggleCadConfPassword.addEventListener('click', function (e) {
        const type = cadConfPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        cadConfPassword.setAttribute('type', type);
        this.classList.toggle('bi-eye');
    });
}
/****************************************************************/


// Conteúdo do seu scripty.js começa aqui ----------------------------------------------------

function validarSenhaLogin() {
    var password = document.getElementById("password").value;

    if (password.length < 8) {
        alert("A senha deve ter pelo menos 8 caracteres.");
        return false;
    }

    if (!/[A-Z]/.test(password)) {
        alert("A senha deve conter pelo menos uma letra maiúscula.");
        return false;
    }

    if (!/[a-z]/.test(password)) {
        alert("A senha deve conter pelo menos uma letra minúscula.");
        return false;
    }

    if (!/[0-9]/.test(password)) {
        alert("A senha deve conter pelo menos um número.");
        return false;
    }

    if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
        alert("A senha deve conter pelo menos um caractere especial.");
        return false;
    }

    return true; // Se todas as validações passarem
}

function validarSenhasCadastro() {
    var newPassword = document.getElementById("j_newpassword").value;
    var confirmNewPassword = document.getElementById("j_newpasswordconf").value;

    if (newPassword.length < 8) {
        alert("A nova senha deve ter pelo menos 8 caracteres.");
        return false;
    }
    /*
    if (!/[A-Z]/.test(newPassword)) {
        alert("A nova senha deve conter pelo menos uma letra maiúscula.");
        return false;
    }

    if (!/[a-z]/.test(newPassword)) {
        alert("A nova senha deve conter pelo menos uma letra minúscula.");
        return false;
    }

    if (!/[0-9]/.test(newPassword)) {
        alert("A nova senha deve conter pelo menos um número.");
        return false;
    }

    if (!/[!@#$%^&*(),.?":{}|<>]/.test(newPassword)) {
        alert("A nova senha deve conter pelo menos um caractere especial.");
        return false;
    }
    */

    if (newPassword !== confirmNewPassword) {
        alert("As senhas não coincidem.");
        return false;
    }

    return true; // Se todas as validações passarem
}

// Associar as funções de validação aos formulários
document.addEventListener('DOMContentLoaded', (event) => {
    const loginForm = document.getElementById('formulario');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            // A validação do login via JSF 'onsubmit="return validarSenhaLogin();"'
            // deve ser tratada aqui para o Spring Boot.
            // Se você quer que o JS bloqueie o submit, descomente a linha abaixo e a função validarSenhaLogin()
            // e.preventDefault(); // Impede o submit padrão se a validação falhar
            // if (!validarSenhaLogin()) {
            //    return false;
            // }
            // Se você não precisa da validação JS no formulário de login (apenas backend),
            // então não precisa chamar validarSenhaLogin() aqui e pode remover o 'onsubmit' do HTML.
        });
    }

    const cadastroForm = document.getElementById('frmCadastro');
    if (cadastroForm) {
        cadastroForm.addEventListener('submit', function(e) {
            if (!validarSenhasCadastro()) {
                e.preventDefault(); // Impede o submit se a validação falhar
            }
        });
    }
});