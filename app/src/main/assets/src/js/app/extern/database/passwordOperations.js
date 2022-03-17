import {getSessionUser, getSessionKey} from "../../sessionHandler.js";

export function getPasswordOverviewList(username) {
  // array structure: [[website, loginName], ...]
  return JSON.parse(Java_InterfacePassword.getPasswordOverviewList(username)).dataArray;
}

export function getLoginName(websiteName) {
  let username = getSessionUser();

  return Java_InterfacePassword.getLoginName(username, websiteName);
}

export function getPassword(websiteName) {
  let username = getSessionUser();
  let key = getSessionKey();

  return Java_InterfacePassword.getPassword(username, websiteName, key);
}

export function createPassword(username, websiteName, loginName, loginPassword) {
  let key = getSessionKey();
  return Java_InterfacePassword.createPassword(username, websiteName, loginName, loginPassword, key);
}

export function updatePassword(username, websiteName, loginName, loginPassword) {
  let key = getSessionKey();
  return Java_InterfacePassword.updatePassword(username, websiteName, loginName, loginPassword, key);
}

export function deletePassword(username, websiteName) {
  return Java_InterfacePassword.deletePassword(username, websiteName);
}