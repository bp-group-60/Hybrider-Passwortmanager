export function getUrlList(username, websiteName) {
  return JSON.parse(Java_InterfaceWebsite.getUrlList(username, websiteName)).dataArray;
}

export function saveUrlList(username, websiteName, list) {
  list.forEach(elm => saveUrl(username, websiteName, elm));
}

export function saveUrl(username, websiteName, url) {
  return Java_InterfaceWebsite.saveUrl(username, websiteName, url);
}

export function deleteUrlList(username, websiteName, list) {
  list.forEach(elm => deleteUrl(username, websiteName, elm));
}

export function deleteUrl(username, websiteName, url) {
  return Java_InterfaceWebsite.deleteUrl(username, websiteName, url);
}