export function getSessionUser() {
  return sessionStorage.getItem("user");
}

export function getSessionPassword() {
  return sessionStorage.getItem("password");
}

export function getSessionKey() {
  return sessionStorage.getItem("key");
}

export function logout() {
  sessionStorage.clear();
  history.back();
}