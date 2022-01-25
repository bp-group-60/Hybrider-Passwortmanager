import {addUrlOnclick} from './onclick/onclickUrl.js';
import {getLoginName, getPassword} from '../database/passwordOperations.js';
import {getUrlList} from '../database/websiteOperations.js';
import {getSessionUser} from '../sessionHandler.js';
import {
  editAbortOnclick,
  editButtonOnclick,
  onclickDeletePassword,
  onclickEditSave,
  showPasswordOnclick
} from './onclick/passwordViewOnclick.js';
import {createUrlItem} from './urlHandler.js';

export function addOnclickPasswordView(page) {
  page.querySelector('#passwordCheckbox').onclick = showPasswordOnclick(page);

  page.querySelector('#editButton').onclick = editButtonOnclick(page);

  page.querySelector('#addUrl').onclick = addUrlOnclick(page);
  page.querySelector('#abortButton').onclick = editAbortOnclick(page);
  page.querySelector('#commitButton').onclick = onclickEditSave(page);
  page.querySelector('#deleteButton').onclick = onclickDeletePassword(page);
}

export function updatePasswordView(page) {
  page.querySelector('#editButton').style.display = '';
  page.querySelector('#abortButton').style.display = 'none';
  page.querySelector('#commitButton').style.display = 'none';

  page.querySelector('#addUrl').style.display = 'none';
  page.querySelector('#username').children[0].readOnly = true;
  page.querySelector('#password').children[0].readOnly = true;

  page.querySelector('ons-toolbar .center').innerHTML = page.data.id;

  page.querySelector('#urlItems').innerHTML = '';
  getUrlList(getSessionUser(), page.data.id).forEach(url => {
    page.querySelector('#urlItems').append(createUrlItem(url));
  })

  page.querySelector('#username').value = getLoginName(page.data.id);
  page.querySelector('#password').value = getPassword(page.data.id);
}