import {getWebsiteOverviewList} from '../extern/database/websiteOperations.js';
import {
  getAddButtonOnclick,
  getDeleteUserOnclick,
  getListItemOnclick,
  getLogoutOnclick,
  getMoreButtonOnclick
} from './onclick/listviewOnclick.js';

export function updateListview(page) {
  page.querySelector('#overview').innerHTML = '';
  createList(page);
}

function createList(page) {
  let list = getWebsiteOverviewList();

  for (let i = 0; i < list.length; i++) {
    addToListview(page, `${list[i][0]} / ${list[i][1]}`, list[i][0]);
  }
}

function addToListview(page, innerText, websiteName) {
  let listItem = ons.createElement('<ons-list-item modifier="chevron" tappable>');

  listItem.onclick = getListItemOnclick(websiteName);
  listItem.innerText = innerText;

  page.querySelector('#overview').append(listItem);
}

export function addOnclickListview(page) {
  page.querySelector('#add-button').onclick = getAddButtonOnclick();

  page.querySelector('#more-button').onclick = getMoreButtonOnclick(page);
  page.querySelector('#delete-user').onclick = getDeleteUserOnclick();
  page.querySelector('#logout').onclick = getLogoutOnclick();
}