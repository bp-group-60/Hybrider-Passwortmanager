import {getAddUrlOnclick} from './onclick/onclickUrl.js';
import {getLoginName, getLoginPassword} from '../extern/database/websiteOperations.js';
import {getUrlList} from '../extern/database/urlOperations.js';
import {
  getCommitButtonOnclick,
  getCopyPasswordOnclick,
  getEditAbortOnclick,
  getEditButtonOnclick, getGenerateRandomPasswordOnclick, getGenerateRandomUsernameOnclick,
  getDeletePasswordOnclick,
  getEditSaveOnclick,
  getShowPasswordOnclick,
} from './onclick/passwordViewOnclick.js';
import {createUrlItem} from './urlHandler.js';

export function addOnclickPasswordView(page) {
  page.querySelector('#password-copy').onclick = getCopyPasswordOnclick(page);
  page.querySelector('#password-checkbox').onclick = getShowPasswordOnclick(page);

  page.querySelector('#edit-button').onclick = getEditButtonOnclick(page);
  page.querySelector('#generate-random-username').onclick = getGenerateRandomUsernameOnclick(page,8);
  page.querySelector('#generate-random-password').onclick = getGenerateRandomPasswordOnclick(page,12);

  page.querySelector('#add-url').onclick = getAddUrlOnclick(page);
  page.querySelector('#abort-button').onclick = getEditAbortOnclick(page);
  page.querySelector('#save-button').onclick = getEditSaveOnclick(page);
  page.querySelector('#delete-button').onclick = getDeletePasswordOnclick(page);

  page.querySelector('#commit-button').onclick = getCommitButtonOnclick(page);
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
  getUrlList(page.data.websiteName).forEach(url => {
    page.querySelector('#url-items').append(createUrlItem(url));
  })

  page.querySelector('#login-name').value = getLoginName(page.data.websiteName);
  page.querySelector('#login-password').value = getLoginPassword(page.data.websiteName);
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