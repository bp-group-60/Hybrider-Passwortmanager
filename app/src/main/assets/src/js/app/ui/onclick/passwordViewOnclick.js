import {createWebsite, deleteWebsite, updateWebsite,} from '../../extern/database/websiteOperations.js';
import {getSessionUsername} from '../../sessionHandler.js';
import {deleteUrlList, saveUrlList,} from '../../extern/database/urlOperations.js';
import {getAddedUrls, getRemovedUrls} from '../urlHandler.js';
import {updatePasswordView} from '../passwordViewHandler.js';
import {copyToClipboardWithTimeout} from "../../extern/clipboard.js";
import {generateRandomString, identityFunction} from "../../extern/webassembly/stringOparation.js";

export function getEditButtonOnclick(page) {
  return () => {
    page.querySelector('#delete-button').style.display = 'none';

    page.querySelector('#edit-button').style.display = 'none';
    page.querySelector('#generate-wrapper').style.display = '';
    page.querySelector('#abort-button').style.display = '';
    page.querySelector('#save-button').style.display = '';

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

export function getEditAbortOnclick(page) {
  return () => {
    updatePasswordView(page);
  };
}

export function getEditSaveOnclick(page) {
  return () => {
    let websiteName = page.data.websiteName;
    let loginName = document.getElementById('login-name').value;
    let loginPassword = document.getElementById('login-password').value;
    let successful = updateWebsite(websiteName, loginName, loginPassword);

    if (successful) {
      saveUrlList(websiteName, getAddedUrls(page));
      deleteUrlList(websiteName, getRemovedUrls(page));
      ons.notification.toast('Änderungen gespeichert!', {timeout: 3000});

      updatePasswordView(page);
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function getDeletePasswordOnclick(page) {
  return () => {
    ons.notification
      .confirm('Passwort wirklich löschen?')
      .then(function (input) {
        if (input === 1) {
          deleteWebsite(page.data.websiteName);
          document.querySelector('#onsen-navigator').popPage();
          ons.notification.toast('Passwort wurde gelöscht!', {timeout: 3000});
        }
      });
  };
}

export function getCopyPasswordOnclick(page) {
  return () => {
    copyToClipboardWithTimeout(page.querySelector('#login-password').value, 20000);
    ons.notification.toast('Passwort wurde kopiert', {timeout: 3000});
  };
}

export function getShowPasswordOnclick(page) {
  return () => {
    if (page.querySelector('#password-checkbox').checked) {
      page.querySelector('#login-password').children[0].type = 'text';
    } else {
      page.querySelector('#login-password').children[0].type = 'password';
    }
  };
}

export function getCommitButtonOnclick(page) {
  return () => {
    let websiteName = document.getElementById('website-name').value;
    let loginName = document.getElementById('login-name').value;
    let loginPassword = document.getElementById('login-password').value;
    let successful = createWebsite(websiteName, loginName, loginPassword);

    if (successful) {
      saveUrlList(websiteName, getAddedUrls(page));
      document.querySelector('#onsen-navigator').popPage();
      ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000});
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function getGenerateRandomPasswordOnclick(page, length) {
  return () => {
    let randomString = generateRandomString(length);
    let currentPasswordInputValue = page.querySelector("#login-password").value;

    if(currentPasswordInputValue === ""){
     page.querySelector("#login-password").value = randomString;
    } else{
     page.querySelector("#login-password").value = currentPasswordInputValue + identityFunction(currentPasswordInputValue);
    }

  }
}

export function getGenerateRandomUsernameOnclick(page, length) {
  return () => {
    let randomString = generateRandomString(length)
    page.querySelector("#login-name").value = randomString;
  }
}