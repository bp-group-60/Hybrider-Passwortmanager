import {clearInputError, setFormMessage, setInputError} from './loginMessages.js';
import {checkUser, createUser, existUser, getSalt} from '../app/extern/database/userOperations.js';
import {hashPassword, generateKey} from '../app/extern/crypto.js';
import {setSessionKey, setSessionPassword, setSessionUser} from "../app/sessionHandler.js";

function checkUserAvailable(username) {
  if (existUser(username)) {
    let inputUsername = document.getElementById('signup-username');
    setInputError(inputUsername, 'Benutzername bereits vergeben');
    return false;
  }
  return true;
}

function checkLoginInformation(username, password) {
  setSessionUser(username)
  let salt = getSalt(username);
  let hashedPassword = hashPassword(password, salt);
  setSessionPassword(hashedPassword);
  if (checkUser(username, hashedPassword)) {
    setSessionKey(generateKey(password, salt));
    window.location.href = './app.html';
  } else {
    const loginForm = document.getElementById('Anmeldung');
    setFormMessage(loginForm, 'error', 'Ungültiger Benutzername oder falsches Passwort');
  }
}

function registerNewUser(username, email, password) {
  if (createUser(username, email, password)) {
    sessionStorage.setItem('regist', true);
    window.location.href = './index.html';
  } else {
    const createAccountForm = document.getElementById('konto-erstellen');
    setFormMessage(createAccountForm, 'error', 'Konto konnte nicht erstellt werden');
  }
}

function checkRegisterInformation(){
  let validUsername = false;
  let validUserPassword = false;
  let validEmail = false;
  let validUserPasswordConfirm = false;

  validUsername = checkUsernameInformation();
  validUserPassword = checkUserPasswordInformation();
  validUserPasswordConfirm = checkUserPasswordConfirmInformation();
  validEmail = checkEmailInformation();

  if(validUsername && validUserPassword && validEmail && validUserPasswordConfirm){
      return true;
  }
  return false;
}

function checkUsernameInformation(){
  const minlengthOfUsername = 3;
  const signupUsernameInputElement = document.getElementById('signup-username');
  let signupUsername = signupUsernameInputElement.value;

  if (signupUsername.length < minlengthOfUsername) {
    setInputError(signupUsernameInputElement, 'Benutzername muss mindestens 3 Zeichen haben');
  } else {
    return checkUserAvailable(signupUsername);
  }
  return false;
}

function checkEmailInformation(){
  //E-mail-Kriterien (TEXT @ TEXT . DOMAIN)
  const signupEmailInputElement = document.getElementById('signup-email');
  let signupEmail = signupEmailInputElement.value;
  if (signupEmail.length > 0) {
    const re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,5})+$/
    if (signupEmail.match(re)) {
      return true;
    }
  }
  setInputError(signupEmailInputElement, 'Bitte gib eine gültige E-Mail-Adresse ein.');
  return false;
}

function checkUserPasswordInformation(){
  const signupPasswordElement = document.getElementById('signup-password');
  if (signupPasswordElement.value.length >= 8) {
    return true;
  }
 setInputError(signupPasswordElement, 'Passwort muss mindestens 8 Zeichen enthalten');
 return false;
}
function checkUserPasswordConfirmInformation(){
  const signupPasswordElement = document.getElementById('signup-password');
  const signupPasswordConfirmElement = document.getElementById('signup-password-confirm');

  if(signupPasswordElement.value == signupPasswordConfirmElement.value) {
   return true;
  }
  setInputError(signupPasswordConfirmElement, 'Passwörter stimmen nicht überein');
  return false;
}

document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('Anmeldung');
  const createAccountForm = document.getElementById('konto-erstellen');

  if (loginForm) {
    if (sessionStorage.getItem('regist') === 'true') {
      setFormMessage(loginForm, 'info', 'Benutzerkonto erfolgreich erstellt.');
    }
    loginForm.addEventListener('submit', e => {
      e.preventDefault();
      let inputUsername = document.getElementById('input-username').value;
      let inputUserPassword = document.getElementById('input-userPassword').value;
      checkLoginInformation(inputUsername, inputUserPassword);
    });
  }

  if (createAccountForm) {
    createAccountForm.addEventListener('submit', e => {
      e.preventDefault();

      let signupUsername = document.getElementById('signup-username').value;
      let signupEmail = document.getElementById('signup-email').value;
      let signupPassword = document.getElementById('signup-password').value;

      if(checkRegisterInformation()){
        registerNewUser(signupUsername, signupEmail, signupPassword);
      }
    });
  }

  document.querySelectorAll('.form-input').forEach(inputElement => {
    inputElement.addEventListener('blur', e => {
      if (e.target.id === 'signup-username') {
        checkUsernameInformation();
      }
      if (e.target.id === 'signup-email' && e.target.value.length > 0) {
        checkEmailInformation();
      }
      if (e.target.id === 'signup-password') {
       checkUserPasswordInformation();
      }
      if ( e.target.id === 'signup-password-confirm') {
        checkUserPasswordConfirmInformation();
      }
    });

    inputElement.addEventListener('input', e => {
      if (e.target.id === 'signup-password') {
        clearInputError(document.getElementById('signup-password-confirm'));
      }
      clearInputError(inputElement);
    });
  });
});