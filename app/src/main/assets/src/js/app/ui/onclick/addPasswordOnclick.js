import {getSessionUser} from '../../sessionHandler.js';
import {createPassword} from '../../database/passwordOperations.js';
import {saveUrlList} from '../../database/websiteOperations.js';
import {getAddedUrls} from '../urlHandler.js';

export function commitButtonOnclick(page) {
  return () => {

    console.log('TODO: validate data');

    let name = document.getElementById('name').value;
    let loginName = document.getElementById('username').value;
    let password = document.getElementById('password').value;
    let successful = createPassword(getSessionUser(), name, loginName, password);

    if (successful) {
      saveUrlList(getSessionUser(), name, getAddedUrls(page));
      document.querySelector('#myNavigator').popPage();
      ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000});
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function showPasswordOnclick(page) {
  return () => {
    if (page.querySelector('#passwordCheckbox').checked) {
      page.querySelector('#password').children[0].type = 'text';
    } else {
      page.querySelector('#password').children[0].type = 'password';
    }
  };
}