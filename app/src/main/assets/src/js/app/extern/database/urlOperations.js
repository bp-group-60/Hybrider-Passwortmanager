import {getSessionUsername} from "../../sessionHandler.js";

export function getUrlList(websiteName) {
  let username = getSessionUsername();

  return JSON.parse(Java_InterfaceUrl.getUrlList(username, websiteName)).dataArray;
}

export function saveUrlList(websiteName, list) {
  list.forEach(elm => saveUrl(websiteName, elm));
}

export function saveUrl(websiteName, webAddress) {
  let username = getSessionUsername();

  return Java_InterfaceUrl.saveUrl(username, websiteName, webAddress);
}

export function deleteUrlList(websiteName, list) {
  list.forEach(elm => deleteUrl(websiteName, elm));
}

export function deleteUrl(websiteName, webAddress) {
  let username = getSessionUsername();

  return Java_InterfaceUrl.deleteUrl(username, websiteName, webAddress);
}