import {deletePassword, updatePassword,} from '../../extern/database/passwordOperations.js';
import {getSessionPassword, getSessionUser} from '../../sessionHandler.js';
import {deleteUrlList, saveUrlList,} from '../../extern/database/websiteOperations.js';
import {getAddedUrls, getRemovedUrls} from '../urlHandler.js';
import {updatePasswordView} from '../passwordViewHandler.js';
import {copyToClipboardWithTimeout} from "../../extern/tools.js";

export function editButtonOnclick(page) {
  return () => {
    page.querySelector('#editButton').style.display = 'none';
    page.querySelector('#generateRandomPassword').style.display = '';
    page.querySelector('#abortButton').style.display = '';
    page.querySelector('#commitButton').style.display = '';

    page.querySelector('#addUrl').style.display = '';
    page
      .querySelectorAll('.removeIcon')
      .forEach((icon) => (icon.style.display = ''));

    page.querySelector('#username').children[0].readOnly = false;
    page.querySelector('#password').children[0].readOnly = false;
    page.querySelector('#passwordCopy').style.display = 'none';

    page.querySelector('ons-toolbar .center').innerHTML = 'Bearbeiten';
  };
}

export function editAbortOnclick(page) {
  return () => {
    updatePasswordView(page);
  };
}

export function onclickEditSave(page) {
  return () => {
    let name = page.data.id;
    let loginName = document.getElementById('username').value;
    let password = document.getElementById('password').value;
    let successful = updatePassword(
      getSessionUser(),
      name,
      loginName,
      password
    );

    if (successful) {
      saveUrlList(getSessionUser(), name, getAddedUrls(page));
      deleteUrlList(getSessionUser(), name, getRemovedUrls(page));
      ons.notification.toast('Änderungen gespeichert!', {timeout: 3000});

      updatePasswordView(page);
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function onclickDeletePassword(page) {
  return () => {
    ons.notification
      .confirm('Passwort wirklich löschen?')
      .then(function (input) {
        if (input === 1) {
          let name = page.data.id;
          deletePassword(getSessionUser(), name);
          document.querySelector('#myNavigator').popPage();
          ons.notification.toast('Passwort wurde gelöscht!', {timeout: 3000});
        }
      });
  };
}

export function copyPasswordOnclick(page) {
  return () => {
    copyToClipboardWithTimeout(page.querySelector('#password').value, 20000);
    ons.notification.toast('Passwort wurde kopiert', {timeout: 3000});
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
