import {clearInputError, setFormMessage, setInputError} from './loginMessages.js';
import {checkUser, createUser, existUser, getSalt} from '../app/extern/database/userOperations.js';
import {hashPassword, generateKey} from '../app/extern/crypto.js';

function checkUserAvailable(username) {
  if (existUser(username)) {
    let inputUsername = document.getElementById('signup-username');
    setInputError(inputUsername, 'Benutzername bereits vergeben');
  }
}

function checkLoginInformation(username, password) {
  sessionStorage.setItem('user', username);
  let salt = getSalt(username);
  let hashedPassword = hashPassword(password, salt);
  sessionStorage.setItem('password', hashedPassword);
  if (checkUser(username, hashedPassword)) {
    sessionStorage.setItem('key', generateKey(password, salt));
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

document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('Anmeldung');
  const createAccountForm = document.getElementById('konto-erstellen');

  if (loginForm) {
    if (sessionStorage.getItem('regist') === 'true') {
      setFormMessage(loginForm, 'info', 'Benutzerkonto erfolgreich erstellt.');
    }
    loginForm.addEventListener('submit', e => {
      e.preventDefault();
      let username = document.getElementById('input-username').value;
      let password = document.getElementById('input-userPassword').value;
      checkLoginInformation(username, password);
    });
  }

  if (createAccountForm) {
    createAccountForm.addEventListener('submit', e => {
      e.preventDefault();

      let signupUsername = document.getElementById('signup-username').value;
      let signupEmail = document.getElementById('signup-email').value;
      let signupPassword = document.getElementById('signup-password').value;

      registerNewUser(signupUsername, signupEmail, signupPassword);
    });
  }

  document.querySelectorAll('.form-input').forEach(inputElement => {
    inputElement.addEventListener('blur', e => {
      //Mindestlänge Benutzername
      if (e.target.id === 'signup-username') {
        if (e.target.value.length > 0 && e.target.value.length < 3) {
          setInputError(inputElement, 'Benutzername muss mindestens 3 Zeichen haben');
        } else {
          checkUserAvailable(e.target.value);
        }
      }
      //E-mail-Kriterien (TEXT @ TEXT . DOMAIN)
      if (e.target.id === 'signup-email' && e.target.value.length > 0) {
        const re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,5})+$/
        if (!e.target.value.match(re)) {
          setInputError(inputElement, 'Bitte gib eine gültige E-Mail-Adresse ein.');
        }
      }
      //Passwortfelder aktiv
      if (e.target.id === 'signup-password' || e.target.id === 'signup-password-confirm') {
        const signupPassword = document.getElementById('signup-password');
        const signupPasswordConfirm = document.getElementById('signup-password-confirm');
        //Passwortlänge
        if (signupPassword.value.length > 0 && signupPassword.value.length < 8) {
          setInputError(signupPassword, 'Passwort muss mindestens 8 Zeichen enthalten');
        }
        // Groß-/Kleinschreibung + Zahlen + Sonderzeichen

        //Passwortabgleich
        if (signupPasswordConfirm.value.length > 0 && signupPassword.value !== signupPasswordConfirm.value) {
          setInputError(signupPasswordConfirm, 'Passwörter stimmen nicht überein');
        }
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