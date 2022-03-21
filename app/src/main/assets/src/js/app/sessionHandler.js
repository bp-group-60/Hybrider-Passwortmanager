export function getSessionUsername() {
  return sessionStorage.getItem("username");
}

export function getSessionHashedUserPassword() {
  return sessionStorage.getItem("hashedUserPassword");
}

export function getSessionEncryptionKey() {
  return sessionStorage.getItem("encryptionKey");
}

export function setSessionUsername(username) {
  return sessionStorage.setItem("username", username);
}

export function setSessionHashedUserPassword(hashedUserPassword) {
  return sessionStorage.setItem("hashedUserPassword", hashedUserPassword);
}

export function setSessionEncryptionKey(encryptionKey) {
  return sessionStorage.setItem("encryptionKey", encryptionKey);
}


export function logout() {
  sessionStorage.clear();
  history.back();
}