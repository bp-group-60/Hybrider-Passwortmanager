export function existUser(user) {
  return Java.existUser(user);
}

export function checkUser(user, password) {
  return Java.checkUser(user, password);
}

export function createUser(user, email, password) {
  return Java.createUser(user, email, password);
}

export function deleteUser(user, password) {
  return Java.deleteUser(user, password);
}