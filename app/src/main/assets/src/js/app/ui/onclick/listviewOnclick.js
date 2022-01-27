import {getSessionPassword, getSessionUser, logout} from '../../sessionHandler.js';
import {deleteUser} from '../../database/userOperations.js';

export function onclickAddButton(page) {
  return () => {
    document.querySelector('#myNavigator').pushPage('onsPages/addPassword.html');
  };
}

export function onclickMoreButton(page) {
  return () => {
    page.querySelector('#popover').show(page.querySelector('#moreButton'));
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

export function onclickListItem(title) {
  return () => {
    document.querySelector('#myNavigator').pushPage('onsPages/passwordView.html', {data: {id: title}});
  };
}