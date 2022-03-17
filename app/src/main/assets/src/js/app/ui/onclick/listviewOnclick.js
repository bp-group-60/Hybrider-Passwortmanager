import {getSessionPassword, getSessionUser, logout} from '../../sessionHandler.js';
import {deleteUser} from '../../extern/database/userOperations.js';

export function onclickAddButton(page) {
  return () => {
    document.querySelector('#onsen-navigator').pushPage('onsPages/addPassword.html');
  };
}

export function onclickMoreButton(page) {
  return () => {
    page.querySelector('#popover').show(page.querySelector('#more-button'));
  };
}

export function onclickDeleteUser(page) {
  return () => {
    ons.notification.confirm('Benutzer wirklich löschen?')
      .then(function (input) {
        if (input === 1) {
          deleteUser(getSessionUser(), getSessionPassword());
          history.back();
        }
      });
  };
}

export function onclickLogout(page) {
  return () => {
    logout();
  };
}

export function onclickListItem(websiteName) {
  return () => {
    document.querySelector('#onsen-navigator').pushPage('onsPages/passwordView.html', {data: {websiteName: websiteName}});
  };
}