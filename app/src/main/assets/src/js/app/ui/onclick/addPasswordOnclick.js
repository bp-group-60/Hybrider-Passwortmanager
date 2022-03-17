import {getSessionUser} from '../../sessionHandler.js';
import {createPassword} from '../../extern/database/passwordOperations.js';
import {saveUrlList} from '../../extern/database/websiteOperations.js';
import {getAddedUrls} from '../urlHandler.js';
import {generateRandomString,IdentityFunction} from '../../extern/webassembly/stringOparation.js';

export function commitButtonOnclick(page) {
  return () => {
    let websiteName = document.getElementById('website-name').value;
    let loginName = document.getElementById('login-name').value;
    let loginPassword = document.getElementById('login-password').value;
    let successful = createPassword(getSessionUser(), websiteName, loginName, loginPassword);

    if (successful) {
      saveUrlList(getSessionUser(), websiteName, getAddedUrls(page));
      document.querySelector('#onsen-navigator').popPage();
      ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000});
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function generateRandomPasswordOnclick(page, length) {
  return () => {
    page.querySelector("#login-password").value = generateRandomString(length);
  }
}


export function generateRandomUsernameOnclick(page, length) {
  return () => {
    let randomString = generateRandomString(length)
    page.querySelector("#login-name").value = randomString + IdentityFunction(randomString);
    }
}


export function showPasswordOnclick(page) {
  return () => {
    if (page.querySelector('#password-checkbox').checked) {
      page.querySelector('#login-password').children[0].type = 'text';
    } else {
      page.querySelector('#login-password').children[0].type = 'password';
    }
  };
}