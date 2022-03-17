import {getPasswordOverviewList} from '../extern/database/passwordOperations.js';
import {
  onclickAddButton,
  onclickDeleteUser,
  onclickListItem,
  onclickLogout,
  onclickMoreButton
} from './onclick/listviewOnclick.js';

export function updateListview(loginName) {
  document.getElementById('overview').innerHTML = '';
  createList(loginName);
}

function createList(loginName) {
  let list = getPasswordOverviewList(loginName);

  for (let i = 0; i < list.length; i++) {
    addToListview(`${list[i][0]} / ${list[i][1]}`, list[i][0]);
  }
}

function addToListview(loginName, websiteName) {
  let listItem = ons.createElement('<ons-list-item modifier="chevron" tappable>');

  listItem.onclick = onclickListItem(websiteName);
  listItem.innerText = loginName;

  document.getElementById('overview').append(listItem);
}

export function addOnclickListview(page) {
  page.querySelector('#add-button').onclick = onclickAddButton(page);

  page.querySelector('#more-button').onclick = onclickMoreButton(page);
  page.querySelector('#delete-user').onclick = onclickDeleteUser(page);
  page.querySelector('#logout').onclick = onclickLogout(page);
}