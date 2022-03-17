import {deletePassword, updatePassword,} from '../../extern/database/passwordOperations.js';
import {getSessionPassword, getSessionUser} from '../../sessionHandler.js';
import {deleteUrlList, saveUrlList,} from '../../extern/database/websiteOperations.js';
import {getAddedUrls, getRemovedUrls} from '../urlHandler.js';
import {updatePasswordView} from '../passwordViewHandler.js';
import {copyToClipboardWithTimeout} from "../../extern/clipboard.js";

export function editButtonOnclick(page) {
  return () => {
    page.querySelector('#edit-button').style.display = 'none';
    page.querySelector('#generate-random-username').style.display = '';
    page.querySelector('#generate-random-password').style.display = '';
    page.querySelector('#abort-button').style.display = '';
    page.querySelector('#commit-button').style.display = '';

    page.querySelector('#add-url').style.display = '';
    page
      .querySelectorAll('.removeIcon')
      .forEach((icon) => (icon.style.display = ''));

    page.querySelector('#login-name').children[0].readOnly = false;
    page.querySelector('#login-password').children[0].readOnly = false;
    page.querySelector('#password-copy').style.display = 'none';

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
    let websiteName = page.data.websiteName;
    let loginName = document.getElementById('login-name').value;
    let loginPassword = document.getElementById('login-password').value;
    let successful = updatePassword(
      getSessionUser(),
      websiteName,
      loginName,
      loginPassword
    );

    if (successful) {
      saveUrlList(getSessionUser(), websiteName, getAddedUrls(page));
      deleteUrlList(getSessionUser(), websiteName, getRemovedUrls(page));
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
          let websiteName = page.data.websiteName;
          deletePassword(getSessionUser(), websiteName);
          document.querySelector('#onsen-navigator').popPage();
          ons.notification.toast('Passwort wurde gelöscht!', {timeout: 3000});
        }
      });
  };
}

export function copyPasswordOnclick(page) {
  return () => {
    copyToClipboardWithTimeout(page.querySelector('#login-password').value, 20000);
    ons.notification.toast('Passwort wurde kopiert', {timeout: 3000});
  };
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
