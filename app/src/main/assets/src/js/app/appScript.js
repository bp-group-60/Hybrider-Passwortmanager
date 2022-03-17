import {addOnclickListview, updateListview} from './ui/listviewHandler.js';
import {addOnclickPasswordView, initViewPassword, initNewPasswordView} from './ui/passwordViewHandler.js';

import {getSessionUser, logout} from './sessionHandler.js';

document.addEventListener('show', function (event) {
  let page = event.target;

  if (page.id === 'listview') {
    updateListview(getSessionUser());
  }
});

document.addEventListener('init', function (event) {
  let page = event.target;

  if (page.id === 'listview') {
    addOnclickListview(page);
  }

  if (page.id === 'password-view') {
    addOnclickPasswordView(page);
    if (page.data.intend === 'view'){
      initViewPassword(page);
    }
    if (page.data.intend === 'new') {
      initNewPasswordView(page);
    }
  }
});
