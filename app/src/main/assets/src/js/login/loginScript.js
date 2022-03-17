import {clearInputError, setFormMessage, setInputError} from './loginMessages.js';
import {checkUser, createUser, existUser, getSalt} from '../app/extern/database/userOperations.js';
import {hashPassword, generateKey} from '../app/extern/crypto.js';

function checkUserAvailable(user) {
  if (existUser(user)) {
    let e = document.getElementById('signup-username');
    setInputError(e, 'Benutzername bereits vergeben');
  }
}

function checkLoginInformation(user, password) {
  sessionStorage.setItem('user', user);
  let salt = getSalt(user);
  let hashedPassword = hashPassword(password, salt);
  sessionStorage.setItem('password', hashedPassword);
  if (checkUser(user, hashedPassword)) {
    sessionStorage.setItem('key', generateKey(password, salt));
    window.location.href = './app.html';
  } else {
    const loginForm = document.getElementById('Anmeldung');
    setFormMessage(loginForm, 'error', 'Ungültiger Benutzername oder falsches Passwort');
  }
}

function registerNewUser(user, email, password) {
  if (createUser(user, email, password)) {
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
      let user = document.getElementById('user').value;
      let password = document.getElementById('password').value;
      checkLoginInformation(user, password);
    });
  }

  if (createAccountForm) {
    createAccountForm.addEventListener('submit', e => {
      e.preventDefault();

      let user = document.getElementById('signup-username').value;
      let email = document.getElementById('signup-email').value;
      let password = document.getElementById('signup-password').value;

      registerNewUser(user, email, password);
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
        const pwd1 = document.getElementById('signup-password');
        const pwd2 = document.getElementById('signup-password-confirm');
        //Passwortlänge
        if (pwd1.value.length > 0 && pwd1.value.length < 8) {
          setInputError(pwd1, 'Passwort muss mindestens 8 Zeichen enthalten');
        }
        // Groß-/Kleinschreibung + Zahlen + Sonderzeichen

        //Passwortabgleich
        if (pwd2.value.length > 0 && pwd1.value !== pwd2.value) {
          setInputError(pwd2, 'Passwörter stimmen nicht überein');
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