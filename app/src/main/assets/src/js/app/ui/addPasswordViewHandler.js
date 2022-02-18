import {addUrlOnclick} from './onclick/onclickUrl.js';
import {commitButtonOnclick, showPasswordOnclick} from './onclick/addPasswordOnclick.js';

export function addOnclickAddPasswordView(page) {
  page.querySelector('#addUrl').onclick = addUrlOnclick(page);
  page.querySelector('#generateRandomPassword').onclick = generateRandomPasswordOnclick(page);
  page.querySelector('#commitButton').onclick = commitButtonOnclick(page);
  page.querySelector('#passwordCheckbox').onclick = showPasswordOnclick(page);
}