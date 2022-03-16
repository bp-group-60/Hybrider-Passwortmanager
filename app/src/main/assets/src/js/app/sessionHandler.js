export function getSessionUser() {
  return sessionStorage.getItem("user");
}

export function getSessionPassword() {
  return sessionStorage.getItem("password");
}

export function getSessionKey() {
  return sessionStorage.getItem("key");
}

export function setSessionUser(user) {
  return sessionStorage.setItem("user", user);
}

export function setSessionPassword(userPasswordHashed) {
  return sessionStorage.setItem("password", userPasswordHashed);
}

export function setSessionKey(generalEncryptionKey) {
  return sessionStorage.setItem("key", generalEncryptionKey);
}


export function logout() {
  sessionStorage.clear();
  history.back();
}