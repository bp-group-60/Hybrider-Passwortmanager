export function getSessionUser() {
  return sessionStorage.getItem("user");
}

export function getSessionPassword() {
  return sessionStorage.getItem("password");
}

export function logout() {
  sessionStorage.clear();
  history.back();
}