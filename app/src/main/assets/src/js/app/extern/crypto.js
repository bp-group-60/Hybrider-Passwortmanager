export function generateKey(plainPassword, salt) {
  return Java_InterfaceCrypto.generateKey(plainPassword, salt);
}

export function getSalt(user) {
  return Java_InterfaceCrypto.getSalt(user);
}

export function hashPassword(userPassword, salt) {
  return Java_InterfaceCrypto.hashPassword(userPassword, salt);
}