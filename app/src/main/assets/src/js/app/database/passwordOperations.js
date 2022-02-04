import {getSessionPassword, getSessionUser} from "../sessionHandler.js";

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

  return Java_InterfacePassword.getPassword(user, password, id);
}

export function createPassword(user, website, loginName, password) {
  return Java_InterfacePassword.createPassword(user, website, loginName, password);
}

export function updatePassword(user, website, loginName, password) {
  return Java_InterfacePassword.updatePassword(user, website, loginName, password);
}

export function deletePassword(user, website) {
  return Java_InterfacePassword.deletePassword(user, website);
}

export function hashPassword(password) {
  return Java_InterfaceCrypto.hashPassword(password);
}