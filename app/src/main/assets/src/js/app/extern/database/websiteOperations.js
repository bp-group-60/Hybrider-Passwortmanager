import {getSessionUsername, getSessionEncryptionKey} from "../../sessionHandler.js";

export function getWebsiteOverviewList() {
  let username = getSessionUsername();

  // array structure: [[websiteName, loginName], ...]
  return JSON.parse(Java_InterfaceWebsite.getWebsiteOverviewList(username)).dataArray;
}

export function getLoginName(websiteName) {
  let username = getSessionUsername();

  return Java_InterfaceWebsite.getLoginName(username, websiteName);
}

export function getLoginPassword(websiteName) {
  let username = getSessionUsername();
  let encryptionKey = getSessionEncryptionKey();

  return Java_InterfaceWebsite.getLoginPassword(username, websiteName, encryptionKey);
}

export function createWebsite(websiteName, loginName, loginPassword) {
  let username = getSessionUsername();
  let encryptionKey = getSessionEncryptionKey();

  return Java_InterfaceWebsite.createWebsite(username, websiteName, loginName, loginPassword, encryptionKey);
}

export function updateWebsite(websiteName, loginName, loginPassword) {
  let username = getSessionUsername();
  let encryptionKey = getSessionEncryptionKey();

  return Java_InterfaceWebsite.updateWebsite(username, websiteName, loginName, loginPassword, encryptionKey);
}

export function deleteWebsite(websiteName) {
  let username = getSessionUsername();

  return Java_InterfaceWebsite.deleteWebsite(username, websiteName);
}