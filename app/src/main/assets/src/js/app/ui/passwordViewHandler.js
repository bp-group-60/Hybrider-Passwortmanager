import {addUrlOnclick} from './onclick/onclickUrl.js';
import {getLoginName, getPassword} from '../extern/database/passwordOperations.js';
import {getUrlList} from '../extern/database/websiteOperations.js';
import {getSessionUser} from '../sessionHandler.js';
import {
  commitButtonOnclick,
  copyPasswordOnclick,
  editAbortOnclick,
  editButtonOnclick, generateRandomPasswordOnclick, generateRandomUsernameOnclick,
  onclickDeletePassword,
  onclickEditSave,
  showPasswordOnclick,
} from './onclick/passwordViewOnclick.js';
import {createUrlItem} from './urlHandler.js';

export function addOnclickPasswordView(page) {
  page.querySelector('#password-copy').onclick = copyPasswordOnclick(page);
  page.querySelector('#password-checkbox').onclick = showPasswordOnclick(page);

  page.querySelector('#edit-button').onclick = editButtonOnclick(page);
  page.querySelector('#generate-random-username').onclick = generateRandomUsernameOnclick(page,8);
  page.querySelector('#generate-random-password').onclick = generateRandomPasswordOnclick(page,12);

  page.querySelector('#add-url').onclick = addUrlOnclick(page);
  page.querySelector('#abort-button').onclick = editAbortOnclick(page);
  page.querySelector('#save-button').onclick = onclickEditSave(page);
  page.querySelector('#delete-button').onclick = onclickDeletePassword(page);

  page.querySelector('#commit-button').onclick = commitButtonOnclick(page);
}

export function updatePasswordView(page) {
  page.querySelector('#edit-button').style.display = '';
  page.querySelector('#delete-button').style.display = '';

  page.querySelector('#abort-button').style.display = 'none';
  page.querySelector('#save-button').style.display = 'none';

  page.querySelector('#add-url').style.display = 'none';
  page.querySelector('#login-name').children[0].readOnly = true;
  page.querySelector('#login-password').children[0].readOnly = true;
  page.querySelector('#password-copy').style.display = '';

  page.querySelector('#generate-wrapper').style.display = 'none';

  page.querySelector('ons-toolbar .center').innerHTML = page.data.websiteName;

  page.querySelector('#url-items').innerHTML = '';
  getUrlList(getSessionUser(), page.data.websiteName).forEach(url => {
    page.querySelector('#url-items').append(createUrlItem(url));
  })

  page.querySelector('#login-name').value = getLoginName(page.data.websiteName);
  page.querySelector('#login-password').value = getPassword(page.data.websiteName);
}

export function initViewPassword(page){
  page.querySelector('#edit-buttons').style.display = '';
  updatePasswordView(page);
}

export function initNewPasswordView(page) {
  page.querySelector('#website-name-wrapper').style.display = '';
  page.querySelector('#add-url').style.display = '';
  page.querySelector('#generate-wrapper').style.display = '';
  page.querySelector('#new-button').style.display = '';
}