import {getSessionPassword, getSessionUser, getSessionKey} from "../../sessionHandler.js";

export function getPasswordList(user, password) {
  // array structure: [[website, loginName], ...]
  return JSON.parse(Java_InterfacePassword.getPasswordList(user, password)).dataArray;
}

export function getLoginName(id) {
  let user = getSessionUser();
  let password = getSessionPassword();

  return Java_InterfacePassword.getLoginName(user, password, id);
}

export function getPassword(id) {
  let user = getSessionUser();
  let password = getSessionPassword();
  let key = getSessionKey();

  return Java_InterfacePassword.getPassword(user, password, id, key);
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