import {addUrlOnclick} from './onclick/onclickUrl.js';
import {commitButtonOnclick, showPasswordOnclick, generateRandomPasswordOnclick, generateRandomUsernameOnclick} from './onclick/addPasswordOnclick.js';

export function addOnclickAddPasswordView(page) {
  page.querySelector('#add-url').onclick = addUrlOnclick(page);
  page.querySelector('#generate-random-username').onclick = generateRandomUsernameOnclick(page,8);
  page.querySelector('#generate-random-password').onclick = generateRandomPasswordOnclick(page,12);
  page.querySelector('#commit-button').onclick = commitButtonOnclick(page);
  page.querySelector('#password-checkbox').onclick = showPasswordOnclick(page);
}