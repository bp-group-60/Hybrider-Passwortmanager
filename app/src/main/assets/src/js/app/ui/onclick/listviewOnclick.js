import {getSessionHashedUserPassword, getSessionUsername, logout} from '../../sessionHandler.js';
import {deleteUser} from '../../extern/database/userOperations.js';

export function getAddButtonOnclick() {
  return () => {
    document.querySelector('#onsen-navigator').pushPage('onsPages/passwordView.html', {data: {intend: 'new'}});
  };
}

export function getMoreButtonOnclick(page) {
  return () => {
    page.querySelector('#popover').show(page.querySelector('#more-button'));
  };
}

export function getDeleteUserOnclick() {
  return () => {
    ons.notification.confirm('Benutzer wirklich löschen?')
      .then(function (input) {
        if (input === 1) {
          deleteUser(getSessionUsername(), getSessionHashedUserPassword());
          history.back();
        }
      });
  };
}

export function getLogoutOnclick() {
  return () => {
    logout();
  };
}

export function getListItemOnclick(websiteName) {
  return () => {
    document.querySelector('#onsen-navigator').pushPage('onsPages/passwordView.html', {data: {websiteName: websiteName, intend: 'view'}});
  };
}