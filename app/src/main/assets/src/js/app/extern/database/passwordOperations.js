import {getSessionPassword, getSessionUser, getSessionKey} from "../../sessionHandler.js";

export function getPasswordList(user, password) {
  // array structure: [[website, loginName], ...]
  return JSON.parse(Java_InterfacePassword.getPasswordList(user)).dataArray;
}

export function getLoginName(id) {
  let user = getSessionUser();

  return Java_InterfacePassword.getLoginName(user, id);
}

export function getPassword(website) {
  let user = getSessionUser();
  let key = getSessionKey();

  return Java_InterfacePassword.getPassword(user, website, key);
}

export function createPassword(user, website, loginName, password) {
  let key = getSessionKey();
  return Java_InterfacePassword.createPassword(user, website, loginName, password, key);
}

export function updatePassword(user, website, loginName, password) {
  let key = getSessionKey();
  return Java_InterfacePassword.updatePassword(user, website, loginName, password, key);
}

export function deletePassword(user, website) {
  return Java_InterfacePassword.deletePassword(user, website);
}