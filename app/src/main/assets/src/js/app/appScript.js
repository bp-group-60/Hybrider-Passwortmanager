import {addOnclickListview, updateListview} from './ui/listviewHandler.js';
import {addOnclickPasswordView, updatePasswordView} from './ui/passwordViewHandler.js';
import {addOnclickAddPasswordView} from './ui/addPasswordViewHandler.js';

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

  if (page.id === 'passwordView') {
    addOnclickPasswordView(page);
    updatePasswordView(page);
  }

  if (page.id === 'addPassword') {
    addOnclickAddPasswordView(page);
  }
});

window.back = function() {
  document.querySelector('#myNavigator').popPage().catch(()=>logout());
}
