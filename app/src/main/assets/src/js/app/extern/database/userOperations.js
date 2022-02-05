export function existUser(user) {
  return Java_InterfaceUser.existUser(user);
}

export function checkUser(user, password) {
  return Java_InterfaceUser.checkUser(user, password);
}

export function createUser(user, email, password) {
  return Java_InterfaceUser.createUser(user, email, password);
}

export function deleteUser(user, password) {
  return Java_InterfaceUser.deleteUser(user, password);
}