import {getPasswordList} from '../extern/database/passwordOperations.js';
import {
  onclickAddButton,
  onclickDeleteUser,
  onclickListItem,
  onclickLogout,
  onclickMoreButton
} from './onclick/listviewOnclick.js';

export function updateListview(user, password) {
  document.getElementById('overview').innerHTML = '';
  createList(user, password);
}

function createList(user, password) {
  let list = getPasswordList(user, password);

  for (let i = 0; i < list.length; i++) {
    addToListview(`${list[i][0]} / ${list[i][1]}`, list[i][0]);
  }
}

function addToListview(name, id) {
  let listItem = ons.createElement('<ons-list-item modifier="chevron" tappable>');

  listItem.onclick = onclickListItem(id);
  listItem.innerText = name;

  document.getElementById('overview').append(listItem);
}

export function addOnclickListview(page) {
  page.querySelector('#addButton').onclick = onclickAddButton(page);

  page.querySelector('#moreButton').onclick = onclickMoreButton(page);
  page.querySelector('#deleteUser').onclick = onclickDeleteUser(page);
  page.querySelector('#logout').onclick = onclickLogout(page);
}