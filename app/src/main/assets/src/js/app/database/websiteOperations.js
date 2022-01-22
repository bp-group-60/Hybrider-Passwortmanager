export function getUrlList(user, website) {
  return JSON.parse(Java.getUrlList(user, website)).dataArray;
}

export function saveUrlList(user, website, list) {
  list.forEach(elm => saveUrl(user, website, elm));
}

export function saveUrl(user, website, url) {
  return Java.saveUrl(user, website, url);
}

export function deleteUrlList(user, website, list) {
  list.forEach(elm => deleteUrl(user, website, elm));
}

export function deleteUrl(user, website, url) {
  return Java.deleteUrl(user, website, url);
}