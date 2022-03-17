export function existUser(username) {
  return Java_InterfaceUser.existUser(username);
}

export function checkUser(username, hashedUserPassword) {
  return Java_InterfaceUser.checkUser(username, hashedUserPassword);
}

export function createUser(username, email, plainUserPassword) {
  return Java_InterfaceUser.createUser(username, email, plainUserPassword);
}

export function deleteUser(username, hashedUserPassword) {
  return Java_InterfaceUser.deleteUser(username, hashedUserPassword);
}

export function getSalt(username) {
  return Java_InterfaceUser.getSalt(username);
}