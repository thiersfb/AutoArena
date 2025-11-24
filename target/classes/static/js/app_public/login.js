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
const logPassword = document.querySelector("#txtLogPassword");

toggleLogPassword.addEventListener('click', function (e) {
    // toggle the type attribute
    const type = logPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    logPassword.setAttribute('type', type);
    // toggle the eye / eye slash icon
    this.classList.toggle('bi-eye');
});


//Senha Confirma Cadastro Login
const toggleCadConfPassword = document.querySelector("#toggleCadConfPassword");
const cadConfPassword = document.querySelector("#txtCadConfPassword");

toggleCadConfPassword.addEventListener('click', function (e) {
    // toggle the type attribute
    const type = cadConfPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    cadConfPassword.setAttribute('type', type);
    // toggle the eye / eye slash icon
    this.classList.toggle('bi-eye');
});


//Senha Cadastro Login
const toggleCadPassword = document.querySelector("#toggleCadPassword");
const cadPassword = document.querySelector("#txtCadPassword");

toggleCadPassword.addEventListener('click', function (e) {
    // toggle the type attribute
    const type = cadPassword.getAttribute('type') === 'password' ? 'text' : 'password';
    cadPassword.setAttribute('type', type);
    // toggle the eye / eye slash icon
    this.classList.toggle('bi-eye');
});

/****************************************************************/