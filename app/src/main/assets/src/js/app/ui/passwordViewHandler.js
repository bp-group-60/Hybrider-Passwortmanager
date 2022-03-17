import {addUrlOnclick} from './onclick/onclickUrl.js';
import {getLoginName, getPassword} from '../extern/database/passwordOperations.js';
import {getUrlList} from '../extern/database/websiteOperations.js';
import {getSessionUser} from '../sessionHandler.js';
import {
  copyPasswordOnclick,
  editAbortOnclick,
  editButtonOnclick,
  onclickDeletePassword,
  onclickEditSave,
  showPasswordOnclick,
} from './onclick/passwordViewOnclick.js';
import {generateRandomPasswordOnclick, generateRandomUsernameOnclick} from './onclick/addPasswordOnclick.js';
import {createUrlItem} from './urlHandler.js';

export function addOnclickPasswordView(page) {
  page.querySelector('#password-copy').onclick = copyPasswordOnclick(page);
  page.querySelector('#password-checkbox').onclick = showPasswordOnclick(page);

  page.querySelector('#edit-button').onclick = editButtonOnclick(page);
  page.querySelector('#generate-random-username').onclick = generateRandomUsernameOnclick(page,8);
  page.querySelector('#generate-random-password').onclick = generateRandomPasswordOnclick(page,12);

  page.querySelector('#add-url').onclick = addUrlOnclick(page);
  page.querySelector('#abort-button').onclick = editAbortOnclick(page);
  page.querySelector('#commit-button').onclick = onclickEditSave(page);
  page.querySelector('#delete-button').onclick = onclickDeletePassword(page);
}

export function updatePasswordView(page) {
  page.querySelector('#edit-button').style.display = '';
  page.querySelector('#generate-random-username').style.display = 'none';
  page.querySelector('#generate-random-password').style.display = 'none';
  page.querySelector('#abort-button').style.display = 'none';
  page.querySelector('#commit-button').style.display = 'none';

  page.querySelector('#add-url').style.display = 'none';
  page.querySelector('#username').children[0].readOnly = true;
  page.querySelector('#password').children[0].readOnly = true;
  page.querySelector('#password-copy').style.display = '';

  page.querySelector('ons-toolbar .center').innerHTML = page.data.id;

  page.querySelector('#url-items').innerHTML = '';
  getUrlList(getSessionUser(), page.data.id).forEach(url => {
    page.querySelector('#url-items').append(createUrlItem(url));
  })

  page.querySelector('#username').value = getLoginName(page.data.id);
  page.querySelector('#password').value = getPassword(page.data.id);
}