import {getSessionPassword, getSessionUser} from "../sessionHandler.js";

export function getPasswordList(user, password) {
  // array structure: [[website, loginName], ...]
  return JSON.parse(Java.getPasswordList(user, password)).dataArray;
}

export function getLoginName(id) {
  let user = getSessionUser();
  let password = getSessionPassword();

  return Java.getLoginName(user, password, id);
}

export function getPassword(id) {
  let user = getSessionUser();
  let password = getSessionPassword();

  return Java.getPassword(user, password, id);
}

export function createPassword(user, website, loginName, password) {
  return Java.createPassword(user, website, loginName, password);
}

export function updatePassword(user, website, loginName, password) {
  return Java.updatePassword(user, website, loginName, password);
}

export function deletePassword(user, website) {
  return Java.deletePassword(user, website);
}

export function hashPassword(password) {
  //Anfrage an C
  //hashedPassword = C.hashPassword(password)
  return password;
}